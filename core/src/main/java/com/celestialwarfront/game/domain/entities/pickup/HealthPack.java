package com.celestialwarfront.game.domain.entities.pickup;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

// Класс HealthPack представляет аптечку, которую игрок может подбирать для восстановления здоровья.
public class HealthPack {
    public float x, y;// Позиция аптечки
    private final float speed;// Скорость движения аптечки (по оси Y)
    private final Texture texture;// Текстура аптечки
    private boolean picked = false;// Флаг, показывающий, была ли аптечка подобрана

    // Конструктор класса, инициализирует позицию, текстуру и скорость
    public HealthPack(float x, float y, Texture texture, float speed) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.speed = speed;
    }
    // Обновление позиции аптечки на экране, движется вниз с заданной скоростью
    public void update(float delta) {
        y -= speed * delta;// Аптечка движется вниз с учетом времени delta (изменение позиции за кадр)

    }

    // Отрисовка аптечки на экране, если она не была подобрана
    public void render(SpriteBatch batch) {
        if (!picked){// Если аптечка не была подобрана
            batch.draw(texture, x, y);  // Отрисовываем аптечку
        }
    }

    // Получение прямоугольника для проверки коллизий (для определения области аптечки на экране)
    public Rectangle getBounds() {
        return new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    // Метод, который помечает аптечку как подобранную
    public void pick() {
        picked = true;// Устанавливаем флаг, что аптечка подобрана
    }

    // Проверка, была ли аптечка подобрана
    public boolean isPicked() {
        return picked;// Возвращаем текущий статус: была ли аптечка подобрана
    }
}

