package com.celestialwarfront.game.ui;

public interface StateListener {
    void onScoreChanged(int newScore);
    void onHPChanged(int newHP);
    void onLevelChanged(int newLevel);
    void onTimeChanged(String timeString);
    void onAmmoChanged(int ammo);
}
