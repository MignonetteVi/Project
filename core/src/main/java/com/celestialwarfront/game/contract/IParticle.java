package com.celestialwarfront.game.contract;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IParticle {
    void update(float delta);
    void render(SpriteBatch batch);
}
