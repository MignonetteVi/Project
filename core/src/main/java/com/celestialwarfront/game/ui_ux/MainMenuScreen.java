package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;

/**
 * Экран главного меню игры.
 * Реализует интерфейс Screen из LibGDX для управления отображением и логикой меню.
 */
public class MainMenuScreen implements Screen {
    private final MainGame game;  // Ссылка на главный класс игры
    private OrthographicCamera camera;  // Камера для отображения
    private SpriteBatch batch;// Объект для отрисовки спрайтов
    private BitmapFont font;// Шрифт для текста

    // Текстуры элементов меню
    private Texture playButtonTexture; // Текстура кнопки "Играть"
    private Texture exitButtonTexture; // Текстура кнопки "Выход"
    private Texture background;// Текстура фона меню

    // Области для взаимодействия с кнопками
    private Rectangle playButton;// Прямоугольник кнопки "Играть"
    private Rectangle exitButton; // Прямоугольник кнопки "Выход"

    /**
     * Конструктор экрана главного меню.
     * @param game ссылка на главный класс игры для управления экранами
     */
    public MainMenuScreen(final MainGame game) {
        this.game = game;

        // Настройка камеры с соотношением сторон 16:9
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        // Инициализация объектов отрисовки
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3);// Увеличение размера шрифта
        font.setColor(Color.WHITE);// Установка белого цвета

        // Загрузка текстур из файлов
        playButtonTexture = new Texture(Gdx.files.internal("btn_play.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("btn_exit.png"));
        background = new Texture(Gdx.files.internal("menu_bg.png")); // Добавьте свой фон

        // Настройка размеров и позиций кнопок
        float buttonWidth = 400f; /// Ширина кнопок
        float buttonHeight = 150f;// Высота кнопок
        float centerX = 1920/2 - buttonWidth/2;// Центрирование по горизонтали

        // Создание областей для кнопок
        playButton = new Rectangle(centerX, 450, buttonWidth, buttonHeight);
        exitButton = new Rectangle(centerX, 250, buttonWidth, buttonHeight);
    }

    /**
     * Основной метод отрисовки, вызывается каждый кадр.
     * @param delta время в секундах с последнего кадра
     */
    @Override
    public void render(float delta) {
        // Очистка экрана черным цветом
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновление камеры и установка матрицы проекции
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Начало отрисовки
        batch.begin();

        // Отрисовка фона (на весь экран)
        batch.draw(background, 0, 0, 1920, 1080);

        // Отрисовка кнопок
        batch.draw(playButtonTexture,
            playButton.x, playButton.y,
            playButton.width, playButton.height);

        batch.draw(exitButtonTexture,
            exitButton.x, exitButton.y,
            exitButton.width, exitButton.height);

        batch.end();

        // Обработка пользовательского ввода
        handleInput();
    }

    /**
     * Обрабатывает пользовательский ввод (касания/клики).
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            // Получение координат касания и преобразование в координаты камеры
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            // Проверка нажатия на кнопки
            if (playButton.contains(touchPos.x, touchPos.y)) {
                // Переход к игровому экрану
                game.setScreen(new FirstScreen());
                dispose();// Освобождение ресурсов меню
            }
            else if (exitButton.contains(touchPos.x, touchPos.y)) {
                // Выход из приложения
                Gdx.app.exit();
            }
        }
    }

    /**
     * Обрабатывает изменение размера окна.
     * @param width новая ширина окна
     * @param height новая высота окна
     */
    @Override
    public void resize(int width, int height) {
        // Поддержание соотношения сторон 16:9
        camera.viewportWidth = 1920;
        camera.viewportHeight = 1080 * ((float)height/width);
        camera.update();
    }

    /**
     * Освобождает ресурсы экрана.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        playButtonTexture.dispose();
        exitButtonTexture.dispose();
        background.dispose();
    }

    // Методы жизненного цикла (не используются в данном классе)
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void show() {}
}
