package com.example.rumi.models;

import android.text.format.DateUtils;

import com.google.firebase.firestore.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    public static final String KEY_MESSAGES = "messages";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    private Date createdAt;
    private String body, userId;

    public Message() {
    }

    public Message(Date createdAt, String body, String userId) {
        this.createdAt = createdAt;
        this.body = body;
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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
