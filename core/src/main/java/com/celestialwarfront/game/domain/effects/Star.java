package com.celestialwarfront.game.domain.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.celestialwarfront.game.contract.IParticle;

public class Star implements IParticle {

    // Загружаем текстуру для звезды
    private static final Texture TEXTURE = new Texture(Gdx.files.internal("star.png"));

    // Позиция звезды по осям X и Y
    public float x, y;

    // Скорость движения звезды
    public float speed;

    // Конструктор класса Star
    // @param x Начальная позиция звезды по оси X.
    // @param y Начальная позиция звезды по оси Y.
    // @param speed Скорость движения звезды.
    public Star(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    // Метод для обновления положения звезды
    // Этот метод сдвигает звезду вниз на основе времени (delta) и ее скорости.
    // Когда звезда выходит за нижнюю границу экрана, она появляется снова вверху, в случайном месте по оси X.
    @Override
    public void update(float delta) {
        y -= speed * delta;// Двигаем звезду вниз на основе скорости и времени
        if (y < 0) { // Если звезда выходит за нижнюю границу экрана
            y = Gdx.graphics.getHeight();// Перемещаем ее в верхнюю часть экрана
            x = MathUtils.random(0, Gdx.graphics.getWidth());// Устанавливаем случайное положение по оси X
        }
    }
    // Метод для отрисовки звезды на экране
    // @param batch Объект SpriteBatch, с помощью которого происходит отрисовка.
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(TEXTURE, x, y);// Рисуем текстуру звезды в текущей позиции
    }
}
