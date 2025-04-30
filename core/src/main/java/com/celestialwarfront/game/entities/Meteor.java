package com.celestialwarfront.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Meteor {
    public float x, y;
    public float width, height;
    private final float speed;
    private final Texture texture;

    public Meteor(float x, float y, Texture texture, float speed) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.speed = speed;
        // в два раза меньше
        this.width  = texture.getWidth()  * 0.5f;
        this.height = texture.getHeight() * 0.5f;
    }

    public void update(float delta) {
        y -= speed * delta;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
