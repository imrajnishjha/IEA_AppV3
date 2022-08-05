package com.example.ieaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class BaasMemberProfile extends AppCompatActivity {
    ImageView baasMemberProfileImage;
    TextView baasMemberProfileCompanyName;
    AppCompatButton baasMemberProfileViewBrochure, baasMemberProfileContactUs, baasMemberBackBtn;
    RecyclerView baasMemberRecyclerView;
    FirebaseRecyclerOptions<MemberProductModel> options;
    BaasProductAdapter baasListRecyclerAdapter;
    String memberBrochureLink, ownerEmail, ownerEmailConverted, ownerContactNumber, ownerContactEmail;
    Dialog baasMemberContactDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baas_member_profile);
        ownerEmail = getIntent().getStringExtra("BaasItemKey");
        ownerEmailConverted = ownerEmail.replaceAll("\\.", "%7");
        baasMemberProfileImage = findViewById(R.id.baas_member_profile_image_iv);
        baasMemberProfileCompanyName = findViewById(R.id.baas_member_profile_company_name);
        baasMemberProfileViewBrochure = findViewById(R.id.baas_member_profile_view_brochure_btn);
        baasMemberProfileContactUs = findViewById(R.id.baas_member_profile_contact_us_btn);
        baasMemberBackBtn = findViewById(R.id.baas_member_back_button);

        baasMemberContactDialog = new Dialog(this);

        DatabaseReference ownerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Registered Users/"+ownerEmailConverted);

        ownerDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberBrochureLink = Objects.requireNonNull(snapshot.child("brochure_url").getValue()).toString();
                ownerContactNumber = Objects.requireNonNull(snapshot.child("phone_number").getValue()).toString();
                ownerContactEmail = snapshot.child("email").getValue().toString();

                Glide.with(baasMemberProfileImage.getContext())
                        .load(Objects.requireNonNull(snapshot.child("purl").getValue()).toString())
                        .placeholder(R.drawable.iea_logo)
                        .circleCrop()
                        .error(R.drawable.iea_logo)
                        .into(baasMemberProfileImage);

                baasMemberProfileCompanyName.setText(Objects.requireNonNull(snapshot.child("company_name").getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        baasMemberRecyclerView = findViewById(R.id.baas_member_rv);

        baasMemberRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL,false));


        options = new FirebaseRecyclerOptions.Builder<MemberProductModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Products by Member/"+ownerEmailConverted), MemberProductModel.class)
                .build();

        baasListRecyclerAdapter = new BaasProductAdapter(options);
        baasMemberRecyclerView.setAdapter(baasListRecyclerAdapter);

        baasListRecyclerAdapter.startListening();

        baasMemberProfileViewBrochure.setOnClickListener(view -> {
            Uri uri = Uri.parse(memberBrochureLink);
            if (!uri.toString().equals("")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Brochure hasn't been uploaded yet", Toast.LENGTH_LONG).show();
            }
        });

        baasMemberProfileContactUs.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            View contactView = inflater.inflate(R.layout.core_member_contact_popup, null);

            TextView baasMemberContactNumber = contactView.findViewById(R.id.core_member_phone_number);
            TextView baasMemberContactEmail = contactView.findViewById(R.id.core_member_email);

            baasMemberContactEmail.setText(ownerContactEmail);
            baasMemberContactNumber.setText(ownerContactNumber);

            baasMemberContactDialog.setContentView(contactView);
            baasMemberContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            baasMemberContactDialog.show();
        });

        baasMemberBackBtn.setOnClickListener(view -> finish());

    }

    @Override
    protected void onResume() {
        super.onResume();
        baasListRecyclerAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        baasListRecyclerAdapter.startListening();
    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        baasListRecyclerAdapter.stopListening();
//    }

    class NpaGridLayoutManager extends GridLayoutManager {

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public NpaGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }
    }
}

