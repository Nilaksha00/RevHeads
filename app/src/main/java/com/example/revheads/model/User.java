package com.example.revheads.model;

public class User {

    public String FullName, Email, Password;
    public User(String fullName, String email, String password) {
        FullName = fullName;
        Email = email;
        Password = password;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }




}
