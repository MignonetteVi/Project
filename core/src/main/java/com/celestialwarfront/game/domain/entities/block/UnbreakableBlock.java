package com.celestialwarfront.game.domain.entities.block;

import com.badlogic.gdx.graphics.Texture;
// Класс UnbreakableBlock, наследует от Block и представляет собой неразрушимый блок
public class UnbreakableBlock extends Block {
    // Конструктор для создания неразрушимого блока с заданной текстурой
    public UnbreakableBlock(float x, float y, Texture texture) {
        super(x, y, texture);// Вызов конструктора родительского класса Block
    }

    // Метод вызывается при попадании в блок (например, при столкновении с пулей)
    @Override
    public void onHit() {
        // Этот блок не разрушается, но можно добавить эффект или звук
        // Пример: проигрывание звука столкновения или эффект, но сам блок не изменяет своего состояния
    }
}
