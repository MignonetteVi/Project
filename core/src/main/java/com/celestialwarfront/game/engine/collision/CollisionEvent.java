package com.celestialwarfront.game.engine.collision;
// Класс представляет собой событие коллизии между двумя объектами.
// Содержит информацию о столкнувшихся объектах и уроне, который должен быть нанесен.
public class CollisionEvent {
    public final Object collider;// Объект, который столкнулся (например, пуля)
    public final Object target;// Объект, с которым произошло столкновение (например, блок или метеор)
    public final int damage;// Урон, который нужно нанести целевому объекту
    // Конструктор, инициализирующий событие коллизии
    public CollisionEvent(Object collider, Object target, int damage) {
        this.collider = collider;// Присваиваем объект, который столкнулся
        this.target = target;// Присваиваем объект, с которым столкнулись
        this.damage = damage;// Присваиваем урон
    }
}
