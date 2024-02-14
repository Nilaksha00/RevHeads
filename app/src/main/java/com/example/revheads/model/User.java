package com.example.revheads.model;

public class User {

    public String FullName, Email, Password, Role;
    public User(String fullName, String email, String password, String role) {
        FullName = fullName;
        Email = email;
        Password = password;
        Role = role;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
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
