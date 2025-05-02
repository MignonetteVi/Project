package com.celestialwarfront.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.celestialwarfront.game.entities.Block;
import com.celestialwarfront.game.entities.BlockFactory;
import com.celestialwarfront.game.entities.FallingBlock;
import com.celestialwarfront.game.entities.Meteor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import com.badlogic.gdx.math.Rectangle;
import com.celestialwarfront.game.entities.BreakableBlock;
import com.celestialwarfront.game.entities.BlockFactory.BlockType;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {

    // --- выбор сложности ---
    private enum Difficulty { EASY, MEDIUM, HARD }

    private Difficulty difficulty;

    // --- параметры спавна блоков ---
    private int    nextLineGroupCount;   // сколько линий спавнить за раз
    private float  blockScrollSpeed;     // скорость «движения» блоков
    private float  minSpawnInterval;     // минимальное время между спавнами
    private float  maxSpawnInterval;     // максимальное время

    private PlayerShip playerShip;
    private List<Bullet> bullets;
    private IBulletFactory bulletFactory;

    private Texture playerTexture;
    private Texture bulletTexture;
    private SpriteBatch batch;


    private boolean wasSpacePressedLastFrame = false;

    private List<Block> blocks;
    private List<Meteor> meteors;
    private Texture breakableTex, unbreakableTex, fallingTex, meteorTex;

    // --- для случайных спавнов ---
    private Random random;
    private float meteorSpawnTimer;
    private float nextMeteorSpawn; // время до следующего метеора

    private boolean gameOver; // флаг конца игры

    // --- для спавна строк блоков ---
    private float blockSpawnTimer;
    private float nextBlockSpawn;    // время до следующей линии

    private int gapIndex;          // текущая свободная колонка
    private int cols;              // число колонок: cols = screenWidth / blockWidth
    private int gapLinesRemaining;

    @Override
    public void show() {
        // Prepare your screen here.

        // --- диалог выбора уровня ---
        String[] options = {"Легкий", "Средний", "Сложный"};
        int sel = JOptionPane.showOptionDialog(
            null,
            "Выберите уровень сложности:",
            "Сложность",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        difficulty = (sel == 1 ? Difficulty.MEDIUM
            : sel == 2 ? Difficulty.HARD
            : Difficulty.EASY);

        // настройка параметров по выбору
        switch(difficulty) {
            case EASY:
                nextLineGroupCount = 1;
                blockScrollSpeed   =  60f;
                minSpawnInterval   =  8f;
                maxSpawnInterval   = 12f;
                break;
            case MEDIUM:
                nextLineGroupCount = 1;
                blockScrollSpeed   = 100f;
                minSpawnInterval   =  6f;
                maxSpawnInterval   = 10f;
                break;
            case HARD:
                nextLineGroupCount = 3;
                blockScrollSpeed   = 140f;
                minSpawnInterval   =  4f;
                maxSpawnInterval   =  8f;
                break;
        }

        playerShip = new PlayerShip(100, 200);
        bullets = new ArrayList<>();
        bulletFactory = new BulletFactory(); // Здесь используется Factory Method

        playerTexture = new Texture(Gdx.files.internal("player.png"));
        bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
        batch = new SpriteBatch();

        breakableTex   = new Texture(Gdx.files.internal("block_breakable.png"));
        unbreakableTex = new Texture(Gdx.files.internal("block_unbreakable.png"));
        fallingTex     = new Texture(Gdx.files.internal("block_falling.png"));
        meteorTex      = new Texture(Gdx.files.internal("meteor.png"));

        blocks = new ArrayList<>();
        blocks.add(BlockFactory.createBlock(BlockFactory.BlockType.BREAKABLE,
            50, 300, breakableTex, 1));
        blocks.add(BlockFactory.createBlock(BlockFactory.BlockType.UNBREAKABLE,
            150, 300, unbreakableTex));
        blocks.add(BlockFactory.createBlock(BlockFactory.BlockType.FALLING,
            250, 300, fallingTex));

        meteors = new ArrayList<>();
        meteors.add(new Meteor(100, 600, meteorTex, 100f));
        meteors.add(new Meteor(200, 800, meteorTex, 120f));


        // --- вычисляем число колонок ---
        int screenW = Gdx.graphics.getWidth();
        float blockW = breakableTex.getWidth();
        cols = (int)Math.ceil(screenW / blockW);

        // --- ставим стартовый «коридор» по центру ---
        gapIndex = cols / 2;
        // --- коридор живёт первые 5 линий ---
        gapLinesRemaining = 5;

        // --- инициализируем таймеры, флаги, спавним первую группу ---
        random           = new Random();
        meteorSpawnTimer = 0f;
        nextMeteorSpawn  = 5f;
        blockSpawnTimer  = 0f;
        nextBlockSpawn   = minSpawnInterval + random.nextFloat()*(maxSpawnInterval-minSpawnInterval);
        gameOver         = false;

        // --- сразу создаём первую линию/группу ---
        spawnBlockGroup(nextLineGroupCount);

    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!gameOver) {
            // --- движение корабля ---
            boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.D);
            playerShip.move(delta, left, right);

            // --- стрельба при нажатии пробела ---
            boolean isSpacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
            if (isSpacePressed && !wasSpacePressedLastFrame) {
                Bullet bullet = bulletFactory.createBullet(playerShip.x, playerShip.y);
                bullets.add(bullet);
            }
            wasSpacePressedLastFrame = isSpacePressed;

            // --- движение снарядов ---
            for (Bullet bullet : bullets) {
                bullet.move(delta);
            }

            // --- скроллим все блоки вниз, чтобы линии ехали на корабль ---
            float blockScrollSpeed = 100f; // пикс/сек
            for (Block b : blocks) {
                b.move(0, -blockScrollSpeed * delta);
            }


            // --- плюс, дополнительная гравитация для специально падающих блоков ---
            for (Block b : blocks) {
                if (b instanceof FallingBlock) {
                    ((FallingBlock) b).update(delta);
                }
            }


            // --- спавн новой линии блоков время от времени ---
            blockSpawnTimer += delta;
            if (blockSpawnTimer >= nextBlockSpawn) {
                spawnBlockGroup(nextLineGroupCount);// спавним именно столько линий, сколько нужно на этом уровне

                blockSpawnTimer = 0f;
                if (difficulty != Difficulty.HARD) {
                    nextLineGroupCount++;
                }
                nextBlockSpawn = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
            }

            // --- спавн новых метеоров (редко, рандомно) ---
            meteorSpawnTimer += delta;
            if (meteorSpawnTimer >= nextMeteorSpawn) {
                float spawnX = random.nextFloat() * (Gdx.graphics.getWidth() - meteorTex.getWidth() / 2f);
                float speed  = 80f + random.nextFloat() * 120f;
                meteors.add(new Meteor(spawnX, Gdx.graphics.getHeight(), meteorTex, speed));
                meteorSpawnTimer = 0f;
                nextMeteorSpawn  = 3f + random.nextFloat() * 5f;  // 3–8 сек до следующего
            }

            // --- обновление всех метеоров ---
            for (Meteor m : meteors) {
                m.update(delta);
            }


            // --- обработка столкновений пуль с блоками ---
            List<Bullet> bulletsToRemove = new ArrayList<>();
            List<Block> blocksToRemove = new ArrayList<>();

            for (Bullet b : bullets) {
                Rectangle bRect = new Rectangle(
                    b.x, b.y,
                    bulletTexture.getWidth(),
                    bulletTexture.getHeight()
                );
                for (Block block : blocks) {
                    if (bRect.overlaps(block.getBounds())) {
                        block.onHit();
                        bulletsToRemove.add(b);
                        if (block instanceof BreakableBlock
                            && ((BreakableBlock) block).isDestroyed()) {
                            blocksToRemove.add(block);
                        }
                        break;
                    }
                }
            }
            bullets.removeAll(bulletsToRemove);
            blocks.removeAll(blocksToRemove);

            // --- проверка коллизии корабль ↔ метеор ---
            Rectangle shipRect = new Rectangle(
                playerShip.x, playerShip.y,
                playerTexture.getWidth(),
                playerTexture.getHeight()
            );
            for (Meteor m : meteors) {
                if (shipRect.overlaps(m.getBounds())) {
                    gameOver = true;
                    showGameOverDialog();   // !!! вызов диалога
                    break;
                }
            }

            // --- отрисовка ---
            batch.begin();
            batch.draw(playerTexture, playerShip.x, playerShip.y);
            for (Bullet bullet : bullets) {
                batch.draw(bulletTexture, bullet.x, bullet.y);
            }
            for (Block b : blocks) {
                b.render(batch);
            }
            for (Meteor m : meteors) {
                m.render(batch);
            }
            batch.end();
        }

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

    private void showGameOverDialog() {
        int res = JOptionPane.showConfirmDialog(
            null,
            "Game Over!\nPlay again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );
        if (res == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            Gdx.app.exit();
        }
    }

    private void restartGame() {
        // --- сброс позиции корабля ---
        playerShip.setPosition(100);
        // --- очистка пуль ---
        bullets.clear();
        // --- сброс блоков (пример начального состояния) ---
        blocks.clear();
        blocks.add(BlockFactory.createBlock(BlockFactory.BlockType.BREAKABLE,   50, 300, breakableTex, 1));
        blocks.add(BlockFactory.createBlock(BlockFactory.BlockType.UNBREAKABLE, 150, 300, unbreakableTex));
        blocks.add(BlockFactory.createBlock(BlockFactory.BlockType.FALLING,     250, 300, fallingTex));
        // --- сброс метеоров и таймеров ---
        meteors.clear();
        meteorSpawnTimer = 0f;
        nextMeteorSpawn  = 5f;
        gameOver         = false;
    }

    // --- спавнит одну горизонтальную линию блоков сверху на экране ---
    private void spawnBlockLineAt(float y) {
        int screenW = Gdx.graphics.getWidth();
        float blockW = breakableTex.getWidth();
        int cols = (int)Math.ceil(screenW / blockW);

        int unbreakCount = 0;

        for (int i = 0; i < cols; i++) {
            if (gapLinesRemaining > 0 && i == gapIndex) continue;

            float x = i * blockW;
            BlockType type;
            if (unbreakCount < 3 && random.nextFloat() < 0.3f) {
                type = BlockType.UNBREAKABLE;
                unbreakCount++;
            } else {
                type = random.nextFloat() < 0.2f
                    ? BlockType.FALLING
                    : BlockType.BREAKABLE;
            }


            switch (type) {
                case BREAKABLE:
                    blocks.add(BlockFactory.createBlock(type, x, y, breakableTex, 1));
                    break;
                case UNBREAKABLE:
                    blocks.add(BlockFactory.createBlock(type, x, y, unbreakableTex));
                    break;
                case FALLING:
                    blocks.add(BlockFactory.createBlock(type, x, y, fallingTex));
                    break;
            }
        }

        if (gapLinesRemaining > 0) {
            gapLinesRemaining--;
        }

        int shift = random.nextInt(3) - 1;
        gapIndex = MathUtils.clamp(gapIndex + shift, 0, cols - 1);
    }

    // ---  Спавнит подряд не более двух линий, одна над другой ---
    private void spawnBlockGroup(int lines) {
        float screenH = Gdx.graphics.getHeight();
        float blockH  = breakableTex.getHeight();

        int count = Math.min(lines, 2);

        for (int i = 0; i < count; i++) {
            spawnBlockLineAt(screenH + i * blockH);
        }
    }



}
