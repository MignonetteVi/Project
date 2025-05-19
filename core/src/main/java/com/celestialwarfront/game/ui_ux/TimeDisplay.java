package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.celestialwarfront.game.contract.ITimeListener;

public class TimeDisplay implements ITimeListener {
    private final Label label;

    public TimeDisplay(Label label) {
        this.label = label;
    }

    @Override
    public void onTimeChanged(String timeString) {
        label.setText("Time: " + timeString);
    }

    public Label getLabel() {
        return label;
    }
}
