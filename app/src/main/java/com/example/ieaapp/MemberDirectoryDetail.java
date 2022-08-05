package com.example.ieaapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberDirectoryDetail extends AppCompatActivity {
    ImageView memberProfileImage;
    TextView memberProfileName, memberMembershipId,
            memberCompanyName, memberAddress, memberPhoneno, memberMail, memberInfoText, memberInfoDetails;
    AppCompatButton memberProfileBackBtn, downloadBrochureBtn, moreProductButton;
    RecyclerView memberProductRecyclerView;
    String memberEmailStr, memberBrochureLink, memberAddressStr, memberPhoneStr;
    MemberProductAdapter memberProductAdapter;
    CircleImageView memberEmailImg, memberPhoneImg, memberAddressImg;
    Dialog MemberinfoDialog;


    FirebaseRecyclerOptions<MemberProductModel> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_directory_detail);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Registered Users");

        MemberinfoDialog = new Dialog(this);
        memberAddress = findViewById(R.id.nullphonenotext);
        memberPhoneno = findViewById(R.id.nullphonenotext);
        memberProfileImage = findViewById(R.id.member_profile_image);
        memberMembershipId = findViewById(R.id.member_membership_id);
        memberProfileName = findViewById(R.id.member_profile_name);
        moreProductButton = findViewById(R.id.moreProduct_button);

        memberMail = findViewById(R.id.nullemailtext);

        memberEmailImg = findViewById(R.id.Member_mail_image);
        memberPhoneImg = findViewById(R.id.Member_phone_image);
        memberAddressImg = findViewById(R.id.Member_address_image);

        memberEmailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = getLayoutInflater();
                View PopupView = inflater.inflate(R.layout.member_info_popup, null);

                memberInfoText = PopupView.findViewById(R.id.memberInfo_text);
                memberInfoDetails = PopupView.findViewById(R.id.memberinfo_details);


                memberInfoText.setText("Email");
                memberInfoDetails.setText(memberEmailStr);
                Linkify.addLinks(memberInfoDetails, Linkify.EMAIL_ADDRESSES);
                memberInfoDetails.setLinksClickable(true);

                MemberinfoDialog.setContentView(PopupView);
                MemberinfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                MemberinfoDialog.show();
            }
        });
        memberAddressImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = getLayoutInflater();
                View PopupView = inflater.inflate(R.layout.member_info_popup, null);


                memberInfoText = PopupView.findViewById(R.id.memberInfo_text);
                memberInfoDetails = PopupView.findViewById(R.id.memberinfo_details);

                memberInfoText.setText("Address");

                memberInfoDetails.setText(memberAddressStr);

                MemberinfoDialog.setContentView(PopupView);
                MemberinfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                MemberinfoDialog.show();

            }
        });
        memberPhoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View PopupView = inflater.inflate(R.layout.member_info_popup, null);

                memberInfoText = PopupView.findViewById(R.id.memberInfo_text);
                memberInfoDetails = PopupView.findViewById(R.id.memberinfo_details);


                memberInfoText.setText("Contact Number");
                memberInfoDetails.setText(memberPhoneStr);
                Linkify.addLinks(memberInfoDetails, Linkify.PHONE_NUMBERS);
                memberInfoDetails.setLinksClickable(true);

                MemberinfoDialog.setContentView(PopupView);
                MemberinfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                MemberinfoDialog.show();

            }
        });

        memberCompanyName = findViewById(R.id.member_company_name);

        memberProfileBackBtn = findViewById(R.id.memberDetail_back_button);
        downloadBrochureBtn = findViewById(R.id.downloadBrochure_button);

        memberProfileBackBtn.setOnClickListener(view -> finish());


        String coreItemKey = getIntent().getStringExtra("MemberItemKey");

        ref.child(coreItemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String memberMembershipDateStr = Objects.requireNonNull(snapshot.child("date_of_membership").getValue()).toString();
                    String memberMembershipIdStr = Objects.requireNonNull(snapshot.child("member_id").getValue()).toString();
                    memberEmailStr = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                    String memberCompanyNameStr = Objects.requireNonNull(snapshot.child("company_name").getValue()).toString();
                    memberAddressStr = Objects.requireNonNull(snapshot.child("address").getValue()).toString();
                    String memberNameStr = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String memberPictureUrl = Objects.requireNonNull(snapshot.child("purl").getValue()).toString();
                    memberPhoneStr = Objects.requireNonNull(snapshot.child("phone_number").getValue()).toString();
                    memberBrochureLink = Objects.requireNonNull(snapshot.child("brochure_url").getValue()).toString();


                    memberMembershipId.setText(memberMembershipIdStr);
                    memberCompanyName.setText(memberCompanyNameStr);
                    memberAddress.setText(memberAddressStr);
                    memberPhoneno.setText(memberPhoneStr);
                    memberMail.setText(memberEmailStr);

                    memberProfileName.setText(memberNameStr);


                    Glide.with(memberProfileImage.getContext())
                            .load(memberPictureUrl)
                            .placeholder(R.drawable.iea_logo)
                            .circleCrop()
                            .error(R.drawable.iea_logo)
                            .into(memberProfileImage);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        memberProductRecyclerView = (RecyclerView) findViewById(R.id.memberProductRecycleView);
        memberProductRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        options = new FirebaseRecyclerOptions.Builder<MemberProductModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Products by Member")
                        .child(coreItemKey.replaceAll("\\.", "%7")), MemberProductModel.class)
                .build();
        memberProductAdapter = new MemberProductAdapter(options);
        memberProductRecyclerView.setAdapter(memberProductAdapter);

        downloadBrochureBtn.setOnClickListener(view -> {
            Uri uri = Uri.parse(memberBrochureLink);
            if (!uri.toString().equals("")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Brochure hasn't been uploaded yet", Toast.LENGTH_LONG).show();
            }

        });

        moreProductButton.setOnClickListener(view -> {
            startActivity(new Intent(this, BAAS.class));
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        memberProductAdapter.startListening();
    }

    public static class WrapContentLinearLayoutManager extends LinearLayoutManager {

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "Recycler View error");
            }
        }
    }
}