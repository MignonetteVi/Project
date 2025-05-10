package com.celestialwarfront.game.ui;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Hud implements StateListener {
    private final Stage stage;

    private final Label scoreLabel;
    private final Label hpLabel;
    private final Label levelLabel;
    private final Label timerLabel;

    public Hud() {
        stage = new Stage(new ScreenViewport());

        BitmapFont font = new BitmapFont();
        Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);

        scoreLabel  = new Label("Score: 0", ls);
        hpLabel = new Label("HP: 100", ls);
        levelLabel  = new Label("Level: 0", ls);
        timerLabel  = new Label("Time: 00:00", ls);

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        table.add(scoreLabel).pad(8);
        table.row();
        table.add(hpLabel).pad(8);
        table.row();
        table.add(levelLabel).padLeft(4);
        table.row();
        table.add(timerLabel).pad(8);

        stage.addActor(table);
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void onScoreChanged(int newScore) {
        scoreLabel.setText("Score: " + newScore);
    }

    @Override
    public void onHPChanged(int newHP) {
        hpLabel.setText("HP: " + newHP);
        hpLabel.addAction(Actions.sequence(
            Actions.color(Color.RED, 0f),
            Actions.color(Color.WHITE, 0.2f),
            Actions.color(Color.RED, 0f),
            Actions.color(Color.WHITE, 0.2f)
        ));
    }

    @Override
    public void onLevelChanged(int newLevel) {
        levelLabel.setText("Level: " + newLevel);
        levelLabel.addAction(Actions.sequence(
            Actions.scaleTo(1.5f, 1.5f, 0.1f),
            Actions.scaleTo(1f, 1f, 0.1f)
        ));
    }

    @Override
    public void onTimeChanged(String timeString) {
        timerLabel.setText("Time: " + timeString);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}

