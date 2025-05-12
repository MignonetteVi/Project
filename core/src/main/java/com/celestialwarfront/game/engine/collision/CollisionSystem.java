package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.contract.ICollisionHandler;
import com.celestialwarfront.game.domain.entities.projectile.Bullet;
import com.celestialwarfront.game.contract.IGameState;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollisionSystem {
    private final ICollisionHandler chain;
    private static final Set<Bullet> bulletsToRemove = new HashSet<>();

    public CollisionSystem(IGameState IGameState) {
        PlayerBlockICollisionHandler pbh = new PlayerBlockICollisionHandler(IGameState);
        PlayerMeteorICollisionHandler pmh = new PlayerMeteorICollisionHandler(IGameState);

        BulletICollisionHandler bch = new BulletICollisionHandler(IGameState);
        MeteorICollisionHandler mch = new MeteorICollisionHandler(IGameState);

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
