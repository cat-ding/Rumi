package com.example.rumi.models;

public class User {

    public static final String KEY_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_MAJOR = "major";
    public static final String KEY_YEAR = "year";
    public static final String KEY_PROFILE_URL = "profileUrl";
    public static final String KEY_SURVEY_STATUS = "surveyStatus";

    private String name, email, major, year, profileUrl;
    private boolean surveyStatus;

    public User () {
        // empty constructor required
    }

    public User (String name, String email, String major, String year) {
        this.name = name;
        this.email = email;
        this.major = major;
        this.year = year;
        this.profileUrl = "";
        this.surveyStatus = false;
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

    public boolean isSurveyStatus() {
        return surveyStatus;
    }

    public void setSurveyStatus(boolean surveyStatus) {
        this.surveyStatus = surveyStatus;
    }
}
