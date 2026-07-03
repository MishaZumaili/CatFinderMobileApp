package com.example.catfindermobileapp;

public class UserModel {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String state;
    private String city;
    private String username;
    private String password;
    private String profileImage;

    public UserModel() {
    }

    public UserModel(String id, String name, String email,
                     String phone, String state, String city,
                     String username, String password) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.state = state;
        this.city = city;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}