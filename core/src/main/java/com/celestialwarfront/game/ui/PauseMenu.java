package com.celestialwarfront.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.celestialwarfront.game.GameManager;

public class PauseMenu {
    private boolean isVisible;

    // Текстуры с проверкой на существование
    private Texture bgTexture;
    private Texture continueTexture;
    private Texture restartTexture;
    private Texture exitTexture;

    // Области кнопок
    private Rectangle bgRect;
    private Rectangle continueRect;
    private Rectangle restartRect;
    private Rectangle exitRect;

    public PauseMenu() {
        // Загрузка текстур с обработкой ошибок
        try {
            bgTexture = new Texture(Gdx.files.internal("pause_bg.png"));
            continueTexture = new Texture(Gdx.files.internal("continue_btn.png"));
            restartTexture = new Texture(Gdx.files.internal("restart_btn.png"));
            exitTexture = new Texture(Gdx.files.internal("btn_exit.png"));
        } catch (Exception e) {
            Gdx.app.error("PauseMenu", "Error loading textures", e);
            // Создаем заглушки если текстуры не найдены
            createFallbackTextures();
        }

        // Позиционирование
        float centerX = (1920 - 300) / 2; // Центр по горизонтали
        bgRect = new Rectangle(centerX - 50, 250, 400, 400);
        continueRect = new Rectangle(centerX, 400, 300, 80);
        restartRect = new Rectangle(centerX, 300, 300, 80);
        exitRect = new Rectangle(centerX, 200, 300, 80);
    }

    private void createFallbackTextures() {
        // Создаем простые цветные прямоугольники как заглушки
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        pm.setColor(Color.DARK_GRAY);
        pm.fill();
        bgTexture = new Texture(pm);

        pm.setColor(Color.GREEN);
        pm.fill();
        continueTexture = new Texture(pm);

        pm.setColor(Color.YELLOW);
        pm.fill();
        restartTexture = new Texture(pm);

        pm.setColor(Color.RED);
        pm.fill();
        exitTexture = new Texture(pm);

        pm.dispose();
    }

    public void render(SpriteBatch batch) {
        if (!isVisible) return;

        // Отрисовка фона
        batch.draw(bgTexture, bgRect.x, bgRect.y, bgRect.width, bgRect.height);

        // Отрисовка кнопок
        batch.draw(continueTexture, continueRect.x, continueRect.y, continueRect.width, continueRect.height);
        batch.draw(restartTexture, restartRect.x, restartRect.y, restartRect.width, restartRect.height);
        batch.draw(exitTexture, exitRect.x, exitRect.y, exitRect.width, exitRect.height);
    }

    public int handleInput() {
        if (!isVisible || !Gdx.input.justTouched()) return 0;

        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        GameManager.getInstance().getCamera().unproject(touchPos);

        if (continueRect.contains(touchPos.x, touchPos.y)) {
            return 1; // Продолжить
        }
        else if (restartRect.contains(touchPos.x, touchPos.y)) {
            return 2; // Рестарт
        }
        else if (exitRect.contains(touchPos.x, touchPos.y)) {
            return 3; // Выйти
        }
        return 0;
    }

    public void toggle() {
        isVisible = !isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void dispose() {
        bgTexture.dispose();
        continueTexture.dispose();
        restartTexture.dispose();
        exitTexture.dispose();
    }
}
