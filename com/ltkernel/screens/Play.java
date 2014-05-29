package com.ltkernel.screens;

import box2dLight.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.ltkernel.entities.*;
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
	private Sprite playerSprite, playerHead;
	private Array<Body> tempBodies = new Array<Body>();
	private float speed = 500f;
	private RayHandler rayHandler;
	private FPSLogger logger;
	private Vector3 camFollow;
	private Vector3 touchPos;
	private float mouseAngle;
	private Vector3 temp;
	private float bulletRad;
	private Person person;

	private final float PIXELS_TO_METERS = 32;

	@Override
	public void show() {
		logger = new FPSLogger();
		movement = new Vector2();
		world = new World(new Vector2(), true);
		debugRenderer = new Box2DDebugRenderer();
		sb = new SpriteBatch();
		cam = new OrthographicCamera(Gdx.graphics.getWidth() / 20, Gdx.graphics.getHeight() / 20);
		touchPos = new Vector3();
		camFollow = new Vector3();

		Gdx.input.setInputProcessor(new InputManager());

		this.person = new Person("anims/Bodbod.png", "anims/Bodhead.png", 1 , 1, world);
		this.player = person.getPlayer();

		//groundbody

		BodyDef personDef = new BodyDef();
		personDef.type = BodyDef.BodyType.StaticBody;
		personDef.position.set(-1, -1);

		ChainShape groundShape = new ChainShape();
		groundShape.createChain(new Vector2[] {
				new Vector2(-20,0), new Vector2(20, 0), new Vector2(25, 10)
		});

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = groundShape;
		fixtureDef.friction = .5f;
		fixtureDef.restitution = 0;

		world.createBody(personDef).createFixture(fixtureDef);

		groundShape.dispose();

		//boxbody

		personDef.type = BodyDef.BodyType.DynamicBody;
		personDef.position.set(5, 10);

		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(2, 2);

		fixtureDef.shape = boxShape;
		fixtureDef.friction = .5f;
		fixtureDef.restitution = .5f;
		fixtureDef.density = 50;

		world.createBody(personDef).createFixture(fixtureDef);

		boxShape.dispose();

		//rayHandler = new RayHandler(world);
		//rayHandler.setCombinedMatrix(cam.combined);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

		debugRenderer.render(world, cam.combined);
		world.step(1/60f, 8, 3);

		handleInput();

		player.applyLinearImpulse(movement, new Vector2(player.getPosition()), true);

		updateCamera();

		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		temp = cam.unproject(touchPos);

		mouseAngle = MathUtils.atan2(temp.y - player.getPosition().y, temp.x - player.getPosition().x);

		player.setTransform(new Vector2(player.getPosition().x, player.getPosition().y), mouseAngle + MathUtils.PI / 2);

		sb.setProjectionMatrix(cam.combined);
		//rayHandler.updateAndRender();

		sb.begin();

		world.getBodies(tempBodies);
		for(Body body : tempBodies) {
			if(body.getUserData() != null && body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite)body.getUserData();
				sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.draw(sb);
			}
		}

		person.getPlayerHead().setY(person.getPlayer().getPosition().y - person.getPlayerSprite().getHeight() / 2);
		person.getPlayerHead().setX(person.getPlayer().getPosition().x - person.getPlayerSprite().getWidth() / 2);
		person.getPlayerHead().setRotation(person.getPlayerSprite().getRotation());
		person.getPlayerHead().draw(sb);

		sb.end();
		logger.log();
	}

	private void updateCamera() {
		if(player.getPosition().x >= cam.position.x + 2.5f) {
			cam.position.set(player.getPosition().x - 2.5f, cam.position.y, 0);
		} else if(player.getPosition().x <= cam.position.x - 2.5f) {
			cam.position.set(player.getPosition().x + 2.5f, cam.position.y, 0);
		}
		if(player.getPosition().y >= cam.position.y + 2.5f) {
			cam.position.set(cam.position.x, player.getPosition().y - 2.5f, 0);
		} else if(player.getPosition().y <= cam.position.y - 2.5f) {
			cam.position.set(cam.position.x, player.getPosition().y + 2.5f, 0);
		}

		cam.position.add(camFollow.set(player.getPosition().x, player.getPosition().y,0).sub(cam.position).scl(.1f));
		cam.update();
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

		if(Gdx.input.justTouched()) {
			bulletRad = player.getAngle() + MathUtils.PI / 2 + MathUtils.random(-.005f, .005f);

			BodyDef bulletDef = new BodyDef();
			bulletDef.type = BodyDef.BodyType.DynamicBody;
			bulletDef.linearVelocity.set(new Vector2(
					player.getLinearVelocity().x + MathUtils.cos(bulletRad) * 6000,
					player.getLinearVelocity().y + MathUtils.sin(bulletRad) * 6000
			));

			CircleShape bulletShape = new CircleShape();
			bulletShape.setRadius(.30f);
			bulletShape.setPosition(new Vector2(player.getPosition()));

			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.density = .001f;
			fixtureDef.restitution = 0;
			fixtureDef.shape = bulletShape;

			Body bullet = world.createBody(bulletDef);
			bullet.isBullet();
			bullet.createFixture(fixtureDef);
		}

	}

	@Override
	public void resize(int width, int height) {

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
		playerHead.getTexture().dispose();
	}
}

