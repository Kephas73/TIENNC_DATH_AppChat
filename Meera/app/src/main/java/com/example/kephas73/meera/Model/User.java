package com.example.kephas73.meera.Model;

public class User {
    private String userId, userName, imageURL;

    public User (String userId, String userName, String imageURL) {
        this.userId = userId;
        this.userName = userName;
        this.imageURL = imageURL;
    }

    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
