    package com.celestialwarfront.game;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.Input;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.math.MathUtils;
    import com.badlogic.gdx.math.Rectangle;
    import com.badlogic.gdx.utils.Timer;
    import com.celestialwarfront.game.entities.*;
    import com.celestialwarfront.game.logic.Difficulty;
    import com.celestialwarfront.game.logic.Level;
    import com.celestialwarfront.game.patterns.*;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    public class GameManager {
        // Game state
        private boolean gameOver;

        // Difficulty and level settings
        private Difficulty difficulty;
        private int nextLineGroupCount;
        private float blockScrollSpeed;
        private float minSpawnInterval;
        private float maxSpawnInterval;
        private float baseBlockScrollSpeed;
        private float speedIncrementPerLine = 2f;

        // Entities
        private PlayerShip playerShip;
        private List<Bullet> bullets;
        private IBulletFactory bulletFactory;
        private List<Block> blocks;
        private List<Meteor> meteors;

        // Textures
        private Texture playerTexture;
        private Texture bulletTexture;
        private Texture breakableTex;
        private Texture unbreakableTex;
        private Texture fallingTex;
        private Texture meteorTex;

        // Spawn logic
        private Random random;
        private float meteorSpawnTimer;
        private float nextMeteorSpawn;
        private float blockSpawnTimer;
        private float nextBlockSpawn;

        // Block spawning
        private int gapIndex;
        private int cols;
        private int gapLinesRemaining;
        private int linesSpawned;

        // Input
        private boolean wasSpacePressedLastFrame = false;

        public GameManager() {
            // Initialize lists
            bullets = new ArrayList<>();
            blocks = new ArrayList<>();
            meteors = new ArrayList<>();
            random = new Random();

            // Initialize bullet factory
            bulletFactory = new BulletFactory();
        }

        public void initialize(Difficulty difficulty) {
            this.difficulty = difficulty;

            // Load level settings
            ILevelProvider provider = new JsonLevelProvider();
            Level level = provider.createLevel(difficulty);

            nextLineGroupCount = level.getNextLineGroupCount();
            blockScrollSpeed = level.getBlockScrollSpeed();
            minSpawnInterval = level.getMinSpawnInterval();
            maxSpawnInterval = level.getMaxSpawnInterval();
            nextMeteorSpawn = level.getInitialMeteorSpawnInterval();

            baseBlockScrollSpeed = blockScrollSpeed;
            linesSpawned = 0;

            // Initialize player
            playerShip = new PlayerShip(100, 200);

            // Load textures
            playerTexture = new Texture(Gdx.files.internal("player.png"));
            bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
            breakableTex = new Texture(Gdx.files.internal("block_breakable.png"));
            unbreakableTex = new Texture(Gdx.files.internal("block_unbreakable.png"));
            fallingTex = new Texture(Gdx.files.internal("block_falling.png"));
            meteorTex = new Texture(Gdx.files.internal("meteor.png"));

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
            gameOver = false;

            // Spawn first block group
            spawnBlockGroup(nextLineGroupCount);
        }

        public void update(float delta) {
            if (gameOver) return;

            // Player movement
            boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean right = Gdx.input.isKeyPressed(Input.Keys.D);
            playerShip.move(delta, left, right);

            // Shooting
            boolean isSpacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
            if (isSpacePressed && !wasSpacePressedLastFrame) {
                Bullet bullet = bulletFactory.createBullet(playerShip.x, playerShip.y);
                bullets.add(bullet);
            }
            wasSpacePressedLastFrame = isSpacePressed;

            // Move bullets
            for (Bullet bullet : bullets) {
                bullet.move(delta);
            }

            // Move blocks
            for (Block b : blocks) {
                b.move(0, -blockScrollSpeed * delta);
            }

            // Check collisions
            checkCollisions();

            // Update falling blocks
            for (Block b : blocks) {
                if (b instanceof FallingBlock) {
                    ((FallingBlock) b).update(delta);
                }
            }

            // Spawn blocks
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
        }

        private void checkCollisions() {
            // Calculate ship rectangle with inset
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

            // Check ship collisions with blocks
            for (Block b : blocks) {
                if (shipRect.overlaps(b.getBounds())) {
                    gameOver = true;
                    return;
                }
            }

            // Check ship collisions with meteors
            for (Meteor m : meteors) {
                if (shipRect.overlaps(m.getBounds())) {
                    gameOver = true;
                    return;
                }
            }

            // Check bullet collisions with blocks
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
                        if (block instanceof BreakableBlock && ((BreakableBlock) block).isDestroyed()) {
                            blocksToRemove.add(block);
                        }
                        break;
                    }
                }
            }
            bullets.removeAll(bulletsToRemove);
            blocks.removeAll(blocksToRemove);

            // Check bullet collisions with meteors
            List<Meteor> meteorsToRemove = new ArrayList<>();
            for (Bullet b : bullets) {
                Rectangle bRect = new Rectangle(
                    b.x, b.y,
                    bulletTexture.getWidth(),
                    bulletTexture.getHeight()
                );
                for (Meteor m : meteors) {
                    if (bRect.overlaps(m.getBounds())) {
                        meteorsToRemove.add(m);
                        bulletsToRemove.add(b);
                        break;
                    }
                }
            }
            meteors.removeAll(meteorsToRemove);
            bullets.removeAll(bulletsToRemove);
        }

        public void render(SpriteBatch batch) {
            // Draw player
            float blockW = breakableTex.getWidth();
            float origW = playerTexture.getWidth();
            float origH = playerTexture.getHeight();
            float scale = blockW / origW;
            float shipW = origW * scale;
            float shipH = origH * scale;

            batch.draw(playerTexture, playerShip.x, playerShip.y, shipW, shipH);

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
        }

        public void restart() {
            // Reset player
            playerShip.setPosition(0);
            bullets.clear();

            // Reset blocks
            blocks.clear();
            nextLineGroupCount = (difficulty == Difficulty.HARD ? 3 : 1);
            gapIndex = cols / 2;
            gapLinesRemaining = 5;
            linesSpawned = 0;

            // Reset timers
            blockSpawnTimer = 0f;
            nextBlockSpawn = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
            blockScrollSpeed = baseBlockScrollSpeed;

            // Spawn first blocks
            spawnBlockGroup(nextLineGroupCount);

            // Reset meteors
            meteors.clear();
            meteorSpawnTimer = 0f;
            nextMeteorSpawn = 5f;

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
                BlockFactory.BlockType type;
                if (unbreakCount < 3 && random.nextFloat() < 0.3f) {
                    type = BlockFactory.BlockType.UNBREAKABLE;
                    unbreakCount++;
                } else {
                    type = random.nextFloat() < 0.2f
                        ? BlockFactory.BlockType.FALLING
                        : BlockFactory.BlockType.BREAKABLE;
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

        public boolean isGameOver() {
            return gameOver;
        }

        public void dispose() {
            playerTexture.dispose();
            bulletTexture.dispose();
            breakableTex.dispose();
            unbreakableTex.dispose();
            fallingTex.dispose();
            meteorTex.dispose();
        }
    }
