package com.celestialwarfront.game.particles;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Particle {
    void update(float delta);
    void render(SpriteBatch batch);
}
