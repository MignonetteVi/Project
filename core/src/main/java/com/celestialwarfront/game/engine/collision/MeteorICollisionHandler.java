package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.domain.entities.projectile.Bullet;
import com.celestialwarfront.game.domain.entities.block.Meteor;
import com.celestialwarfront.game.contract.IGameState;
// Обработчик коллизий для взаимодействия пуль с метеорами
// Этот класс обрабатывает столкновения пуль с метеорами и начисляет очки игроку за уничтоженные метеоры
public class MeteorICollisionHandler extends AbstractICollisionHandler {
    private final IGameState IGameState;// Ссылка на состояние игры для изменения очков

    // Конструктор, инициализирует обработчик с состоянием игры
    public MeteorICollisionHandler(IGameState IGameState) {
        this.IGameState = IGameState;
    }

    // Метод для обработки события столкновения
    @Override
    public void handle(CollisionEvent event) {
        // Проверка: если collider - пуля, а target - метеор
        if (event.collider instanceof Bullet && event.target instanceof Meteor) {
            Meteor meteor = (Meteor) event.target;// Приводим target к объекту Meteor
            meteor.applyDamage(event.damage);// Наносим урон метеор

            // Если метеор уничтожен, начисляем игроку очки
            if (meteor.isDestroyed()) {
                IGameState.changeScore(2);// За уничтожение метеора даём 2 очка
            }
            // Помечаем пулю на удаление
            CollisionSystem.markBulletForRemoval((Bullet)event.collider);
        } else {
            // Если столкновение не с метеором, передаем событие следующему обработчику в цепочке
            passToNext(event);
        }
    }
}
