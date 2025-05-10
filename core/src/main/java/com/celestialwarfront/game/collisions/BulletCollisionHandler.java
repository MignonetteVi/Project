package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Bullet;
import com.celestialwarfront.game.entities.Block;
import com.celestialwarfront.game.entities.Meteor;
import com.celestialwarfront.game.ui.GameState;
import java.util.Objects;

public class BulletCollisionHandler extends AbstractCollisionHandler {
    private final GameState gameState;

    public BulletCollisionHandler(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof Bullet
            && event.target instanceof IDamageable) {

            IDamageable target = (IDamageable) event.target;
            target.applyDamage(event.damage);

            if (target instanceof Block && ((Block) target).isDestroyed()) {
                gameState.changeScore(1);
            } else if (target instanceof Meteor && ((Meteor) target).isDestroyed()) {
                gameState.changeScore(2);
            }

            CollisionSystem.markBulletForRemoval((Bullet) event.collider);
        } else {
            passToNext(event);
        }
    }
}
