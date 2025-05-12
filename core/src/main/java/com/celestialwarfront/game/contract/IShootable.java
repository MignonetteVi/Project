package com.celestialwarfront.game.contract;

import com.celestialwarfront.game.domain.entities.projectile.Bullet;
// Интерфейс для объектов, которые могут стрелять.
public interface IShootable {
    // Метод, который отвечает за создание и возврат пули.
    // Этот метод должен быть реализован для всех объектов, которые могут стрелять (например, корабль, пушка и т.д.).
    // @return возвращает объект типа Bullet, который представляет пулю, выстреливаемую объектом.
    Bullet shoot();
}
