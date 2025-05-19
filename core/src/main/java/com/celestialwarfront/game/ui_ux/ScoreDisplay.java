package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.celestialwarfront.game.contract.IScoreListener;

public class ScoreDisplay implements IScoreListener {
    private final Label label;

    public ScoreDisplay(Label label) {
        this.label = label;
    }

    @Override
    public void onScoreChanged(int newScore) {
        label.setText("Score: " + newScore);
    }

    public Label getLabel() {
        return label;
    }
}
