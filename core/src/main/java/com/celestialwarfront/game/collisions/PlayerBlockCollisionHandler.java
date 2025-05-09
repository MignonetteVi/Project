package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Block;
import com.celestialwarfront.game.entities.PlayerShip;

public class PlayerBlockCollisionHandler extends AbstractCollisionHandler {
    private final Runnable gameOverCallback;
    public PlayerBlockCollisionHandler(Runnable gameOverCallback) {
        this.gameOverCallback = gameOverCallback;
    }
    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof PlayerShip
            && event.target instanceof Block) {
            gameOverCallback.run();
        } else {
            passToNext(event);
        }
    }
}

