package com.kneelawk.simplebrickbreaker.level;

public interface ILevelComponent {
    <R> R accept(ILevelComponentVisitor<R> visitor);
}
