package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Bullet;

public class MeteorCollisionHandler extends AbstractCollisionHandler{
    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof Bullet
            && event.target instanceof com.celestialwarfront.game.entities.Meteor) {

            ((IDamageable)event.target).applyDamage(event.damage);
            CollisionSystem.markBulletForRemoval((Bullet)event.collider);
        } else {
            passToNext(event);
        }
    }
}
