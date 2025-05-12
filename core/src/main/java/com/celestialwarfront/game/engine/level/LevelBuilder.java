package com.celestialwarfront.game.engine.level;

public class LevelBuilder {
    // Параметры для создания уровня
    private int    nextLineGroupCount;// Количество групп блоков на следующем уровне
    private float  blockScrollSpeed;// Скорость прокрутки блоков
    private float  minSpawnInterval;// Минимальный интервал спавна блоков
    private float  maxSpawnInterval;// Максимальный интервал спавна блоков
    private float  initialMeteorSpawnInterval;// Начальный интервал для спавна метеоров

    // Метод для настройки параметров уровня из конфигурационного объекта
    public LevelBuilder fromConfig(LevelConfig cfg) {
        this.nextLineGroupCount       = cfg.nextLineGroupCount;// Получение данных из конфигурации
        this.blockScrollSpeed         = cfg.blockScrollSpeed;
        this.minSpawnInterval         = cfg.minSpawnInterval;
        this.maxSpawnInterval         = cfg.maxSpawnInterval;
        this.initialMeteorSpawnInterval = cfg.initialMeteorSpawnInterval;
        return this;// Возвращаем сам объект, чтобы можно было продолжить цепочку вызовов
    }

    // Метод для создания объекта Level с текущими настройками
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
