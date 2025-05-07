package com.celestialwarfront.game.patterns;

import com.celestialwarfront.game.entities.Bullet;

public interface IBulletFactory {
    //Интерфейс метода Фабрика
    Bullet createBullet(float shipX, float shipY);
}
