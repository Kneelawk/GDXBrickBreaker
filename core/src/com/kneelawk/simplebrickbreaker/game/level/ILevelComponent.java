package com.kneelawk.simplebrickbreaker.game.level;

public interface ILevelComponent {
    <R> R accept(ILevelComponentVisitor<R> visitor);
}
