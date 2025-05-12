package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.celestialwarfront.game.contract.IStateListener;

/**
 * Класс HUD (Heads-Up Display) - отображает игровую информацию на экране.
 * Реализует интерфейс IStateListener для реагирования на изменения состояния игры.
 */
public class Hud implements IStateListener {
    // Основные компоненты HUD
    private final Stage stage;// Сцена для организации UI элементов
    private final Label ammoLabel;// Метка для отображения боеприпасов
    private final Label scoreLabel;// Метка для отображения очков
    private final Label hpLabel;// Метка для отображения здоровья
    private final Label levelLabel;// Метка для отображения уровня
    private final Label timerLabel; // Метка для отображения времени
    private final BitmapFont font;  // Шрифт для текста

    /**
     * Конструктор HUD.
     * Инициализирует все UI элементы и размещает их на сцене.
     */
    public Hud() {
        // Создаем сцену с адаптивным вьюпортом
        stage = new Stage(new ScreenViewport());

        // Создаем шрифт и стиль для меток
        font = new BitmapFont();
        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);

        // Инициализация меток с начальными значениями
        scoreLabel  = new Label("Score: 0", ls);
        hpLabel = new Label("HP: 100", ls);
        ammoLabel  = new Label("Ammo: 15", ls);
        levelLabel  = new Label("Level: 0", ls);
        timerLabel  = new Label("Time: 00:00", ls);

        // Создаем таблицу для организации элементов
        Table table = new Table();
        table.top().left();// Выравнивание по верхнему левому углу
        table.setFillParent(true); // Занимает всю доступную область

        // Добавляем метки в таблицу с отступами
        table.add(ammoLabel).pad(8);
        table.row();
        table.add(scoreLabel).pad(8);
        table.row();
        table.add(hpLabel).pad(8);
        table.row();
        table.add(levelLabel).padLeft(4);
        table.row();
        table.add(timerLabel).pad(8);

        // Добавляем таблицу на сцену
        stage.addActor(table);
    }

    /**
     * Возвращает используемый шрифт.
     * @return объект BitmapFont
     */
    public BitmapFont getFont() {
        return font;
    }

    /**
     * Отрисовывает HUD.
     * @param batch SpriteBatch для отрисовки
     */
    public void draw(SpriteBatch batch) {
        stage.act(Gdx.graphics.getDeltaTime());// Обновляем состояние сцены
        stage.draw();// Отрисовываем сцену
    }

    /**
     * Обрабатывает изменение счета.
     * @param newScore новое значение счета
     */
    @Override
    public void onScoreChanged(int newScore) {
        scoreLabel.setText("Score: " + newScore);
    }

    /**
     * Обрабатывает изменение здоровья.
     * Добавляет эффект мерцания при изменении здоровья.
     * @param newHP новое значение здоровья
     */
    @Override
    public void onHPChanged(int newHP) {
        hpLabel.setText("HP: " + newHP);
        // Эффект мерцания (красный-белый)
        hpLabel.addAction(Actions.sequence(
            Actions.color(Color.RED, 0f),
            Actions.color(Color.WHITE, 0.2f),
            Actions.color(Color.RED, 0f),
            Actions.color(Color.WHITE, 0.2f)
        ));
    }

    /**
     * Обрабатывает изменение уровня.
     * Добавляет эффект увеличения при изменении уровня.
     * @param newLevel новый уровень
     */
    @Override
    public void onLevelChanged(int newLevel) {
        levelLabel.setText("Level: " + newLevel);
        // Эффект увеличения и возврата к нормальному размеру
        levelLabel.addAction(Actions.sequence(
            Actions.scaleTo(1.5f, 1.5f, 0.1f),
            Actions.scaleTo(1f, 1f, 0.1f)
        ));
    }

    /**
     * Обрабатывает изменение количества боеприпасов.
     * @param ammo новое количество боеприпасов
     */
    @Override
    public void onAmmoChanged(int ammo) {
        ammoLabel.setText("Ammo: " + ammo);
    }

    /**
     * Визуальный эффект при нехватке боеприпасов.
     * Вызывает мерцание метки боеприпасов.
     */
    public void flashAmmo() {
        ammoLabel.addAction(Actions.sequence(
                Actions.color(Color.RED, 0f),
                Actions.color(Color.WHITE, 0.2f),
                Actions.color(Color.RED, 0f),
                Actions.color(Color.WHITE, 0.2f)
        ));
    }

    /**
     * Обрабатывает изменение времени.
     * @param timeString строка с отформатированным временем
     */
    @Override
    public void onTimeChanged(String timeString) {
        timerLabel.setText("Time: " + timeString);
    }

    /**
     * Обрабатывает изменение размера экрана.
     * @param width новая ширина
     * @param height новая высота
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }


}

