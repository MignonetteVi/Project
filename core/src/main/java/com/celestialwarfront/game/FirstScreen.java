package com.celestialwarfront.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private PlayerShip playerShip;
    private List<Bullet> bullets;
    private IBulletFactory bulletFactory;

    private Texture playerTexture;
    private Texture bulletTexture;
    private SpriteBatch batch;


    private boolean wasSpacePressedLastFrame = false;

    @Override
    public void show() {
        // Prepare your screen here.
        playerShip = new PlayerShip(100, 200);
        bullets = new ArrayList<>();
        bulletFactory = new BulletFactory(); // Здесь используется Factory Method

        playerTexture = new Texture(Gdx.files.internal("player.png"));
        bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
        batch = new SpriteBatch();



    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Движение корабля
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D);
        playerShip.move(delta, left, right);

        // Стрельба при нажатии пробела
        boolean isSpacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if (isSpacePressed && !wasSpacePressedLastFrame) {
            Bullet bullet = bulletFactory.createBullet(playerShip.x, playerShip.y);
            bullets.add(bullet);
        }
        wasSpacePressedLastFrame = isSpacePressed;

        // Движение снарядов
        for (Bullet bullet : bullets) {
            bullet.move(delta);
        }

        // Отрисовка
        batch.begin();
        batch.draw(playerTexture, playerShip.x, playerShip.y);
        for (Bullet bullet : bullets) {
            batch.draw(bulletTexture, bullet.x, bullet.y);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        playerTexture.dispose();
        bulletTexture.dispose();
        batch.dispose();
    }
}
