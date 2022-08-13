package com.example.ieaapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

public class BAAS extends AppCompatActivity {

    RecyclerView baasListRecyclerView;
    FirebaseRecyclerOptions<BaasListModel> options;
    FirebaseRecyclerOptions<MemberProductModel> productOptions;
    BaasListAdapter baasListAdapter;
    AppCompatButton baasBackBtn, baasFilterOkayBtn;
    EditText searchBaasEdtTxt;
    AutoCompleteTextView baasIndustrySearchTv;
    CardView baasFilterIconCv, wormosCv;
    Dialog baasFilterPopup;
    RadioGroup baasFilterRadioGroup;
    RadioButton baasFilterRadioButton;
    TextView baasRadioIdHolder;
    TextInputLayout baasFilterSearchOutbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baas);
        baasBackBtn = findViewById(R.id.baas_back_button);
        searchBaasEdtTxt = findViewById(R.id.search_baas_edtTxt);
        baasIndustrySearchTv = findViewById(R.id.baas_industry_search_autocomplete);
        baasListRecyclerView = findViewById(R.id.baas_list_rv);
        baasFilterIconCv = findViewById(R.id.baas_filter_cv);
        baasRadioIdHolder = findViewById(R.id.baas_radio_id_holder);
        baasFilterSearchOutbox = findViewById(R.id.baas_filter_search_outbox);
        wormosCv = findViewById(R.id.WormosCv);

        baasFilterPopup = new Dialog(this);

        baasRadioIdHolder.setText(String.valueOf(R.id.search_by_industry_type_rBtn));

        baasListRecyclerView.setLayoutManager(new MembersDirectory.WrapContentLinearLayoutManager(this));

        options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                .build();

        baasListAdapter = new BaasListAdapter(options);
        baasListRecyclerView.setAdapter(baasListAdapter);
        baasListAdapter.startListening();

        baasBackBtn.setOnClickListener(view -> finish());

        dropdownInit();

        baasIndustrySearchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                        .build();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (baasIndustrySearchTv.getText().toString().equals("Clear Selection")) {
                    baasIndustrySearchTv.setText("");
                    options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                            .build();

                } else if (baasIndustrySearchTv.getText().toString().equals("")) {
                    options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                            .build();
                } else {
                    options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users").orderByChild("industry_type").equalTo(baasIndustrySearchTv.getText().toString()), BaasListModel.class)
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

        baasFilterIconCv.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            View view1 = inflater.inflate(R.layout.baas_filter_popup, null);

            baasFilterRadioGroup = view1.findViewById(R.id.baas_filter_popup_radio_group);
            baasFilterRadioButton = view1.findViewById(Integer.parseInt(baasRadioIdHolder.getText().toString()));
            baasFilterRadioGroup.check(Integer.parseInt(baasRadioIdHolder.getText().toString()));
            baasFilterOkayBtn = view1.findViewById(R.id.baas_filter_popup_okay_btn);

            baasFilterPopup.setContentView(view1);
            baasFilterPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            baasFilterPopup.show();

            baasFilterOkayBtn.setOnClickListener(view2 -> {
                baasRadioIdHolder.setText(String.valueOf(baasFilterRadioGroup.getCheckedRadioButtonId()));
                baasFilterRadioButton = view1.findViewById(Integer.parseInt(baasRadioIdHolder.getText().toString()));
                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                        .build();
                baasListAdapter = new BaasListAdapter(options);
                baasListRecyclerView.setAdapter(baasListAdapter);
                baasListAdapter.startListening();
                Log.d("RadioBtn", baasFilterRadioButton.getText().toString());
                if (baasFilterRadioButton.getText().toString().equals("Industry Type")) {
                    baasFilterSearchOutbox.setVisibility(View.VISIBLE);
                    searchBaasEdtTxt.setVisibility(View.GONE);

                    baasIndustrySearchTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                    .build();
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (baasIndustrySearchTv.getText().toString().equals("Clear Selection")) {
                                baasIndustrySearchTv.setText("");
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                        .build();

                            } else if (baasIndustrySearchTv.getText().toString().isEmpty()) {
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                        .build();
                            } else {
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users").orderByChild("industry_type").equalTo(baasIndustrySearchTv.getText().toString()), BaasListModel.class)
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
                } else if (baasFilterRadioButton.getText().toString().equals("Company Name")) {
                    baasFilterSearchOutbox.setVisibility(View.GONE);
                    searchBaasEdtTxt.setVisibility(View.VISIBLE);
                    searchBaasEdtTxt.setHint("Search by Company Name");

                    searchBaasEdtTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                    .build();
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (TextUtils.isEmpty(searchBaasEdtTxt.getText().toString())) {
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                        .build();
                            } else {
                                String companyNameFLC = searchBaasEdtTxt.getText().toString().substring(0, 1).toUpperCase() + searchBaasEdtTxt.getText().toString().substring(1);
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users").orderByChild("company_name").startAt(companyNameFLC).endAt(companyNameFLC + "\uf8ff"), BaasListModel.class)
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
                baasFilterPopup.dismiss();
            });
        });

        wormosCv.setOnClickListener(view -> {
            startActivity(new Intent(BAAS.this, WormosDetail.class));
        });
    }

    public void dropdownInit() {
        String[] departments = getResources().getStringArray(R.array.department_search);
        ArrayAdapter<String> arrayAdapterDepartments = new ArrayAdapter<>(getBaseContext(), R.layout.drop_down_item, departments);
        baasIndustrySearchTv.setAdapter(arrayAdapterDepartments);
    }

    @Override
    protected void onStart() {
        super.onStart();
        baasListAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dropdownInit();
    }

}