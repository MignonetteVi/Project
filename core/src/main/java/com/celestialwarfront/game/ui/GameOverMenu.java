package com.celestialwarfront.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.celestialwarfront.game.GameManager;

public class GameOverMenu {
    private Texture backgroundTexture;
    private Texture restartButtonTexture;
    private Texture exitButtonTexture;

    private boolean isVisible;
    private final Rectangle background;
    private final Rectangle restartButton;
    private final Rectangle exitButton;

    private Viewport viewport;
    private final Vector3 touchPos = new Vector3();

    public GameOverMenu() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("gameover_bg.png"));
            restartButtonTexture = new Texture(Gdx.files.internal("restart_btn.png"));
            exitButtonTexture = new Texture(Gdx.files.internal("btn_exit.png"));
        } catch (Exception e) {
            Gdx.app.error("GameOverMenu", "Failed to load textures", e);
            throw new RuntimeException("Texture loading failed", e);
        }

        background = new Rectangle();
        restartButton = new Rectangle();
        exitButton = new Rectangle();
    }

    private void recalcLayout() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        // фон на весь экран
        background.set(0, 0, w, h);
        // размер кнопки — 40% ширины, 10% высоты
        float bw = w * 0.4f;
        float bh = h * 0.1f;
        float cx = (w - bw) / 2f;
        restartButton.set(cx, h * 0.46f, bw, bh);
        exitButton   .set(cx, h * 0.32f, bw, bh);
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
        recalcLayout();
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void render(SpriteBatch batch) {
        if (!isVisible) return;

        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.draw(
            backgroundTexture,
            background.x,
            background.y,
            background.width,
            background.height
        );
        batch.setColor(Color.WHITE);

        drawButton(batch, restartButton, restartButtonTexture);
        drawButton(batch, exitButton, exitButtonTexture);
    }


    private void drawButton(SpriteBatch batch, Rectangle rect, Texture texture) {
        if (isMouseOver(rect)) {
            batch.setColor(1.2f, 1.2f, 1.2f, 1);
        } else {
            batch.setColor(Color.WHITE);
        }
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        batch.setColor(Color.WHITE);
    }

    private boolean isMouseOver(Rectangle rect) {
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);
        return rect.contains(touchPos.x, touchPos.y);
    }

    public void handleInput(GameManager gameManager) {
        if (!isVisible || !Gdx.input.justTouched() || viewport == null) return;

        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);

        if (restartButton.contains(touchPos.x, touchPos.y)) {
            gameManager.restartGame();
            isVisible = false;
        }
        else if (exitButton.contains(touchPos.x, touchPos.y)) {
            Gdx.app.exit();
        }
    }

    public void dispose() {
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (restartButtonTexture != null) restartButtonTexture.dispose();
        if (exitButtonTexture != null) exitButtonTexture.dispose();
    }
}
