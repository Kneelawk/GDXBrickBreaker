package com.kneelawk.simplebrickbreaker;

public interface GameManager {
    void startNextLevel();

    void startLevel(int levelNumber);

    void exitLevel();
}
