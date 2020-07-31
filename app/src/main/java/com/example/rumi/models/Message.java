package com.example.rumi.models;

import java.util.Date;

public class Message {
    public static final String KEY_MESSAGES = "messages";
    public static final String KEY_CREATED_AT = "createdAt";

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
}
