package com.celestialwarfront.game.engine.factory;

import com.badlogic.gdx.graphics.Texture;
import com.celestialwarfront.game.domain.entities.block.Block;
import com.celestialwarfront.game.domain.entities.block.BreakableBlock;
import com.celestialwarfront.game.domain.entities.block.FallingBlock;
import com.celestialwarfront.game.domain.entities.block.UnbreakableBlock;


// Фабрика блоков (использует паттерн Factory)
public class BlockFactory {
    // Перечисление типов блоков
    // Это помогает определить, какой тип блока нужно создать
    public enum BlockType {
        BREAKABLE,// Разрушимый блок
        UNBREAKABLE,// Неразрушимый блок
        FALLING // Падающий блок
    }

    // Метод для создания блока
    // Принимает тип блока (BlockType), его координаты (x, y), текстуру и дополнительные параметры (например, здоровье для разрушимого блока)
    public static Block createBlock(BlockType type, float x, float y, Texture texture, Object... params) {
        switch (type) {
            case BREAKABLE:
                // Для разрушимого блока можно передать здоровье, по умолчанию - 1
                int health = (params.length > 0 && params[0] instanceof Integer)
                    ? (Integer) params[0] : 1;
                return new BreakableBlock(x, y, texture, health);// Создаем разрушимый блок с заданным здоровьем
            case UNBREAKABLE:
                // Для неразрушимого блока просто возвращаем его
                return new UnbreakableBlock(x, y, texture);
            case FALLING:
                // Для падающего блока создаем его и передаем текстуру
                return new FallingBlock(x, y, texture);
            default:
                // Если тип блока неизвестен, выбрасываем исключение
                throw new IllegalArgumentException("Unknown BlockType: " + type);
        }
    }
}
