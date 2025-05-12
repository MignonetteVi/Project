package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;


public class FirstScreen implements Screen {
    private GameManager gameManager;

    public FirstScreen() {
        // Получаем единственный экземпляр GameManager
        gameManager = GameManager.getInstance();
    }

    @Override
    public void show() {
        // Инициализируем новую игру через GameManager
        gameManager.startNewGame();
    }

    @Override
    public void render(float delta) {
        // Очищаем экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем игровую логику через GameManager
        gameManager.update(delta);

        // Отрисовываем игру через GameManager
        gameManager.render();
    }

    @Override
    public void resize(int width, int height) {
        // Передаем изменение размера в GameManager
        gameManager.resize(width, height);
    }

    @Override
    public void pause() {
        // Пауза игры (если нужно)
    }

    @Override
    public void resume() {
        // Возобновление игры (если нужно)
    }

    @Override
    public void hide() {
        // Действия при скрытии экрана
    }

    @Override
    public void dispose() {
        // Освобождаем ресурсы через GameManager
        gameManager.dispose();
    }
}
