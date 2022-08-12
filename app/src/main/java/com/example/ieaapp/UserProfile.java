package com.example.ieaapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class UserProfile extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
    String userEmailConverted, userCompanyNameStr;
    Uri resultUri, pdfUri, productImageUri = null;
    ProgressDialog productUploadProgressDialog;
    Bitmap imageBitmap;

    {
        assert userEmail != null;
        userEmailConverted = userEmail.replaceAll("\\.", "%7");
    }

    DatabaseReference ref = database.getReference("Registered Users/" + userEmailConverted);

    ImageView userProfileImage, uploadProductImageIv, logoutImg;
    TextView userProfileName, userMembershipId, userMembershipDate, userMembershipExpiryDate, logoutTv;
    EditText userContactNumberEdtTxt, userDateOfBirthEdtTxt, userEmailEdtTxt, userCompanyNameEdtTxt, userAddressEdtTxt,
            productTitleEdtTxt, productDescriptionEdtTxt, productPriceEdtTxt;
    AppCompatButton saveProfileBtn, userProfileBackBtn, uploadBrochureBtn, addProductBtn, editProductBtn;
    ActivityResultLauncher<String> mGetContent, mGetPdf, mGetProductImage;
    TextInputEditText userBioEditText;
    CardView uploadProductImageCv;

    StorageReference storageProfilePicReference;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userProfileImage = findViewById(R.id.user_profile_image);
        userProfileName = findViewById(R.id.user_profile_name);
        userMembershipDate = findViewById(R.id.user_membership_date);
        userContactNumberEdtTxt = findViewById(R.id.user_profile_contactNumber_edtTxt);
        userDateOfBirthEdtTxt = findViewById(R.id.user_profile_dateOfBirth_edtTxt);
        userEmailEdtTxt = findViewById(R.id.user_profile_email_edtTxt);
        userCompanyNameEdtTxt = findViewById(R.id.user_profile_company_name_edtTxt);
        userAddressEdtTxt = findViewById(R.id.user_profile_address_edtTxt);
        saveProfileBtn = findViewById(R.id.user_profile_save_button);
        userBioEditText = findViewById(R.id.user_bio_input_edttxt);
        userMembershipExpiryDate = findViewById(R.id.expiry_dateId);
        userProfileBackBtn = findViewById(R.id.userProfile_back_button);
        uploadBrochureBtn = findViewById(R.id.upload_brochure_btn);
        addProductBtn = findViewById(R.id.add_product_btn);
        editProductBtn = findViewById(R.id.edit_product_btn);
        productTitleEdtTxt = findViewById(R.id.product_title_edtTxt);
        productDescriptionEdtTxt = findViewById(R.id.product_description_edtTxt);
        productPriceEdtTxt = findViewById(R.id.product_price_edtTxt);
        uploadProductImageCv = findViewById(R.id.upload_product_image_cv);
        uploadProductImageIv = findViewById(R.id.upload_product_image_iv);
        logoutImg = findViewById(R.id.logout_img);
        logoutTv = findViewById(R.id.logout_text);
        productUploadProgressDialog = new ProgressDialog(this);

        userProfileBackBtn.setOnClickListener(view -> {
            finish();
        });

        editProductBtn.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), BaasMemberProfile.class);
            intent.putExtra("BaasItemKey", userEmailConverted);
            startActivity(intent);
        });

        storageProfilePicReference = FirebaseStorage.getInstance().getReference();

        StorageReference fileRef = storageProfilePicReference.child("User Profile Pictures/" + mAuth.getCurrentUser().getEmail() + "ProfilePicture");
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .placeholder(R.drawable.iea_logo)
                        .circleCrop()
                        .error(R.drawable.iea_logo)
                        .into(userProfileImage);
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userProfileNameStr = Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                userProfileName.setText(userProfileNameStr);

                String userMembershipIdStr = Objects.requireNonNull(dataSnapshot.child("member_id").getValue()).toString();

                String userMembershipDateStr = Objects.requireNonNull(dataSnapshot.child("date_of_membership").getValue()).toString();
                userMembershipDate.setText(userMembershipDateStr);
                String userExpiryDate = yearincrementer(userMembershipDateStr);
                userMembershipExpiryDate.setText(userExpiryDate);

                String userContactNumberStr = Objects.requireNonNull(dataSnapshot.child("phone_number").getValue()).toString();
                userContactNumberEdtTxt.setText(userContactNumberStr);

                String userDOBStr = Objects.requireNonNull(dataSnapshot.child("date_of_birth").getValue()).toString();
                if (dataSnapshot.child("date_of_birth").getValue().toString().equals("")) {
                    userDateOfBirthEdtTxt.setText(userDOBStr);
                } else {
                    userDateOfBirthEdtTxt.setText(userDOBStr);
                    userDateOfBirthEdtTxt.setFocusable(false);
                    userDateOfBirthEdtTxt.setTextColor(getResources().getColor(R.color.grey));
                }

                String userEmailStr = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                userEmailEdtTxt.setText(userEmailStr);


                userCompanyNameStr = Objects.requireNonNull(dataSnapshot.child("company_name").getValue()).toString();
                userCompanyNameEdtTxt.setText(userCompanyNameStr);

                String userAddressStr = Objects.requireNonNull(dataSnapshot.child("address").getValue()).toString();
                userAddressEdtTxt.setText(userAddressStr);

                String userBioStr = Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                userBioEditText.setText(userBioStr);

                String corePictureUrl = Objects.requireNonNull(dataSnapshot.child("purl").getValue()).toString();
                Glide.with(getApplicationContext())
                        .load(corePictureUrl)
                        .placeholder(R.drawable.iea_logo)
                        .circleCrop()
                        .error(R.drawable.iea_logo)
                        .into(userProfileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        saveProfileBtn.setOnClickListener(view -> {
            String userContactNumberStr = userContactNumberEdtTxt.getText().toString();
            String userDOBStr = userDateOfBirthEdtTxt.getText().toString();
            String userAddressStr = userAddressEdtTxt.getText().toString();
            String userBioStr = userBioEditText.getText().toString();

            updateData(userContactNumberStr, userDOBStr, userAddressStr, userBioStr);

            if (resultUri != null) {
                uploadImageToFirebase(resultUri);
            } else if(imageBitmap!=null) {
                Uri camImg = getimageUri(UserProfile.this,imageBitmap);
                uploadImageToFirebase(camImg);
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(1, 1)
                        .start(UserProfile.this);
            }
        });

        mGetProductImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(5, 6)
                        .start(UserProfile.this, 2);
                uploadProductImageIv.setPadding(0, 0, 0, 0);
            }
        });

        uploadProductImageCv.setOnClickListener(view -> {
            mGetProductImage.launch("image/*");

        });

        userProfileImage.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
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

            galleryCardView.setOnClickListener(view1 -> {
                if (!checkStoragePermission()) {
                    requestStoragePermission();

                } else {
                    mGetContent.launch("image/*");
                    alertDialogImg.dismiss();
                    imageBitmap = null;
                }

            });
            cameraCardView.setOnClickListener(view1 -> {
                if (!checkCameraPermission()) {
                    requestCameraPermission();

                } else {
                    PickImagefromcamera();
                    resultUri = null;
                    alertDialogImg.dismiss();
                }
            });
        });


        mGetPdf = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                pdfUploadDialog = new ProgressDialog(UserProfile.this);
                pdfUploadDialog.setMessage("Uploading");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users/" + userEmailConverted);

                pdfUploadDialog.show();
                pdfUri = result;
                StorageReference pdfUploadRef = FirebaseStorage.getInstance().getReference().child("Product Brochure/" + userCompanyNameStr + " Brochure" + ".pdf");

                if (pdfUri != null) {
                    pdfUploadRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pdfUploadRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap UserData = new HashMap();
                                    UserData.put("brochure_url", uri.toString());

                                    databaseReference.updateChildren(UserData).addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Toast.makeText(UserProfile.this, "Brochure Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            pdfUploadDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UserProfile.this, "Failed to Upload Brochure", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    pdfUploadDialog.dismiss();
                    Toast.makeText(UserProfile.this, "File not selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        uploadBrochureBtn.setOnClickListener(view -> {

            mGetPdf.launch("application/pdf");

        });

        addProductBtn.setOnClickListener(view -> {
            if (productTitleEdtTxt.getText().toString().isEmpty()) {
                productTitleEdtTxt.setError("Enter product title");
                productTitleEdtTxt.requestFocus();
            } else if (productDescriptionEdtTxt.getText().toString().isEmpty()) {
                productDescriptionEdtTxt.setError("Enter product description");
                productDescriptionEdtTxt.requestFocus();
            } else if (productPriceEdtTxt.getText().toString().isEmpty()) {
                productPriceEdtTxt.setError("Enter product price");
                productPriceEdtTxt.requestFocus();
            } else if (productImageUri == null) {
                Toast.makeText(this, "Select a product image", Toast.LENGTH_SHORT).show();
                uploadProductImageIv.requestFocus();
            } else {
                String productPriceStr = productPriceEdtTxt.getText().toString();

                uploadProductImage(productImageUri);
            }

        });

        logoutImg.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(UserProfile.this, LandingPage.class).setFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
        logoutTv.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(UserProfile.this, LandingPage.class).setFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });

    }

    private void uploadProductImage(Uri productImageUri) {
        productUploadProgressDialog.setMessage("Uploading Product");
        productUploadProgressDialog.show();
        StorageReference productFileRef = storageProfilePicReference.child("Product Images/" + mAuth.getCurrentUser().getEmail().toString() + productTitleEdtTxt.getText().toString());
        productFileRef.putFile(productImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                productFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference productReferenceByUser = FirebaseDatabase.getInstance().getReference().child("Products by Member")
                                .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7"));
                        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference().child("Products");
                        String productKey = productReferenceByUser.push().getKey();

                        String productTitleStr = productTitleEdtTxt.getText().toString();
                        String productDescriptionStr = productDescriptionEdtTxt.getText().toString();
                        String productPriceStr = productPriceEdtTxt.getText().toString();

                        ProductModel newProduct = new ProductModel(uri.toString(), productTitleStr, productDescriptionStr, productPriceStr, mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7"));
                        productReferenceByUser.child(productKey).setValue(newProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                ProductDetailsModel newProductDetail = new ProductDetailsModel(mAuth.getCurrentUser().getEmail(),
                                        userContactNumberEdtTxt.getText().toString(), productDescriptionStr, uri.toString(), productPriceStr,
                                        productTitleStr, productTitleStr.toLowerCase());
                                productReference.child(productKey).setValue(newProductDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(getApplicationContext(), UserProfile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        productUploadProgressDialog.dismiss();
                                        Toast.makeText(UserProfile.this, "Your product has been added", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        productUploadProgressDialog.dismiss();
                                        Toast.makeText(UserProfile.this, "Product could not be added", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                productUploadProgressDialog.dismiss();
                                Toast.makeText(UserProfile.this, "Product could not be added", Toast.LENGTH_SHORT).show();
                            }
                        });

//                        productTitleEdtTxt.setText("");
//                        productDescriptionEdtTxt.setText("");
//                        productPriceEdtTxt.setText("");
//                        uploadProductImageIv.setImageResource(R.drawable.add_image_icon);
//                        uploadProductImageIv.setPadding(24, 24, 24, 24);
                    }
                });
            }
        });
    }

    ProgressDialog pdfUploadDialog;

    private void updateData(String userContactNumberStr, String userDOBStr, String userAddressStr, String userBioStr) {
        HashMap UserData = new HashMap();
        UserData.put("phone_number", userContactNumberStr);
        UserData.put("date_of_birth", userDOBStr);
        UserData.put("address", userAddressStr);
        UserData.put("description", userBioStr);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users/" + userEmailConverted);
        databaseReference.updateChildren(UserData).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UserProfile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Failed to Update Data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resultUri = UCrop.getOutput(data);
            userProfileImage.setImageURI(resultUri);
        }else if (resultCode == RESULT_OK && requestCode == 3) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            userProfileImage.setImageBitmap(imageBitmap);
        } else if (resultCode == RESULT_OK && requestCode == 2) {
            productImageUri = UCrop.getOutput(data);
            uploadProductImageIv.setImageURI(productImageUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageProfilePicReference.child("User Profile Pictures/" + mAuth.getCurrentUser().getEmail().toString() + "ProfilePicture");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .placeholder(R.drawable.iea_logo)
                                .circleCrop()
                                .error(R.drawable.iea_logo)
                                .into(userProfileImage);

                        HashMap UserData = new HashMap();
                        UserData.put("purl", uri.toString());

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users/" + userEmailConverted);
                        databaseReference.updateChildren(UserData).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(UserProfile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserProfile.this, "Failed to Update Data", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Toast.makeText(UserProfile.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                    }

                });
                String fileReference = String.valueOf(fileRef.getDownloadUrl());
                Log.d("downloadUrl", fileReference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Failed to Update Profile Picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String yearincrementer(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 365);
        date = sdf.format(c.getTime());
        return date;
    }


    private void PickImagefromcamera() {
        Intent fromcamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(fromcamera, 3);
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
}