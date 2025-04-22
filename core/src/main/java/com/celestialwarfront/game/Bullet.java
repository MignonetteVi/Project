package com.celestialwarfront.game;

public class Bullet implements IMovable{
    public float x;
    public float y;
    public float speed;

    public Bullet(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    @Override
    public void move(float deltaTime) {
        y+=speed*deltaTime;
    }
}
