package com.celestialwarfront.game.contract;

import com.celestialwarfront.game.state.Difficulty;
import com.celestialwarfront.game.engine.level.Level;

public interface ILevelProvider {
    Level createLevel(Difficulty difficulty);
}
