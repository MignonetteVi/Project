package com.celestialwarfront.game.contract;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// Интерфейс для всех типов частиц (например, эффекты взрывов, дым, частицы).
public interface IParticle {

    // Метод для обновления состояния частицы, например, её положения или анимации.
    // @param delta - время, прошедшее с последнего обновления (используется для анимации).
    void update(float delta);

    // Метод для отрисовки частицы на экране.
    // @param batch - объект для отрисовки текстур в процессе рендеринга.
    void render(SpriteBatch batch);
}
