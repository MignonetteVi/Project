package com.celestialwarfront.game.ui_ux;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.celestialwarfront.game.contract.IAmmoListener;
public class AmmoDisplay implements IAmmoListener {
    private final Label label;

    public AmmoDisplay(Label label) {
        this.label = label;
    }

    @Override
    public void onAmmoChanged(int ammo) {
        label.setText("Ammo: " + ammo);
    }

    public void flash() {
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
