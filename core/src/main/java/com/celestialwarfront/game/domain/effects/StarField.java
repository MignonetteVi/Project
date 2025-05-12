package com.celestialwarfront.game.domain.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.celestialwarfront.game.contract.IParticle;

import java.util.ArrayList;
import java.util.List;

public class StarField implements IParticle {
    // Список звезд, который содержит объекты IParticle (в данном случае это звезды)
    private final List<IParticle> stars = new ArrayList<>();
    // Скорость движения звезд
    private float speed;

    // Конструктор для создания поля звезд
    // @param count Количество звезд, которые будут созданы.
    // @param initialSpeed Начальная скорость звезд.
    public StarField(int count, float initialSpeed) {
        this.speed = initialSpeed;
        // Создаем заданное количество звезд с случайными начальными координатами и заданной скоростью
        for (int i = 0; i < count; i++) {
            float x = MathUtils.random(0, Gdx.graphics.getWidth());// Случайная координата X
            float y = MathUtils.random(0, Gdx.graphics.getHeight());// Случайная координата Y
            stars.add(new Star(x, y, speed));// Добавляем звезды в список
        }
    }

    // Метод для изменения скорости движения звезд
    // @param newSpeed Новая скорость, которую нужно установить.
    public void setSpeed(float newSpeed) {
        this.speed = newSpeed;
    }

    // Обновление состояния всех звезд
    // @param delta Время с последнего обновления.
    @Override
    public void update(float delta) {
        for (IParticle p : stars) {
            // Если объект является звездой, обновляем его скорость
            if (p instanceof Star) {
                ((Star) p).speed = speed;
            }
            p.update(delta);// Обновляем каждую звезду
        }
    }

    // Отрисовка всех звезд
    // @param batch Объект SpriteBatch, используемый для рисования.
    @Override
    public void render(SpriteBatch batch) {
        for (IParticle p : stars) {
            p.render(batch);// Рисуем каждую звезду
        }
    }
}
