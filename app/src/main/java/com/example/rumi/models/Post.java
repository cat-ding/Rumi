package com.example.rumi.models;

import android.text.format.DateUtils;

import com.google.firebase.firestore.Exclude;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Parcel
public class Post {

    public static final String KEY_POSTS = "posts";
    public static final String KEY_NAME = "name";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_USER_ID = "userId";
    public static final String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";


    public String title, description, startMonth, userId, startDate, endDate, photoUrl, postId;
    public int numRooms, duration, rent;
    public boolean furnished, lookingForHouse;
    public Date createdAt;

    public Post() {
        // empty constructor required
    }

    public Post(String title, String description, String startMonth, String userId, int numRooms,
                int duration, int rent, boolean furnished, boolean lookingForHouse,
                String startDate, String endDate, String photoUrl, String postId) {
        this.title = title;
        this.description = description;
        this.startMonth = startMonth;
        this.numRooms = numRooms;
        this.duration = duration;
        this.rent = rent;
        this.furnished = furnished;
        this.lookingForHouse = lookingForHouse;
        this.createdAt = new java.util.Date();
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.photoUrl = photoUrl;
        this.postId = postId;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() { return createdAt; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

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

    public void setLookingForHouse(boolean lookingForHouse) { this.lookingForHouse = lookingForHouse; }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPhotoUrl() { return photoUrl; }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getPostId() { return postId; }

    public void setPostId(String postId) { this.postId = postId; }

    @Exclude
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
