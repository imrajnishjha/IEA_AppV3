package com.example.ieaapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class explore_menu extends AppCompatActivity {

    FirebaseAuth mAuth;
    final DatabaseReference MemberOfMonthref = FirebaseDatabase.getInstance().getReference("Member of Month");
    TextView exploreUsername, Memberofmonthname, activeValue, solvedValue, MoMdescription;
    ImageView logoutImg, userImage;
    CircleImageView MemberofmonthImg;
    CardView coreMembersCard, memberDirectoryCard, grievanceCard, contactUs, refer, baasCard, eventsCard;
    Dialog exploreIeaContactDialog;
    DatabaseReference databaseReference, solvedReference, unResolvedReference, rejectedReference;
    StorageReference storageProfilePicReference;
    AppCompatButton exploreMenuLogoutBtn, memberNotificationIcon;
    long allsolvedValue, allProblemValue, allRejectedValue;
    RelativeLayout activeBar, activeVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_menu);

        exploreUsername = findViewById(R.id.explore_username);
        Memberofmonthname = findViewById(R.id.description_username);
        MoMdescription = findViewById(R.id.description_text);
        logoutImg = findViewById(R.id.logout_img);
        coreMembersCard = findViewById(R.id.core_mem);
        memberDirectoryCard = findViewById(R.id.member_directory);
        grievanceCard = findViewById(R.id.grievance);
        contactUs = findViewById(R.id.explore_menu_contact_us_cardView);
        refer = findViewById(R.id.refer);
        exploreIeaContactDialog = new Dialog(this);
        exploreMenuLogoutBtn = findViewById(R.id.logout_text);
        userImage = findViewById(R.id.user_img);
        activeValue = findViewById(R.id.active_value);
        solvedValue = findViewById(R.id.solved_value);
        activeBar = findViewById(R.id.activebar);
        activeVal = findViewById(R.id.activeval);
        baasCard = findViewById(R.id.bbas);
        eventsCard = findViewById(R.id.events);
        MemberofmonthImg = findViewById(R.id.description_img);
        memberNotificationIcon = findViewById(R.id.member_notification_icon);

        mAuth = FirebaseAuth.getInstance();

        storageProfilePicReference = FirebaseStorage.getInstance().getReference();

        rejectedReference = FirebaseDatabase.getInstance().getReference().child("Rejected Grievance").child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7"));

        solvedReference = FirebaseDatabase.getInstance().getReference().child("Solved Grievance").child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7"));

        unResolvedReference = FirebaseDatabase.getInstance().getReference().child("Unresolved Grievances").child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7"));
        unResolvedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allProblemValue = snapshot.getChildrenCount();
                solvedReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allsolvedValue = snapshot.getChildrenCount();
                        solvedValue.setText(String.valueOf(allsolvedValue));
                        Log.d("Problems", "Solved Grievances " + snapshot.getChildrenCount());
                        rejectedReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                allRejectedValue = snapshot.getChildrenCount();
                                activeValue.setText(String.valueOf(allProblemValue - allsolvedValue - allRejectedValue));
                                Log.d("Problems", "All Count: " + snapshot.getChildrenCount());
                                Log.d("Problems", "onDataChange: " + allsolvedValue);
                                Log.d("Problems", "onDataChange: " + allRejectedValue);
                                Log.d("Problems", "Rejected Grievances " + snapshot.getChildrenCount());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        String userEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        assert userEmail != null;
        String userEmailConverted = userEmail.replaceAll("\\.", "%7");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Registered Users/" + userEmailConverted);

        final int[] solvedGrievances = {0};
        final int[] activeGrievances = {0};

        DatabaseReference grievanceReference = FirebaseDatabase.getInstance().getReference().child("Unsolved Grievances");

        grievanceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Query grievanceQuery = grievanceReference.orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
                grievanceQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        solvedGrievances[0] = (int) snapshot.getChildrenCount();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Log.d("grievance value", String.valueOf(activeGrievances[0]));
        Log.d("grievance value", String.valueOf(solvedGrievances[0]));


//        activeValue.setText(activeGrievances[0]);
//        solvedValue.setText(solvedGrievances[0]);
        MemberOfMonthref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String UserNameStr = Objects.requireNonNull(snapshot.child("name").getValue().toString());
                String purl = Objects.requireNonNull(snapshot.child("purl").getValue().toString());
                String description = Objects.requireNonNull(snapshot.child("description").getValue().toString());
                Memberofmonthname.setText(UserNameStr);
                MoMdescription.setText(description);
                Glide.with(getApplicationContext())
                        .load(purl)
                        .placeholder(R.drawable.iea_logo)
                        .circleCrop()
                        .error(R.drawable.iea_logo)
                        .into(MemberofmonthImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        StorageReference fileRef = storageProfilePicReference.child("User Profile Pictures/" + mAuth.getCurrentUser().getEmail().toString() + "ProfilePicture");
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .placeholder(R.drawable.iea_logo)
                        .circleCrop()
                        .error(R.drawable.iea_logo)
                        .into(userImage);
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userNameDatabase = Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                exploreUsername.setText(userNameDatabase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        CardView explore = (CardView) findViewById(R.id.explore);
        explore.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, explore_iea.class)));

        logoutImg.setOnClickListener(view -> {
            mAuth.signOut();
            finish();
        });
        exploreMenuLogoutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            finish();
        });

        coreMembersCard.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, CoreTeamMembers.class)));
        memberDirectoryCard.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, MembersDirectory.class)));
        grievanceCard.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, Grievance.class)));
        refer.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, Refer.class)));
        baasCard.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, BAAS.class)));
        eventsCard.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, Events.class)));


        contactUs.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View exploreUsView = inflater.inflate(R.layout.support_contact_popup, null);

            exploreIeaContactDialog.setContentView(exploreUsView);
            exploreIeaContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            exploreIeaContactDialog.show();
        });

        userImage.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, UserProfile.class)));

        activeBar.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, MyGrievances.class)));
        activeVal.setOnClickListener(view -> startActivity(new Intent(explore_menu.this, MyGrievances.class)));

        memberNotificationIcon.setOnClickListener(view -> {
            startActivity(new Intent(explore_menu.this, MembersNotification.class));
        });
    }
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        finish();
//        overridePendingTransition(0, 0);
//        startActivity(getIntent());
//        overridePendingTransition(0, 0);
//    }
}