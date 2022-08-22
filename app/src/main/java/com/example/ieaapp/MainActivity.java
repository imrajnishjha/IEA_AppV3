package com.example.ieaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    RecyclerView splashRV;
    MembersDirectoryAdapter memberDirectoryAdapter2;
    FirebaseRecyclerOptions<MembersDirectoryModel> options3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashRV = findViewById(R.id.splash_RV);
        MembersDirectory.WrapContentLinearLayoutManager wrapContentLinearLayoutManager = new MembersDirectory.WrapContentLinearLayoutManager(getApplicationContext());
        splashRV.setLayoutManager(wrapContentLinearLayoutManager);
        options3 = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                .build();
        memberDirectoryAdapter2 =  new MembersDirectoryAdapter(options3);
        splashRV.setAdapter(memberDirectoryAdapter2);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, LandingPage.class);
                startActivity(i);
                finish();
            }
        },4100);
    }

    @Override
    protected void onStart() {
        super.onStart();
        memberDirectoryAdapter2.startListening();
    }


}