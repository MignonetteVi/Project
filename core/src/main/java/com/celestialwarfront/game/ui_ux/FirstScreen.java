package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;


public class FirstScreen implements Screen {
    private GameManager gameManager;// Ссылка на менеджер игры, который управляет всей логикой игры

    public FirstScreen() {
        // Получаем единственный экземпляр GameManager (паттерн Singleton)
        gameManager = GameManager.getInstance();
    }

    @Override
    public void show() {
        //  Когда экран показывается, инициализируем новую игру через GameManager
        gameManager.startNewGame();
    }

    @Override
    public void render(float delta) {
        // Очищаем экран перед отрисовкой нового кадра

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем игровую логику через GameManager
        gameManager.update(delta);

        // Отрисовываем игру через GameManager (это будет отображать все игровые объекты и интерфейс)
        gameManager.render();
    }

    @Override
    public void resize(int width, int height) {
        // Когда окно изменяет размер, передаем новые размеры в GameManager
        gameManager.resize(width, height);
    }

    @Override
    public void pause() {
        // Если игра приостановлена, можно выполнить дополнительные действия (например, сохранить состояние игры)
    }

    @Override
    public void resume() {
        // Если игра возобновляется, выполняем восстановление необходимых данных или состояния
    }

    @Override
    public void hide() {
        // Когда экран скрывается, выполняем очистку ресурсов, если нужно
    }

    @Override
    public void dispose() {
        // Освобождаем ресурсы (например, текстуры, шрифты, игровые объекты)
        gameManager.dispose();
    }
}
