package com.example.rumi.models;

public class Comment {

    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_POST_ID = "postId";

    String postId, userId, text;

    public Comment() {
    }

    public Comment(String postId, String userId, String text) {
        this.postId = postId;
        this.userId = userId;
        this.text = text;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
