package com.kneelawk.simplebrickbreaker.game.level;

public interface ILevelComponentVisitor<R> {
    R visitLevelSpace();
    R visitLevelBrick(LevelBrick levelBrick);
}
