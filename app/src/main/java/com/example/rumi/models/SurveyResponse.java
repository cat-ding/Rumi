package com.example.rumi.models;

import com.example.rumi.MatchConstants;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

public class SurveyResponse {

    public static final String KEY_SURVEY_RESPONSE = "surveyResponse";
    public static final String KEY_IMAGE_URL = "imageUrl";

    private String house, weekend, guests, cleanliness, temperature, gender, selfIdentifyGender, genderPref, smoking, description;
    private String userId, imageUrl, name, major, year;
    private ArrayList<String> activities, hobbies, entertainment, music;

    @Exclude
    private String surveyId;

    public SurveyResponse() {
        // empty constructor required
    }

    public SurveyResponse(String house, String weekend, String guests, String cleanliness,
                          String temperature, String gender, String selfIdentifyGender,
                          String genderPref, String smoking, String description,
                          ArrayList<String> activities, ArrayList<String> hobbies,
                          ArrayList<String> entertainment, ArrayList<String> music,
                          String userId, String imageUrl, String name, String major, String year) {
        this.house = house;
        this.weekend = weekend;
        this.guests = guests;
        this.cleanliness = cleanliness;
        this.temperature = temperature;
        this.gender = gender;
        this.selfIdentifyGender = selfIdentifyGender;
        this.genderPref = genderPref;
        this.smoking = smoking;
        this.description = description;
        this.activities = activities;
        this.hobbies = hobbies;
        this.entertainment = entertainment;
        this.music = music;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.name = name;
        this.major = major;
        this.year = year;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getWeekend() {
        return weekend;
    }

    public void setWeekend(String weekend) {
        this.weekend = weekend;
    }

    public String getGuests() {
        return guests;
    }

    public void setGuests(String guests) {
        this.guests = guests;
    }

    public String getCleanliness() {
        return cleanliness;
    }

    public void setCleanliness(String cleanliness) {
        this.cleanliness = cleanliness;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSelfIdentifyGender() {
        return selfIdentifyGender;
    }

    public void setSelfIdentifyGender(String selfIdentifyGender) {
        this.selfIdentifyGender = selfIdentifyGender;
    }

    public String getGenderPref() {
        return genderPref;
    }

    public void setGenderPref(String genderPref) {
        this.genderPref = genderPref;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<String> activities) {
        this.activities = activities;
    }

    public ArrayList<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(ArrayList<String> hobbies) {
        this.hobbies = hobbies;
    }

    public ArrayList<String> getEntertainment() {
        return entertainment;
    }

    public void setEntertainment(ArrayList<String> entertainment) {
        this.entertainment = entertainment;
    }

    public ArrayList<String> getMusic() {
        return music;
    }

    public void setMusic(ArrayList<String> music) {
        this.music = music;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
