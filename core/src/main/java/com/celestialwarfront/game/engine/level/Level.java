package com.celestialwarfront.game.engine.level;

public class Level {
    private final int    nextLineGroupCount;
    private final float  blockScrollSpeed;
    private final float  minSpawnInterval;
    private final float  maxSpawnInterval;
    private final float  initialMeteorSpawnInterval;

    public Level(int nextLineGroupCount, float blockScrollSpeed,
                 float minSpawnInterval, float maxSpawnInterval,
                 float initialMeteorSpawnInterval) {
        this.nextLineGroupCount       = nextLineGroupCount;
        this.blockScrollSpeed         = blockScrollSpeed;
        this.minSpawnInterval         = minSpawnInterval;
        this.maxSpawnInterval         = maxSpawnInterval;
        this.initialMeteorSpawnInterval = initialMeteorSpawnInterval;
    }

    public int   getNextLineGroupCount()       { return nextLineGroupCount; }
    public float getBlockScrollSpeed()         { return blockScrollSpeed; }
    public float getMinSpawnInterval()         { return minSpawnInterval; }
    public float getMaxSpawnInterval()         { return maxSpawnInterval; }
    public float getInitialMeteorSpawnInterval() { return initialMeteorSpawnInterval; }
}
