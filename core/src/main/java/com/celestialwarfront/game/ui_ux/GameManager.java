package com.celestialwarfront.game.ui_ux;

import com.celestialwarfront.game.contract.IBulletFactory;
import com.celestialwarfront.game.contract.ILevelProvider;
import com.celestialwarfront.game.contract.IParticle;
import com.celestialwarfront.game.domain.entities.pickup.AmmoBox;
import com.celestialwarfront.game.domain.entities.pickup.HealthPack;
import com.celestialwarfront.game.domain.entities.player.PlayerShip;
import com.celestialwarfront.game.domain.entities.block.Block;
import com.celestialwarfront.game.domain.entities.block.FallingBlock;
import com.celestialwarfront.game.domain.entities.block.Meteor;
import com.celestialwarfront.game.domain.entities.projectile.Bullet;
import com.celestialwarfront.game.domain.effects.StarField;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.celestialwarfront.game.engine.factory.BlockFactory;
import com.celestialwarfront.game.engine.factory.BulletFactory;
import com.celestialwarfront.game.engine.level.JsonLevelProvider;
import com.celestialwarfront.game.state.DefaultIGameState;
import com.celestialwarfront.game.engine.level.Level;
import com.celestialwarfront.game.engine.factory.BlockFactory.BlockType;
import com.celestialwarfront.game.state.Difficulty;
import com.celestialwarfront.game.engine.collision.CollisionSystem;
import com.celestialwarfront.game.contract.IStateListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Главный класс управления игровым процессом.
 * Реализует паттерн Singleton для обеспечения единственного экземпляра менеджера.
 * Отвечает за:
 * - Инициализацию игровых объектов
 * - Обновление игрового состояния
 * - Обработку ввода
 * - Управление коллизиями
 * - Отрисовку игровых объектов
 */
public class GameManager {
    private static GameManager instance;

    // Основные игровые компоненты
    private OrthographicCamera camera; // Камера для отображения игрового мира
    private Viewport gameViewport;// Вьюпорт для игрового мира
    private Viewport menuViewport;// Вьюпорт для меню (отдельный для корректного отображения UI)


    private Difficulty difficulty;// Текущий уровень сложности
    private CollisionSystem collisionSystem;// Система обработки столкновений
    private PlayerShip playerShip;// Корабль игрока
    private List<Bullet> bullets;// Список активных пуль
    private IBulletFactory bulletFactory;// Фабрика для создания пуль
    private Texture playerTexture;// Текстура корабля игрока
    private Texture bulletTexture;// Текстура пуль
    private SpriteBatch batch;// Основной объект для отрисовки
    private List<Block> blocks;// Список блоков на поле
    private List<Meteor> meteors;// Список метеоров
    private List<AmmoBox> ammoBoxes;// Список ящиков с боеприпасами
    private IParticle starField;// Эффект звездного поля (фон)
    private List<HealthPack> healthPacks;// Список аптечек
    private Texture breakableTex, unbreakableTex, fallingTex, ammoBoxTex, healthPackTex;// Текстуры объектов
    private Random random;// Генератор случайных чисел
    private DefaultIGameState gameState;// Состояние игры (очки, здоровье и т.д.)
    private Hud hud;// Игровой интерфейс
    private boolean gameOver;// Флаг завершения игры
    private boolean pendingGameOverDialog;// Флаг ожидания показа диалога Game Over
    private boolean wasSpacePressedLastFrame;// Флаг состояния кнопки стрельбы в предыдущем кадре
    private PauseMenu pauseMenu; // Меню паузы
    private GameOverMenu gameOverMenu;// Меню окончания игры

    // Параметры игры
    private int nextLineGroupCount;// Количество линий блоков для следующей группы
    private float blockScrollSpeed;// Текущая скорость прокрутки блоков
    private float minSpawnInterval;// Минимальный интервал между спавном блоков
    private float maxSpawnInterval;// Максимальный интервал между спавном блоков
    private float baseBlockScrollSpeed;// Базовая скорость прокрутки блоков
    private float speedIncrementPerLine = 2f; // Увеличение скорости за каждую линию
    private int linesSpawned;// Количество созданных линий
    private int gapIndex;// Индекс пропуска в линии блоков
    private int cols; // Количество колонок блоков
    private int gapLinesRemaining;// Оставшееся количество линий с пропуском
    private float meteorSpawnTimer, nextMeteorSpawn;// Таймеры для спавна метеоров
    private float ammoSpawnTimer, nextAmmoSpawn;// Таймеры для спавна боеприпасов
    private float healthSpawnTimer, nextHealthSpawn;// Таймеры для спавна аптечек
    private float blockSpawnTimer, nextBlockSpawn;// Таймеры для спавна блоков

    /**
     * Получает единственный экземпляр GameManager (реализация Singleton)
     * @return экземпляр GameManager
     */
    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
    /**
     * Приватный конструктор для инициализации игрового менеджера.
     * Создает основные игровые системы и настраивает начальное состояние.
     */
    private GameManager() {
        // Инициализация графических компонентов
        camera = new OrthographicCamera();
        gameViewport = new FitViewport(1920, 1080, camera);
        menuViewport = new FitViewport(1920, 1080, new OrthographicCamera());

        // Инициализация меню
        pauseMenu = new PauseMenu();
        pauseMenu.setViewport(menuViewport);
        gameOverMenu = new GameOverMenu();
        gameOverMenu.setViewport(menuViewport);

        // Инициализация игровых систем
        gameState = new DefaultIGameState();
        hud = new Hud();
        gameState.addListener(hud);

        // Инициализация коллекций игровых объектов
        bullets = new ArrayList<>();
        blocks = new ArrayList<>();
        meteors = new ArrayList<>();
        ammoBoxes = new ArrayList<>();
        healthPacks = new ArrayList<>();

        random = new Random();

        // Настройка слушателя изменений состояния игры
        gameState.addListener(new IStateListener() {
            @Override
            public void onHPChanged(int newHP) {
                // Обработка изменения здоровья игрока
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
    /**
     * Начинает новую игру с выбранным уровнем сложности.
     * Загружает уровень, инициализирует игровые объекты и сбрасывает состояние.
     */

    public void startNewGame() {
        // Диалог выбора сложности
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

        // Загрузка параметров уровня
        ILevelProvider provider = new JsonLevelProvider();
        Level level = provider.createLevel(difficulty);

        // Установка параметров уровня
        nextLineGroupCount = level.getNextLineGroupCount();
        blockScrollSpeed = level.getBlockScrollSpeed();
        minSpawnInterval = level.getMinSpawnInterval();
        maxSpawnInterval = level.getMaxSpawnInterval();
        nextMeteorSpawn = level.getInitialMeteorSpawnInterval();

        baseBlockScrollSpeed = blockScrollSpeed;
        linesSpawned = 0;

        // Создание игровых объектов
        playerShip = new PlayerShip(100, 200);
        bulletFactory = new BulletFactory();

        // Загрузка текстур
        playerTexture = new Texture(Gdx.files.internal("player.png"));
        bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
        batch = new SpriteBatch();

        breakableTex = new Texture(Gdx.files.internal("block_breakable.png"));
        unbreakableTex = new Texture(Gdx.files.internal("block_unbreakable.png"));
        fallingTex = new Texture(Gdx.files.internal("block_falling.png"));
        healthPackTex = new Texture(Gdx.files.internal("health_pack.png"));
        ammoBoxTex = new Texture(Gdx.files.internal("ammo_box.png"));

        // Инициализация системы столкновений
        collisionSystem = new CollisionSystem(gameState);

        // Расчет количества колонок блоков
        int screenW = Gdx.graphics.getWidth();
        float blockW = breakableTex.getWidth();
        cols = (int)Math.ceil(screenW / blockW);

        // Начальные параметры генерации блоков
        gapIndex = cols / 2;
        gapLinesRemaining = 5;

        // Инициализация таймеров спавна
        meteorSpawnTimer = 0f;
        nextMeteorSpawn = 5f;
        blockSpawnTimer = 0f;
        nextBlockSpawn = minSpawnInterval + random.nextFloat()*(maxSpawnInterval-minSpawnInterval);
        ammoSpawnTimer = 0f;
        nextAmmoSpawn = 15f + random.nextFloat()*10f;
        healthSpawnTimer = 0f;
        nextHealthSpawn = 20f + random.nextFloat() * 20f;

        // Сброс состояния игры
        gameOver = false;
        pendingGameOverDialog = false;
        starField = new StarField(100, blockScrollSpeed);
        spawnBlockGroup(nextLineGroupCount);
    }

    /**
     * Основной метод обновления игрового состояния.
     * Вызывается каждый кадр для обработки ввода, обновления объектов и проверки состояния игры.
     * @param delta время, прошедшее с последнего обновления (в секундах)
     */

    public void update(float delta) {
        // Обработка паузы
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            pauseMenu.toggleVisibility();
        }
        // Если игра на паузе - обрабатываем только меню паузы
        if (pauseMenu.isVisible()) {
            pauseMenu.handleInput(this);
            return;
        }
        // Обработка состояния "игра окончена"
        if (gameOver) {
            if (pendingGameOverDialog) {
                gameOverMenu.setVisible(true);
                pendingGameOverDialog = false;
            }
            gameOverMenu.handleInput(this);
            return;
        }

        // Обновление камеры и проекционной матрицы
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Обновление игрового состояния
        gameState.updateTimer(delta);
        updateGameState(delta);
    }

    /**
     * Обновляет состояние всех игровых объектов.
     * @param delta время, прошедшее с последнего обновления (в секундах)
     */
    private void updateGameState(float delta) {
        // Обновление фона (звездного поля)
        if (starField != null) {
            ((StarField) starField).setSpeed(blockScrollSpeed);
            starField.update(delta);
        }
        // Обработка движения игрока
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D);
        playerShip.move(delta, left, right);

        // Чит-код для сброса уровня (для тестирования)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gameState.resetLevel();
        }

        // Создание хитбокса игрока (уменьшенного на 20% для более честного геймплея)
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

        // Обработка стрельбы
        boolean isSpacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        if (isSpacePressed && !wasSpacePressedLastFrame) {
            if (gameState.getAmmo() > 0) {
                gameState.changeAmmo(-1);
                bullets.add(bulletFactory.createBullet(playerShip.x, playerShip.y));
            } else {
                hud.flashAmmo();// Визуальная подсказка о нехватке боеприпасов
            }
        }
        wasSpacePressedLastFrame = isSpacePressed;

        // Обновление положения пуль
        for (Bullet bullet : bullets) {
            bullet.move(delta);
        }

        // Обновление положения блоков
        for (Block b : blocks) {
            b.move(0, -blockScrollSpeed * delta);
            if (b instanceof FallingBlock) {
                ((FallingBlock) b).update(delta);
            }
        }

        // Спавн новых линий блоков
        blockSpawnTimer += delta;
        if (blockSpawnTimer >= nextBlockSpawn) {
            spawnBlockGroup(nextLineGroupCount);
            blockSpawnTimer = 0f;
            if (difficulty != Difficulty.HARD) {
                nextLineGroupCount++;// Увеличиваем сложность (кроме максимального уровня)
            }
            nextBlockSpawn = minSpawnInterval + random.nextFloat() * (maxSpawnInterval - minSpawnInterval);
        }

        // Спавн метеоров
        meteorSpawnTimer += delta;
        if (meteorSpawnTimer >= nextMeteorSpawn) {
            float spawnX = random.nextFloat() * Gdx.graphics.getWidth();
            float speed  = 80f + random.nextFloat() * 120f;
            meteors.add(new Meteor(spawnX, Gdx.graphics.getHeight(), speed));
            meteorSpawnTimer = 0f;
            nextMeteorSpawn = 3f + random.nextFloat() * 5f;
        }

        // Обновление метеоров
        for (Meteor m : meteors) {
            m.update(delta);
        }

        // Спавн ящиков с боеприпасами
        ammoSpawnTimer += delta;
        if (ammoSpawnTimer >= nextAmmoSpawn) {
            float x = random.nextFloat() * (Gdx.graphics.getWidth() - ammoBoxTex.getWidth());
            ammoBoxes.add(new AmmoBox(x, Gdx.graphics.getHeight(), ammoBoxTex, 100f));
            ammoSpawnTimer = 0f;
            nextAmmoSpawn = 15f + random.nextFloat()*10f;
        }
        for (AmmoBox box : ammoBoxes) box.update(delta);

        // Спавн аптечек
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

        // Обработка столкновений
        handleCollisions(shipRect);


        // Очистка уничтоженных объектов
        collisionSystem.purgeTaggedBullets(bullets);
        blocks.removeIf(Block::isDestroyed);
        meteors.removeIf(Meteor::isDestroyed);
        ammoBoxes.removeIf(b -> b.isPicked() || b.y + ammoBoxTex.getHeight() < 0);
    }

    /**
     * Обрабатывает все возможные столкновения между игровыми объектами.
     * @param shipRect прямоугольник, представляющий игрока (для проверки столкновений)
     */
    private void handleCollisions(Rectangle shipRect) {
        // Столкновения пуль с блоками и метеорами
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

        // Столкновения игрока с блоками и метеорами
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

        // Подбор боеприпасов
        for (AmmoBox box : ammoBoxes) {
            if (!box.isPicked() && shipRect.overlaps(box.getBounds())) {
                box.pick();
                gameState.changeAmmo(+5);
            }
        }

        // Подбор аптечек
        for (HealthPack hp : healthPacks) {
            if (!hp.isPicked() && shipRect.overlaps(hp.getBounds())) {
                hp.pick();
                gameState.changeHP(+30);
            }
        }
    }

    /**
     * Отрисовывает все игровые объекты и UI.
     * Вызывается каждый кадр для визуализации игрового состояния.
     */
    public void render() {
        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Отрисовка фона (звездного поля)
        if (starField != null) {
            starField.render(batch);
        }

        // Отрисовка игрового мира (только если игра не завершена)
        if (!gameOver) {
            // Рассчет размеров корабля для корректного отображения
            float blockW = breakableTex.getWidth();
            float origW = playerTexture.getWidth();
            float origH = playerTexture.getHeight();
            float scale = blockW / origW;
            float shipW = origW * scale;
            float shipH = origH * scale;

            // Отрисовка игрока
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

    /**
     * Обрабатывает изменение размера окна.
     * @param width новая ширина окна
     * @param height новая высота окна
     */
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        menuViewport.update(width, height, true);
        hud.resize(width, height);
    }

    /**
     * Освобождает все ресурсы, используемые игрой.
     * Должен вызываться при завершении работы приложения.
     */
    public void dispose() {
        playerTexture.dispose();
        bulletTexture.dispose();
        batch.dispose();
        breakableTex.dispose();
        unbreakableTex.dispose();
        fallingTex.dispose();
        Meteor.dispose();
        ammoBoxTex.dispose();
        healthPackTex.dispose();
        pauseMenu.dispose();
        gameOverMenu.dispose();
    }

    /**
     * Перезапускает игру с текущими настройками сложности.
     * Сбрасывает все игровые объекты и состояние.
     */
    public void restartGame() {
        // Сброс позиции игрока
        playerShip.setPosition(0);

        // Очистка всех списков объектов
        bullets.clear();
        blocks.clear();

        // Сброс параметров генерации
        nextLineGroupCount = (difficulty == Difficulty.HARD ? 3 : 1);
        gapIndex = cols/2;
        gapLinesRemaining = 5;
        linesSpawned = 0;
        blockSpawnTimer = 0f;
        nextBlockSpawn = minSpawnInterval + random.nextFloat()*(maxSpawnInterval-minSpawnInterval);
        blockScrollSpeed = baseBlockScrollSpeed;

        // Создание начальной группы блоков
        spawnBlockGroup(nextLineGroupCount);

        // Очистка метеоров
        meteors.clear();
        meteorSpawnTimer = 0f;
        nextMeteorSpawn = 5f;

        // Сброс состояния игры
        gameState.resetSession();
        pendingGameOverDialog = false;
        wasSpacePressedLastFrame = false;
        gameOver = false;
        gameOverMenu.setVisible(false);
    }

    /**
     * Создает линию блоков на указанной Y-координате.
     * @param y координата Y для создания линии блоков
     */
    private void spawnBlockLineAt(float y) {
        int screenW = Gdx.graphics.getWidth();
        float blockW = breakableTex.getWidth();
        int cols = (int)Math.ceil(screenW / blockW);

        int unbreakCount = 0;// Счетчик неразрушаемых блоков в линии

        // Создание блоков в каждой колонке
        for (int i = 0; i < cols; i++   ) {
            // Пропуск блока в указанной колонке (для создания прохода)
            if (gapLinesRemaining > 0 && i == gapIndex) continue;

            float x = i * blockW;
            BlockType type;

            // Определение типа блока с учетом ограничений
            if (unbreakCount < 3 && random.nextFloat() < 0.3f) {
                type = BlockType.UNBREAKABLE;
                unbreakCount++;
            } else {
                type = random.nextFloat() < 0.2f
                    ? BlockType.FALLING
                    : BlockType.BREAKABLE;
            }

            // Создание блока соответствующего типа
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

        // Обновление счетчика линий с пропуском
        if (gapLinesRemaining > 0) {
            gapLinesRemaining--;
        }

        // Случайное смещение позиции пропуска
        int shift = random.nextInt(3) - 1;
        gapIndex = MathUtils.clamp(gapIndex + shift, 0, cols - 1);

        // Увеличение сложности
        linesSpawned++;
        blockScrollSpeed = baseBlockScrollSpeed + linesSpawned * speedIncrementPerLine;
    }

    /**
     * Создает группу линий блоков.
     * @param lines количество линий для создания
     */
    private void spawnBlockGroup(int lines) {
        float screenH = Gdx.graphics.getHeight();
        float blockH = breakableTex.getHeight();

        // Ограничение максимального количества линий в группе
        int count = Math.min(lines, 2);

        // Создание указанного количества линий
        for (int i = 0; i < count; i++) {
            spawnBlockLineAt(screenH + i * blockH);
        }
    }

    /**
     * Получает игровую камеру.
     * @return экземпляр OrthographicCamera
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Получает HUD игры.
     * @return экземпляр Hud
     */
    public Hud getHud() {
        return hud;
    }
}
