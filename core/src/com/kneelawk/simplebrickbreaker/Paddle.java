package com.kneelawk.simplebrickbreaker;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Paddle extends Actor implements Collidable {
    private TextureRegion texture;

    public Paddle(TextureAtlas atlas) {
        texture = atlas.findRegion("paddle");
        setSize(texture.getRegionWidth(), texture.getRegionHeight());
    }

    /**
     * Draws the actor. The batch is configured to draw in the parent's coordinate system.
     * {@link Batch#draw(TextureRegion, float, float, float, float, float, float, float, float, float)
     * This draw method} is convenient to draw a rotated and scaled TextureRegion. {@link Batch#begin()} has already been called on
     * the batch. If {@link Batch#end()} is called to draw without the batch then {@link Batch#begin()} must be called before the
     * method returns.
     *
     * @param batch
     * @param parentAlpha The parent alpha, to be multiplied with this actor's alpha, allowing the parent's alpha to affect all
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public RayCastResult rayCast(Vector2 start, Vector2 end) {
        Vector2 bottomLeft = new Vector2(getX(), getY()),
                bottomRight = new Vector2(getX() + getWidth(), getY()),
                topLeft = new Vector2(getX(), getY() + getHeight()),
                topRight = new Vector2(getX() + getWidth(), getY() + getHeight());

        Vector2 intersection = new Vector2();
        if (Intersector.intersectSegments(start, end, bottomLeft, bottomRight, intersection)) {
            return new RayCastResult(true, start, end, intersection, new Vector2(0, -1), this);
        }
        if (Intersector.intersectSegments(start, end, bottomRight, topRight, intersection)) {
            return new RayCastResult(true, start, end, intersection, new Vector2(1, 0), this);
        }
        if (Intersector.intersectSegments(start, end, topLeft, topRight, intersection)) {
            return new RayCastResult(true, start, end, intersection, new Vector2(0, 1), this);
        }
        if (Intersector.intersectSegments(start, end, bottomLeft, topLeft, intersection)) {
            return new RayCastResult(true, start, end, intersection, new Vector2(-1, 0), this);
        }

        return new RayCastResult(false, start, end, null, null, this);
    }

    public void adjustBallVelocity(Vector2 vec, RayCastResult cast) {
        if (!cast.getNormal().equals(new Vector2(0, 1))) {
            return;
        }

        float speed = vec.len();

        float offset = (cast.getIntersection().x - getX()) / getWidth() - 0.5f;

        vec.x += offset * speed;

        vec.setLength(speed);
    }
}
