package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.domain.entities.projectile.Bullet;
import com.celestialwarfront.game.domain.entities.block.Meteor;
import com.celestialwarfront.game.contract.IGameState;

public class MeteorICollisionHandler extends AbstractICollisionHandler {
    private final IGameState IGameState;

    public MeteorICollisionHandler(IGameState IGameState) {
        this.IGameState = IGameState;
    }

    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof Bullet && event.target instanceof Meteor) {
            Meteor meteor = (Meteor) event.target;
            meteor.applyDamage(event.damage);
            if (meteor.isDestroyed()) {
                IGameState.changeScore(2);
            }
            CollisionSystem.markBulletForRemoval((Bullet)event.collider);
        } else {
            passToNext(event);
        }
    }
}
