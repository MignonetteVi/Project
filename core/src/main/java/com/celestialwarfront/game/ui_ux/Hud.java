package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.celestialwarfront.game.contract.*;

public class Hud implements IScoreListener, IHPListener, ILevelListener, ITimeListener, IAmmoListener {
    private final Stage stage;
    private final BitmapFont font;

    private final ScoreDisplay scoreDisplay;
    private final HPDisplay hpDisplay;
    private final LevelDisplay levelDisplay;
    private final TimeDisplay timeDisplay;
    private final AmmoDisplay ammoDisplay;

    public Hud() {
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();

        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        // Инициализация компонентов
        scoreDisplay = new ScoreDisplay(new Label("Score: 0", ls));
        hpDisplay = new HPDisplay(new Label("HP: 100", ls));
        levelDisplay = new LevelDisplay(new Label("Level: 0", ls));
        timeDisplay = new TimeDisplay(new Label("Time: 00:00", ls));
        ammoDisplay = new AmmoDisplay(new Label("Ammo: 15", ls));

        // Добавление в таблицу
        table.add(ammoDisplay.getLabel()).pad(8);
        table.row();
        table.add(scoreDisplay.getLabel()).pad(8);
        table.row();
        table.add(hpDisplay.getLabel()).pad(8);
        table.row();
        table.add(levelDisplay.getLabel()).padLeft(4);
        table.row();
        table.add(timeDisplay.getLabel()).pad(8);

        stage.addActor(table);
    }

    // Делегирование методов интерфейсов
    @Override
    public void onScoreChanged(int newScore) {
        scoreDisplay.onScoreChanged(newScore);
    }

    @Override
    public void onHPChanged(int newHP) {
        hpDisplay.onHPChanged(newHP);
    }

    @Override
    public void onLevelChanged(int newLevel) {
        levelDisplay.onLevelChanged(newLevel);
    }

    @Override
    public void onTimeChanged(String timeString) {
        timeDisplay.onTimeChanged(timeString);
    }

    @Override
    public void onAmmoChanged(int ammo) {
        ammoDisplay.onAmmoChanged(ammo);
    }

    public void flashAmmo() {
        ammoDisplay.flash();
    }

    public void draw(SpriteBatch batch) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public BitmapFont getFont() {
        return font;
    }
}
