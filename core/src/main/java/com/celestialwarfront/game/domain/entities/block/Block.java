package com.celestialwarfront.game.domain.entities.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.celestialwarfront.game.contract.IDamageable;

// Абстрактный класс для блоков, который реализует интерфейс IDamageable
public abstract class Block implements IDamageable {
    // Позиция блока по оси X и Y
    protected float x, y;

    // Ширина и высота блока
    protected float width, height;

    // Текстура, используемая для отрисовки блока
    protected Texture texture;

    // Флаг уничтожения блока
    private boolean destroyed = false;  // флаг удаления

    // Конструктор блока, принимает координаты (x, y) и текстуру для отрисовки
    public Block(float x, float y, Texture texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }
    // Метод из интерфейса IDamageable для применения урона
    // В данном случае, при получении урона вызывается метод onHit()
    @Override
    public void applyDamage(int damage) {
        onHit();
    }


    // Абстрактный метод, который будет реализован в дочерних классах.
    // Этот метод будет вызываться при попадании в блок
    public abstract void onHit();

    // Метод, который возвращает статус блока (разрушен он или нет)
    public boolean isDestroyed() {
        return destroyed;
    }

    // Метод, который помечает блок как разрушенный
    public void markDestroyed() {
        this.destroyed = true;
    }

    // Метод для перемещения блока (изменение координат)
    public void move(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    // Метод для получения прямоугольника, представляющего границы блока
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Метод отрисовки блока, если он не разрушен
    public void render(SpriteBatch batch) {
        if (!destroyed) {
            batch.draw(texture, x, y);
        }
    }
}
