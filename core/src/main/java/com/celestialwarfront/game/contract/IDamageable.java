package com.celestialwarfront.game.contract;


// Интерфейс для объектов, которые могут получать урон.
public interface IDamageable {
    // Метод для применения урона к объекту.
    // При вызове этого метода объект должен уменьшить свои очки здоровья на указанное количество урона.
    void applyDamage(int damage);
}
