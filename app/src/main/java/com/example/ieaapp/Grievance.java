package com.example.ieaapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Grievance extends AppCompatActivity {

    AppCompatButton grievance_back_button, grievance_submit;
    FirebaseDatabase grievanceDb;
    DatabaseReference grievanceReference, grievanceReference2;
    EditText grievanceSubjectEdtTxt, issue;
    AutoCompleteTextView dept;
    CardView myGrievancesBtn;
    Dialog grievanceSubmissionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance);

        grievance_back_button = findViewById(R.id.grievance_back_button);
        myGrievancesBtn = findViewById(R.id.my_grievances_btn);
        grievanceSubjectEdtTxt = findViewById(R.id.grievance_subject_edtTxt);

        dropdownInit();

        grievance_back_button.setOnClickListener(view -> finish());

        grievanceSubmissionDialog = new Dialog(this);

        grievance_submit = findViewById(R.id.grievance_submit_btn);
        grievance_submit.setOnClickListener(v -> {
            grievanceDb = FirebaseDatabase.getInstance();
            issue = findViewById(R.id.issue_input_edtTxt);
            dept = findViewById(R.id.grievance_department_field);
            if (issue.getText().toString().isEmpty()) {
                Toast.makeText(Grievance.this, "Issue cannot be empty", Toast.LENGTH_SHORT).show();
                issue.requestFocus();
            } else if (dept.getText().toString().isEmpty()) {
                Toast.makeText(Grievance.this, "Department cannot be empty", Toast.LENGTH_SHORT).show();
                dept.requestFocus();
            } else if (grievanceSubjectEdtTxt.getText().toString().isEmpty()) {
                Toast.makeText(Grievance.this, "Subject cannot be empty", Toast.LENGTH_SHORT).show();
                grievanceSubjectEdtTxt.requestFocus();
            } else {
                String complainerEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                String complain = issue.getText().toString();
                String departments = dept.getText().toString();
                String subject = grievanceSubjectEdtTxt.getText().toString();

                grievanceReference = grievanceDb.getReference("Unsolved Grievances");

                String grievanceKey = grievanceReference.push().getKey();

                grievanceReference2 = grievanceDb.getReference("Unresolved Grievances").child(Objects.requireNonNull(complainerEmail)
                        .replaceAll("\\.", "%7")).child(Objects.requireNonNull(grievanceKey));
                GrievanceModel solvedModel = new GrievanceModel(complain, departments, complainerEmail, "Unsolved", subject);
                grievanceReference2.setValue(solvedModel);
                grievanceReference.child(grievanceKey).setValue(solvedModel);

                new AlertDialog.Builder(Grievance.this)
                        .setTitle("Grievance ID")
                        .setMessage("Your Grievance ID is: " + grievanceKey).show();

                issue.setText("");
                Toast.makeText(Grievance.this, "We have received your request", Toast.LENGTH_SHORT).show();
                confirmationPopup();
                sendGrievanceNotification();
            }
        });

        myGrievancesBtn.setOnClickListener(view -> startActivity(new Intent(Grievance.this, MyGrievances.class)));
    }

    private void sendGrievanceNotification() {
        FirebaseDatabase.getInstance().getReference("Core Member Token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tokenSnapshot : snapshot.getChildren()) {
                    String grievanceUserToken = Objects.requireNonNull(tokenSnapshot.getValue()).toString();

                    FcmNotificationsSender grievanceNotificationSender = new FcmNotificationsSender(grievanceUserToken,
                            "IEA New Grievance Submitted",
                            "New Grievance has been submitted:\n" + grievanceSubjectEdtTxt.getText().toString(),
                            getApplicationContext(),
                            Grievance.this);

                    grievanceNotificationSender.SendNotifications();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dropdownInit();
    }

    public void dropdownInit() {
        String[] grievance_departments = getResources().getStringArray(R.array.grievance_department);
        ArrayAdapter<String> arrayAdapterDepartments = new ArrayAdapter<>(getBaseContext(), R.layout.drop_down_item, grievance_departments);
        AutoCompleteTextView autoCompleteTextViewDepartments = findViewById(R.id.grievance_department_field);
        autoCompleteTextViewDepartments.setAdapter(arrayAdapterDepartments);
    }

    public void confirmationPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View confirmationView = inflater.inflate(R.layout.confirmation_popup, null);
        grievanceSubmissionDialog.setContentView(confirmationView);
        grievanceSubmissionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        grievanceSubmissionDialog.show();

        new Handler().postDelayed(() -> {
            Intent i = new Intent(getApplicationContext(), Grievance.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }, 3000);
    }
}