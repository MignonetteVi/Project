package com.celestialwarfront.game.domain.entities.block;

import com.badlogic.gdx.graphics.Texture;

public class UnbreakableBlock extends Block {
    public UnbreakableBlock(float x, float y, Texture texture) {
        super(x, y, texture);
    }

    @Override
    public void onHit() {
        // просто проиграть звук или эффект, но не разрушаться
    }
}
