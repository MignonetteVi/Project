package com.celestialwarfront.game.collisions;

import com.celestialwarfront.game.entities.Bullet;
import com.celestialwarfront.game.ui.GameState;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollisionSystem {
    private final CollisionHandler chain;
    private static final Set<Bullet> bulletsToRemove = new HashSet<>();

    public CollisionSystem(GameState gameState) {
        PlayerBlockCollisionHandler pbh = new PlayerBlockCollisionHandler(gameState);
        PlayerMeteorCollisionHandler pmh = new PlayerMeteorCollisionHandler(gameState);

        BulletCollisionHandler bch = new BulletCollisionHandler(gameState);
        MeteorCollisionHandler mch = new MeteorCollisionHandler(gameState);

        pbh.setNext(pmh);
        pmh.setNext(bch);
        bch.setNext(mch);

        this.chain = pbh;
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
