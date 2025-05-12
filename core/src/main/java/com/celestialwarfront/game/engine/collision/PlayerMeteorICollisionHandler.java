package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.domain.entities.player.PlayerShip;
import com.celestialwarfront.game.domain.entities.block.Meteor;
import com.celestialwarfront.game.contract.IGameState;

public class PlayerMeteorICollisionHandler extends AbstractICollisionHandler {
    private final IGameState IGameState;

    public PlayerMeteorICollisionHandler(IGameState IGameState) {
        this.IGameState = IGameState;
    }

    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof PlayerShip
                && event.target instanceof Meteor) {

            Meteor meteor = (Meteor)event.target;
            IGameState.changeHP(-20);
            meteor.applyDamage(event.damage);

        } else {
            passToNext(event);
        }
    }
}
