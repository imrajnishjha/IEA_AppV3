package com.example.ieaapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import com.google.android.gms.tasks.OnSuccessListener;
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


public class payment_proof extends AppCompatActivity {

    ImageView proof_img;
    AppCompatButton insert_btn, payment_proofbackbtn, upload_btn;
    String fullname, email, companyName, Department, phoneNo, Turnover, memberfees, amountleft, paymentMethod, nameOfReceiver = "",gstNo="";
    FirebaseDatabase memberDirectoryRoot;
    DatabaseReference memberDirectoryRef;
    private StorageReference memberstorageRef;
    Bitmap imageBitmap;
    EditText paymentReceiverName;
    TextView paymentReceiverHeading;
    Dialog registrarionConfirmationDialog;
    ProgressDialog dialog;
    ActivityResultLauncher<String> mGetImage;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_proof);
        memberstorageRef = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");

        proof_img = findViewById(R.id.proof_img);
        insert_btn = findViewById(R.id.insert_proof_img_btn);
        payment_proofbackbtn = findViewById(R.id.paymentproof_back_button);
        upload_btn = findViewById(R.id.proofupload_btn);
        paymentReceiverName = findViewById(R.id.receiver_name_editxt);
        paymentReceiverHeading = findViewById(R.id.name_of_receiver_text);

        Intent intent = getIntent();
        fullname = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        phoneNo = intent.getStringExtra("phoneno");
        companyName = intent.getStringExtra("cname");
        Department = intent.getStringExtra("department");
        Turnover = intent.getStringExtra("annual_turn");
        memberfees = intent.getStringExtra("memberfee");
        amountleft = intent.getStringExtra("costleft");
        paymentMethod = intent.getStringExtra("paymentMethod");
        gstNo = intent.getStringExtra("GstNo");



        if (paymentMethod.equals("Physical Method Via Cash")) {
            paymentReceiverName.setVisibility(View.VISIBLE);
            paymentReceiverHeading.setVisibility(View.VISIBLE);
        }

        payment_proofbackbtn.setOnClickListener(view -> {
            finish();
        });

        mGetImage =registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(1, 1)
                        .start(payment_proof.this);
            }
        });

        insert_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(payment_proof.this);
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
                        mGetImage.launch("image/*");
                        alertDialogImg.dismiss();
                        imageBitmap = null;
                    }

                });
                cameraCardView.setOnClickListener(view -> {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();

                    } else {
                        PickImagefromcamera();
                        imageUri = null;
                        alertDialogImg.dismiss();
                    }
                });
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            Boolean sendNotification = true;

            @Override
            public void onClick(View v) {

                if (!paymentReceiverName.getText().toString().isEmpty()) {
                    nameOfReceiver = paymentReceiverName.getText().toString();
                }
                if (paymentMethod.equals("Physical Method Via Cash")) {
                    if (paymentReceiverName.getText().toString().isEmpty()) {
                        paymentReceiverName.setError("Enter receiver name");
                        paymentReceiverName.requestFocus();
                    } else {
                        if (sendNotification) {
                            dialog.show();
                            imguploader(imageBitmap,dialog,imageUri);
                            sendNotification = false;
                        }

                    }
                } else {
                    if (sendNotification) {
                        dialog.show();
                        imguploader(imageBitmap,dialog,imageUri);
                        sendNotification = false;
                    }
                }

            }
        });


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
            proof_img.setImageBitmap(imageBitmap);
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imageUri = UCrop.getOutput(data);
            proof_img.setImageURI(imageUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);

        }

    }

    public void imguploader(Bitmap imageBitmap,ProgressDialog dialog,Uri uri) {

        memberDirectoryRoot = FirebaseDatabase.getInstance();
        memberDirectoryRef = memberDirectoryRoot.getReference("Temp Registry");

        if (imageBitmap != null) {

            Uri proofimg_uri = getimageUri(payment_proof.this, imageBitmap);
            Log.d("imguri", "onClick: " + proofimg_uri.toString());
            ImgdataHaldler(dialog,proofimg_uri);

        }else if(uri != null){
            ImgdataHaldler(dialog,uri);

        } else {
            dialog.dismiss();
            Toast.makeText(payment_proof.this, "Please Upload Image", Toast.LENGTH_SHORT).show();
        }

    }

    public void ImgdataHaldler(ProgressDialog dialog,Uri proofimg_uri){
        StorageReference urirefence = memberstorageRef.child("paymentproof/" + UUID.randomUUID().toString());
        urirefence.putFile(proofimg_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                urirefence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserRegistrationHelperClass userRegistrationHelperClass = new UserRegistrationHelperClass(fullname, email, phoneNo, companyName, Department, Turnover, uri.toString(), amountleft, memberfees, nameOfReceiver,gstNo);
                        memberDirectoryRef.child(email.replaceAll("\\.", "%7")).setValue(userRegistrationHelperClass);

                        final long[] registrationCount = new long[1];

                        FirebaseDatabase.getInstance().getReference("Temp Registry").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                registrationCount[0] = snapshot.getChildrenCount();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        FirebaseDatabase.getInstance().getReference("Core Member Token").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot tokenSnapshot : snapshot.getChildren()) {
                                    String grievanceUserToken = Objects.requireNonNull(tokenSnapshot.getValue()).toString();

                                    FcmNotificationsSender grievanceNotificationSender = new FcmNotificationsSender(grievanceUserToken,
                                            "IEA New Registration",
                                            "New registration application has been submitted. (" + registrationCount[0] + ")",
                                            getApplicationContext(),
                                            payment_proof.this,
                                            (int) registrationCount[0]);

                                    grievanceNotificationSender.SendNotifications();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(payment_proof.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        confirmationPopup();

                    }
                });

            }
        });
    }

    public void confirmationPopup() {
        registrarionConfirmationDialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View confirmationView = inflater.inflate(R.layout.confirmation_popup, null);
        registrarionConfirmationDialog.setContentView(confirmationView);
        registrarionConfirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        registrarionConfirmationDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), LandingPage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }, 3000);
    }

}