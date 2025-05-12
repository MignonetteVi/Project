package com.celestialwarfront.game.contract;

import com.celestialwarfront.game.domain.entities.projectile.Bullet;
// Метод создания пули. Этот метод будет реализован в классах, которые будут
// отвечать за создание объектов пуль для конкретных типов оружия или ситуаций.
// Параметры: shipX и shipY - координаты, где будет создана пуля.
public interface IBulletFactory {
    // Этот метод должен создать пулю в указанной позиции.
    // shipX и shipY - это координаты, где будет появляться пуля (обычно, это положение корабля).
    Bullet createBullet(float shipX, float shipY);
}
