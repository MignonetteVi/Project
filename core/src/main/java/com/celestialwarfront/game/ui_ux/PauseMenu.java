package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Класс меню паузы.
 * Предоставляет функционал для отображения и взаимодействия с меню паузы в игре.
 */
public class PauseMenu {
    // Текстуры элементов меню
    private Texture pauseBgTexture;// Текстура фона меню
    private Texture pauseContinueTexture;// Текстура кнопки "Продолжить"
    private Texture pauseRestartTexture;// Текстура кнопки "Рестарт"
    private Texture pauseExitTexture;// Текстура кнопки "Выход"
    private Texture pauseTexture;// Полупрозрачная текстура затемнения

    private boolean isVisible;// Флаг видимости меню
    private Rectangle pauseBackground;// Область фона меню
    private Rectangle resumeButton;// Область кнопки "Продолжить"
    private Rectangle mainMenuButton;// Область кнопки "Рестарт"
    private Rectangle exitButton;// Область кнопки "Выход"

    private Viewport viewport;// Вьюпорт для корректного позиционирования

    /**
     * Конструктор меню паузы.
     * Инициализирует текстуры и настраивает позиции элементов.
     */
    public PauseMenu() {
        // Загрузка текстур из файлов
        pauseBgTexture = new Texture(Gdx.files.internal("pause_bg.png"));
        pauseContinueTexture = new Texture(Gdx.files.internal("continue_btn.png"));
        pauseRestartTexture = new Texture(Gdx.files.internal("restart_btn.png"));
        pauseExitTexture = new Texture(Gdx.files.internal("btn_exit.png"));

        // Настройка размеров и позиций кнопок
        float buttonWidth = 500f;// Ширина кнопок
        float buttonHeight = 100f;// Высота кнопок
        // Центральное позиционирование (для 1920x1080)
        float centerX = (1920 - buttonWidth) / 2;// Центрирование по горизонтали
        float centerY = 1080 / 2;// Центр по вертикали

        // Создание областей для кнопок (вертикальный список)
        resumeButton = new Rectangle(centerX, centerY + 100, buttonWidth, buttonHeight);
        mainMenuButton = new Rectangle(centerX, centerY - 50, buttonWidth, buttonHeight);
        exitButton = new Rectangle(centerX, centerY - 200, buttonWidth, buttonHeight);
        pauseBackground = new Rectangle(0, 0, 1920, 1080); // На весь экран

        /// Создание полупрозрачной текстуры для затемнения
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);// Черный с прозрачностью 70%
        pixmap.fill();
        pauseTexture = new Texture(pixmap);
        pixmap.dispose(); // Освобождаем Pixmap после создания текстуры
    }

    /**
     * Устанавливает вьюпорт для меню.
     * @param viewport вьюпорт для корректного позиционирования
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    /**
     * Устанавливает видимость меню.
     * @param visible true - показать меню, false - скрыть
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Проверяет видимость меню.
     * @return true если меню видимо
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Переключает видимость меню.
     */
    public void toggleVisibility() {
        isVisible = !isVisible;
    }

    /**
     * Отрисовывает меню паузы.
     * @param batch SpriteBatch для отрисовки
     */
    public void render(SpriteBatch batch) {
        if (!isVisible) return;

        // Устанавливаем проекцию для меню
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // Отрисовка затемнения
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pauseTexture, 0, 0, 1920, 1080);
        batch.setColor(Color.WHITE);  // Сбрасываем цвет

        // Отрисовка фона меню
        batch.draw(pauseBgTexture, pauseBackground.x, pauseBackground.y,
            pauseBackground.width, pauseBackground.height);

        // Отрисовка кнопок с эффектами
        drawButton(batch, resumeButton, pauseContinueTexture);
        drawButton(batch, mainMenuButton, pauseRestartTexture);
        drawButton(batch, exitButton, pauseExitTexture);
    }

    /**
     * Отрисовывает кнопку с эффектом наведения.
     * @param batch SpriteBatch для отрисовки
     * @param rect область кнопки
     * @param texture текстура кнопки
     */
    private void drawButton(SpriteBatch batch, Rectangle rect, Texture texture) {
        // Эффект подсветки при наведении
        if (isMouseOver(rect)) {
            batch.setColor(1.2f, 1.2f, 1.2f, 1);  // Увеличение яркости на 20%
        } else {
            batch.setColor(Color.WHITE);
        }

        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        batch.setColor(Color.WHITE);// Сбрасываем цвет после отрисовки
    }

    /**
     * Проверяет, находится ли курсор над элементом.
     * @param rect область элемента
     * @return true если курсор над элементом
     */
    private boolean isMouseOver(Rectangle rect) {
        if (viewport == null) return false;

        // Преобразование координат касания в координаты игры
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);
        return rect.contains(touchPos.x, touchPos.y);
    }

    /**
     * Обрабатывает пользовательский ввод.
     * @param gameManager менеджер игры для управления игровым процессом
     */
    public void handleInput(GameManager gameManager) {
        if (!isVisible || !Gdx.input.justTouched()) return;

        // Получение позиции касания
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);

        // Обработка нажатий на кнопки
        if (resumeButton.contains(touchPos.x, touchPos.y)) {
            isVisible = false;// Скрываем меню
            Gdx.app.log("INPUT", "Continue pressed");
        }
        else if (mainMenuButton.contains(touchPos.x, touchPos.y)) {
            gameManager.restartGame(); // Перезапуск игры
            isVisible = false;
            Gdx.app.log("INPUT", "Restart pressed");
        }
        else if (exitButton.contains(touchPos.x, touchPos.y)) {
            Gdx.app.log("INPUT", "Exit pressed");
            Gdx.app.exit();// Выход из игры
        }
    }

    /**
     * Освобождает ресурсы меню.
     */
    public void dispose() {
        pauseBgTexture.dispose();
        pauseContinueTexture.dispose();
        pauseRestartTexture.dispose();
        pauseExitTexture.dispose();
        pauseTexture.dispose();
    }
}
