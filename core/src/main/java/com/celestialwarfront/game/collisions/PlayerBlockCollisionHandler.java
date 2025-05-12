package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Block;
import com.celestialwarfront.game.entities.PlayerShip;
import com.celestialwarfront.game.ui.GameState;  // <- ваш интерфейс

public class PlayerBlockCollisionHandler extends AbstractCollisionHandler {
    private final GameState gameState;

    public PlayerBlockCollisionHandler(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof PlayerShip
            && event.target instanceof Block) {
            Block block = (Block)event.target;
            gameState.changeHP(-50);
            block.onHit();
            block.markDestroyed();
        } else {
            passToNext(event);
        }
    }
}
