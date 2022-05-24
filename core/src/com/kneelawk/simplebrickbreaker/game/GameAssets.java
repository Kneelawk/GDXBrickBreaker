package com.kneelawk.simplebrickbreaker.game;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.kneelawk.simplebrickbreaker.game.level.LevelManager;

public class GameAssets implements Disposable {
    public final ApplicationLogger log;

    public final TextureAtlas atlas;
    public final SpriteBatch batch;
    public final Skin skin;
    public final ShapeRenderer shapeRenderer;
    public final LevelManager levelManager;

    public GameAssets() {
        log = Gdx.app.getApplicationLogger();
        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("brickBreaker.atlas"));
        skin = new Skin(Gdx.files.internal("brickBreaker.json"), atlas);
        // set font to be a size that's actually readable
        skin.getFont("default-font").getData().setScale(5f);
        shapeRenderer = new ShapeRenderer();
        levelManager = new LevelManager();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        skin.dispose();
        atlas.dispose();
    }
}
