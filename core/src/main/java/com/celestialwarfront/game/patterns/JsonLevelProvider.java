package com.celestialwarfront.game.patterns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.celestialwarfront.game.logic.Difficulty;
import com.celestialwarfront.game.logic.Level;

public class JsonLevelProvider implements ILevelProvider {
    private final ObjectMap<String, LevelConfig> configs;

    public JsonLevelProvider() {
        Json json = new Json();
        configs = json.fromJson(
            ObjectMap.class,
            LevelConfig.class,
            Gdx.files.internal("levels.json")
        );
    }

    @Override
    public Level createLevel(Difficulty difficulty) {
        LevelConfig cfg = configs.get(difficulty.name());
        if (cfg == null) {
            throw new IllegalArgumentException(
                "Не найден конфиг для уровня: " + difficulty.name());
        }

        return new LevelBuilder()
            .fromConfig(cfg)
            .build();
    }
}
