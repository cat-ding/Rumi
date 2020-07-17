package com.example.rumi;

public class User {

    public static final String KEY_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_MAJOR = "major";
    public static final String KEY_YEAR = "year";
    public static final String KEY_PROFILE_URL = "profileUrl";

    private String name, email, major, year, profileUrl;

    public User () {
        // empty constructor required
    }

    public User (String name, String email, String major, String year, String profileUrl) {
        this.name = name;
        this.email = email;
        this.major = major;
        this.year = year;
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
