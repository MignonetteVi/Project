package com.celestialwarfront.game.engine.level;
// Параметры уровня, получаемые из конфигурации
public class Level {
    private final int    nextLineGroupCount; // Количество групп блоков для следующей линии
    private final float  blockScrollSpeed; // Скорость прокрутки блоков (насколько быстро блоки двигаются вниз)
    private final float  minSpawnInterval;// Минимальный интервал для спавна блоков
    private final float  maxSpawnInterval;// Максимальный интервал для спавна блоков
    private final float  initialMeteorSpawnInterval;// Начальный интервал для спавна метеоров

    // Конструктор класса Level, инициализирует все параметры уровня
    public Level(int nextLineGroupCount, float blockScrollSpeed,
                 float minSpawnInterval, float maxSpawnInterval,
                 float initialMeteorSpawnInterval) {
        this.nextLineGroupCount       = nextLineGroupCount; // Количество блоков в группе
        this.blockScrollSpeed         = blockScrollSpeed; // Скорость прокрутки блоков
        this.minSpawnInterval         = minSpawnInterval;// Минимальный интервал спавна блоков
        this.maxSpawnInterval         = maxSpawnInterval;// Максимальный интервал спавна блоков
        this.initialMeteorSpawnInterval = initialMeteorSpawnInterval;// Начальный интервал для метеоров
    }
    // Геттеры для всех параметров уровня
    public int   getNextLineGroupCount()       { return nextLineGroupCount; } // Получение количества групп блоков
    public float getBlockScrollSpeed()         { return blockScrollSpeed; }// Получение скорости прокрутки блоков
    public float getMinSpawnInterval()         { return minSpawnInterval; }// Получение минимального интервала спавна блоков
    public float getMaxSpawnInterval()         { return maxSpawnInterval; }// Получение максимального интервала спавна блоков
    public float getInitialMeteorSpawnInterval() { return initialMeteorSpawnInterval; }// Получение интервала для спавна метеоров
}
