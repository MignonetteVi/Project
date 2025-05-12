package com.celestialwarfront.game.engine.collision;

import com.celestialwarfront.game.contract.ICollisionHandler;

public abstract class AbstractICollisionHandler implements ICollisionHandler {
    private ICollisionHandler next;

    @Override
    public void setNext(ICollisionHandler next) {
        this.next = next;
    }

    protected void passToNext(CollisionEvent event) {
        if (next != null) next.handle(event);
    }
}
