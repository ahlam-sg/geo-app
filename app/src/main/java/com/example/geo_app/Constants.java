package com.example.geo_app;

public class Constants {

    //Int
    public static final int START_ACTIVITY_DELAY = 3000;
    public static final int TOP_RANKING_LIMIT = 10;
    public static final long TIMER_VALUE = 60000;
    public static final int TIMER_COUNT_DOWN_INTERVAL = 1000;
    public static final int START_ROUND_DELAY = 1000;
    public static final int PASSWORD_LENGTH_MINIMUM = 8;

    //Intent Keys
    public static final String CATEGORY_KEY = "CATEGORY_KEY";
    public static final String CATEGORY_CAPITAL = "CATEGORY_CAPITAL";
    public static final String CATEGORY_FLAG = "CATEGORY_FLAG";
    public static final String CATEGORY_MAP = "CATEGORY_MAP";
    public static final String COUNTRIES_ARRAYLIST = "COUNTRIES_ARRAYLIST";
    public static final String REVIEW_MODEL_ARRAYLIST = "REVIEW_MODEL_ARRAYLIST";
    public static final String COUNT_CORRECT = "COUNT_CORRECT";
    public static final String SCORE = "SCORE";

    //Storage References
    public static final String STORAGE_URL = "gs://geo-app-f2b0d.appspot.com";
    public static final String PROFILE_REFERENCE = "profile/";

    //Realtime Database References
    public static final String DB_URL = "https://geo-app-f2b0d-default-rtdb.europe-west1.firebasedatabase.app/";
    public static final String COUNTRIES_EN_REFERENCE = "Countries_EN/";
    public static final String COUNTRIES_AR_REFERENCE = "Countries_AR/";
    public static final String USERS_REFERENCE = "Users/";

    //Users Nodes References
    public static final String USERNAME_REFERENCE = "username";
    public static final String TOTAL_SCORE_REFERENCE = "totalScore";
    public static final String HIGH_SCORE_REFERENCE = "highScore";
    public static final String IMAGE_URL_REFERENCE = "imageURL";

    //Providers
    public static final String GOOGLE_PROVIDER = "google.com";
    public static final String EMAIL_PROVIDER = "password";

    //Request Codes
    public static final int ONE_TAP_REQ = 1;
    public static final int PICK_IMAGE_REQ = 2;

    //Custom Broadcast Actions
    public static final String USER_TOP_RANKING_STATUS_ACTION = "USER_TOP_RANKING_STATUS_ACTION";
    public static final String USER_TOP_RANKING_STATUS = "USER_TOP_RANKING_STATUS";
    public static final String MUSIC_STATUS_ACTION = "MUSIC_STATUS_ACTION";
    public static final String MUSIC_STATUS = "MUSIC_STATUS";
    public static final String RESUME_MUSIC = "1";
    public static final String PAUSE_MUSIC = "0";
}

