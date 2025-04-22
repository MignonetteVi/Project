package com.celestialwarfront.game;

public class BulletFactory implements IBulletFactory{
    @Override
    public Bullet createBullet(float shipX, float shipY) {
        return new Bullet(shipX + 20, shipY + 60, 500);
    }
}
