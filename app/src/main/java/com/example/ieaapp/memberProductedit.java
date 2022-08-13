package com.example.ieaapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class memberProductedit extends AppCompatActivity {

    ImageView Productimg;
    EditText productName,productDesc,productPrice;
    AppCompatButton editproductbackbutton;
    CardView Addproductbtn;
    ActivityResultLauncher<String> mGetProductImage;
    Uri productImageUri=null;
    String productKey,ownerEmail,productPurlStr;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    StorageReference storageProfilePicReference;
    Bitmap imageBitmap;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_productedit);

        productKey = getIntent().getStringExtra("EditItemKey");
        Productimg = findViewById(R.id.edit_product_image_iv);
        productName = findViewById(R.id.edit_title_edtTxt);
        productDesc = findViewById(R.id.edit_description_edtTxt);
        productPrice = findViewById(R.id.edit_price_edtTxt);
        Addproductbtn = findViewById(R.id.edit_product_btn);
        mAuth=FirebaseAuth.getInstance();
        editproductbackbutton = findViewById(R.id.editProduct_back_button);
        editproductbackbutton.setOnClickListener(view -> finish());

        ownerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products by Member").child(ownerEmail.replaceAll("\\.","%7")).child(productKey);
        storageProfilePicReference = FirebaseStorage.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String productPriceStr= Objects.requireNonNull(snapshot.child("productPrice").getValue()).toString();
                String productNameStr= Objects.requireNonNull(snapshot.child("productTitle").getValue()).toString();
                String productDescStr= Objects.requireNonNull(snapshot.child("productDescription").getValue()).toString();
                productPurlStr= Objects.requireNonNull(snapshot.child("productImageUrl").getValue()).toString();

                productPrice.setText("\u20B9"+productPriceStr);
                productName.setText(productNameStr);
                productDesc.setText(productDescStr);
                Glide.with(getApplicationContext())
                        .load(productPurlStr)
                        .placeholder(R.drawable.iea_logo)
                        .error(R.drawable.iea_logo)
                        .into(Productimg);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mGetProductImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(5, 6)
                        .start(memberProductedit.this, 2);
            }
        });

        Productimg.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(memberProductedit.this);
            LayoutInflater layoutInflater= getLayoutInflater();
            View pickImgview = layoutInflater.inflate(R.layout.image_picker_item,null);
            builder.setCancelable(true);
            builder.setView(pickImgview);
            AlertDialog alertDialogImg = builder.create();
            Window window = alertDialogImg.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            alertDialogImg.show();
            window.setAttributes(wlp);

            CardView cameraCardView = pickImgview.findViewById(R.id.chooseCamera);
            CardView galleryCardView = pickImgview.findViewById(R.id.chooseGallery);

            galleryCardView.setOnClickListener(view -> {
                if (!checkStoragePermission()) {
                    requestStoragePermission();

                } else {
                    mGetProductImage.launch("image/*");
                    alertDialogImg.dismiss();
                    imageBitmap = null;
                }

            });
            cameraCardView.setOnClickListener(view -> {
                if (!checkCameraPermission()) {
                    requestCameraPermission();

                } else {
                    PickImagefromcamera();
                    productImageUri = null;
                    alertDialogImg.dismiss();
                }
            });
        });

        Addproductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productImageUri != null){
                    uploadProductImage(productImageUri);
                } else {
                    uploadProductImageStr(productPurlStr);
                }

            }
        });


    }



    private void uploadProductImage(Uri productImageUri) {
        StorageReference productFileRef = storageProfilePicReference.child("Product Images/" + mAuth.getCurrentUser().getEmail().toString() + productName.getText().toString());
        productFileRef.putFile(productImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                productFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference productReferenceByUser = FirebaseDatabase.getInstance().getReference().child("Products by Member")
                                .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7")).child(productKey);


                        String productTitleStr = productName.getText().toString();
                        String productDescriptionStr = productDesc.getText().toString();
                        String productPriceStr = productPrice.getText().toString();

                        ProductModel newProduct = new ProductModel(uri.toString(), productTitleStr, productDescriptionStr, productPriceStr, mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7"));
                        productReferenceByUser.setValue(newProduct);

                    }
                });
            }
        });
    }

    private void uploadProductImageStr(String productImageUri) {

        DatabaseReference productReferenceByUser = FirebaseDatabase.getInstance().getReference().child("Products by Member")
                                .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7")).child(productKey);

        String productTitleStr = productName.getText().toString();
        String productDescriptionStr = productDesc.getText().toString();
        String productPriceStr = productPrice.getText().toString();

        ProductModel newProduct = new ProductModel(productPurlStr, productTitleStr, productDescriptionStr, productPriceStr, mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7"));
        productReferenceByUser.setValue(newProduct);

    }

    private void PickImagefromcamera() {
        Intent fromcamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(fromcamera, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

    public Uri getimageUri(Context context, Bitmap bitimage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitimage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            Productimg.setImageBitmap(imageBitmap);
        } else if (resultCode == RESULT_OK && requestCode == 2) {
            productImageUri = UCrop.getOutput(data);
            Productimg.setImageURI(productImageUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);

        }

    }


}