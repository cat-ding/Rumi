package com.example.rumi;

public class MatchConstants {

    public enum House {
        QUIET, SOCIAL, COMBO, NO_PREFERENCE
    }

    public enum Weekend {
        PARTY, HANG, QUIET, NO_PREFERENCE
    }

    public enum Guests {
        OCCASIONAL, NO_GUESTS, NO_PREFERENCE
    }

    private MatchConstants() { } // private constructor so class cannot be instantiated
}
