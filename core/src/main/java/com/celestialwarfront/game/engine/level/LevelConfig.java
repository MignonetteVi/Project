package com.celestialwarfront.game.engine.level;

public class LevelConfig {
    // Количество групп блоков, которые должны появиться на уровне
    public int    nextLineGroupCount;

    // Скорость прокрутки блоков на уровне
    public float  blockScrollSpeed;

    // Минимальный интервал между спавнами блоков
    public float  minSpawnInterval;

    // Максимальный интервал между спавнами блоков
    public float  maxSpawnInterval;
    // Начальный интервал спавна метеоров на уровне

    public float  initialMeteorSpawnInterval;

    // Конструктор по умолчанию
    public LevelConfig() { }
}
