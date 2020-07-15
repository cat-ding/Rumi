package com.example.rumi;

import android.text.format.DateUtils;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Parcel
public class Post {
    public static final String KEY_COLLECTION = "posts";
//    public static final String KEY_CREATED_AT = "createdAt";
//    public static final String KEY_TITLE = "title";
//    public static final String KEY_BODY = "body";
//    public static final String KEY_IMAGE = "image";
//    public static final String KEY_USER_EMAIL = "userEmail";
//    public static final String KEY_USER_NAME = "userName";
//    public static final String KEY_USER_IMAGE = "userImage";
//    public static final String KEY_START_MONTH = "startMonth";
    public static final String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";


    private String title, description, userName, userEmail, startMonth;
    private int numRooms, duration, rent;
    private boolean furnished, lookingForHouse;
    private Date createdAt;

    public Post() {
        // empty constructor required
    }

    public Post(String title, String description, String userName, String userEmail, String startMonth,
                int numRooms, int duration, int rent, boolean furnished, boolean lookingForHouse) {
        this.title = title;
        this.description = description;
        this.userName = userName;
        this.userEmail = userEmail;
        this.startMonth = startMonth;
        this.numRooms = numRooms;
        this.duration = duration;
        this.rent = rent;
        this.furnished = furnished;
        this.lookingForHouse = lookingForHouse;
        this.createdAt = new java.util.Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public int getNumRooms() {
        return numRooms;
    }

    public void setNumRooms(int numRooms) {
        this.numRooms = numRooms;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public boolean isFurnished() {
        return furnished;
    }

    public void setFurnished(boolean furnished) {
        this.furnished = furnished;
    }

    public boolean isLookingForHouse() {
        return lookingForHouse;
    }

    public void setLookingForHouse(boolean lookingForHouse) {
        this.lookingForHouse = lookingForHouse;
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
}