package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Bullet;
import com.celestialwarfront.game.entities.Meteor;
import com.celestialwarfront.game.ui.GameState;

public class MeteorCollisionHandler extends AbstractCollisionHandler {
    private final GameState gameState;

    public MeteorCollisionHandler(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof Bullet && event.target instanceof Meteor) {
            Meteor meteor = (Meteor) event.target;
            meteor.applyDamage(event.damage);
            if (meteor.isDestroyed()) {
                gameState.changeScore(2);
            }
            CollisionSystem.markBulletForRemoval((Bullet)event.collider);
        } else {
            passToNext(event);
        }
    }
}
