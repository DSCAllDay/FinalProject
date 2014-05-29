package com.ltkernel.entities;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by esauKang on 5/29/14.
 */
public class Person {

	private float health;
	private float posX;
	private float posY;
	private Body player;
	private Sprite playerSprite, playerHead;

	public Person(String playerBodyPath, String playerHeadPath, float posX, float posY, World world) {

		this.posX = posX;
		this.posY = posY;
		this.health = 100;

		BodyDef personDef = new BodyDef();
		personDef.type = BodyDef.BodyType.DynamicBody;
		personDef.position.set(posX, posY);
		personDef.linearDamping = 4f;
		personDef.angularDamping = 4f;

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(1.3f, .6f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 100f;
		fixtureDef.friction = .75f;
		fixtureDef.restitution = .05f;

		player = world.createBody(personDef);
		player.createFixture(fixtureDef);

		playerSprite = new Sprite(new Texture(playerBodyPath));
		playerSprite.setSize(4, 4);
		playerSprite.setOrigin(playerSprite.getWidth() / 2, playerSprite.getHeight() / 2);
		player.setUserData(playerSprite);

		playerHead = new Sprite(new Texture(playerHeadPath));
		playerHead.setSize(4, 4);
		playerHead.setOrigin(playerHead.getWidth() / 2, playerHead.getHeight() / 2);

		shape.dispose();
	}

	public Body getPlayer() {
		return player;
	}

	public Sprite getPlayerHead() {
		return playerHead;
	}

	public Sprite getPlayerSprite() {
		return playerSprite;
	}

	public void drawHeadOnBody(SpriteBatch sb) {
		this.playerHead.setY(this.player.getPosition().y - this.playerSprite.getHeight() / 2);
		this.playerHead.setX(this.player.getPosition().x - this.playerSprite.getWidth() / 2);
		this.playerHead.setRotation(this.playerSprite.getRotation());
		this.playerHead.draw(sb);
	}
}
