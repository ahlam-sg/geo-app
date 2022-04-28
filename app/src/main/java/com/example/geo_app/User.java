package com.example.geo_app;

public class User {
    private String username;
    private String provider;
    private String imageURL;
    private int highScore;
    private int totalScore;

    public User(String provider) {
        this.provider = provider;
        highScore = 0;
        totalScore = 0;
        username = "";
        imageURL = "";
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getHighScore() {
        return highScore;
    }
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getTotalScore() {
        return totalScore;
    }
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
}
