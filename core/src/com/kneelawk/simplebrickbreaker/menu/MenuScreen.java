package com.kneelawk.simplebrickbreaker.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kneelawk.simplebrickbreaker.GameManager;
import com.kneelawk.simplebrickbreaker.game.GameAssets;
import com.kneelawk.simplebrickbreaker.util.FChangeListener;

public class MenuScreen extends ScreenAdapter {
    private final Viewport viewport;
    private final Stage stage;

    public MenuScreen(GameManager gameManager, GameAssets assets) {
        Skin skin = assets.skin;

        viewport = new ScreenViewport();
        stage = new Stage(viewport);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton newGameButton = new TextButton("New Game", skin);
        table.add(newGameButton).fillX().uniformX();
        table.row().pad(10);

        TextButton exitButton = new TextButton("Exit", skin);
        table.add(exitButton).fillX().uniformX();

        newGameButton.addListener((FChangeListener) (event, actor) -> gameManager.startLevel(0));
        exitButton.addListener((FChangeListener) (event, actor) -> Gdx.app.exit());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
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
