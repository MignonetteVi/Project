package com.celestialwarfront.game.domain.entities.block;

import com.badlogic.gdx.graphics.Texture;
// Класс FallingBlock, наследует от Block и представляет собой падающий блок
public class FallingBlock extends Block {

    // Флаг, указывающий, что блок начал падать
    private boolean isFalling = false;

    // Скорость падения блока в пикселях в секунду
    private final float fallSpeed = 200f; // пикселей/сек

    // Конструктор для создания блока с возможностью падения
    public FallingBlock(float x, float y, Texture texture) {
        super(x, y, texture);// Вызов конструктора родительского класса Block
    }

    // Метод вызывается при попадании в блок (например, при столкновении)
    @Override
    public void onHit() {
        // Помечаем блок как падающий
        isFalling = true;
    }
    // Метод обновления состояния блока
    public void update(float delta) {
        if (isFalling) {
            // Если блок падает, изменяем его положение по оси Y (падение вниз)
            y -= fallSpeed * delta;
        }
    }
}
