package com.ltkernel.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.ltkernel.managers.*;

/**
 * Created by esauKang on 5/24/14.
 */
public class Play implements Screen {
	private SpriteBatch sb;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera cam;
	private Texture texture;
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private Body player;
	public static Vector2 movement;
	private Sprite playerSprite;
	private Array<Body> tempBodies = new Array<Body>();
	private float edgeX = 4;
	private float edgeY = 3;

	private final float PIXELS_TO_METERS = 32;
	private final float TIMESTEP = 1 / 60f;
	private final int VELOCITYITERATIONS = 8;
	private final int POSITIONITERATIONS = 3;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		debugRenderer.render(world, cam.combined);

		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);
		player.applyLinearImpulse(movement, new Vector2(player.getPosition()), true);










		System.out.println(player.getPosition().x + " " + player.getPosition().y);
		if(player.getPosition().x > edgeX) {

		}
		if(player.getPosition().x < -edgeX) {

		}
		if(player.getPosition().y > edgeY) {

		}
		if(player.getPosition().y < -edgeY) {

		}

		cam.position.set(player.getPosition().x, player.getPosition().y, 0);







		cam.update();

		sb.setProjectionMatrix(cam.combined);

		sb.begin();
		if(Gdx.input.isTouched()) {
			System.out.println("hey");
		}
		world.getBodies(tempBodies);
		for(Body body : tempBodies) {
			if(body.getUserData() != null && body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite)body.getUserData();
				sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.draw(sb);
			}
		}
		sb.end();


	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		movement = new Vector2();
		world = new World(new Vector2(), true);
		debugRenderer = new Box2DDebugRenderer();
		sb = new SpriteBatch();
		cam = new OrthographicCamera(Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 20);

		Gdx.input.setInputProcessor(new InputManager(player));
		//body def
		BodyDef personDef = new BodyDef();
		personDef.type = BodyDef.BodyType.DynamicBody;
		personDef.position.set(1, 1);
		personDef.linearDamping = 4f;

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(1.3f, .7f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1000f;
		fixtureDef.friction = .75f;
		fixtureDef.restitution = .05f;

		player = world.createBody(personDef);
		player.createFixture(fixtureDef);

		playerSprite = new Sprite(new Texture("anims/Bodbod.png"));
		playerSprite.setSize(4, 4);
		playerSprite.setOrigin(playerSprite.getWidth() / 2, playerSprite.getHeight() / 2);
		player.setUserData(playerSprite);

		shape.dispose();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
		playerSprite.getTexture().dispose();
	}
}

