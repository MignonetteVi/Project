package com.celestialwarfront.game.patterns;

import com.celestialwarfront.game.logic.Difficulty;
import com.celestialwarfront.game.logic.Level;

public interface ILevelProvider {
    Level createLevel(Difficulty difficulty);
}
