package com.example.ieaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MyGrievances extends AppCompatActivity {

    RecyclerView myGrievancesRecyclerView;
    AppCompatButton myGrievancesBackButton;
    MyGrievancesAdapter myGrievancesAdapter;
    FirebaseRecyclerOptions<MyGrievanceModel> options;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_grievances);

        myGrievancesRecyclerView = (RecyclerView) findViewById(R.id.my_grievances_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myGrievancesRecyclerView.setLayoutManager(linearLayoutManager);
        myGrievancesBackButton = findViewById(R.id.my_grievances_back_button);

        notify = getIntent().getStringExtra("notify");

        options = new FirebaseRecyclerOptions.Builder<MyGrievanceModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Unresolved Grievances").child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7")), MyGrievanceModel.class)
                .build();

        myGrievancesAdapter = new MyGrievancesAdapter(options);
        myGrievancesRecyclerView.setAdapter(myGrievancesAdapter);
        myGrievancesBackButton.setOnClickListener(view -> {
            if(notify!=null){
                startActivity(new Intent(this,explore_menu.class));
                finish();
            } else{finish();}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        myGrievancesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myGrievancesAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(notify!=null){
            startActivity(new Intent(this,explore_menu.class));
            finish();
        } else{finish();}
    }
}

