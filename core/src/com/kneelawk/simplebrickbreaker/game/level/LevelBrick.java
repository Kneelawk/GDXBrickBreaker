package com.kneelawk.simplebrickbreaker.game.level;

public class LevelBrick implements ILevelComponent {
    private final int hp;

    private LevelBrick(int hp) {
        this.hp = hp;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public <R> R accept(ILevelComponentVisitor<R> visitor) {
        return visitor.visitLevelBrick(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LevelBrick that = (LevelBrick) o;

        return hp == that.hp;
    }

    @Override
    public int hashCode() {
        return hp;
    }

    public static LevelBrick create(int hp) {
        return new LevelBrick(hp);
    }

    public static class Builder {
        private int hp;

        public Builder() {
        }

        public Builder(int hp) {
            this.hp = hp;
        }

        public LevelBrick build() {
            return new LevelBrick(hp);
        }

        public int getHp() {
            return hp;
        }

        public Builder setHp(int hp) {
            this.hp = hp;
            return this;
        }
    }
}
