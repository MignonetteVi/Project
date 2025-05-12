package com.celestialwarfront.game.domain.entities.block;

import com.badlogic.gdx.graphics.Texture;
// Класс BreakableBlock, наследует от Block и представляет собой разрушимый блок
public class BreakableBlock extends Block {

    // Здоровье блока, при попадании в блок оно уменьшается
    private int health;


    // Флаг, показывающий, был ли блок уничтожен
    private boolean destroyed = false;

    // Конструктор для создания блока с заданным количеством здоровья
    public BreakableBlock(float x, float y, Texture texture, int initialHealth) {
        super(x, y, texture);// Вызов конструктора родительского класса Block
        this.health = initialHealth;// Инициализация здоровья
    }
    // Метод вызывается при попадании в блок (при повреждении)
    @Override
    public void onHit() {
        // Если блок уже разрушен, ничего не делаем
        if (destroyed) return;
        health--;// Уменьшаем здоровье блока
        if (health <= 0) destroy();// Если здоровье стало 0, блок уничтожается
    }

    // Метод уничтожения блока
    private void destroy() {
        destroyed = true;// Помечаем блок как разрушенный
        // TODO: проиграть анимацию, звук и т.п.
    }

    // Геттер для флага разрушения
    public boolean isDestroyed() {
        return destroyed;
    }
}
