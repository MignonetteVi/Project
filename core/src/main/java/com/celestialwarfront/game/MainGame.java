package com.celestialwarfront.game;

import com.badlogic.gdx.Game;
import com.celestialwarfront.game.ui.MainMenuScreen;

public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
