package com.example.taxibookingapp.Models;

public class User {
    Boolean user_created;
    String user_id;
    String user_name;
    String user_email;
    String user_phone;
    String user_password;

    public User () {
    }

    public User(String user_name, String user_email, String user_phone, String user_password) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_phone = user_phone;
        this.user_password = user_password;
    }
    public User(Boolean user_created, String user_id, String user_name, String user_phone, String user_email, String user_password) {
        this.user_created = user_created;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_email = user_email;
        this.user_password = user_password;
    }


    public Boolean getUser_created() {
        return user_created;
    }

    public void setUser_created(Boolean user_created) {
        this.user_created = user_created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }
}
