package com.celestialwarfront.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class AmmoBox {
    public float x, y;
    private final float speed;
    private final Texture texture;
    private boolean picked = false;

    public AmmoBox(float x, float y, Texture tex, float speed) {
        this.x = x; this.y = y; this.texture = tex; this.speed = speed;
    }

    public void update(float delta) {
        y -= speed * delta;
    }

    public void render(SpriteBatch batch) {
        if (!picked) batch.draw(texture, x, y);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void pick() {
        picked = true;
    }

    public boolean isPicked() {
        return picked;
    }
}
