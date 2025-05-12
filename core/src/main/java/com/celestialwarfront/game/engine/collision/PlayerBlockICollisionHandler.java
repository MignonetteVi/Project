package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.domain.entities.player.PlayerShip;
import com.celestialwarfront.game.domain.entities.block.Block;
import com.celestialwarfront.game.contract.IGameState;  // <- ваш интерфейс

// Обработчик коллизий для взаимодействия игрока с блоками
// Этот класс обрабатывает столкновения между игроком и блоками, налагая урон игроку и разрушая блоки
public class PlayerBlockICollisionHandler extends AbstractICollisionHandler {
    private final IGameState IGameState;// Ссылка на состояние игры для изменения очков/HP

    // Конструктор, инициализирует обработчик с состоянием игры
    public PlayerBlockICollisionHandler(IGameState IGameState) {
        this.IGameState = IGameState;
    }

    // Метод для обработки события столкновения
    @Override
    public void handle(CollisionEvent event) {
        // Проверка: если collider - это игрок (PlayerShip), а target - блок (Block)
        if (event.collider instanceof PlayerShip
            && event.target instanceof Block) {
            Block block = (Block)event.target;// Приводим target к объекту Block
            IGameState.changeHP(-50);// Наносим игроку урон (50 HP) при столкновении с блоком
            block.onHit();// Блок получает урон
            block.markDestroyed();// Помечаем блок как разрушенный
        } else {
            // Если столкновение не между игроком и блоком, передаем событие следующему обработчику
            passToNext(event);
        }
    }
}
