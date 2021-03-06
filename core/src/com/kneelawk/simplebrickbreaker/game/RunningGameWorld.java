package com.kneelawk.simplebrickbreaker.game;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kneelawk.simplebrickbreaker.GameManager;
import com.kneelawk.simplebrickbreaker.game.level.LevelManager;
import com.kneelawk.simplebrickbreaker.util.FChangeListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RunningGameWorld extends ScreenAdapter {
    private static final float STARTING_SPEED = 4;

    private static final int VIEW_WIDTH = 1280;
    private static final int VIEW_HEIGHT = 720;

    private final ApplicationLogger log;

    private final GameManager gameManager;

    private final Random rng = new Random();
    private final TextureAtlas atlas;
    private final Skin skin;
    private final Viewport viewport;
    private final Stage stage;
    private final LevelManager levelManager;
    private final ShapeRenderer shapeRenderer;
    private final InputHandler inputHandler = new InputHandler();
    private final Ball ball;
    private Vector2 ballVelocity;
    private final Paddle paddle;
    private final Boundaries boundaries;
    private GameState state = GameState.STARTING, oldState = GameState.STARTING;
    private final List<Collidable> collidables = new ArrayList<>();
    private boolean screenTouched = false;
    private boolean gameScreenTouched = false;
    private Group textGroup;
    private Table gameOverUi;
    private Table levelCompleteUi;
    private final Group brickGroup;
    private final Set<Brick> bricks;
    private final int levelNumber;

    public RunningGameWorld(GameManager gameManager, GameAssets assets, int levelNumber) {
        this.gameManager = gameManager;
        this.levelManager = assets.levelManager;
        this.levelNumber = levelNumber;

        log = assets.log;
        SpriteBatch batch = assets.batch;
        atlas = assets.atlas;
        skin = assets.skin;
        viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT);
        stage = new Stage(viewport, batch);
        stage.getViewport().setScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
        shapeRenderer = assets.shapeRenderer;
        shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);
        boundaries = new Boundaries(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
        collidables.add(boundaries);

        ball = new Ball(atlas);
        stage.addActor(ball);

        paddle = new Paddle(atlas);
        stage.addActor(paddle);
        collidables.add(paddle);

        bricks = new HashSet<>();
        brickGroup = new Group();
        stage.addActor(brickGroup);

        setupUI();
    }

    private void setupUI() {
        textGroup = new Group();
        textGroup.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        stage.addActor(textGroup);

        gameOverUi = new Table();
        gameOverUi.setFillParent(true);
        gameOverUi.row().pad(10, 0, 10, 0);
        Label gameOverLabel = new Label("Game Over", skin);
        gameOverLabel.setAlignment(Align.center);
        gameOverUi.add(gameOverLabel).fillX().uniformX();
        gameOverUi.row().pad(10, 0, 10, 0);
        TextButton retryButton = new TextButton("Retry", skin);
        gameOverUi.add(retryButton).fillX().uniformX();
        gameOverUi.row().pad(10, 0, 10, 0);
        TextButton exitLevelButton = new TextButton("Exit Level", skin);
        gameOverUi.add(exitLevelButton).fillX().uniformX();

        retryButton.addListener((FChangeListener) (event, actor) -> handleRetry());
        exitLevelButton.addListener((FChangeListener) (event, actor) -> gameManager.exitLevel());

        levelCompleteUi = new Table();
        levelCompleteUi.setFillParent(true);
        levelCompleteUi.row().pad(10, 0, 10, 0);
        Label levelCompleteLabel = new Label("Level Complete", skin);
        levelCompleteUi.add(levelCompleteLabel).fillX().uniformX();
        levelCompleteUi.row().pad(10, 0, 10, 0);
        TextButton nextLevelButton = new TextButton("Next Level", skin);
        levelCompleteUi.add(nextLevelButton).fillX().uniformX();
        levelCompleteUi.row().pad(10, 0, 10, 0);
        TextButton exitLevel2Button = new TextButton("Exit Level", skin);
        levelCompleteUi.add(exitLevel2Button).fillX().uniformX();

        nextLevelButton.addListener((FChangeListener) (event, actor) -> gameManager.startNextLevel());
        exitLevel2Button.addListener((FChangeListener) (event, actor) -> gameManager.exitLevel());
    }

    private class InputHandler extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.BACK) {
                gameManager.exitLevel();
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void show() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setInputProcessor(new InputMultiplexer(inputHandler, stage));
        resetGame();
    }

    @Override
    public void hide() {
        Gdx.input.setCatchKey(Input.Keys.BACK, false);
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
    }

    private void resetGame() {
        resetBall();
        resetPaddle();
        resetLevel();
    }

    private void resetBall() {
        ball.setPosition((VIEW_WIDTH - ball.getWidth()) / 2,
                80);
        float angle = (float) (rng.nextFloat() * 2 * Math.PI);
        float len = 4;
        ballVelocity = new Vector2((float) Math.cos(angle) * len, (float) Math.sin(angle) * len);
    }

    private void resetPaddle() {
        paddle.setPosition((VIEW_WIDTH - paddle.getWidth()) / 2, 40);
    }

    private void resetLevel() {
        levelManager.placeBricks(levelNumber, atlas, collidables, brickGroup, bricks, VIEW_WIDTH, VIEW_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();

        stage.draw();
        boundaries.render(shapeRenderer);
    }

    private void update() {
        if (state == GameState.STARTING) {
            stage.act();
            updatePointer();
        }
        if (state == GameState.RUNNING) {
            stage.act();
            updatePaddle();
            updateBall();
        }

        if (state == GameState.GAME_OVER) {
            stage.act();
            if (oldState != GameState.GAME_OVER) {
                textGroup.addActor(gameOverUi);
            }
        }
        if (state != GameState.GAME_OVER && oldState == GameState.GAME_OVER) {
            textGroup.removeActor(gameOverUi);
        }

        if (state == GameState.LEVEL_COMPLETE) {
            stage.act();
            if (oldState != GameState.LEVEL_COMPLETE) {
                textGroup.addActor(levelCompleteUi);
            }
        }
        if (state != GameState.LEVEL_COMPLETE && oldState == GameState.LEVEL_COMPLETE) {
            textGroup.removeActor(levelCompleteUi);
        }

        oldState = state;
    }

    private void updatePointer() {
        Vector2 start = new Vector2(ball.getX() + ball.getWidth() / 2, ball.getY() + ball.getHeight() / 2);
        double angle = Math.atan2(VIEW_HEIGHT - getTouchY() - start.y, getTouchX() - start.x);
        ballVelocity.set((float) Math.cos(angle) * STARTING_SPEED, (float) Math.sin(angle) * STARTING_SPEED);
        Vector2 pointer = new Vector2((float) Math.cos(angle) * STARTING_SPEED * 20,
                (float) Math.sin(angle) * STARTING_SPEED * 20);
        pointer.add(start);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rectLine(start, pointer, 2);
        shapeRenderer.end();

        if (screenTouched && !Gdx.input.isTouched()) {
            state = GameState.RUNNING;
        }

        if (gameScreenTouched) {
            // wait for the screen to stop being touched first before we start detecting screen
            // touches here
            gameScreenTouched = Gdx.input.isTouched();
        } else {
            screenTouched = Gdx.input.isTouched();
        }
    }

    private float getTouchX() {
        return ((float) Gdx.input.getX() - viewport.getLeftGutterWidth()) * VIEW_WIDTH / viewport.getScreenWidth();
    }

    private float getTouchY() {
        return ((float) Gdx.input.getY() - viewport.getTopGutterHeight()) * VIEW_HEIGHT / viewport.getScreenHeight();
    }

    private void updatePaddle() {
        float paddleX = paddle.getX() + paddle.getWidth() / 2;
        float targetX = Math.max(paddle.getWidth() / 2, Math.min(VIEW_WIDTH - paddle.getWidth() / 2,
                getTouchX()));
        float diffX = targetX - paddleX;
        float absDiffX = Math.abs(diffX);
        float velX = 0;
        if (absDiffX > 0) {
            if (absDiffX > 120) {
                if (diffX < 0) {
                    velX = -12;
                } else {
                    velX = 12;
                }
            } else {
                velX = diffX / 10;
            }
        }
        paddle.setX(velX + paddle.getX());

        // update game-screen-touched so that we don't automatically detect a screen touch when the
        // game is over if we're touching the screen right now
        gameScreenTouched = Gdx.input.isTouched();
    }

    private void updateBall() {
        Vector2 start = new Vector2(ball.getX(), ball.getY());
        Vector2 end = new Vector2(start).add(ballVelocity);
        float ballSpeed = ballVelocity.len();

        Paddle collidedPaddle = null;
        RayCastResult paddleCollision = null;

        boolean collided = false;
        boolean intersected;
        float distance;
        do {
            distance = start.dst(end);
            intersected = false;

            Vector2 bottomLeftStart = new Vector2(start),
                    bottomRightStart = new Vector2(start).add(ball.getWidth(), 0),
                    topLeftStart = new Vector2(start).add(0, ball.getHeight()),
                    topRightStart = new Vector2(start).add(ball.getWidth(), ball.getHeight());
            Vector2 bottomLeftEnd = new Vector2(end),
                    bottomRightEnd = new Vector2(end).add(ball.getWidth(), 0),
                    topLeftEnd = new Vector2(end).add(0, ball.getHeight()),
                    topRightEnd = new Vector2(end).add(ball.getWidth(), ball.getHeight());

            RayCastResult bottomLeftCast = rayCast(bottomLeftStart, bottomLeftEnd);
            RayCastResult bottomRightCast = rayCast(bottomRightStart, bottomRightEnd);
            RayCastResult topLeftCast = rayCast(topLeftStart, topLeftEnd);
            RayCastResult topRightCast = rayCast(topRightStart, topRightEnd);

            List<RayCastResult> collisions = new ArrayList<>();

            if (bottomLeftCast.isIntersected() &&
                    bottomLeftCast.getNormal().dot(new Vector2(bottomLeftEnd).sub(bottomLeftStart)) < 0) {
                collisions.add(bottomLeftCast);
                distance = bottomLeftStart.dst(bottomLeftCast.getIntersection());
            }
            if (bottomRightCast.isIntersected() &&
                    bottomRightCast.getNormal().dot(new Vector2(bottomRightEnd).sub(bottomRightStart)) < 0) {
                float bottomRightRayLength = bottomRightStart.dst(bottomRightCast.getIntersection());
                if (roughlyEqual(distance, bottomRightRayLength)) {
                    collisions.add(bottomRightCast);
                } else if (distance > bottomRightRayLength) {
                    collisions.clear();
                    collisions.add(bottomRightCast);
                    distance = bottomRightRayLength;
                }
            }
            if (topLeftCast.isIntersected() &&
                    topLeftCast.getNormal().dot(new Vector2(topLeftEnd).sub(topLeftStart)) < 0) {
                float topLeftRayLength = topLeftStart.dst(topLeftCast.getIntersection());
                if (roughlyEqual(distance, topLeftRayLength)) {
                    collisions.add(topLeftCast);
                } else if (distance > topLeftRayLength) {
                    collisions.clear();
                    collisions.add(topLeftCast);
                    distance = topLeftRayLength;
                }
            }
            if (topRightCast.isIntersected() &&
                    topRightCast.getNormal().dot(new Vector2(topRightEnd).sub(topRightStart)) < 0) {
                float topRightRayLength = topRightStart.dst(topRightCast.getIntersection());
                if (roughlyEqual(distance, topRightRayLength)) {
                    collisions.add(topRightCast);
                } else if (distance > topRightRayLength) {
                    collisions.clear();
                    collisions.add(topRightCast);
                }
            }

            if (collisions.size() > 0) {
                log.debug("Collision-Detection", "Collisions: " + collisions.size());

                intersected = true;
                collided = true;

                Vector2 sumNormal = new Vector2(0, 0);
                Set<Vector2> normalDuplicates = new HashSet<>();
                Set<Collidable> collidableDuplicates = new HashSet<>();
                for (RayCastResult result : collisions) {
                    if (!normalDuplicates.contains(result.getNormal())) {
                        normalDuplicates.add(result.getNormal());
                        sumNormal.add(result.getNormal());
                    }
                    if (!collidableDuplicates.contains(result.getCollidable())) {
                        collidableDuplicates.add(result.getCollidable());
                        handleCollision(result.getCollidable(), result.getNormal());

                        Collidable collidable = result.getCollidable();
                        if (collidable instanceof Paddle) {
                            collidedPaddle = (Paddle) collidable;
                            paddleCollision = result;
                        }
                    }
                }
                sumNormal.nor();

                // use the first collision for intersection because all collisions that happen at the same time will
                // have the same relative intersection
                RayCastResult result = collisions.get(0);

                // get the intersection relative to the start position
                Vector2 intersection = new Vector2(result.getIntersection()).sub(result.getRayStart()).add(start);

                log.debug("Collision-Detection", "Intersection: " + intersection);

                Vector2 remainingRay = new Vector2(end).sub(intersection);
                if (remainingRay.len() == 0) {
                    break;
                }

                log.debug("Collision-Detection", "Remaining ray: " + remainingRay);

                sumNormal.scl(remainingRay.dot(sumNormal));
                log.debug("Collision-Detection", "Projected normal: " + sumNormal);
                end.sub(sumNormal.scl(2));
                log.debug("Collision-Detection", "New end: " + end);
                start.set(intersection);
                log.debug("Collision-Detection", "New start: " + start);
                log.debug("Collision-Detection", "================");
            }
        } while (intersected);

        ballVelocity.set(end).sub(start).setLength(ballSpeed);

        ball.setPosition(end.x, end.y);

        if (collided) {
            addBallBounce(ballVelocity, collidedPaddle, paddleCollision);
        }
    }

    private RayCastResult rayCast(Vector2 start, Vector2 end) {
        float initialDistance = start.dst(end);

        float distance = initialDistance;
        Vector2 normal = null;
        Vector2 intersection = null;
        Collidable detected = null;

        for (Collidable collidable : collidables) {
            RayCastResult result = collidable.rayCast(start, end);
            if (result.isIntersected()) {
                float newDistance = start.dst(result.getIntersection());
                if (newDistance < distance) {
                    distance = newDistance;
                    normal = result.getNormal();
                    intersection = result.getIntersection();
                    detected = collidable;
                }
            }
        }

        return new RayCastResult(distance < initialDistance, start, end, intersection, normal, detected);
    }

    private boolean roughlyEqual(float a, float b) {
        float diff = a - b;
        return diff < 0.001 && diff > -0.001;
    }

    private void handleCollision(Collidable collidable, Vector2 normal) {
        if (collidable instanceof Boundaries && normal.y == 1) {
            state = GameState.GAME_OVER;
        }
        if (collidable instanceof Brick) {
            Brick brick = (Brick) collidable;
            if (brick.getHp() < 1) {
                removeBrick(brick);
            } else {
                brick.decrementHp();
            }
        }
    }

    private void removeBrick(Brick brick) {
        brickGroup.removeActor(brick);
        collidables.remove(brick);
        bricks.remove(brick);

        if (bricks.isEmpty()) {
            gameManager.markCompleted(levelNumber);
            state = GameState.LEVEL_COMPLETE;
        }
    }

    private void addBallBounce(Vector2 vec, Paddle collidedPaddle, RayCastResult paddleCollision) {
        if (vec.len() < 10) {
            vec.scl(1 + rng.nextFloat() * 0.05f);
        }

        if (collidedPaddle != null && paddleCollision != null) {
            collidedPaddle.adjustBallVelocity(vec, paddleCollision);
        } else {
            vec.setAngleDeg(vec.angleDeg() + (rng.nextFloat() * 10 - 5));
        }
    }

    private void handleRetry() {
        resetGame();
        state = GameState.STARTING;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
