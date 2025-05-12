package com.celestialwarfront.game.engine.factory;

import com.celestialwarfront.game.contract.IBulletFactory;
import com.celestialwarfront.game.domain.entities.projectile.Bullet;

public class BulletFactory implements IBulletFactory {
    @Override
    public Bullet createBullet(float shipX, float shipY) {
        return new Bullet(shipX + 20, shipY + 60, 500);
    }
}
