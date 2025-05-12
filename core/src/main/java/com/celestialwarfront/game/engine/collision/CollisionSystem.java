package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.contract.ICollisionHandler;
import com.celestialwarfront.game.domain.entities.projectile.Bullet;
import com.celestialwarfront.game.contract.IGameState;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// Класс обработки коллизий в игре
// Система организует цепочку обработчиков коллизий, которая последовательно обрабатывает различные типы столкновений
public class CollisionSystem {
    private final ICollisionHandler chain;// Цепочка обработчиков коллизий
    private static final Set<Bullet> bulletsToRemove = new HashSet<>();// Множество для хранения пуль, которые нужно удалить

    // Конструктор системы коллизий
    public CollisionSystem(IGameState IGameState) {
        // Создание обработчиков коллизий для разных типов объектов
        PlayerBlockICollisionHandler pbh = new PlayerBlockICollisionHandler(IGameState);
        PlayerMeteorICollisionHandler pmh = new PlayerMeteorICollisionHandler(IGameState);

        BulletICollisionHandler bch = new BulletICollisionHandler(IGameState);
        MeteorICollisionHandler mch = new MeteorICollisionHandler(IGameState);

        // Устанавливаем последовательность обработчиков: каждый обработчик передает событие следующему
        pbh.setNext(pmh);
        pmh.setNext(bch);
        bch.setNext(mch);

        // Устанавливаем начальный обработчик коллизий в цепочке
        this.chain = pbh;
    }

    // Метод для обработки коллизий
    // Этот метод вызывает обработчик из цепочки для каждого события столкновения
    public void onCollision(Object collider, Object target, int damage) {
        chain.handle(new CollisionEvent(collider, target, damage)); // Передаем событие коллизии обработчику
    }


    // Метод для пометки пули на удаление
    public static void markBulletForRemoval(Bullet b) {
        bulletsToRemove.add(b);// Добавляем пулю в список на удаление
    }

    // Метод для удаления пуль, помеченных на удаление
    public void purgeTaggedBullets(List<Bullet> bullets) {
        bullets.removeAll(bulletsToRemove);// Удаляем все пули из списка
        bulletsToRemove.clear();// Очищаем список удаляемых пуль
    }
}
