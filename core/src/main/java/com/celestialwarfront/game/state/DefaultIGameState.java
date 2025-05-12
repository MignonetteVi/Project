package com.celestialwarfront.game.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.celestialwarfront.game.contract.IGameState;
import com.celestialwarfront.game.contract.IStateListener;

import java.util.ArrayList;
import java.util.List;

public class DefaultIGameState implements IGameState {
    private static final String PREFS_NAME = "celestial_warfront"; // Имя файла настроек
    private static final String KEY_LEVEL = "level";// Ключ для сохранения текущего уровня

    private final Preferences prefs; // Для работы с локальными настройками (сохранение прогресса)
    private final List<IStateListener> listeners = new ArrayList<>();// Слушатели для обновления состояния игры

    private int score;// Текущий счет
    private int hp;// Текущие очки здоровья
    private int level;// Текущий уровень
    private float elapsed;// Время с начала игры
    private int ammo;// Количество патронов


    // Конструктор, инициализирует значения
    public DefaultIGameState() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);// Получаем доступ к файлу настроек
        level = prefs.getInteger(KEY_LEVEL, 0);// Загружаем текущий уровень, по умолчанию 0
        score = 0;// Начальный счет
        hp = 100;// Начальные очки здоровья
        elapsed = 0f;// Время с начала игры
        ammo = 15;// Начальное количество патронов
    }

    // Добавить слушателя состояния
    @Override public void addListener(IStateListener l) { listeners.add(l); }
    // Удалить слушателя состояния
    @Override public void removeListener(IStateListener l) { listeners.remove(l); }

    // Получить текущий счет
    @Override public int getScore() { return score; }

    // Получить текущее количество здоровья
    @Override public int getHP() { return hp; }
    // Получить текущий уровень
    @Override public int getLevel() { return level; }
    // Получить строковое представление времени
    @Override public String getTimeString() {
        int totalSec = (int) elapsed; // Преобразуем время в секунды
        int min = totalSec / 60;// Вычисляем минуты
        int sec = totalSec % 60;// Вычисляем секунды
        return String.format("%02d:%02d", min, sec); // Форматируем время в строку
    }


    // Изменить счет (прибавить или отнять)
    @Override
    public void changeScore(int delta) {
        score += delta;// Изменяем счет
        listeners.forEach(l -> l.onScoreChanged(score));// Уведомляем слушателей о новом счете

        // Повышаем уровень, пока достаточно очков для следующего
        while (score >= thresholdFor(level + 1)) {
            level++;// Увеличиваем уровень
            prefs.putInteger(KEY_LEVEL, level);// Сохраняем новый уровень в настройках
            prefs.flush();// Сохраняем изменения
            listeners.forEach(l -> l.onLevelChanged(level));// Уведомляем слушателей о новом уровне
        }
    }

    // Определяем порог для следующего уровня в зависимости от текущего
    private int thresholdFor(int targetLevel) {
        if (targetLevel <= 1) {
            return 10; // чтобы level стал 1 нужно 10
        } else if (targetLevel == 2) {
            return 30; // чтобы level ствл 2 нужно 30
        } else { // начиная с третьего: 30 + (уровень-2) * 10
            return 30 + (targetLevel - 2) * 10;
        }
    }

    // Изменить количество здоровья
    @Override
    public void changeHP(int delta) {
        int old = hp;
        int newHp = old + delta;// Новое количество здоровья
        if (newHp > 100) newHp = 100;// Ограничиваем здоровье максимумом
        if (newHp < 0)   newHp = 0;// Ограничиваем здоровье минимумом
        hp = newHp;

        if (hp != old) {
            for (IStateListener l : listeners) {
                l.onHPChanged(hp); // Уведомляем слушателей о изменении здоровья
            }
        }
    }

    // Обновить таймер игры
    @Override
    public void updateTimer(float delta) {
        elapsed += delta;// Прибавляем время
        if ((int) (elapsed - delta) != (int) elapsed) {
            for (IStateListener l : listeners) l.onTimeChanged(getTimeString()); // Уведомляем слушателей о времени
        }
    }

    // Сбросить все параметры игры (счет, здоровье, время, патроны)
    public void resetSession() {
        score = 0;
        hp = 100;
        elapsed = 0f;
        ammo = 15;

        // Уведомляем слушателей о сбросе игры
        for (IStateListener l : listeners) {
            l.onScoreChanged(score);
            l.onHPChanged(hp);
            l.onAmmoChanged(ammo);
            l.onTimeChanged(getTimeString());
        }
    }

    // --- ДЛЯ РАЗРАБОТЧИКА ---

    // Сбросить уровень (вернуть в уровень 0)
    public void resetLevel() {
        level = 0;
        prefs.putInteger(KEY_LEVEL, level); // Сохраняем уровень 0 в настройках
        prefs.flush();
        for (IStateListener l : listeners) {
            l.onLevelChanged(level);// Уведомляем слушателей о сбросе уровня
        }
    }

    // Получить текущее количество патронов
    @Override
    public int getAmmo() {
        return ammo;
    }

    // Изменить количество патронов
    @Override
    public void changeAmmo(int delta) {
        int old = ammo;
        ammo = Math.max(0, ammo + delta);// Изменяем количество патронов (не ниже 0)
        if (ammo != old) {
            for (IStateListener l : listeners) {
                l.onAmmoChanged(ammo);// Уведомляем слушателей о новом количестве патронов
            }
        }
    }

}
