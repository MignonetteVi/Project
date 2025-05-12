package com.celestialwarfront.game.contract;

// Интерфейс для объектов типа пули, которые могут двигаться по экрану.
public interface IMovableBullet {
    // Метод для перемещения пули. Принимает время с последнего обновления (deltaTime).
    // @param deltaTime - время, прошедшее с последнего обновления экрана.
    void move(float deltaTime);
}
