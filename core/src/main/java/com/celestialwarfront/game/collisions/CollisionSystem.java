package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Bullet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollisionSystem {
    private final CollisionHandler chain;
    private static final Set<Bullet> bulletsToRemove = new HashSet<>();

    public CollisionSystem(Runnable gameOverCallback) {
        PlayerBlockCollisionHandler  hb = new PlayerBlockCollisionHandler(gameOverCallback);
        PlayerMeteorCollisionHandler hm = new PlayerMeteorCollisionHandler(gameOverCallback);
        BulletCollisionHandler       hb2= new BulletCollisionHandler();
        MeteorCollisionHandler       hm2= new MeteorCollisionHandler();

        hb.setNext(hm);
        hm.setNext(hb2);
        hb2.setNext(hm2);

        this.chain = hb;
    }

    public void onCollision(Object collider, Object target, int damage) {
        chain.handle(new CollisionEvent(collider, target, damage));
    }

    public static void markBulletForRemoval(Bullet b) {
        bulletsToRemove.add(b);
    }

    public void purgeTaggedBullets(List<Bullet> bullets) {
        bullets.removeAll(bulletsToRemove);
        bulletsToRemove.clear();
    }
}
