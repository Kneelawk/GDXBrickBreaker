package com.kneelawk.simplebrickbreaker.game.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level {
    private final String name;
    private final ILevelComponent[][] components;

    public Level(String name, ILevelComponent[][] components) {
        this.name = name;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public ILevelComponent getComponent(int x, int y) {
        return components[y][x];
    }

    public int getHeight() {
        return components.length;
    }

    public int getWidth() {
        return components.length > 0 ? components[0].length : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        return Arrays.deepEquals(components, level.components);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(components);
    }

    public static class Builder {
        private String name;
        private List<List<ILevelComponent>> components = new ArrayList<>();

        public Builder() {
        }

        public Builder(String name, ILevelComponent[][] components) {
            this.name = name;
            for (ILevelComponent[] rowArray : components) {
                this.components.add(new ArrayList<>(Arrays.asList(rowArray)));
            }
        }

        public Level build() {
            // build primitive array out of the components lists
            int height = components.size();
            int width = 0;
            for (List<ILevelComponent> row : components) {
                if (row.size() > width) {
                    width = row.size();
                }
            }

            ILevelComponent[][] componentArray = new ILevelComponent[height][width];
            for (int y = 0; y < height; y++) {
                List<ILevelComponent> row = components.get(y);
                for (int x = 0; x < width; x++) {
                    if (x < row.size() && row.get(x) != null) {
                        componentArray[y][x] = row.get(x);
                    } else {
                        componentArray[y][x] = LevelSpace.INSTANCE;
                    }
                }
            }

            return new Level(name, componentArray);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<List<ILevelComponent>> getComponents() {
            return components;
        }

        public Builder setComponents(ILevelComponent[][] components) {
            for (ILevelComponent[] rowArray : components) {
                this.components.add(new ArrayList<>(Arrays.asList(rowArray)));
            }
            return this;
        }

        public Builder setComponents(List<List<ILevelComponent>> components) {
            this.components.clear();
            this.components.addAll(components);
            return this;
        }

        public Builder setComponent(int x, int y, ILevelComponent component) {
            while (y >= components.size()) {
                components.add(new ArrayList<ILevelComponent>());
            }
            List<ILevelComponent> row = components.get(y);
            while (x >= row.size()) {
                row.add(LevelSpace.INSTANCE);
            }
            row.set(x, component);
            return this;
        }
    }
}
