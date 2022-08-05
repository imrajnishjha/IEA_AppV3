package com.example.ieaapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class BAAS extends AppCompatActivity {

    RecyclerView baasListRecyclerView;
    FirebaseRecyclerOptions<BaasListModel> options;
    BaasListAdapter baasListAdapter;
    AppCompatButton baasBackBtn;
    EditText searchBaasEdtTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baas);
        baasBackBtn = findViewById(R.id.baas_back_button);
        searchBaasEdtTxt = findViewById(R.id.search_baas_edtTxt);

        baasListRecyclerView = findViewById(R.id.baas_list_rv);
        baasListRecyclerView.setLayoutManager(new MembersDirectory.WrapContentLinearLayoutManager(this));
        options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                .build();

        baasListAdapter = new BaasListAdapter(options);
        baasListRecyclerView.setAdapter(baasListAdapter);
        baasListAdapter.startListening();

        baasBackBtn.setOnClickListener(view -> finish());

        //search functionality in baas
        searchBaasEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchTerm = "";
                if(searchBaasEdtTxt.getText().toString().length()>1){
                    searchTerm = searchBaasEdtTxt.getText().toString().substring(0, 1).toUpperCase() + searchBaasEdtTxt.getText().toString().substring(1);
                }

                if (!searchTerm.isEmpty()) {
                    options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users")
                                    .orderByChild("industry_type").startAt(searchTerm).endAt(searchTerm + "\uf8ff"), BaasListModel.class)
                            .build();
                } else {
                    options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                            .build();
                }
                baasListAdapter = new BaasListAdapter(options);
                baasListRecyclerView.setAdapter(baasListAdapter);
                baasListAdapter.startListening();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        baasListAdapter.startListening();
    }

}