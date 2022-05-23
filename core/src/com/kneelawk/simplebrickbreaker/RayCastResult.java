package com.kneelawk.simplebrickbreaker;

import com.badlogic.gdx.math.Vector2;

public class RayCastResult {
    private final boolean intersected;
    private final Vector2 rayStart;
    private final Vector2 rayEnd;
    private final Vector2 intersection;
    private final Vector2 normal;
    private final Collidable collidable;

    public RayCastResult(boolean intersected, Vector2 rayStart, Vector2 rayEnd, Vector2 intersection,
                         Vector2 normal, Collidable collidable) {
        this.intersected = intersected;
        this.rayStart = rayStart;
        this.rayEnd = rayEnd;
        this.intersection = intersection;
        this.normal = normal;
        this.collidable = collidable;
    }

    public boolean isIntersected() {
        return intersected;
    }

    public Vector2 getRayStart() {
        return rayStart;
    }

    public Vector2 getRayEnd() {
        return rayEnd;
    }

    public Vector2 getIntersection() {
        return intersection;
    }

    public Vector2 getNormal() {
        return normal;
    }

    public Collidable getCollidable() {
        return collidable;
    }
}
