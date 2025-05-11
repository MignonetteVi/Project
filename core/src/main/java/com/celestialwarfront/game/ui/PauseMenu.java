package com.celestialwarfront.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.celestialwarfront.game.GameManager;

public class PauseMenu {
    private Texture pauseBgTexture;
    private Texture pauseContinueTexture;
    private Texture pauseRestartTexture;
    private Texture pauseExitTexture;
    private Texture pauseTexture;

    private boolean isVisible;
    private Rectangle pauseBackground;
    private Rectangle resumeButton;
    private Rectangle mainMenuButton;
    private Rectangle exitButton;

    private Viewport viewport;

    public PauseMenu() {
        pauseBgTexture = new Texture(Gdx.files.internal("pause_bg.png"));
        pauseContinueTexture = new Texture(Gdx.files.internal("continue_btn.png"));
        pauseRestartTexture = new Texture(Gdx.files.internal("restart_btn.png"));
        pauseExitTexture = new Texture(Gdx.files.internal("btn_exit.png"));

        // Размеры кнопок
        float buttonWidth = 500f;
        float buttonHeight = 100f;
        // Центральное позиционирование (для 1920x1080)
        float centerX = (1920 - buttonWidth) / 2;
        float centerY = 1080 / 2;

        // Позиции кнопок (по вертикали)
        resumeButton = new Rectangle(centerX, centerY + 100, buttonWidth, buttonHeight);
        mainMenuButton = new Rectangle(centerX, centerY - 50, buttonWidth, buttonHeight);
        exitButton = new Rectangle(centerX, centerY - 200, buttonWidth, buttonHeight);
        pauseBackground = new Rectangle(0, 0, 1920, 1080);

        // Создаем простую текстуру для фона меню паузы
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        pauseTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
    }

    public void render(SpriteBatch batch) {
        if (!isVisible) return;

        // Переключаемся на меню-камеру
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // Отрисовка меню
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pauseTexture, 0, 0, 1920, 1080);
        batch.setColor(Color.WHITE);

        // Фон меню
        batch.draw(pauseBgTexture, pauseBackground.x, pauseBackground.y,
            pauseBackground.width, pauseBackground.height);

        // Отрисовка кнопок
        drawButton(batch, resumeButton, pauseContinueTexture);
        drawButton(batch, mainMenuButton, pauseRestartTexture);
        drawButton(batch, exitButton, pauseExitTexture);
    }

    private void drawButton(SpriteBatch batch, Rectangle rect, Texture texture) {
        // Эффект при наведении
        if (isMouseOver(rect)) {
            batch.setColor(1.2f, 1.2f, 1.2f, 1); // Подсветка
        } else {
            batch.setColor(Color.WHITE);
        }

        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        batch.setColor(Color.WHITE);
    }

    private boolean isMouseOver(Rectangle rect) {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);
        return rect.contains(touchPos.x, touchPos.y);
    }

    public void handleInput(GameManager gameManager) {
        if (!isVisible || !Gdx.input.justTouched()) return;

        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);

        if (resumeButton.contains(touchPos.x, touchPos.y)) {
            isVisible = false;
            Gdx.app.log("INPUT", "Continue pressed");
        }
        else if (mainMenuButton.contains(touchPos.x, touchPos.y)) {
            gameManager.restartGame();
            isVisible = false;
            Gdx.app.log("INPUT", "Restart pressed");
        }
        else if (exitButton.contains(touchPos.x, touchPos.y)) {
            Gdx.app.log("INPUT", "Exit pressed");
            Gdx.app.exit();
        }
    }

    public void dispose() {
        pauseBgTexture.dispose();
        pauseContinueTexture.dispose();
        pauseRestartTexture.dispose();
        pauseExitTexture.dispose();
        pauseTexture.dispose();
    }
}
