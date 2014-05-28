package com.libgdxgaming.angryshooter.Objects;

import com.badlogic.gdx.math.Vector2;

public class PistolBullet extends Object {
    public PistolBullet(float x, float y, Vector2 velocity) {
        super(x, y);

        this.velocity = velocity;
    }

    // We must override the original method in order to make the bullet go a little faster
    @Override
    public void update() {
        position.add(velocity.x * 10, velocity.y * 10);
    }
}