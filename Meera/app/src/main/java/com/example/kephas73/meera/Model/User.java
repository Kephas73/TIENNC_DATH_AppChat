package com.example.kephas73.meera.Model;

public class User {
    private String userId;
    private String userName;
    private String imageURL;
    private String status;

    public User (String userId, String userName, String imageURL, String status) {
        this.userId = userId;
        this.userName = userName;
        this.imageURL = imageURL;
        this.status = status;
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
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
