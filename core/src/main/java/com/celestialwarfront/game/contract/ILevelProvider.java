package com.celestialwarfront.game.contract;

import com.celestialwarfront.game.state.Difficulty;
import com.celestialwarfront.game.engine.level.Level;

// Интерфейс, который предоставляет способ создания уровня
// в зависимости от сложности игры. Реализует паттерн Factory.
public interface ILevelProvider {
    // Метод для создания уровня в зависимости от сложности игры
    // @param difficulty - сложность игры (например, Легкий, Средний, Сложный)
    // @return создаваемый уровень, соответствующий заданной сложности
    Level createLevel(Difficulty difficulty);
}
