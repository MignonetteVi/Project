package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;

public class MainMenuScreen implements Screen {
    private final MainGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;

    // Текстуры кнопок
    private Texture playButtonTexture;
    private Texture exitButtonTexture;

    // Области кнопок
    private Rectangle playButton;
    private Rectangle exitButton;

    // Фон
    private Texture background;

    public MainMenuScreen(final MainGame game) {
        this.game = game;

        // Настройка камеры (16:9)
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3);
        font.setColor(Color.WHITE);

        // Загрузка текстур
        playButtonTexture = new Texture(Gdx.files.internal("btn_play.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("btn_exit.png"));
        background = new Texture(Gdx.files.internal("menu_bg.png")); // Добавьте свой фон

        // Размеры и позиции кнопок (под размеры ваших текстур)
        float buttonWidth = 400f; // Подправьте под реальные размеры
        float buttonHeight = 150f;
        float centerX = 1920/2 - buttonWidth/2;

        playButton = new Rectangle(centerX, 450, buttonWidth, buttonHeight);
        exitButton = new Rectangle(centerX, 250, buttonWidth, buttonHeight);
    }

    @Override
    public void render(float delta) {
        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Отрисовка фона
        batch.draw(background, 0, 0, 1920, 1080);

        // Отрисовка кнопок
        batch.draw(playButtonTexture,
            playButton.x, playButton.y,
            playButton.width, playButton.height);

        batch.draw(exitButtonTexture,
            exitButton.x, exitButton.y,
            exitButton.width, exitButton.height);

        batch.end();

        // Обработка кликов
        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (playButton.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new FirstScreen());
                dispose();
            }
            else if (exitButton.contains(touchPos.x, touchPos.y)) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = 1920;
        camera.viewportHeight = 1080 * ((float)height/width);
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        playButtonTexture.dispose();
        exitButtonTexture.dispose();
        background.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void show() {}
}
