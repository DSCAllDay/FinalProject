package com.ltkernel.items;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.ltkernel.entities.*;

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
	public ArrayList<Bullet> bullets;

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

	class Bullet {
		public float timeAlive;
		public float damage;
		public boolean shouldDelete;
		public float bulletRad;
		public BodyDef bulletDef;
		public CircleShape bulletShape;
		public FixtureDef fixtureDef;

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
		}

		public void ignite(Body player, World world) {
			bulletRad = player.getAngle() + MathUtils.PI / 2 + MathUtils.random(-ProjectileLauncher.this.spread, ProjectileLauncher.this.spread);

			this.bulletShape.setPosition(new Vector2(player.getPosition()));
			this.bulletDef.linearVelocity.set(new Vector2(
					player.getLinearVelocity().x + MathUtils.cos(bulletRad) * 7500,
					player.getLinearVelocity().y + MathUtils.sin(bulletRad) * 7500
			));
			Body bullet = world.createBody(bulletDef);
			bullet.createFixture(fixtureDef);
		}
	}
}