package com.celestialwarfront.game.collisions;

public interface CollisionHandler {
    void setNext(CollisionHandler next);
    void handle(CollisionEvent event);
}
