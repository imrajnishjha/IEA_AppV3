package com.example.ieaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class Refer extends AppCompatActivity {

    EditText nameEdtTxt, company_nameEdtTxt, contact_numberEdtTxt, email_addressEdtTxt;
    AppCompatButton referBackButton, referButton;
    String name, company_name, contact_number, email_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        referBackButton = findViewById(R.id.refer_back_button);
        referButton = findViewById(R.id.refer_button);
        nameEdtTxt = findViewById(R.id.refer_name_editText);
        company_nameEdtTxt = findViewById(R.id.refer_company_name_editText);
        contact_numberEdtTxt = findViewById(R.id.refer_contact_number_editText);
        email_addressEdtTxt = findViewById(R.id.refer_email_address_editText);


        referBackButton.setOnClickListener(view -> finish());

        referButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(nameEdtTxt.getText().toString())) {
                nameEdtTxt.setError("Name cannot be empty!");
                nameEdtTxt.requestFocus();
            } else if (TextUtils.isEmpty(company_nameEdtTxt.getText().toString())) {
                company_nameEdtTxt.setError("Company name cannot be empty!");
                company_nameEdtTxt.requestFocus();
            } else if (TextUtils.isEmpty(contact_numberEdtTxt.getText().toString())) {
                contact_numberEdtTxt.setError("Contact number cannot be empty!");
                contact_numberEdtTxt.requestFocus();
            } else {
                sendEmail();
            }
        });
    }

    @SuppressLint("IntentReset")
    protected void sendEmail() {
        name = nameEdtTxt.getText().toString();
        company_name = company_nameEdtTxt.getText().toString();
        contact_number = contact_numberEdtTxt.getText().toString();
        email_address = email_addressEdtTxt.getText().toString();
        Log.i("Send email", "");

        String[] TO = {"wormoscorporation@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "IEA Member Referral");
        if(email_address.isEmpty()){
            emailIntent.putExtra(Intent.EXTRA_TEXT, "New Member Referral\n\nName: " + name + "\nCompany Name: " + company_name + "\nContact Number: " + contact_number);
        } else {
            emailIntent.putExtra(Intent.EXTRA_TEXT, "New Member Referral\n\nName: " + name + "\nCompany Name: " + company_name + "\nContact Number: " + contact_number + "\nEmail Address: " + email_address);
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Refer.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}