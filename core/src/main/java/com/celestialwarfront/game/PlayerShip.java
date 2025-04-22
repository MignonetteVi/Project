package com.celestialwarfront.game;

public class PlayerShip implements  IMovable, IShootable {
    public float x;
    public float y;
    private float speedX, speedY;

    public PlayerShip(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speedX = speed;
        this.speedY = speed;
    }

    @Override
    public void move(float deltaTime, boolean left, boolean right) {
        if (left) x -= speedX * deltaTime;
        if (right) x += speedX * deltaTime;
    }

    @Override
    public Bullet shoot() {
        return new Bullet(x+20,y+60,500);
    }
}
