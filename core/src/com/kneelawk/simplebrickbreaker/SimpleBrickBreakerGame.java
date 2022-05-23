package com.kneelawk.simplebrickbreaker;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kneelawk.simplebrickbreaker.level.LevelMaster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SimpleBrickBreakerGame extends ApplicationAdapter {
    private static final float STARTING_SPEED = 4;

    private static final int VIEW_WIDTH = 1280;
    private static final int VIEW_HEIGHT = 720;

    private ApplicationLogger log;

    private final Random rng = new Random();
    private SpriteBatch batch;
    private TextureAtlas atlas;
    private Skin skin;
    private Stage stage;
    private LevelMaster levelMaster;
    private ShapeRenderer shapeRenderer;
    private Ball ball;
    private Vector2 ballVelocity;
    private Paddle paddle;
    private Boundaries boundaries;
    private GameState state = GameState.STARTING, oldState = GameState.STARTING;
    private final List<Collidable> collidables = new ArrayList<>();
    private boolean screenTouched = false;
    private Group textGroup;
    private Group gameOverGroup;
    private Group levelCompleteGroup;
    private Group brickGroup;
    private Set<Brick> bricks;
    private int levelNumber;

    @Override
    public void create() {
        log = Gdx.app.getApplicationLogger();
        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("brickBreaker.atlas"));
        skin = new Skin(Gdx.files.internal("brickBreaker.json"), atlas);
        stage = new Stage(new FitViewport(VIEW_WIDTH, VIEW_HEIGHT), batch);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(stage.getViewport().getCamera().combined);
        boundaries = new Boundaries(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
        collidables.add(boundaries);

        setupBall();
        setupPaddle();
        setupBricks();
        setupLabels();
        setupLevel();
        resetGame();
    }

    private void setupBall() {
        ball = new Ball(atlas);
        stage.addActor(ball);
    }

    private void setupPaddle() {
        paddle = new Paddle(atlas);
        stage.addActor(paddle);
        collidables.add(paddle);
    }

    private void setupBricks() {
        bricks = new HashSet<>();
        brickGroup = new Group();
        stage.addActor(brickGroup);
    }

    private void setupLabels() {
        textGroup = new Group();
        stage.addActor(textGroup);

        gameOverGroup = new Group();
        gameOverGroup.setScale(3f);
        Label gameOverLabel = new Label("Game Over", skin);
        gameOverLabel.setPosition((VIEW_WIDTH - gameOverLabel.getWidth() * 3) / 6f,
                (VIEW_HEIGHT - gameOverLabel.getHeight() * 3) / 6f);
        Label pressToRetryLabel = new Label("Press to Retry", skin);
        gameOverGroup.addActor(gameOverLabel);
        pressToRetryLabel.setPosition((VIEW_WIDTH - pressToRetryLabel.getWidth() * 3) / 6f,
                (VIEW_HEIGHT - pressToRetryLabel.getHeight() * 3) / 6f - 20);
        gameOverGroup.addActor(pressToRetryLabel);

        levelCompleteGroup = new Group();
        levelCompleteGroup.setScale(3f);
        Label levelCompleteLabel = new Label("Level Complete", skin);
        levelCompleteLabel.setPosition((VIEW_WIDTH - levelCompleteLabel.getWidth() * 3) / 6f,
                (VIEW_HEIGHT - levelCompleteLabel.getHeight() * 3) / 6f);
        levelCompleteGroup.addActor(levelCompleteLabel);
    }

    private void setupLevel() {
        levelMaster = new LevelMaster(Gdx.files.internal("levels.txt"));
        levelNumber = 0;
    }

    private void resetLevel() {
        levelMaster.placeBricks(levelNumber, atlas, collidables, brickGroup, bricks, VIEW_WIDTH, VIEW_HEIGHT);
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

    @Override
    public void render() {
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
            handleGameOver();
            if (oldState != GameState.GAME_OVER) {
                textGroup.addActor(gameOverGroup);
            }
        }
        if (state != GameState.GAME_OVER && oldState == GameState.GAME_OVER) {
            textGroup.removeActor(gameOverGroup);
        }

        if (state == GameState.LEVEL_COMPLETE) {
            stage.act();
            handleLevelComplete();
            if (oldState != GameState.LEVEL_COMPLETE) {
                textGroup.addActor(levelCompleteGroup);
            }
        }
        if (state != GameState.LEVEL_COMPLETE && oldState == GameState.LEVEL_COMPLETE) {
            textGroup.removeActor(levelCompleteGroup);
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
        screenTouched = Gdx.input.isTouched();
    }

    private float getTouchX() {
        return ((float) Gdx.input.getX()) * VIEW_WIDTH / Gdx.graphics.getWidth();
    }

    private float getTouchY() {
        return ((float) Gdx.input.getY()) * VIEW_HEIGHT / Gdx.graphics.getHeight();
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

    private void handleGameOver() {
        if (screenTouched && !Gdx.input.isTouched()) {
            resetGame();
            setupRetry();
            state = GameState.STARTING;
        }
        screenTouched = Gdx.input.isTouched();
    }

    private void setupRetry() {
        // we'll just leave everything the same for a retry for now
    }

    private void handleLevelComplete() {
        if (screenTouched && !Gdx.input.isTouched()) {
            if (levelNumber + 1 < levelMaster.getLevelCount()) {
                levelNumber++;
                resetGame();
                state = GameState.STARTING;
            } else {
                state = GameState.NO_MORE_LEVELS;
            }
        }
        screenTouched = Gdx.input.isTouched();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        atlas.dispose();
    }
}
