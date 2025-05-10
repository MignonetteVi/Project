package com.celestialwarfront.game.ui;

public interface GameState {
    void addListener(StateListener l);
    void removeListener(StateListener l);

    int getScore();
    int getHP();
    int getLevel();
    String getTimeString();

    void changeScore(int delta);
    void changeHP(int delta);
    void updateTimer(float delta);

    void resetSession();
}
