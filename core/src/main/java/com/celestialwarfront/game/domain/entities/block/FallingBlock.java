package com.celestialwarfront.game.domain.entities.block;

import com.badlogic.gdx.graphics.Texture;

public class FallingBlock extends Block {
    private boolean isFalling = false;
    private final float fallSpeed = 200f; // пикселей/сек

    public FallingBlock(float x, float y, Texture texture) {
        super(x, y, texture);
    }

    @Override
    public void onHit() {
        isFalling = true;
    }

    public void update(float delta) {
        if (isFalling) {
            y -= fallSpeed * delta;
        }
    }
}
