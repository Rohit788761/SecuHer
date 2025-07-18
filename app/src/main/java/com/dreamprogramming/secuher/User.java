package com.dreamprogramming.secuher;

public class User {
    public String name;
    public String email,phone;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

}
