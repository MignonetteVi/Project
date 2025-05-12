package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.contract.IDamageable;
import com.celestialwarfront.game.domain.entities.projectile.Bullet;
import com.celestialwarfront.game.domain.entities.block.Block;
import com.celestialwarfront.game.domain.entities.block.Meteor;
import com.celestialwarfront.game.contract.IGameState;
import java.util.Objects;

// Обработчик коллизий для пуль. Этот класс обрабатывает коллизии пуль с объектами,
// которые могут быть повреждены (например, блоки и метеоры).
public class BulletICollisionHandler extends AbstractICollisionHandler {
    private final IGameState IGameState;// Ссылка на объект состояния игры

    // Конструктор, принимающий состояние игры
    public BulletICollisionHandler(IGameState IGameState) {
        this.IGameState = Objects.requireNonNull(IGameState);// Инициализация состояния игры
    }

    // Метод обработки события коллизии
    @Override
    public void handle(CollisionEvent event) {
        // Проверяем, что коллайдер - пуля, а цель - объект, который можно повредить
        if (event.collider instanceof Bullet
            && event.target instanceof IDamageable) {

            // Применяем урон к объекту, который был поврежден
            IDamageable target = (IDamageable) event.target;
            target.applyDamage(event.damage);// Наносим урон

            // Если объектом является блок, увеличиваем счет на 1
            if (target instanceof Block && ((Block) target).isDestroyed()) {
                IGameState.changeScore(1);
            }
            // Если объектом является метеор, увеличиваем счет на 2
            else if (target instanceof Meteor && ((Meteor) target).isDestroyed()) {
                IGameState.changeScore(2);
            }
            // Отметить пулю для удаления
            CollisionSystem.markBulletForRemoval((Bullet) event.collider);
        } else {
            passToNext(event);// Если объект не подошел, передаем событие следующему обработчику
        }
    }
}
