package com.celestialwarfront.game.contract;

public interface IGameState {
    void addListener(IStateListener l);
    void removeListener(IStateListener l);

    int getScore();
    int getHP();
    int getLevel();
    String getTimeString();
    int getAmmo();

    void changeAmmo(int delta);
    void changeScore(int delta);
    void changeHP(int delta);
    void updateTimer(float delta);

    void resetSession();

}
