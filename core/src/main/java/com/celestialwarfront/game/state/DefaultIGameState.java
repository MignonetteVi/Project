package com.celestialwarfront.game.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.celestialwarfront.game.contract.IGameState;
import com.celestialwarfront.game.contract.IStateListener;

import java.util.ArrayList;
import java.util.List;

public class DefaultIGameState implements IGameState {
    private static final String PREFS_NAME = "celestial_warfront";
    private static final String KEY_LEVEL = "level";

    private final Preferences prefs;
    private final List<IStateListener> listeners = new ArrayList<>();

    private int score;
    private int hp;
    private int level;
    private float elapsed;
    private int ammo;

    public DefaultIGameState() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        level = prefs.getInteger(KEY_LEVEL, 0);
        score = 0;
        hp = 100;
        elapsed = 0f;
        ammo = 15;
    }

    @Override public void addListener(IStateListener l) { listeners.add(l); }
    @Override public void removeListener(IStateListener l) { listeners.remove(l); }

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
        listeners.forEach(l -> l.onScoreChanged(score));

        // Повышаем уровень, пока достаточно очков для следующего
        while (score >= thresholdFor(level + 1)) {
            level++;
            prefs.putInteger(KEY_LEVEL, level);
            prefs.flush();
            listeners.forEach(l -> l.onLevelChanged(level));
        }
    }

    private int thresholdFor(int targetLevel) {
        if (targetLevel <= 1) {
            return 10; // чтобы level стал 1 нужно 10
        } else if (targetLevel == 2) {
            return 30; // чтобы level ствл 2 нужно 30
        } else { // начиная с третьего: 30 + (уровень-2) * 10
            return 30 + (targetLevel - 2) * 10;
        }
    }

    @Override
    public void changeHP(int delta) {
        int old = hp;
        int newHp = old + delta;
        if (newHp > 100) newHp = 100;
        if (newHp < 0)   newHp = 0;
        hp = newHp;

        if (hp != old) {
            for (IStateListener l : listeners) {
                l.onHPChanged(hp);
            }
        }
    }

    @Override
    public void updateTimer(float delta) {
        elapsed += delta;
        if ((int) (elapsed - delta) != (int) elapsed) {
            for (IStateListener l : listeners) l.onTimeChanged(getTimeString());
        }
    }

    public void resetSession() {
        score = 0;
        hp = 100;
        elapsed = 0f;
        ammo = 15;

        for (IStateListener l : listeners) {
            l.onScoreChanged(score);
            l.onHPChanged(hp);
            l.onAmmoChanged(ammo);
            l.onTimeChanged(getTimeString());
        }
    }

    // --- ДЛЯ РАЗРАБОТЧИКА ---
    public void resetLevel() {
        level = 0;
        prefs.putInteger(KEY_LEVEL, level);
        prefs.flush();
        for (IStateListener l : listeners) {
            l.onLevelChanged(level);
        }
    }

    @Override
    public int getAmmo() {
        return ammo;
    }

    @Override
    public void changeAmmo(int delta) {
        int old = ammo;
        ammo = Math.max(0, ammo + delta);
        if (ammo != old) {
            for (IStateListener l : listeners) {
                l.onAmmoChanged(ammo);
            }
        }
    }

}
