package com.celestialwarfront.game.logic;

public enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

    public static Difficulty fromIndex(int idx) {
        switch (idx) {
            case 1:  return MEDIUM;
            case 2:  return HARD;
            default: return EASY;
        }
    }
}
