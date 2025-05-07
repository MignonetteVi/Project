package com.celestialwarfront.game.entities;

import com.badlogic.gdx.Gdx;

public class PlayerShip implements IMovable, IShootable {
    public float x;
    public float y;
    private float speedX;
    private float screenWidth = Gdx.graphics.getWidth();
    private float width = 115;

    public PlayerShip(float x, float speed) {
        this.x = x;

        this.speedX = speed;

    }

    public void setPosition(float x) {
        this.x = x;
    }

    @Override
    public void move(float deltaTime, boolean left, boolean right) {
        if (left) x -= speedX * deltaTime;
        if (right) x += speedX * deltaTime;
        if (x < 0) x = 0;
        if (x + width > screenWidth) x = screenWidth - width;
    }

    @Override
    public Bullet shoot() {
        return new Bullet(x+20,y+60,500);
    }
}
