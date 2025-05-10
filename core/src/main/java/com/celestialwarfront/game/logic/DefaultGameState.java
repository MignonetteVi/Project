package com.celestialwarfront.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.celestialwarfront.game.ui.GameState;
import com.celestialwarfront.game.ui.StateListener;

import java.util.ArrayList;
import java.util.List;

public class DefaultGameState implements GameState {
    private static final String PREFS_NAME = "celestial_warfront";
    private static final String KEY_LEVEL = "level";

    private final Preferences prefs;
    private final List<StateListener> listeners = new ArrayList<>();

    private int score;
    private int hp;
    private int level;
    private float elapsed;

    public DefaultGameState() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        level = prefs.getInteger(KEY_LEVEL, 1);
        score = 0;
        hp = 100;
        elapsed = 0f;
    }

    @Override public void addListener(StateListener l) { listeners.add(l); }
    @Override public void removeListener(StateListener l) { listeners.remove(l); }

    @Override public int getScore() { return score; }
    @Override public int getHP() { return hp; }
    @Override public int getLevel() { return level; }
    @Override public String getTimeString() {
        int totalSec = (int) elapsed;
        int min = totalSec / 60;
        int sec = totalSec % 60;
        return String.format("%02d:%02d", min, sec);
    }

    @Override
    public void changeScore(int delta) {
        score += delta;
        for (StateListener l : listeners) l.onScoreChanged(score);

        // авто-повышение уровня за каждые мастер*10 очков
        while (score >= level * 10) {
            level++;
            prefs.putInteger(KEY_LEVEL, level);
            prefs.flush();
            for (StateListener l : listeners) l.onLevelChanged(level);
        }
    }

    @Override
    public void changeHP(int delta) {
        int old = hp;
        hp = Math.max(0, hp + delta);
        if (hp != old) {
            for (StateListener l : listeners) l.onHPChanged(hp);
        }
    }

    @Override
    public void updateTimer(float delta) {
        elapsed += delta;
        if ((int) (elapsed - delta) != (int) elapsed) {
            for (StateListener l : listeners) l.onTimeChanged(getTimeString());
        }
    }

    public void resetSession() {
        score = 0;
        hp = 100;
        elapsed = 0f;

        for (StateListener l : listeners) {
            l.onScoreChanged(score);
            l.onHPChanged(hp);
            l.onTimeChanged(getTimeString());
        }
    }

}
