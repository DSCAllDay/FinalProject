package com.ltkernel.items;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.ltkernel.entities.*;
import com.ltkernel.screens.Play;
import net.dermetfan.utils.libgdx.graphics.Box2DSprite;
import net.dermetfan.utils.libgdx.graphics.*;

import java.util.*;

/**
 * Created by esauKang on 5/29/14.
 */
public class ProjectileLauncher {

	public int currentBullets;
	public int totalBullets;
	public int bulletsPerClip;
	public float reloadTime;
	public boolean isReloading;
	public float timeElapsed;
	public float spread;

	public ProjectileLauncher() {
		currentBullets = 8;
		totalBullets = 64;
		bulletsPerClip = 8;
		reloadTime = 1;
		timeElapsed = 0;
		spread = .005f;
	}

	public ProjectileLauncher(int currentBullets, int totalBullets, int bulletsPerClip, float reloadTime, float spread) {
		this.currentBullets = currentBullets;
		this.totalBullets = totalBullets;
		this.bulletsPerClip = bulletsPerClip;
		this.reloadTime = reloadTime;
		this.timeElapsed = 0;
		this.spread = spread;
	}

	public void fire(Body player, World world, float bulletRad) {
		if(currentBullets == 0 && !isReloading) {
			startReload();
		} else if(currentBullets > 0 && !isReloading) {
			new Bullet().ignite(player, world);
			currentBullets--;
			totalBullets--;
		}
	}

	public void startReload() {
		isReloading = true;
	}

	public void reload() {
		if(totalBullets >= bulletsPerClip) {
			currentBullets = bulletsPerClip;
		} else {
			currentBullets = totalBullets;
		}
		timeElapsed = 0;
		isReloading = false;
	}

	public class Bullet {
		public float timeAlive;
		public float damage;
		public boolean shouldDelete;
		public float bulletRad;
		public BodyDef bulletDef;
		public CircleShape bulletShape;
		public FixtureDef fixtureDef;
        private Body body;
        private int waitTime;
		public Sprite bulletSprite;
		private final String PATH = "TracerRed.png";

		public Bullet() {
			this.shouldDelete = false;
			this.timeAlive = 2;
			this.damage = 34;
			this.bulletDef = new BodyDef();
			this.bulletDef.bullet = true;
			this.bulletDef.type = BodyDef.BodyType.DynamicBody;

			this.bulletShape = new CircleShape();
			this.bulletShape.setRadius(.30f);

			this.fixtureDef = new FixtureDef();
			this.fixtureDef.density = .001f;
			this.fixtureDef.restitution = 0;
			this.fixtureDef.shape = bulletShape;
            Play.bullets.add(this);
            waitTime = 120;
		}
		public Bullet(float damage) {
			this.shouldDelete = false;
			this.timeAlive = 2;
			this.damage = damage;
			this.bulletDef = new BodyDef();
			this.bulletDef.bullet = true;
			this.bulletDef.type = BodyDef.BodyType.DynamicBody;

			this.bulletShape = new CircleShape();
			this.bulletShape.setRadius(.30f);

			this.fixtureDef = new FixtureDef();
			this.fixtureDef.density = .001f;
			this.fixtureDef.restitution = 0;
			this.fixtureDef.shape = bulletShape;
            Play.bullets.add(this);
            waitTime = 120;
		}

		public void ignite(Body player, World world) {
			bulletRad = player.getAngle() +  MathUtils.PI / 2 + MathUtils.random(-ProjectileLauncher.this.spread, ProjectileLauncher.this.spread);
			this.bulletDef.position.set(new Vector2(player.getPosition().x + MathUtils.cos(bulletRad), player.getPosition().y + MathUtils.sin(bulletRad)));
			this.bulletDef.linearVelocity.set(new Vector2(
					player.getLinearVelocity().x + MathUtils.cos(bulletRad) * 7500,
					player.getLinearVelocity().y + MathUtils.sin(bulletRad) * 7500
			));
			body = world.createBody(bulletDef);
			body.createFixture(fixtureDef);
			bulletSprite = new Sprite(new Texture(PATH));
			bulletSprite.setSize(4, 4);
			bulletSprite.setOrigin(
					bulletSprite.getWidth() /  2, bulletSprite.getHeight() / 2);
			bulletSprite.setRotation(player.getAngle() * MathUtils.radiansToDegrees);
            body.setUserData(this);
		}

        public void updateWaitTime() {
            waitTime--;
            if (waitTime <= 0) {
                Play.bodiesToDestroy.add(body);
            }
        }

        public int getWaitTime() {
            return waitTime;
        }

        public Body getBody() {
            return body;
        }

		public Sprite getBulletSprite() {
			return bulletSprite;
		}
	}
}