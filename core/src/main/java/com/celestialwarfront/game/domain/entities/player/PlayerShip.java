package com.celestialwarfront.game.domain.entities.player;

import com.badlogic.gdx.Gdx;
import com.celestialwarfront.game.contract.IMovable;
import com.celestialwarfront.game.contract.IShootable;
import com.celestialwarfront.game.domain.entities.projectile.Bullet;

// Класс PlayerShip представляет игрока в игре (корабль), который может двигаться и стрелять.
public class PlayerShip implements IMovable, IShootable {
    public float x;// Текущая позиция корабля по оси X
    public float y; // Текущая позиция корабля по оси Y
    private float speedX;// Скорость движения по оси X
    private float screenWidth = Gdx.graphics.getWidth();// Ширина экрана
    private float width = 115;// Ширина корабля (для ограничения движения по экрану)

    // Конструктор, который инициализирует начальную позицию и скорость
    public PlayerShip(float x, float speed) {
        this.x = x;

        this.speedX = speed;

    }

    // Устанавливает позицию корабля по оси X
    public void setPosition(float x) {
        this.x = x;
    }

    // Реализация интерфейса IMovable (перемещение корабля)
    @Override
    public void move(float deltaTime, boolean left, boolean right) {
        if (left) x -= speedX * deltaTime;// Если нажата клавиша для движения влево
        if (right) x += speedX * deltaTime;// Если нажата клавиша для движения вправо


        // Проверка границ экрана
        if (x < 0) x = 0; // Не даем кораблю выходить за левый край
        if (x + width > screenWidth) x = screenWidth - width;// Не даем кораблю выходить за правый край
    }

    // Реализация интерфейса IShootable (выстрел)
    @Override
    public Bullet shoot() {
        return new Bullet(x+20,y+60,500);// Создаем новый объект Bullet (пуля), который начинается с позиции корабля
    }
}
