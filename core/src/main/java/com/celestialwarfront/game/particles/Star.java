package com.celestialwarfront.game.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Star implements Particle {
    private static final Texture TEXTURE = new Texture(Gdx.files.internal("star.png"));
    public float x, y;
    public float speed;

    public Star(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    @Override
    public void update(float delta) {
        y -= speed * delta;
        if (y < 0) {
            y = Gdx.graphics.getHeight();
            x = MathUtils.random(0, Gdx.graphics.getWidth());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(TEXTURE, x, y);
    }
}
