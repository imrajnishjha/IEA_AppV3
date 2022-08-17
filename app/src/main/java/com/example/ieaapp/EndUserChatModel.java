package com.example.ieaapp;

public class EndUserChatModel {
    String email,message;

    public EndUserChatModel() {
    }

    public EndUserChatModel(String email, String message) {
        this.email = email;
        this.message = message;

    }

    public String getName() {
        return email;
    }

    public void setName(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

