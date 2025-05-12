package com.celestialwarfront.game.domain.entities.projectile;

import com.celestialwarfront.game.contract.IMovableBullet;
// Класс Bullet представляет снаряд (пулю), который движется по экрану.
public class Bullet implements IMovableBullet {
    public float x;// Текущая позиция пули по оси X
    public float y;// Текущая позиция пули по оси Y
    public float speed;// Скорость пули

    // Конструктор пули, который инициализирует ее позицию и скорость
    public Bullet(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }
    // Реализация метода move() из интерфейса IMovableBullet
    @Override
    public void move(float deltaTime) {
        y+=speed*deltaTime;// Перемещаем пулю по оси Y в зависимости от ее скорости и времени
    }
}
