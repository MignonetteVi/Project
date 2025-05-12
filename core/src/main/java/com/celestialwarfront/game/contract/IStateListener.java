package com.celestialwarfront.game.contract;
// Интерфейс для слушателя изменений состояния игры
// Этот интерфейс позволяет объектам (например, UI, звуковым системам) отслеживать изменения состояния игры.

public interface IStateListener {

    // Метод, вызываемый при изменении счета в игре.
    // @param newScore новый счет.
    void onScoreChanged(int newScore);

    // Метод, вызываемый при изменении здоровья игрока.
    // @param newHP новое количество здоровья.
    void onHPChanged(int newHP);
    // Метод, вызываемый при изменении уровня игры.
    // @param newLevel новый уровень.

    void onLevelChanged(int newLevel);

    // Метод, вызываемый при изменении времени игры.
    // @param timeString строковое представление текущего времени игры.
    void onTimeChanged(String timeString);

    // Метод, вызываемый при изменении количества боеприпасов.
    // @param ammo новое количество боеприпасов.
    void onAmmoChanged(int ammo);
}
