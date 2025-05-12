package com.celestialwarfront.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.celestialwarfront.game.entities.*;
import com.celestialwarfront.game.logic.DefaultGameState;
import com.celestialwarfront.game.logic.Level;
import com.celestialwarfront.game.patterns.*;
import com.celestialwarfront.game.patterns.BlockFactory.BlockType;
import com.celestialwarfront.game.logic.Difficulty;
import com.celestialwarfront.game.collisions.CollisionSystem;
import com.celestialwarfront.game.ui.GameOverMenu;
import com.celestialwarfront.game.ui.Hud;
import com.celestialwarfront.game.ui.PauseMenu;
import com.celestialwarfront.game.ui.StateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

public class GameManager {
    private static GameManager instance;

    // Game components
    private OrthographicCamera camera;
    private Viewport gameViewport;
    private Viewport menuViewport;
    private Difficulty difficulty;
    private CollisionSystem collisionSystem;
    private PlayerShip playerShip;
    private List<Bullet> bullets;
    private IBulletFactory bulletFactory;
    private Texture playerTexture;
    private Texture bulletTexture;
    private SpriteBatch batch;
    private List<Block> blocks;
    private List<Meteor> meteors;
    private List<AmmoBox> ammoBoxes;
    private List<HealthPack> healthPacks;
    private Texture breakableTex, unbreakableTex, fallingTex, meteorTex, ammoBoxTex, healthPackTex;
    private Random random;
    private DefaultGameState gameState;
    private Hud hud;
    private boolean gameOver;
    private boolean pendingGameOverDialog;
    private boolean wasSpacePressedLastFrame;
    private PauseMenu pauseMenu;
    private GameOverMenu gameOverMenu;

    // Game parameters
    private int nextLineGroupCount;
    private float blockScrollSpeed;
    private float minSpawnInterval;
    private float maxSpawnInterval;
    private float baseBlockScrollSpeed;
    private float speedIncrementPerLine = 2f;
    private int linesSpawned;
    private int gapIndex;
    private int cols;
    private int gapLinesRemaining;
    private float meteorSpawnTimer, nextMeteorSpawn;
    private float ammoSpawnTimer, nextAmmoSpawn;
    private float healthSpawnTimer, nextHealthSpawn;
    private float blockSpawnTimer, nextBlockSpawn;

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    private GameManager() {
        // Initialize graphics first
        camera = new OrthographicCamera();
        gameViewport = new FitViewport(1920, 1440, camera);
        menuViewport = new FitViewport(1920, 1080, new OrthographicCamera());

        // Initialize menus
        pauseMenu = new PauseMenu();
        pauseMenu.setViewport(menuViewport);
        gameOverMenu = new GameOverMenu();
        gameOverMenu.setViewport(menuViewport);

        // Initialize game systems
        gameState = new DefaultGameState();
        hud = new Hud();
        gameState.addListener(hud);

        bullets = new ArrayList<>();
        blocks = new ArrayList<>();
        meteors = new ArrayList<>();
        ammoBoxes = new ArrayList<>();
        healthPacks = new ArrayList<>();

        random = new Random();

        gameState.addListener(new StateListener() {
            @Override
            public void onHPChanged(int newHP) {
                if (newHP <= 0) {
                    gameOver = true;
                    pendingGameOverDialog = true;
                }
            }
            public void onScoreChanged(int s) {}
            public void onLevelChanged(int lvl) {}
            public void onTimeChanged(String t) {}
            public void onAmmoChanged(int ammo) {}
        });
    }

    public void startNewGame() {
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

        ILevelProvider provider = new JsonLevelProvider();
        Level level = provider.createLevel(difficulty);

        nextLineGroupCount = level.getNextLineGroupCount();
        blockScrollSpeed = level.getBlockScrollSpeed();
        minSpawnInterval = level.getMinSpawnInterval();
        maxSpawnInterval = level.getMaxSpawnInterval();
        nextMeteorSpawn = level.getInitialMeteorSpawnInterval();

        baseBlockScrollSpeed = blockScrollSpeed;
        linesSpawned = 0;

        playerShip = new PlayerShip(100, 200);
        bulletFactory = new BulletFactory();

        playerTexture = new Texture(Gdx.files.internal("player.png"));
        bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
        batch = new SpriteBatch();

        breakableTex = new Texture(Gdx.files.internal("block_breakable.png"));
        unbreakableTex = new Texture(Gdx.files.internal("block_unbreakable.png"));
        fallingTex = new Texture(Gdx.files.internal("block_falling.png"));
        meteorTex = new Texture(Gdx.files.internal("meteor.png"));
        healthPackTex = new Texture(Gdx.files.internal("health_pack.png"));
        ammoBoxTex = new Texture(Gdx.files.internal("ammo_box.png"));

        collisionSystem = new CollisionSystem(gameState);

        int screenW = Gdx.graphics.getWidth();
        float blockW = breakableTex.getWidth();
        cols = (int)Math.ceil(screenW / blockW);

        gapIndex = cols / 2;
        gapLinesRemaining = 5;

        meteorSpawnTimer = 0f;
        nextMeteorSpawn = 5f;
        blockSpawnTimer = 0f;
        nextBlockSpawn = minSpawnInterval + random.nextFloat()*(maxSpawnInterval-minSpawnInterval);
        ammoSpawnTimer = 0f;
        nextAmmoSpawn = 15f + random.nextFloat()*10f;
        healthSpawnTimer = 0f;
        nextHealthSpawn = 20f + random.nextFloat() * 20f;
        gameOver = false;
        pendingGameOverDialog = false;

        spawnBlockGroup(nextLineGroupCount);
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pauseMenu.toggleVisibility();
        }

        if (pauseMenu.isVisible()) {
            pauseMenu.handleInput(this);
            return;
        }

        if (gameOver) {
            if (pendingGameOverDialog) {
                gameOverMenu.setVisible(true);
                pendingGameOverDialog = false;
            }
            gameOverMenu.handleInput(this);
            return;
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        gameState.updateTimer(delta);
        updateGameState(delta);
    }

    private void updateGameState(float delta) {
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D);
        playerShip.move(delta, left, right);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gameState.resetLevel();
        }

        float blockW = breakableTex.getWidth();
        float origW = playerTexture.getWidth();
        float origH = playerTexture.getHeight();
        float scale = blockW / origW;
        float shipW = origW * scale;
        float shipH = origH * scale;
        float inset = shipW * 0.2f;
        Rectangle shipRect = new Rectangle(
            playerShip.x + inset,
            playerShip.y + inset,
            shipW - inset*2,
            shipH - inset*2
        );

        boolean isSpacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if (isSpacePressed && !wasSpacePressedLastFrame) {
            if (gameState.getAmmo() > 0) {
                gameState.changeAmmo(-1);
                bullets.add(bulletFactory.createBullet(playerShip.x, playerShip.y));
            } else {
                hud.flashAmmo();
            }
        }
        wasSpacePressedLastFrame = isSpacePressed;

        for (Bullet bullet : bullets) {
            bullet.move(delta);
        }

        for (Block b : blocks) {
            b.move(0, -blockScrollSpeed * delta);
            if (b instanceof FallingBlock) {
                ((FallingBlock) b).update(delta);
            }
        }

        blockSpawnTimer += delta;
        if (blockSpawnTimer >= nextBlockSpawn) {
            spawnBlockGroup(nextLineGroupCount);
            blockSpawnTimer = 0f;
            if (difficulty != Difficulty.HARD) {
                nextLineGroupCount++;
            }
            nextBlockSpawn = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
        }

        meteorSpawnTimer += delta;
        if (meteorSpawnTimer >= nextMeteorSpawn) {
            float spawnX = random.nextFloat() * (Gdx.graphics.getWidth() - meteorTex.getWidth() / 2f);
            float speed = 80f + random.nextFloat() * 120f;
            meteors.add(new Meteor(spawnX, Gdx.graphics.getHeight(), meteorTex, speed));
            meteorSpawnTimer = 0f;
            nextMeteorSpawn = 3f + random.nextFloat() * 5f;
        }

        for (Meteor m : meteors) {
            m.update(delta);
        }

        ammoSpawnTimer += delta;
        if (ammoSpawnTimer >= nextAmmoSpawn) {
            float x = random.nextFloat() * (Gdx.graphics.getWidth() - ammoBoxTex.getWidth());
            ammoBoxes.add(new AmmoBox(x, Gdx.graphics.getHeight(), ammoBoxTex, 100f));
            ammoSpawnTimer = 0f;
            nextAmmoSpawn = 15f + random.nextFloat()*10f;
        }
        for (AmmoBox box : ammoBoxes) box.update(delta);

        healthSpawnTimer += delta;
        if (healthSpawnTimer >= nextHealthSpawn) {
            float x = random.nextFloat() * (Gdx.graphics.getWidth() - healthPackTex.getWidth());
            healthPacks.add(new HealthPack(x, Gdx.graphics.getHeight(), healthPackTex, 100f));
            healthSpawnTimer = 0f;
            nextHealthSpawn = 20f + random.nextFloat() * 20f;
        }
        for (HealthPack hp : healthPacks) {
            hp.update(delta);
        }

        handleCollisions(shipRect);

        collisionSystem.purgeTaggedBullets(bullets);
        blocks.removeIf(Block::isDestroyed);
        meteors.removeIf(Meteor::isDestroyed);
        ammoBoxes.removeIf(b -> b.isPicked() || b.y + ammoBoxTex.getHeight() < 0);
    }

    private void handleCollisions(Rectangle shipRect) {
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

        for (AmmoBox box : ammoBoxes) {
            if (!box.isPicked() && shipRect.overlaps(box.getBounds())) {
                box.pick();
                gameState.changeAmmo(+5);
            }
        }

        for (HealthPack hp : healthPacks) {
            if (!hp.isPicked() && shipRect.overlaps(hp.getBounds())) {
                hp.pick();
                gameState.changeHP(+30);
            }
        }
    }

    public void render() {
        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Отрисовка игрового мира (только если игра не завершена)
        if (!gameOver) {
            // Рассчет размеров корабля
            float blockW = breakableTex.getWidth();
            float origW = playerTexture.getWidth();
            float origH = playerTexture.getHeight();
            float scale = blockW / origW;
            float shipW = origW * scale;
            float shipH = origH * scale;

            // Отрисовка игровых объектов
            batch.draw(playerTexture, playerShip.x, playerShip.y, shipW, shipH);

            // Отрисовка пуль
            for (Bullet bullet : bullets) {
                batch.draw(bulletTexture, bullet.x, bullet.y);
            }

            // Отрисовка блоков
            for (Block b : blocks) {
                b.render(batch);
            }

            // Отрисовка метеоров
            for (Meteor m : meteors) {
                m.render(batch);
            }

            // Отрисовка ящиков с боеприпасами
            for (AmmoBox box : ammoBoxes) {
                box.render(batch);
            }

            // Отрисовка аптечек
            for (HealthPack hp : healthPacks) {
                hp.render(batch);
            }
        }

        // Отрисовка HUD (всегда поверх игрового мира)
        hud.draw(batch);

        // Отрисовка меню паузы (если активно)
        if (pauseMenu.isVisible()) {
            pauseMenu.render(batch);
        }

        // Отрисовка меню Game Over (если активно)
        if (gameOverMenu.isVisible()) {
            // Важно: переключаем проекционную матрицу для меню
            batch.end();
            batch.setProjectionMatrix(menuViewport.getCamera().combined);
            batch.begin();

            gameOverMenu.render(batch);

            // Возвращаем проекцию для игрового мира
            batch.end();
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
        }

        batch.end();
    }

    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        menuViewport.update(width, height, true);
        hud.resize(width, height);
    }

    public void dispose() {
        playerTexture.dispose();
        bulletTexture.dispose();
        batch.dispose();
        breakableTex.dispose();
        unbreakableTex.dispose();
        fallingTex.dispose();
        meteorTex.dispose();
        ammoBoxTex.dispose();
        healthPackTex.dispose();
        pauseMenu.dispose();
        gameOverMenu.dispose();
    }

    public void restartGame() {
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

        gameState.resetSession();
        pendingGameOverDialog = false;
        wasSpacePressedLastFrame = false;
        gameOver = false;
        gameOverMenu.setVisible(false);
    }

    private void spawnBlockLineAt(float y) {
        int screenW = Gdx.graphics.getWidth();
        float blockW = breakableTex.getWidth();
        int cols = (int)Math.ceil(screenW / blockW);

        int unbreakCount = 0;

        for (int i = 0; i < cols; i++   ) {
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

        linesSpawned++;
        blockScrollSpeed = baseBlockScrollSpeed + linesSpawned * speedIncrementPerLine;
    }

    private void spawnBlockGroup(int lines) {
        float screenH = Gdx.graphics.getHeight();
        float blockH = breakableTex.getHeight();

        int count = Math.min(lines, 2);

        for (int i = 0; i < count; i++) {
            spawnBlockLineAt(screenH + i * blockH);
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Hud getHud() {
        return hud;
    }
}
