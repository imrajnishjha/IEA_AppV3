package com.example.ieaapp;

public class GrievanceModel {
    String complain, department, email, status, subject;

    public GrievanceModel(){

    }

    public GrievanceModel(String complain, String department, String email, String status, String subject) {
        this.complain = complain;
        this.department = department;
        this.email = email;
        this.status = status;
        this.subject = subject;
    }

    public String getComplain() {
        return complain;
    }

    public void setComplain(String complain) {
        this.complain = complain;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
