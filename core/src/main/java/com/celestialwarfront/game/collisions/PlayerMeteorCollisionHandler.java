package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Meteor;
import com.celestialwarfront.game.entities.PlayerShip;
import com.celestialwarfront.game.ui.GameState;

public class PlayerMeteorCollisionHandler extends AbstractCollisionHandler {
    private final GameState gameState;

    public PlayerMeteorCollisionHandler(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof PlayerShip
                && event.target instanceof Meteor) {

            Meteor meteor = (Meteor)event.target;
            gameState.changeHP(-20);
            meteor.applyDamage(event.damage);

        } else {
            passToNext(event);
        }
    }
}
