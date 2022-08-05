package com.example.ieaapp;

import java.util.ArrayList;

public class BaasListModel {

    String company_name, email, purl, industry_type;
    ArrayList<BaasListRecyclerModel> baasListRecyclerModels;

    BaasListModel() {

    }

    public BaasListModel(String company_name, String email, String purl, String industry_type, ArrayList<BaasListRecyclerModel> baasListRecyclerModels) {
        this.company_name = company_name;
        this.email = email;
        this.purl = purl;
        this.industry_type = industry_type;
        this.baasListRecyclerModels = baasListRecyclerModels;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getIndustry_type() {
        return industry_type;
    }

    public void setIndustry_type(String industry_type) {
        this.industry_type = industry_type;
    }

    public ArrayList<BaasListRecyclerModel> getBaasListRecyclerModels() {
        return baasListRecyclerModels;
    }

    public void setBaasListRecyclerModels(ArrayList<BaasListRecyclerModel> baasListRecyclerModels) {
        this.baasListRecyclerModels = baasListRecyclerModels;
    }
}
