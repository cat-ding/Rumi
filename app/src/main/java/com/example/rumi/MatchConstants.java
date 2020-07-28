package com.example.rumi;

public class MatchConstants {

    // All
    public static final int PAGE_ZERO = 0;
    public static final int PAGE_ONE = 1;
    public static final int PAGE_TWO = 2;
    public static final int PAGE_THREE = 3;
    public static final int PAGE_FOUR = 4;

    // MatchDialogOne
    public enum House {
        QUIET, SOCIAL, COMBO, NO_PREFERENCE
    }
    public enum Weekend {
        PARTY, HANG, QUIET, NO_PREFERENCE
    }
    public enum Guests {
        OCCASIONAL, NO_GUESTS, NO_PREFERENCE
    }

    // MatchDialogTwo
    public enum Clean {
        ALWAYS, FAIRLY, MESSY, NO_PREFERENCE
    }
    public enum Temperature {
        COLD, FAIRLY_COLD, FAIRLY_WARM, WARM, NO_PREFERENCE
    }

    private MatchConstants() { } // private constructor so class cannot be instantiated
}
