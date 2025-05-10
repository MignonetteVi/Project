package com.celestialwarfront.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.celestialwarfront.game.entities.*;
import com.celestialwarfront.game.logic.DefaultGameState;
import com.celestialwarfront.game.logic.Level;
import com.celestialwarfront.game.patterns.*;
import com.celestialwarfront.game.patterns.BlockFactory.BlockType;
import com.celestialwarfront.game.logic.Difficulty;
import com.celestialwarfront.game.collisions.CollisionSystem;
import com.celestialwarfront.game.ui.Hud;
import com.celestialwarfront.game.ui.StateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

public class GameManager {
    private Texture pauseBgTexture;
    private Texture pauseContinueTexture;
    private Texture pauseRestartTexture;
    private Texture pauseExitTexture;
    // Pause menu
    private boolean isPaused;
    private Rectangle pauseBackground;
    private Rectangle resumeButton;
    private Rectangle mainMenuButton;
    private Rectangle exitButton;
    private Texture pauseTexture; // Для фона меню паузы


    // Singleton instance
    private static GameManager instance;

    // Facade components
    private OrthographicCamera camera;
    private Viewport gameViewport;
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

    // Private constructor for Singleton
    private GameManager() {
        initialize();
    }

    // Singleton access method
    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // Facade initialization method
    private void initialize() {
        pauseBgTexture = new Texture(Gdx.files.internal("pause_bg.png"));
        pauseContinueTexture = new Texture(Gdx.files.internal("continue.png"));
        pauseRestartTexture = new Texture(Gdx.files.internal("restart_btn.png"));
        pauseExitTexture = new Texture(Gdx.files.internal("btn_exit.png"));
        // Initialize pause menu
        isPaused = false;
        float buttonWidth = 400f;
        float buttonHeight = 150f;
        float centerX = 1920/2 - buttonWidth/2;
        resumeButton = new Rectangle(centerX, 450, buttonWidth, buttonHeight);
        mainMenuButton = new Rectangle(centerX, 300, buttonWidth, buttonHeight);
        exitButton = new Rectangle(centerX, 150, buttonWidth, buttonHeight);
        /*float menuWidth = 500;
        float menuHeight = 400;
        float centerX = (1920 - menuWidth) / 2;
        float centerY = (1080 - menuHeight) / 2;*/



// Создаем простую текстуру для фона меню паузы
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        pauseTexture = new Texture(pixmap);
        pixmap.dispose();
        // Initialize camera and viewport
        camera = new OrthographicCamera();
        gameViewport = new FitViewport(1920, 1440, camera);
        gameViewport.apply();

        // Initialize game state and HUD
        gameState = new DefaultGameState();
        hud = new Hud();
        gameState.addListener(hud);

        hud.onScoreChanged(gameState.getScore());
        hud.onHPChanged(gameState.getHP());
        hud.onLevelChanged(gameState.getLevel());
        hud.onTimeChanged(gameState.getTimeString());

        // Setup game state listener
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

        // Initialize collections
        bullets = new ArrayList<>();
        blocks = new ArrayList<>();
        meteors = new ArrayList<>();
        ammoBoxes = new ArrayList<>();
        healthPacks = new ArrayList<>();

        // Initialize random
        random = new Random();
    }

    // Facade methods for game control
    public void startNewGame() {
        // Show difficulty selection dialog
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

        // Setup level parameters
        ILevelProvider provider = new JsonLevelProvider();
        Level level = provider.createLevel(difficulty);

        nextLineGroupCount = level.getNextLineGroupCount();
        blockScrollSpeed = level.getBlockScrollSpeed();
        minSpawnInterval = level.getMinSpawnInterval();
        maxSpawnInterval = level.getMaxSpawnInterval();
        nextMeteorSpawn = level.getInitialMeteorSpawnInterval();

        baseBlockScrollSpeed = blockScrollSpeed;
        linesSpawned = 0;

        // Initialize player and factories
        playerShip = new PlayerShip(100, 200);
        bulletFactory = new BulletFactory();

        // Load textures
        playerTexture = new Texture(Gdx.files.internal("player.png"));
        bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
        batch = new SpriteBatch();

        breakableTex = new Texture(Gdx.files.internal("block_breakable.png"));
        unbreakableTex = new Texture(Gdx.files.internal("block_unbreakable.png"));
        fallingTex = new Texture(Gdx.files.internal("block_falling.png"));
        meteorTex = new Texture(Gdx.files.internal("meteor.png"));
        healthPackTex = new Texture(Gdx.files.internal("health_pack.png"));
        ammoBoxTex = new Texture(Gdx.files.internal("ammo_box.png"));

        // Initialize collision system
        collisionSystem = new CollisionSystem(gameState);

        // Calculate columns
        int screenW = Gdx.graphics.getWidth();
        float blockW = breakableTex.getWidth();
        cols = (int)Math.ceil(screenW / blockW);

        // Set initial gap
        gapIndex = cols / 2;
        gapLinesRemaining = 5;

        // Initialize timers
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

        // Spawn first block group
        spawnBlockGroup(nextLineGroupCount);
    }

    public void update(float delta) {
        // Pause handling
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        if (isPaused) return; // Не обновляем игру на паузе
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        gameState.updateTimer(delta);

        if (pendingGameOverDialog) {
            showGameOverDialog();
            return;
        }

        if (!gameOver) {
            updateGameState(delta);
        }
    }

    private void updateGameState(float delta) {
        // Player movement
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D);
        playerShip.move(delta, left, right);

        // Developer reset (R key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gameState.resetLevel();
        }

        // Ship collision rectangle
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

        // Shooting
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

        // Update bullets
        for (Bullet bullet : bullets) {
            bullet.move(delta);
        }

        // Update blocks
        for (Block b : blocks) {
            b.move(0, -blockScrollSpeed * delta);
            if (b instanceof FallingBlock) {
                ((FallingBlock) b).update(delta);
            }
        }

        // Spawn new blocks
        blockSpawnTimer += delta;
        if (blockSpawnTimer >= nextBlockSpawn) {
            spawnBlockGroup(nextLineGroupCount);
            blockSpawnTimer = 0f;
            if (difficulty != Difficulty.HARD) {
                nextLineGroupCount++;
            }
            nextBlockSpawn = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
        }

        // Spawn meteors
        meteorSpawnTimer += delta;
        if (meteorSpawnTimer >= nextMeteorSpawn) {
            float spawnX = random.nextFloat() * (Gdx.graphics.getWidth() - meteorTex.getWidth() / 2f);
            float speed = 80f + random.nextFloat() * 120f;
            meteors.add(new Meteor(spawnX, Gdx.graphics.getHeight(), meteorTex, speed));
            meteorSpawnTimer = 0f;
            nextMeteorSpawn = 3f + random.nextFloat() * 5f;
        }

        // Update meteors
        for (Meteor m : meteors) {
            m.update(delta);
        }

        // Spawn and update ammo boxes
        ammoSpawnTimer += delta;
        if (ammoSpawnTimer >= nextAmmoSpawn) {
            float x = random.nextFloat() * (Gdx.graphics.getWidth() - ammoBoxTex.getWidth());
            ammoBoxes.add(new AmmoBox(x, Gdx.graphics.getHeight(), ammoBoxTex, 100f));
            ammoSpawnTimer = 0f;
            nextAmmoSpawn = 15f + random.nextFloat()*10f;
        }
        for (AmmoBox box : ammoBoxes) box.update(delta);

        // Spawn and update health packs
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

        // Handle collisions
        handleCollisions(shipRect);

        // Clean up destroyed objects
        collisionSystem.purgeTaggedBullets(bullets);
        blocks.removeIf(Block::isDestroyed);
        meteors.removeIf(Meteor::isDestroyed);
        ammoBoxes.removeIf(b -> b.isPicked() || b.y + ammoBoxTex.getHeight() < 0);
    }

    private void handleCollisions(Rectangle shipRect) {
        // Bullet collisions
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

        // Ship collisions
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

        // Ammo box collisions
        for (AmmoBox box : ammoBoxes) {
            if (!box.isPicked() && shipRect.overlaps(box.getBounds())) {
                box.pick();
                gameState.changeAmmo(+5);
            }
        }

        // Health pack collisions
        for (HealthPack hp : healthPacks) {
            if (!hp.isPicked() && shipRect.overlaps(hp.getBounds())) {
                hp.pick();
                gameState.changeHP(+30);
            }
        }
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Calculate ship dimensions
        float blockW = breakableTex.getWidth();
        float origW = playerTexture.getWidth();
        float origH = playerTexture.getHeight();
        float scale = blockW / origW;
        float shipW = origW * scale;
        float shipH = origH * scale;

        batch.begin();

        // Draw player
        batch.draw(
            playerTexture,
            playerShip.x, playerShip.y,
            shipW, shipH
        );

        // Draw HUD
        hud.draw();

        // Draw bullets
        for (Bullet bullet : bullets) {
            batch.draw(bulletTexture, bullet.x, bullet.y);
        }

        // Draw blocks
        for (Block b : blocks) {
            b.render(batch);
        }

        // Draw meteors
        for (Meteor m : meteors) {
            m.render(batch);
        }

        // Draw ammo boxes
        for (AmmoBox box : ammoBoxes) {
            box.render(batch);
        }

        // Draw health packs
        for (HealthPack hp : healthPacks) {
            hp.render(batch);
        }

        // Draw pause menu if game is paused
        if (isPaused) {
            // Полупрозрачный фон
            batch.setColor(1, 1, 1, 0.5f);
            batch.draw(pauseTexture, 0, 0, 1920, 1080);
            batch.setColor(Color.WHITE);

            // Фон меню
            batch.draw(pauseBgTexture, pauseBackground.x, pauseBackground.y,
                pauseBackground.width, pauseBackground.height);

            // Отрисовка кнопок
            drawPauseMenuButton(batch, resumeButton, pauseContinueTexture);
            drawPauseMenuButton(batch, mainMenuButton, pauseRestartTexture);
            drawPauseMenuButton(batch, exitButton, pauseExitTexture);
        }

        batch.end();
    }

    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
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
        pauseBgTexture.dispose();
        pauseContinueTexture.dispose();
        pauseRestartTexture.dispose();
        pauseExitTexture.dispose();
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

    public void restartGame() {
        // Reset world
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

        // Reset game state
        gameState.resetSession();

        // Reset flags
        pendingGameOverDialog = false;
        wasSpacePressedLastFrame = false;
        gameOver = false;
    }

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

    private void drawPauseMenuButton(SpriteBatch batch, Rectangle rect, Texture texture) {
        boolean isHovered = isMouseOver(rect);
        if (isHovered) {
            batch.setColor(1, 1, 1, 0.8f);
        }
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        batch.setColor(Color.WHITE);
    }

    private boolean isMouseOver(Rectangle rect) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        return rect.contains(mousePos.x, mousePos.y);
    }

    private void handlePauseMenuInput() {
        if (!isPaused) return;

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (resumeButton.contains(touchPos.x, touchPos.y)) {
                isPaused = false;
            }
            else if (mainMenuButton.contains(touchPos.x, touchPos.y)) {
                isPaused = false;
                // Здесь должен быть переход в главное меню
                // Например: game.setScreen(new MainMenuScreen(game));
            }
            else if (exitButton.contains(touchPos.x, touchPos.y)) {
                Gdx.app.exit();
            }
        }
    }
    public OrthographicCamera getCamera() {
        return camera;
    }
    public Hud getHud() {
        return hud;
    }
    public boolean isPaused() {
        return isPaused;
    }
}
