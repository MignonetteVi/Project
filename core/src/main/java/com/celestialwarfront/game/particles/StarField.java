package com.celestialwarfront.game.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

public class StarField implements Particle {
    private final List<Particle> stars = new ArrayList<>();
    private float speed;

    public StarField(int count, float initialSpeed) {
        this.speed = initialSpeed;
        for (int i = 0; i < count; i++) {
            float x = MathUtils.random(0, Gdx.graphics.getWidth());
            float y = MathUtils.random(0, Gdx.graphics.getHeight());
            stars.add(new Star(x, y, speed));
        }
    }

    public void setSpeed(float newSpeed) {
        this.speed = newSpeed;
    }

    @Override
    public void update(float delta) {
        for (Particle p : stars) {
            if (p instanceof Star) {
                ((Star) p).speed = speed;
            }
            p.update(delta);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        for (Particle p : stars) {
            p.render(batch);
        }
    }
}
