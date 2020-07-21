package com.example.rumi.models;

import android.text.format.DateUtils;

import com.google.firebase.firestore.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {

    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_POST_ID = "postId";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_BODY = "body";
    public static final String dateFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    public String postId, userId, body;
    public Date createdAt;

    public Comment() {
        this.createdAt = new java.util.Date();
    }

    public Comment(String postId, String userId, String body) {
        this.createdAt = new java.util.Date();
        this.postId = postId;
        this.userId = userId;
        this.body = body;
    }

    public Date getCreatedAt() { return createdAt; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
