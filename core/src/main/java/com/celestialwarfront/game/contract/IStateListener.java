package com.celestialwarfront.game.contract;

public interface IStateListener {
    void onScoreChanged(int newScore);
    void onHPChanged(int newHP);
    void onLevelChanged(int newLevel);
    void onTimeChanged(String timeString);
    void onAmmoChanged(int ammo);
}
