package com.celestialwarfront.game;

public interface IBulletFactory {
    //Интерфейс метода Фабрика
    Bullet createBullet(float shipX, float shipY);
}
