package com.example.rumi;

public class MatchConstants {

    // All
    public static final int PAGE_ZERO = 0;
    public static final int PAGE_ONE = 1;
    public static final int PAGE_TWO = 2;
    public static final int PAGE_THREE = 3;
    public static final int PAGE_FOUR = 4;
    public static final int PAGE_FIVE = 5;
    public static final int PAGE_SIX = 6;
    public static final int NO_PAGE = -1;

    // MatchDialogOne
    public enum Week {
        QUIET, SOCIAL, COMBO, NO_PREFERENCE
    }
    public enum Weekend {
        PARTY, HANG, QUIET, NO_PREFERENCE
    }
    public enum Guests {
        OCCASIONAL, NONE , NO_PREFERENCE
    }

    // MatchDialogTwo
    public enum Clean {
        ALWAYS_CLEAN, FAIRLY_CLEAN, FAIRLY_MESSY, VERY_MESSY
    }
    public enum Temperature {
        COLD, FAIRLY_COLD, FAIRLY_WARM, WARM
    }

    // MatchDialogFive
    public enum Gender {
        MALE, FEMALE, SELF_IDENTIFY, NO_ANSWER
    }
    public enum GenderPref {
        MALE, FEMALE, NO_PREFERENCE
    }
    public enum Smoke {
        NON_SMOKER_NO, NON_SMOKER_YES, SMOKER
    }

    private MatchConstants() { } // private constructor so class cannot be instantiated
}
