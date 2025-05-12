package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.contract.IDamageable;
import com.celestialwarfront.game.domain.entities.projectile.Bullet;
import com.celestialwarfront.game.domain.entities.block.Block;
import com.celestialwarfront.game.domain.entities.block.Meteor;
import com.celestialwarfront.game.contract.IGameState;
import java.util.Objects;

public class BulletICollisionHandler extends AbstractICollisionHandler {
    private final IGameState IGameState;

    public BulletICollisionHandler(IGameState IGameState) {
        this.IGameState = Objects.requireNonNull(IGameState);
    }

    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof Bullet
            && event.target instanceof IDamageable) {

            IDamageable target = (IDamageable) event.target;
            target.applyDamage(event.damage);

            if (target instanceof Block && ((Block) target).isDestroyed()) {
                IGameState.changeScore(1);
            } else if (target instanceof Meteor && ((Meteor) target).isDestroyed()) {
                IGameState.changeScore(2);
            }

            CollisionSystem.markBulletForRemoval((Bullet) event.collider);
        } else {
            passToNext(event);
        }
    }
}
