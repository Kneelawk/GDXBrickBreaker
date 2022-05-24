package com.kneelawk.simplebrickbreaker.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kneelawk.simplebrickbreaker.GameManager;
import com.kneelawk.simplebrickbreaker.game.GameAssets;
import com.kneelawk.simplebrickbreaker.game.level.Level;
import com.kneelawk.simplebrickbreaker.game.level.LevelManager;
import com.kneelawk.simplebrickbreaker.util.FChangeListener;

public class MenuScreen extends ScreenAdapter {
    private static final int VIEWPORT_WITH = 1280;
    private static final int VIEWPORT_HEIGHT = 720;

    private final GameManager gameManager;
    private final Viewport viewport;
    private final Skin skin;
    private final Stage stage;
    private final LevelManager levelManager;

    public MenuScreen(GameManager gameManager, GameAssets assets) {
        this.gameManager = gameManager;
        skin = assets.skin;
        levelManager = assets.levelManager;

        viewport = new FitViewport(VIEWPORT_WITH, VIEWPORT_HEIGHT);
        stage = new Stage(viewport);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table mainMenu = new Table();
        mainMenu.setFillParent(true);
        stage.addActor(mainMenu);

        mainMenu.row().pad(10, 0, 10, 0);
        TextButton newGameButton = new TextButton("New Game", skin);
        mainMenu.add(newGameButton).fillX().uniformX();

        mainMenu.row().pad(10, 0, 10, 0);
        TextButton levelsButton = new TextButton("Levels", skin);
        mainMenu.add(levelsButton).fillX().uniformX();

        mainMenu.row().pad(10, 0, 10, 0);
        TextButton exitButton = new TextButton("Exit", skin);
        mainMenu.add(exitButton).fillX().uniformX();

        Table levelsMenu = new Table();
        levelsMenu.setFillParent(true);

        levelsMenu.row().pad(10, 0, 10, 0);
        TextButton backToMainMenuButton = new TextButton("Back to Main Menu", skin);
        levelsMenu.add(backToMainMenuButton).fillX().uniformX();

        levelsMenu.row().pad(10, 0, 10, 0);
        Table levels = new Table();
        levels.setFillParent(true);
        ScrollPane levelsScroll = new ScrollPane(levels);
        levelsMenu.add(levelsScroll).fill().uniformX();

        for (int i = 0; i < levelManager.getLevelCount(); i++) {
            levels.row().pad(10, 0, 10, 0);

            Level level = levelManager.getLevel(i);
            TextButton levelButton = new TextButton(level.getName(), skin);
            levelButton.setDisabled(!levelManager.isLevelUnlocked(i));
            levels.add(levelButton).fillX().uniformX();

            final int levelNumber = i;
            levelButton.addListener((FChangeListener) (event, actor) -> gameManager.startLevel(levelNumber));
        }

        newGameButton.addListener((FChangeListener) (event, actor) -> gameManager.startLevel(0));
        exitButton.addListener((FChangeListener) (event, actor) -> Gdx.app.exit());

        levelsButton.addListener((FChangeListener) (event, actor) -> {
            stage.clear();
            stage.addActor(levelsMenu);
        });
        backToMainMenuButton.addListener((FChangeListener) (event, actor) -> {
            stage.clear();
            stage.addActor(mainMenu);
        });
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        stage.clear();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
