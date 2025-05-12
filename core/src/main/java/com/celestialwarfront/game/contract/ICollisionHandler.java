package com.celestialwarfront.game.contract;

import com.celestialwarfront.game.engine.collision.CollisionEvent;
// Интерфейс для обработчиков коллизий.
// Каждый обработчик должен уметь обработать событие коллизии, а также передать обработку следующему в цепочке обработчику.
public interface ICollisionHandler {
    // Этот метод позволяет задать следующий обработчик в цепочке.
    // С помощью этой функции можно создавать цепочку обработчиков коллизий.
    void setNext(ICollisionHandler next);

    // Метод, который должен обработать событие коллизии.
    // Когда коллизия произошла, событие будет передано обработчикам для соответствующей обработки.
    void handle(CollisionEvent event);
}
