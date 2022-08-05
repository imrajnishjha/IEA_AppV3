package com.example.ieaapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

public class explore_iea extends AppCompatActivity {

    CardView exploreIeaContactUsCardView;
    Dialog exploreIeaContactDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_iea);

        exploreIeaContactUsCardView = findViewById(R.id.explore_iea_contact_us_cardView);
        exploreIeaContactDialog = new Dialog(this);

        AppCompatButton exploreback_btn = findViewById(R.id.explore_back_button);
        exploreback_btn.setOnClickListener(view -> {finish();});

        exploreIeaContactUsCardView.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            View exploreUsView = inflater.inflate(R.layout.support_contact_popup, null);

            exploreIeaContactDialog.setContentView(exploreUsView);
            exploreIeaContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            exploreIeaContactDialog.show();
        });
    }
}