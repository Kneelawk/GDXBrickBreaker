package com.kneelawk.simplebrickbreaker;

import com.badlogic.gdx.Game;
import com.kneelawk.simplebrickbreaker.game.GameAssets;
import com.kneelawk.simplebrickbreaker.game.RunningGameWorld;
import com.kneelawk.simplebrickbreaker.menu.MenuScreen;

public class SimpleBrickBreakerGameMain extends Game implements GameManager {
    private GameAssets assets;
    private MenuScreen menuScreen;
    private RunningGameWorld world = null;
    private int levelNumber = 0;

    @Override
    public void create() {
        assets = new GameAssets();
        menuScreen = new MenuScreen(this, assets);
        setScreen(menuScreen);
    }

    public void setWorld(RunningGameWorld world) {
        final RunningGameWorld curWorld = this.world;
        if (curWorld != null) {
            curWorld.dispose();
        }

        this.world = world;

        if (world != null) {
            setScreen(world);
        }
    }

    @Override
    public void startNextLevel() {
        levelNumber++;
        if (levelNumber < assets.levelManager.getLevelCount()) {
            setWorld(new RunningGameWorld(this, assets, levelNumber));
        } else {
            exitLevel();
        }
    }

    @Override
    public void startLevel(int levelNumber) {
        if (levelNumber >= 0 && levelNumber < assets.levelManager.getLevelCount()) {
            this.levelNumber = levelNumber;
            setWorld(new RunningGameWorld(this, assets, levelNumber));
        }
    }

    @Override
    public void exitLevel() {
        setWorld(null);
        setScreen(menuScreen);
    }

    @Override
    public void markCompleted(int levelNumber) {
        // does bounds checks in unlockLevel
        assets.levelManager.unlockLevel(levelNumber + 1);
    }

    @Override
    public void dispose() {
        super.dispose();

        final RunningGameWorld curWorld = this.world;
        if (curWorld != null) {
            curWorld.dispose();
        }

        menuScreen.dispose();

        assets.dispose();
    }
}
