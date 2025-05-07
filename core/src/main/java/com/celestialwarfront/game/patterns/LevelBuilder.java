package com.celestialwarfront.game.patterns;

import com.celestialwarfront.game.logic.Level;

public class LevelBuilder {
    private int    nextLineGroupCount;
    private float  blockScrollSpeed;
    private float  minSpawnInterval;
    private float  maxSpawnInterval;
    private float  initialMeteorSpawnInterval;

    public LevelBuilder fromConfig(LevelConfig cfg) {
        this.nextLineGroupCount       = cfg.nextLineGroupCount;
        this.blockScrollSpeed         = cfg.blockScrollSpeed;
        this.minSpawnInterval         = cfg.minSpawnInterval;
        this.maxSpawnInterval         = cfg.maxSpawnInterval;
        this.initialMeteorSpawnInterval = cfg.initialMeteorSpawnInterval;
        return this;
    }

    public Level build() {
        return new Level(
            nextLineGroupCount,
            blockScrollSpeed,
            minSpawnInterval,
            maxSpawnInterval,
            initialMeteorSpawnInterval
        );
    }
}
