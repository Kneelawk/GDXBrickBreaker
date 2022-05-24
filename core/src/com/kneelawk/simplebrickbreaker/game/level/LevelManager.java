package com.kneelawk.simplebrickbreaker.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.kneelawk.simplebrickbreaker.game.Brick;
import com.kneelawk.simplebrickbreaker.game.Collidable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LevelManager {
    private static final String LEVELS_FILE = "levels.txt";
    private static final String PREFERENCES_KEY = "com.kneelawk.simplebrickbreaker.game.level.LevelManager";

    private static final float BRICK_WIDTH = 48;
    private static final float BRICK_HEIGHT = 24;
    private static final float BRICK_PADDING_X = 8;
    private static final float BRICK_PADDING_Y = 8;
    private static final float BRICK_SPACING_X = BRICK_WIDTH + BRICK_PADDING_X;
    private static final float BRICK_SPACING_Y = BRICK_HEIGHT + BRICK_PADDING_Y;
    private static final Pattern LEVEL_START = Pattern.compile("^\\$level (.+)$");

    private final Preferences unlockedLevels;
    private final List<Level> levels = new ArrayList<>();

    public LevelManager() {
        unlockedLevels = Gdx.app.getPreferences(PREFERENCES_KEY);

        try {
            loadLevels(Gdx.files.internal(LEVELS_FILE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadLevels(FileHandle levelsFile) throws IOException {
        BufferedReader reader = levelsFile.reader(8192);
        Level.Builder currentLevel = null;
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            Matcher m = LEVEL_START.matcher(line);
            if (m.matches()) {
                if (currentLevel != null) {
                    levels.add(currentLevel.build());
                }
                currentLevel = new Level.Builder();
                currentLevel.setName(m.group(1));
                lineNumber = 0;
            } else if (currentLevel != null) {
                char[] chars = line.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    char c = chars[i];
                    if (c >= '0' && c <= '8') {
                        currentLevel.setComponent(i, lineNumber, LevelBrick.create(c - '0'));
                    }
                }
                lineNumber++;
            }
        }

        if (currentLevel != null) {
            levels.add(currentLevel.build());
        }
    }

    public boolean isLevelUnlocked(int levelNumber) {
        return levelNumber == 0 || unlockedLevels.contains(levels.get(levelNumber).getName());
    }

    public void unlockLevel(int levelNumber) {
        if (levelNumber >= 0 && levelNumber < levels.size()) {
            unlockedLevels.putBoolean(levels.get(levelNumber).getName(), true);
            unlockedLevels.flush();
        }
    }

    public int getLevelCount() {
        return levels.size();
    }

    public Level getLevel(int level) {
        return levels.get(level);
    }

    public void placeBricks(int levelNumber, final TextureAtlas atlas, final List<Collidable> collidables,
                            final Group brickGroup, final Set<Brick> bricks, float windowWidth, float windowHeight) {
        collidables.removeAll(bricks);
        brickGroup.clear();
        bricks.clear();

        Level level = getLevel(levelNumber);

        final int height = level.getHeight();
        final int width = level.getWidth();

        final float groupX = (windowWidth - width * BRICK_WIDTH - (width - 1) * BRICK_PADDING_X) / 2;
        final float groupY = windowHeight - height * BRICK_HEIGHT - (height - 1) * BRICK_PADDING_Y - 8;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ILevelComponent component = level.getComponent(x, y);
                final int finalY = y;
                final int finalX = x;
                component.accept(new ILevelComponentVisitor<Void>() {
                    @Override
                    public Void visitLevelSpace() {
                        return null;
                    }

                    @Override
                    public Void visitLevelBrick(LevelBrick levelBrick) {
                        Brick brick = new Brick(atlas, levelBrick.getHp());
                        brick.setPosition(finalX * BRICK_SPACING_X + groupX, (height - finalY - 1) * BRICK_SPACING_Y + groupY);
                        collidables.add(brick);
                        brickGroup.addActor(brick);
                        bricks.add(brick);
                        return null;
                    }
                });
            }
        }
    }
}
