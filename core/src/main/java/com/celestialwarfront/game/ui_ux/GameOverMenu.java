package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Класс меню окончания игры.
 * Отображается при завершении игры и предоставляет варианты:
 * - Перезапуск игры
 * - Выход из приложения
 */
public class GameOverMenu {
    // Текстуры элементов меню
    private Texture backgroundTexture;// Фон меню
    private Texture restartButtonTexture;// Кнопка перезапуска
    private Texture exitButtonTexture;// Кнопка выхода

    private boolean isVisible;// Флаг видимости меню
    private final Rectangle background;// Область фона
    private final Rectangle restartButton;// Область кнопки перезапуска
    private final Rectangle exitButton; // Область кнопки выхода

    private Viewport viewport;// Вьюпорт для корректного позиционирования
    private final Vector3 touchPos = new Vector3();// Вектор для позиции касания

    /**
     * Конструктор меню окончания игры.
     * Загружает текстуры и инициализирует позиции элементов.
     * @throws RuntimeException если не удалось загрузить текстуры
     */
    public GameOverMenu() {
        try {
            // Загрузка текстур с обработкой возможных ошибок
            backgroundTexture = new Texture(Gdx.files.internal("gameover_bg.png"));
            restartButtonTexture = new Texture(Gdx.files.internal("restart_btn.png"));
            exitButtonTexture = new Texture(Gdx.files.internal("btn_exit.png"));
        } catch (Exception e) {
            Gdx.app.error("GameOverMenu", "Failed to load textures", e);
            throw new RuntimeException("Texture loading failed", e);
        }

        // Размеры и позиционирование элементов
        float buttonWidth = 400f;// Ширина кнопок
        float buttonHeight = 100f;// Высота кнопок
        float centerX = (1920 - buttonWidth) / 2;// Центрирование по горизонтали

        // Инициализация областей для элементов
        background = new Rectangle(0, 0, 1920, 1080);// На весь экран
        restartButton = new Rectangle(centerX, 500, buttonWidth, buttonHeight);
        exitButton = new Rectangle(centerX, 350, buttonWidth, buttonHeight);
    }

    /**
     * Устанавливает вьюпорт для корректного позиционирования элементов.
     * @param viewport вьюпорт меню
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
     * Отрисовывает меню на экране.
     * @param batch SpriteBatch для отрисовки
     */
    public void render(SpriteBatch batch) {
        if (!isVisible) return;

        // Устанавливаем проекционную матрицу для корректного отображения
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // Отрисовка фона
        batch.draw(backgroundTexture, 0, 0, 1920, 1080);  // Рисуем фон
        batch.setColor(Color.WHITE);  // Сброс цвета на случай изменений

        // Отрисовка кнопок с эффектом наведения
        drawButton(batch, restartButton, restartButtonTexture);
        drawButton(batch, exitButton, exitButtonTexture);
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
            batch.setColor(1.2f, 1.2f, 1.2f, 1); // Увеличиваем яркость
        } else {
            batch.setColor(Color.WHITE);// Сбрасываем цвет после отрисовки
        }
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
        batch.setColor(Color.WHITE);
    }

    /**
     * Проверяет, находится ли курсор над элементом.
     * @param rect область элемента
     * @return true если курсор над элементом
     */
    private boolean isMouseOver(Rectangle rect) {
        if (viewport == null) return false;
        // Преобразуем координаты касания в координаты мира игры
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);
        return rect.contains(touchPos.x, touchPos.y);
    }


    /**
     * Обрабатывает пользовательский ввод.
     * @param gameManager менеджер игры для управления перезапуском
     */
    public void handleInput(GameManager gameManager) {
        // Проверяем условия для обработки ввода
        if (!isVisible || !Gdx.input.justTouched() || viewport == null) return;

        // Получаем позицию касания
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);

        // Обработка нажатий на кнопки
        if (restartButton.contains(touchPos.x, touchPos.y)) {
            gameManager.restartGame();// Перезапуск игры
            isVisible = false;// Скрываем меню
        }
        else if (exitButton.contains(touchPos.x, touchPos.y)) {
            Gdx.app.exit();// Выход из приложения
        }
    }

    /**
     * Освобождает ресурсы, используемые меню.
     */
    public void dispose() {
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (restartButtonTexture != null) restartButtonTexture.dispose();
        if (exitButtonTexture != null) exitButtonTexture.dispose();
    }
}
