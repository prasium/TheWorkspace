package com.example.android.theworkspace;

public class UserHelperClass {
    String name,userName,email,gender ;
    public UserHelperClass()
    {

    }
    public UserHelperClass(String name, String userName, String email, String gender) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
