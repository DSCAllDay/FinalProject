package com.ltkernel.screens;

import box2dLight.*;
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
	private float speed = 500f;
	private RayHandler rayHandler;
	private FPSLogger logger;
	private Vector3 camFollow;

	private final float PIXELS_TO_METERS = 32;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		debugRenderer.render(world, cam.combined);

		world.step(1/60f, 8, 3);
		handleInput();

		player.applyLinearImpulse(movement, new Vector2(player.getPosition()), true);
		//player.setTransform(player.getPosition(), Gdx.input.getX());

		if(player.getPosition().x >= cam.position.x + 2.5f) {
			cam.position.set(player.getPosition().x - 2.5f, cam.position.y, 0);
		}
		if(player.getPosition().x <= cam.position.x - 2.5f) {
			cam.position.set(player.getPosition().x + 2.5f, cam.position.y, 0);
		}
		if(player.getPosition().y >= cam.position.y + 2.5f) {
			cam.position.set(cam.position.x, player.getPosition().y - 2.5f, 0);
		}
		if(player.getPosition().y <= cam.position.y - 2.5f) {
			cam.position.set(cam.position.x, player.getPosition().y + 2.5f, 0);
		}

		cam.position.add(camFollow.set(player.getPosition().x, player.getPosition().y,0).sub(cam.position).scl(.1f));
		cam.update();

		sb.setProjectionMatrix(cam.combined);
		//rayHandler.updateAndRender();

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
		logger.log();
	}

	private void handleInput() {
		if(InputManager.W) {
			movement.y = speed;
		} else if (InputManager.S) {
			movement.y = -speed;
		} else if(!InputManager.W && !InputManager.S) {
			movement.y = 0;
		}

		if(InputManager.A) {
			movement.x = -speed;
		} else if(InputManager.D) {
			movement.x = speed;
		} else if(!InputManager.A && !InputManager.D) {
			movement.x = 0;
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		logger = new FPSLogger();
		movement = new Vector2();
		world = new World(new Vector2(), true);
		debugRenderer = new Box2DDebugRenderer();
		sb = new SpriteBatch();
		cam = new OrthographicCamera(Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 20);

		camFollow = new Vector3();
		Gdx.input.setInputProcessor(new InputManager(player));

		//body def
		BodyDef personDef = new BodyDef();
		personDef.type = BodyDef.BodyType.DynamicBody;
		personDef.position.set(0, 0);
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

		playerSprite = new Sprite(new Texture("anims/Bodbod.png"));
		playerSprite.setSize(4, 4);
		playerSprite.setOrigin(playerSprite.getWidth() / 2, playerSprite.getHeight() / 2);
		player.setUserData(playerSprite);

		shape.dispose();

		personDef.type = BodyDef.BodyType.StaticBody;
		personDef.position.set(-1, -1);

		ChainShape groundShape = new ChainShape();
		groundShape.createChain(new Vector2[] {
				new Vector2(-20,0), new Vector2(20, 0), new Vector2(25, 10)
		});

		fixtureDef.shape = groundShape;
		fixtureDef.friction = .5f;
		fixtureDef.restitution = 0;

		world.createBody(personDef).createFixture(fixtureDef);

		//rayHandler = new RayHandler(world);
		//rayHandler.setCombinedMatrix(cam.combined);
		groundShape.dispose();
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

