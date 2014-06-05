package com.ltkernel.managers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.ltkernel.entities.Enemy;
import com.ltkernel.entities.Person;
import com.ltkernel.items.ProjectileLauncher;
import com.ltkernel.screens.Play;

/**
 * Created by esauKang on 5/29/14.
 */
public class CollisionManager implements ContactListener {
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody() ;
        Body bodyB = contact.getFixtureB().getBody() ;
        Object userData1 = bodyA.getUserData();
        Object userData2 = bodyB.getUserData();
        if (userData1 != null && userData2 != null && userData1 instanceof Person && userData2 instanceof ProjectileLauncher.Bullet) {
            ((Person)(userData1)).setHealth(((Person)(userData1)).getHealth() - randomLoss(20, 30));
            Play.bodiesToDestroy.add(bodyB);
            if (userData1 instanceof Enemy) {
                System.out.println("You hit an enemy!");
                Play.bodiesToDestroy.add(bodyA);
            }
        } else if (userData1 != null && userData2 != null && userData1 instanceof ProjectileLauncher.Bullet && userData2 instanceof Person) {
            ((Person)(userData2)).setHealth(((Person)(userData2)).getHealth() - randomLoss(20, 30));
            Play.bodiesToDestroy.add(bodyA);
            if (userData2 instanceof Enemy) {
                System.out.println("You hit an enemy!");
                Play.bodiesToDestroy.add(bodyA);
            }
        }  else if (userData1 != null && userData2 != null && userData1 instanceof ProjectileLauncher.Bullet && userData2 instanceof ProjectileLauncher.Bullet) {
            return;
        } else if (userData1 != null && userData1 instanceof ProjectileLauncher.Bullet) {
            Play.bodiesToDestroy.add(bodyA);
        } else if (userData2 != null && userData2 instanceof ProjectileLauncher.Bullet) {
            Play.bodiesToDestroy.add(bodyB);
        }
    }

    public void endContact(Contact contact) {
    }

    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    public static int randomLoss(int first, int second) {
        return (int)((Math.random() * (second - first)) + first);
    }
}