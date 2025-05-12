package com.celestialwarfront.game.engine.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.celestialwarfront.game.contract.ILevelProvider;
import com.celestialwarfront.game.state.Difficulty;
// Реализация фабрики уровней с использованием паттерна Factory
public class JsonLevelProvider implements ILevelProvider {
    private final ObjectMap<String, LevelConfig> configs;// Словарь конфигов уровней

    // Конструктор, который загружает JSON конфигурации уровней
    public JsonLevelProvider() {
        Json json = new Json();
        // Загружаем данные из файла levels.json
        configs = json.fromJson(
            ObjectMap.class,// Тип коллекции для хранения уровней
            LevelConfig.class,// Тип конфигурации для каждого уровня
            Gdx.files.internal("levels.json")// Путь к файлу
        );
    }
    // Реализация метода createLevel, который создает уровень на основе сложности
    @Override
    public Level createLevel(Difficulty difficulty) {
        // Получаем конфиг уровня из словаря по имени сложности
        LevelConfig cfg = configs.get(difficulty.name());
        if (cfg == null) {
            // Если конфиг для данной сложности не найден, выбрасываем исключение
            throw new IllegalArgumentException(
                "Не найден конфиг для уровня: " + difficulty.name());
        }
        // Создаем уровень с использованием конфигурации
        return new LevelBuilder()
            .fromConfig(cfg) // Строим уровень на основе конфигурации
            .build(); // Возвращаем построенный уровень
    }
}
