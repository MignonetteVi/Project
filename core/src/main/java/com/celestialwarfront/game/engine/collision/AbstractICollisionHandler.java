package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.contract.ICollisionHandler;

// Абстрактный класс для обработки коллизий. Реализует интерфейс ICollisionHandler.
// Он использует цепочку ответственности для передачи событий коллизий.
public abstract class AbstractICollisionHandler implements ICollisionHandler {
    private ICollisionHandler next; // Следующий обработчик в цепочке

    // Устанавливаем следующий обработчик в цепочке
    @Override
    public void setNext(ICollisionHandler next) {
        this.next = next;// Присваиваем следующий обработчик
    }

    // Метод для передачи события коллизии следующему обработчику в цепочке
    protected void passToNext(CollisionEvent event) {
        if (next != null) next.handle(event);// Если следующий обработчик есть, передаем событие
    }
}
