package com.celestialwarfront.game.domain.entities.pickup;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.celestialwarfront.game.contract.IPrototype;

public class HealthPack implements IPrototype<HealthPack> {
    public float x, y;
    private  float speed;
    private final Texture texture;
    private boolean picked = false;

    public HealthPack(float x, float y, Texture texture, float speed) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.speed = speed;
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

    public HealthPack setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public HealthPack setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    // Метод клонирования - создает новую аптечку с теми же параметрами
    @Override
    public HealthPack clone() {
        return new HealthPack(this.x, this.y, this.texture, this.speed); // Создаем копию с теми же значениями
    }
}


