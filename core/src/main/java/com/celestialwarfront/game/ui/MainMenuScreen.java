package com.celestialwarfront.game.ui;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.celestialwarfront.game.FirstScreen;
import com.celestialwarfront.game.MainGame;

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

    private Viewport viewport;

    public MainMenuScreen(final MainGame game) {
        this.game = game;

        // Настройка камеры (16:9) и вьюпорта
        camera   = new OrthographicCamera();
        viewport = new FitViewport(1920, 1080, camera);

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3);
        font.setColor(Color.WHITE);

        // Загрузка текстур
        playButtonTexture = new Texture(Gdx.files.internal("btn_play.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("btn_exit.png"));
        background        = new Texture(Gdx.files.internal("menu_bg.png"));

        // Просто создаём "контейнеры" — размеры зададутся в recalcLayout()
        playButton = new Rectangle();
        exitButton = new Rectangle();

        // Сразу расcчитываем макет под текущий viewport
        recalcLayout();
    }

    private void recalcLayout() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        float bw = w * 0.25f;   // 25% ширины
        float bh = h * 0.14f;   // 14% высоты
        float cx = (w - bw)/2f;
        playButton.set(cx, h * 0.42f, bw, bh);
        exitButton.set(cx, h * 0.23f, bw, bh);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(
            background,
            0, 0,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );

        batch.draw(
            playButtonTexture,
            playButton.x, playButton.y,
            playButton.width, playButton.height
        );
        batch.draw(
            exitButtonTexture,
            exitButton.x, exitButton.y,
            exitButton.width, exitButton.height
        );

        batch.end();

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
        viewport.update(width, height, true);
        recalcLayout();
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
