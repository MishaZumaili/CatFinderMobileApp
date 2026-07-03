package com.example.catfindermobileapp;

public class CatModel {

    private String id;
    private String catName;
    private String age;
    private String breed;
    private String gender;
    private String size;
    private String status;
    private String imageUri;
    private String lastSeen;
    private String lastLocation;
    private String imageBase64;
    private String ownerId;

    public CatModel() {
        // Required for Firebase
    }

    public CatModel(String id,
                    String ownerId,
                    String catName,
                    String age,
                    String breed,
                    String gender,
                    String size,
                    String status,
                    String imageUri,
                    String imageBase64) {

        this.id = id;
        this.ownerId = ownerId;
        this.catName = catName;
        this.age = age;
        this.breed = breed;
        this.gender = gender;
        this.size = size;
        this.status = status;
        this.imageUri = imageUri;
        this.lastSeen = "";
        this.lastLocation = "";
        this.imageBase64 = imageBase64;
    }

    // ================= GETTERS =================

    public String getId() {
        return id;
    }
    public String getOwnerId() {
        return ownerId;
    }

    public String getCatName() {
        return catName;
    }

    public String getAge() {
        return age;
    }

    public String getBreed() {
        return breed;
    }

    public String getGender() {
        return gender;
    }

    public String getSize() {
        return size;
    }

    public String getStatus() {
        return status;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    // ================= SETTERS =================

    public void setId(String id) {
        this.id = id;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}