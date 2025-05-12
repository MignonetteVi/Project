package com.celestialwarfront.game.domain.entities.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.celestialwarfront.game.contract.IDamageable;

public abstract class Block implements IDamageable {
    protected float x, y;
    protected float width, height;
    protected Texture texture;
    private boolean destroyed = false;  // флаг удаления

    public Block(float x, float y, Texture texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    @Override
    public void applyDamage(int damage) {
        onHit();
    }

    public abstract void onHit();

    public boolean isDestroyed() {
        return destroyed;
    }

    public void markDestroyed() {
        this.destroyed = true;
    }


    public void move(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void render(SpriteBatch batch) {
        if (!destroyed) {
            batch.draw(texture, x, y);
        }
    }
}
