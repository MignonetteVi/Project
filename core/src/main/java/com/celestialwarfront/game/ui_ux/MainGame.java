package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.Game;

/**
 * Главный класс игры, наследующий от Game из LibGDX.
 * Управляет переключением между экранами приложения.
 */
public class MainGame extends Game {

    /**
     * Метод инициализации игры, вызываемый при запуске приложения.
     * Здесь происходит первоначальная настройка и установка стартового экрана.
     */
    @Override
    public void create() {
        // Устанавливаем стартовый экран - главное меню
        // Передаем this (текущий экземпляр MainGame) для возможности управления экранами
        setScreen(new MainMenuScreen(this));
    }
}
