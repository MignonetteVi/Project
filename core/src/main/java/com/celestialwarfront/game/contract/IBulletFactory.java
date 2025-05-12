package com.celestialwarfront.game.contract;

import com.celestialwarfront.game.domain.entities.projectile.Bullet;

public interface IBulletFactory {
    //Интерфейс метода Фабрика
    Bullet createBullet(float shipX, float shipY);
}
