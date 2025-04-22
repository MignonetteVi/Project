package com.celestialwarfront.game;

public class PlayerShip implements  IMovable, IShootable {
    public float x;
    public float y;
    public float speed;

    public PlayerShip(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    @Override
    public void move(float deltaTime) {
        x+=speed*deltaTime;
    }

    @Override
    public Bullet shoot() {
        return new Bullet(x+20,y+60,500);
    }
}
