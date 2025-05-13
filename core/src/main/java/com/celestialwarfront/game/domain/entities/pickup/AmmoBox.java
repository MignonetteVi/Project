package com.celestialwarfront.game.domain.entities.pickup;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.celestialwarfront.game.contract.IPrototype;

public class AmmoBox implements IPrototype<AmmoBox> {
    public float x, y;
    private float speed;
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
    public AmmoBox setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    public AmmoBox setSpeed(float speed) {
        this.speed = speed;
        return this;
    }


    // Метод клонирования - создает новый ящик с теми же параметрами
    @Override
    public AmmoBox clone() {
        return new AmmoBox(this.x, this.y, this.texture, this.speed);// Создаем копию с теми же значениями
    }
}
