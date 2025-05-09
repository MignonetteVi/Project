package com.celestialwarfront.game.collisions;

public class CollisionEvent {
    public final Object collider;
    public final Object target;
    public final int damage;

    public CollisionEvent(Object collider, Object target, int damage) {
        this.collider = collider;
        this.target = target;
        this.damage = damage;
    }
}
