package com.ltkernel.entities;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by tommy_000 on 6/3/2014.
 */
public class Enemy extends Person {
    public Enemy(String playerImgPath, float posX, float posY, World world) {
        super(playerImgPath, posX, posY, world);
    }
}
