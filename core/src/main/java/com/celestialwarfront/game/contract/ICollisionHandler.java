package com.celestialwarfront.game.contract;

import com.celestialwarfront.game.engine.collision.CollisionEvent;

public interface ICollisionHandler {
    void setNext(ICollisionHandler next);
    void handle(CollisionEvent event);
}
