package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.domain.entities.player.PlayerShip;
import com.celestialwarfront.game.domain.entities.block.Block;
import com.celestialwarfront.game.contract.IGameState;  // <- ваш интерфейс

public class PlayerBlockICollisionHandler extends AbstractICollisionHandler {
    private final IGameState IGameState;

    public PlayerBlockICollisionHandler(IGameState IGameState) {
        this.IGameState = IGameState;
    }

    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof PlayerShip
            && event.target instanceof Block) {
            Block block = (Block)event.target;
            IGameState.changeHP(-50);
            block.onHit();
            block.markDestroyed();
        } else {
            passToNext(event);
        }
    }
}
