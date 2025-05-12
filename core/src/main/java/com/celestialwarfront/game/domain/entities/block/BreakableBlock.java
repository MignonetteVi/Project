package com.celestialwarfront.game.domain.entities.block;

import com.badlogic.gdx.graphics.Texture;

public class BreakableBlock extends Block {

    private int health;
    private boolean destroyed = false;

    public BreakableBlock(float x, float y, Texture texture, int initialHealth) {
        super(x, y, texture);
        this.health = initialHealth;
    }

    @Override
    public void onHit() {
        if (destroyed) return;
        health--;
        if (health <= 0) destroy();
    }

    private void destroy() {
        destroyed = true;
        // TODO: проиграть анимацию, звук и т.п.
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
