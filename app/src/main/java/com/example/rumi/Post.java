package com.example.rumi;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post {
    public static final String KEY_COLLECTION = "posts";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_IMAGE = "userImage";
    public static final String KEY_START_MONTH = "startMonth";
    public static final String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    private Date createdAt;
    private String title, description, image, userEmail, userName, userImage, startMonth;
    private Integer numRooms, duration, rent;
    private Boolean furnished, lookingForHouse;

    public Post() {
        // empty constructor required
    }

    public Post(String title, String description, String image, String userEmail, String userName,
                String userImage, String startMonth, Integer numRooms, Integer duration, Integer rent, Boolean furnished) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userImage = userImage;
        this.numRooms = numRooms;
        this.duration = duration;
        this.rent = rent;
        this.furnished = furnished;
        this.startMonth = startMonth;
        this.createdAt = new java.util.Date();
    }

    public String getRelativeTime() {
        String timeCreated = createdAt.toString();

        SimpleDateFormat sf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(timeCreated).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public Boolean getLookingForHouse() {
        return lookingForHouse;
    }

    public void setLookingForHouse(Boolean lookingForHouse) {
        this.lookingForHouse = lookingForHouse;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Integer getNumRooms() {
        return numRooms;
    }

    public void setNumRooms(Integer numRooms) {
        this.numRooms = numRooms;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getRent() {
        return rent;
    }

    public void setRent(Integer rent) {
        this.rent = rent;
    }

    public Boolean getFurnished() {
        return furnished;
    }

    public void setFurnished(Boolean furnished) {
        this.furnished = furnished;
    }
}
