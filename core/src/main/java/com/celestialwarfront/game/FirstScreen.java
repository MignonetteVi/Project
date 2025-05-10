package com.celestialwarfront.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.celestialwarfront.game.entities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import com.badlogic.gdx.math.Rectangle;
import com.celestialwarfront.game.logic.DefaultGameState;
import com.celestialwarfront.game.logic.Level;
import com.celestialwarfront.game.patterns.*;
import com.celestialwarfront.game.patterns.BlockFactory.BlockType;
import com.celestialwarfront.game.logic.Difficulty;
import com.celestialwarfront.game.collisions.CollisionSystem;
import com.celestialwarfront.game.ui.Hud;
import com.celestialwarfront.game.ui.StateListener;


/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private OrthographicCamera camera;
    private Viewport gameViewport;

    // --- выбор сложности ---
    private Difficulty difficulty;

    // --- коллизия ---
    private CollisionSystem collisionSystem;

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

    // --- для спавна строк блоков ---
    private float blockSpawnTimer;
    private float nextBlockSpawn;    // время до следующей линии

    private int gapIndex;          // текущая свободная колонка
    private int cols;              // число колонок: cols = screenWidth / blockWidth
    private int gapLinesRemaining;

    // базовая скорость прокрутки блоков (устанавливается при выборе сложности)
    private float baseBlockScrollSpeed;
    // на сколько прибавляем к скорости за каждую линию
    private float speedIncrementPerLine = 2f;

    // сколько уже сгенерировано линий
    private int linesSpawned;

    private DefaultGameState gameState;
    private Hud hud;

    private boolean gameOver; // флаг конца игры

    @Override
    public void show() {
        // Prepare your screen here.

        // --- инициализируем камеру и вьюпорт ---
        camera = new OrthographicCamera();
        gameViewport = new FitViewport(1920, 1440, camera);
        gameViewport.apply();

        // --- инициализируем состояние игры и HUD ---
        gameState = new DefaultGameState();
        hud = new Hud();
        gameState.addListener(hud);

        // --- обновим HUD, чтобы он сразу отобразил текущее level/score/hp ---
        hud.onScoreChanged(gameState.getScore());
        hud.onHPChanged(gameState.getHP());
        hud.onLevelChanged(gameState.getLevel());
        hud.onTimeChanged(gameState.getTimeString());


        // --- слушаем HP и при 0 ставим Game Over ---
        gameState.addListener(new StateListener() {
          @Override public void onHPChanged(int newHP) {
              if (newHP <= 0) {
                  // переключаем признак конца игры
                  gameOver = true;
                  // и ставим флаг на показ диалога
                  pendingGameOverDialog = true;
              }
          }
            public void onScoreChanged(int s) {}
            public void onLevelChanged(int lvl) {}
            public void onTimeChanged(String t) {}
        });

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
        difficulty = Difficulty.fromIndex(sel);


        // настройка параметров по выбору
        ILevelProvider provider = new JsonLevelProvider();
        Level level = provider.createLevel(difficulty);

        nextLineGroupCount = level.getNextLineGroupCount();
        blockScrollSpeed   = level.getBlockScrollSpeed();
        minSpawnInterval   = level.getMinSpawnInterval();
        maxSpawnInterval   = level.getMaxSpawnInterval();
        nextMeteorSpawn    = level.getInitialMeteorSpawnInterval();

        baseBlockScrollSpeed = blockScrollSpeed;
        linesSpawned        = 0;

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

        meteors = new ArrayList<>();

        collisionSystem = new CollisionSystem(gameState);


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
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        gameState.updateTimer(delta);

        // показ диалога
        if (pendingGameOverDialog) {
            showGameOverDialog();
            return;
        }

        // Draw your screen here. "delta" is the time since last render in seconds.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- настройка коллизии корабля вне зависимости от текстуры ---
        float blockW = breakableTex.getWidth();

        float origW   = playerTexture.getWidth();
        float origH   = playerTexture.getHeight();

        float scale   = blockW / origW;
        float shipW   = origW * scale;  // blockW
        float shipH   = origH * scale;

        // СБРОС НА R (ФУНКЦИОНАЛ РАЗРАБОТЧИКА)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gameState.resetLevel();
        }

        if (!gameOver) {
            // --- движение корабля ---
            boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.D);
            playerShip.move(delta, left, right);


            // --- коллизия корабля с inset-ом в 20% ---
            float inset = shipW * 0.2f;
            Rectangle shipRect = new Rectangle(
                playerShip.x + inset,
                playerShip.y + inset,
                shipW - inset*2,
                shipH - inset*2
            );

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
            // --- обработка столкновений через CollisionSystem ---
            // 1) пуля - блок и пуля - метеор
            for (Bullet b : bullets) {
                Rectangle r = new Rectangle(b.x, b.y, bulletTexture.getWidth(), bulletTexture.getHeight());
                for (Block block : blocks) {
                    if (r.overlaps(block.getBounds())) {
                        collisionSystem.onCollision(b, block, 1);
                    }
                }
                for (Meteor m : meteors) {
                    if (r.overlaps(m.getBounds())) {
                        collisionSystem.onCollision(b, m, 1);
                    }
                }
            }

            // 2) корабль - блок и корабль - метеор
            for (Block block : blocks) {
                if (shipRect.overlaps(block.getBounds())) {
                    collisionSystem.onCollision(playerShip, block, 0);
                }
            }
            for (Meteor m : meteors) {
                if (shipRect.overlaps(m.getBounds())) {
                    collisionSystem.onCollision(playerShip, m, 0);
                }
            }

            // 3) удаляем все помеченные пули и разрушенные объекты
            collisionSystem.purgeTaggedBullets(bullets);
            blocks.removeIf(Block::isDestroyed);
            meteors.removeIf(Meteor::isDestroyed);

            // --- отрисовка ---
            batch.begin();

            batch.draw(
                playerTexture,
                playerShip.x, playerShip.y,
                shipW, shipH
            );

            hud.draw();

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
        // обновляем игровой вьюпорт
        gameViewport.update(width, height, true);
        // и HUD
        hud.resize(width, height);
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
        // --- сброс мира ---
        playerShip.setPosition(0);
        bullets.clear();
        blocks.clear();
        nextLineGroupCount = (difficulty == Difficulty.HARD ? 3 : 1);
        gapIndex = cols/2;
        gapLinesRemaining = 5;
        linesSpawned = 0;
        blockSpawnTimer = 0f;
        nextBlockSpawn = minSpawnInterval + random.nextFloat()*(maxSpawnInterval-minSpawnInterval);
        blockScrollSpeed = baseBlockScrollSpeed;
        spawnBlockGroup(nextLineGroupCount);
        meteors.clear();
        meteorSpawnTimer = 0f;
        nextMeteorSpawn = 5f;

        // --- сброс состояния игры (HP, score, timer), но не уровня ---
        gameState.resetSession();

        // --- очистка флагов ---
        pendingGameOverDialog = false;
        wasSpacePressedLastFrame = false;
        gameOver = false;
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

        // --- учёт новой линии ---
        linesSpawned++;

        // --- пересчитываем текущую скорость прокрутки ---
        blockScrollSpeed = baseBlockScrollSpeed + linesSpawned * speedIncrementPerLine;

    }

    // --- Спавнит подряд не более двух линий, одна над другой ---
    private void spawnBlockGroup(int lines) {
        float screenH = Gdx.graphics.getHeight();
        float blockH  = breakableTex.getHeight();

        int count = Math.min(lines, 2);

        for (int i = 0; i < count; i++) {
            spawnBlockLineAt(screenH + i * blockH);
        }
    }

    private boolean pendingGameOverDialog = false;

    private void onPlayerDeath() {
        pendingGameOverDialog = true;
    }


}
