package com.celestialwarfront.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Block {
    protected float x, y;
    protected float width, height;
    protected Texture texture;

    public Block(float x, float y, Texture texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    public abstract void onHit();

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void move(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }
}
