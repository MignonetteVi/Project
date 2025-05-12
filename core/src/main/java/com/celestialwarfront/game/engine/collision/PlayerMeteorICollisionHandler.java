package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.domain.entities.player.PlayerShip;
import com.celestialwarfront.game.domain.entities.block.Meteor;
import com.celestialwarfront.game.contract.IGameState;

// Обработчик коллизий для взаимодействия игрока с метеорами
// Этот класс обрабатывает столкновения между игроком и метеорами, налагая урон игроку и метеору
public class PlayerMeteorICollisionHandler extends AbstractICollisionHandler {
    private final IGameState IGameState;// Ссылка на состояние игры для изменения очков/HP


    // Конструктор, инициализирует обработчик с состоянием игры
    public PlayerMeteorICollisionHandler(IGameState IGameState) {
        this.IGameState = IGameState;
    }

    // Метод для обработки события столкновения
    @Override
    public void handle(CollisionEvent event) {
        // Проверка: если collider - это игрок (PlayerShip), а target - метеор (Meteor)
        if (event.collider instanceof PlayerShip
                && event.target instanceof Meteor) {

            Meteor meteor = (Meteor)event.target;// Приводим target к объекту Meteor
            IGameState.changeHP(-20);// Наносим игроку урон (20 HP) при столкновении с метеором
            meteor.applyDamage(event.damage);// Наносим урон метеору, используя переданный damage

        } else {
            // Если столкновение не между игроком и метеором, передаем событие следующему обработчику
            passToNext(event);
        }
    }
}
