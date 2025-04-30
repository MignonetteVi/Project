package com.celestialwarfront.game.entities;

import com.badlogic.gdx.graphics.Texture;

public class BlockFactory {
    public enum BlockType { BREAKABLE, UNBREAKABLE, FALLING }

    public static Block createBlock(BlockType type, float x, float y, Texture texture, Object... params) {
        switch (type) {
            case BREAKABLE:
                int health = (params.length > 0 && params[0] instanceof Integer)
                    ? (Integer) params[0] : 1;
                return new BreakableBlock(x, y, texture, health);
            case UNBREAKABLE:
                return new UnbreakableBlock(x, y, texture);
            case FALLING:
                return new FallingBlock(x, y, texture);
            default:
                throw new IllegalArgumentException("Unknown BlockType: " + type);
        }
    }
}
