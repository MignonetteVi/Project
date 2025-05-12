package com.celestialwarfront.game.state;

public enum Difficulty {
    EASY, // Легкий уровень сложности
    MEDIUM,// Средний уровень сложности
    HARD;// Сложный уровень сложности

    // Метод для преобразования индекса в уровень сложности
    // Индекс 0 - Легкий, 1 - Средний, 2 - Сложный

    public static Difficulty fromIndex(int idx) {
        switch (idx) {
            case 1:  return MEDIUM;// Если индекс 1, возвращаем Средний уровень
            case 2:  return HARD;// Если индекс 2, возвращаем Сложный уровень
            default: return EASY;// По умолчанию, если индекс 0, возвращаем Легкий уровень
        }
    }
}
