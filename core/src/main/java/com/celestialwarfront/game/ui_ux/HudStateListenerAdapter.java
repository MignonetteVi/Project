package com.celestialwarfront.game.ui_ux;

import com.celestialwarfront.game.contract.IStateListener;

public class HudStateListenerAdapter implements IStateListener {
    private final Hud hud;
    public HudStateListenerAdapter(Hud hud) {
        this.hud = hud;
    }

    @Override public void onScoreChanged(int newScore) { hud.onScoreChanged(newScore); }
    @Override public void onHPChanged(int newHP) { hud.onHPChanged(newHP); }
    @Override public void onLevelChanged(int newLevel) { hud.onLevelChanged(newLevel); }
    @Override public void onTimeChanged(String timeString) { hud.onTimeChanged(timeString); }
    @Override public void onAmmoChanged(int ammo) { hud.onAmmoChanged(ammo); }
}
