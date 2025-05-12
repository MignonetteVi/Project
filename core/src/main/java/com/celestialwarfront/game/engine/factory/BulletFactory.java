package com.celestialwarfront.game.engine.factory;

import com.celestialwarfront.game.contract.IBulletFactory;
import com.celestialwarfront.game.domain.entities.projectile.Bullet;
// Фабрика пуль (использует паттерн Factory)
public class BulletFactory implements IBulletFactory {
    // Реализация метода из интерфейса IBulletFactory
    // Этот метод создает новую пулю, используя координаты корабля.
    @Override
    public Bullet createBullet(float shipX, float shipY) {
        // Создаем пулю с заданными координатами и фиксированной скоростью 500
        return new Bullet(shipX + 20, shipY + 60, 500);
    }
}
