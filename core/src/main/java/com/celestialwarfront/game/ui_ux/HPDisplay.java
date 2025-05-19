package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.celestialwarfront.game.contract.IHPListener;

public class HPDisplay implements IHPListener {
    private final Label label;

    public HPDisplay(Label label) {
        this.label = label;
    }

    @Override
    public void onHPChanged(int newHP) {
        label.setText("HP: " + newHP);
        label.addAction(Actions.sequence(
            Actions.color(Color.RED, 0f),
            Actions.color(Color.WHITE, 0.2f),
            Actions.color(Color.RED, 0f),
            Actions.color(Color.WHITE, 0.2f)
        ));
    }

    public Label getLabel() {
        return label;
    }
}
