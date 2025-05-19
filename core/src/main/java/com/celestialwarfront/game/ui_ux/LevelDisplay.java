package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.celestialwarfront.game.contract.ILevelListener;

public class LevelDisplay implements ILevelListener {
    private final Label label;

    public LevelDisplay(Label label) {
        this.label = label;
    }

    @Override
    public void onLevelChanged(int newLevel) {
        label.setText("Level: " + newLevel);
        label.addAction(Actions.sequence(
            Actions.scaleTo(1.5f, 1.5f, 0.1f),
            Actions.scaleTo(1f, 1f, 0.1f)
        ));
    }

    public Label getLabel() {
        return label;
    }
}
