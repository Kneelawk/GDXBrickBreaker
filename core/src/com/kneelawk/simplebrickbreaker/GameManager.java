package com.kneelawk.simplebrickbreaker;

public interface GameManager {
    void markCompleted(int levelNumber);

    void startNextLevel();

    void startLevel(int levelNumber);

    void exitLevel();
}
