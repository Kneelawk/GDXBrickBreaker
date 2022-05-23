package com.kneelawk.simplebrickbreaker;

import com.badlogic.gdx.math.Vector2;

public interface Collidable {
    RayCastResult rayCast(Vector2 start, Vector2 end);
}
