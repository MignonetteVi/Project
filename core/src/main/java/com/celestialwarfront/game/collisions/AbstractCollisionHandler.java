package com.celestialwarfront.game.collisions;

public abstract class AbstractCollisionHandler implements CollisionHandler {
    private CollisionHandler next;

    @Override
    public void setNext(CollisionHandler next) {
        this.next = next;
    }

    protected void passToNext(CollisionEvent event) {
        if (next != null) next.handle(event);
    }
}
