package com.kneelawk.simplebrickbreaker.level;

public class LevelSpace implements ILevelComponent {
    public static final LevelSpace INSTANCE = new LevelSpace();

    private LevelSpace() {
    }

    @Override
    public <R> R accept(ILevelComponentVisitor<R> visitor) {
        return visitor.visitLevelSpace();
    }
}
