package com.celestialwarfront.game.contract;
// Интерфейс для объектов, которые могут двигаться по экрану (например, игрок, пули и другие объекты)
public interface IMovable {

    // Метод для перемещения объекта. Принимает время с последнего обновления (deltaTime)
    // и флаги для движения влево и вправо.
    // @param deltaTime - время с последнего обновления экрана
    // @param left - флаг, указывающий, нужно ли двигаться влево
    // @param right - флаг, указывающий, нужно ли двигаться вправо
    void move(float deltaTime, boolean left, boolean right);
}
