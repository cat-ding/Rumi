package com.example.rumi;

public class FilterConstants {

    public static final String SORT_DEFAULT = "Recent (Default)";
    public static final String SORT_POPULARITY = "Popularity";
    public static final String SORT_RENT_HIGH_TO_LOW = "Rent (High to Low)";
    public static final String SORT_RENT_LOW_TO_HIGH = "Rent (Low to High)";

    public static final int LOOKING_FOR_DEFAULT = -1; // any
    public static final int LOOKING_FOR_PLACE = 0; // looking for a place
    public static final int LOOKING_FOR_TENANT = 1; // looking for tenant

    public static final int FILTER_ROOMS_DEFAULT = 0; // any number of rooms

    public static final int FURNISHED_DEFAULT = -1; // include both furnished and unfurnished
    public static final int FURNISHED_YES = 0;
    public static final int FURNISHED_NO = 1;

    private FilterConstants() { } // private constructor to prevent instantiation of this class
}
