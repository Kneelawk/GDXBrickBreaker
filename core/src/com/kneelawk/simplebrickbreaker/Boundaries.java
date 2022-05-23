package com.kneelawk.simplebrickbreaker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class Boundaries implements Collidable {
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    public Boundaries(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public RayCastResult rayCast(Vector2 start, Vector2 end) {
        Vector2 bottomLeft = new Vector2(x, y),
                bottomRight = new Vector2(x + width, y),
                topLeft = new Vector2(x, y + height),
                topRight = new Vector2(x + width, y + height);

        Vector2 intersection = new Vector2();
        if (Intersector.intersectSegments(start, end, bottomLeft, bottomRight, intersection)) {
            return new RayCastResult(true, start, end, intersection, new Vector2(0, 1), this);
        }
        if (Intersector.intersectSegments(start, end, bottomRight, topRight, intersection)) {
            return new RayCastResult(true, start, end, intersection, new Vector2(-1, 0), this);
        }
        if (Intersector.intersectSegments(start, end, topLeft, topRight, intersection)) {
            return new RayCastResult(true, start, end, intersection, new Vector2(0, -1), this);
        }
        if (Intersector.intersectSegments(start, end, bottomLeft, topLeft, intersection)) {
            return new RayCastResult(true, start, end, intersection, new Vector2(1, 0), this);
        }

        return new RayCastResult(false, start, end, null, null, this);
    }
}
