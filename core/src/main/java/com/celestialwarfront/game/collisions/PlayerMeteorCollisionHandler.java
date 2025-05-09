package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Meteor;
import com.celestialwarfront.game.entities.PlayerShip;

public class PlayerMeteorCollisionHandler extends AbstractCollisionHandler {
    private final Runnable gameOverCallback;
    public PlayerMeteorCollisionHandler(Runnable gameOverCallback) {
        this.gameOverCallback = gameOverCallback;
    }
    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof PlayerShip
            && event.target instanceof Meteor) {
            gameOverCallback.run();
        } else {
            passToNext(event);
        }
    }
}
