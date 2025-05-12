package com.celestialwarfront.game.domain.entities.pickup;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// Класс AmmoBox представляет собой коробку с патронами, которую игрок может подбирать
public class AmmoBox {
    public float x, y;// Позиция коробки
    private final float speed;// Скорость движения коробки (по оси Y)
    private final Texture texture;// Текстура коробки
    private boolean picked = false;// Флаг, показывающий, была ли коробка подобрана
    // Конструктор класса, инициализирует позицию, текстуру и скорость
    public AmmoBox(float x, float y, Texture tex, float speed) {
        this.x = x; this.y = y; this.texture = tex; this.speed = speed;
    }

    // Обновление позиции коробки на экране, движется вниз с заданной скоростью
    public void update(float delta) {
        y -= speed * delta; // Коробка движется вниз с учетом времени delta (изменение позиции за кадр)

    }

    // Отрисовка коробки на экране, если она не была подобрана
    public void render(SpriteBatch batch) {
        if (!picked) batch.draw(texture, x, y);
    }


    // Получение прямоугольника для проверки коллизий (для определения области коробки на экране)
    public Rectangle getBounds() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    // Метод, который помечает коробку как подобранную
    public void pick() {
        picked = true;// Устанавливаем флаг, что коробка подобрана
    }

    // Проверка, была ли коробка подобрана
    public boolean isPicked() {
        return picked;// Возвращаем текущий статус: была ли коробка подобрана
    }
}
