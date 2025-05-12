package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.Game;

public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
