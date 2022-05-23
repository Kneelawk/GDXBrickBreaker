package com.kneelawk.simplebrickbreaker.level;

public interface ILevelComponentVisitor<R> {
    R visitLevelSpace();
    R visitLevelBrick(LevelBrick levelBrick);
}
