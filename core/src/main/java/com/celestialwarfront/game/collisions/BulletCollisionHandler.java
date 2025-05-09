package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Bullet;

public class BulletCollisionHandler extends AbstractCollisionHandler{
    @Override
    public void handle(CollisionEvent event) {
        if (event.collider instanceof Bullet
            && event.target instanceof IDamageable) {

            ((IDamageable)event.target).applyDamage(event.damage);
            CollisionSystem.markBulletForRemoval((Bullet)event.collider);

        } else {
            passToNext(event);
        }
    }
}
