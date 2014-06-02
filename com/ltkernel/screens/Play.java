package com.ltkernel.screens;

import box2dLight.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import com.ltkernel.entities.*;
import com.ltkernel.items.*;
import com.ltkernel.managers.*;

import java.util.*;

/**
 * Created by esauKang on 5/24/14.
 */
public class Play implements Screen {
	
	private TiledMap map;
	public ProjectileLauncher weapon;
	private SpriteBatch sb;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera cam;
	private Texture texture;
	private World world;
	private Box2DDebugRenderer debugRenderer;
	public static Vector2 movement;
	private Array<Body> tempBodies = new Array<Body>();
	private float speed = 500f;
	private RayHandler rayHandler;
	private FPSLogger logger;
	private Vector3 camFollow;
	private Vector3 touchPos;
	private float mouseAngle;
	private Vector3 temp;
	private float bulletRad;
	private Body player;
	private Person person;
    public static Array<Body> bodiesToDestroy;
    public static Array<ProjectileLauncher.Bullet> bullets;
    public CollisionManager collisionManager;

	private final float PIXELS_TO_METERS = 32;

	@Override
	public void show() {
        bullets = new Array<ProjectileLauncher.Bullet>();
        bodiesToDestroy = new Array<Body>();
		logger = new FPSLogger();
		movement = new Vector2();
		world = new World(new Vector2(), true);
        collisionManager = new CollisionManager();
        world.setContactListener(collisionManager);
		debugRenderer = new Box2DDebugRenderer();
		sb = new SpriteBatch();
		cam = new OrthographicCamera(Gdx.graphics.getWidth() / 22, Gdx.graphics.getHeight() / 22);
		touchPos = new Vector3();
		camFollow = new Vector3();
		TmxMapLoader loader = new TmxMapLoader();
		map = loader.load("FixedTest.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, .06f);                                             //scale

		Gdx.input.setInputProcessor(new InputManager());

		this.person = new Person("Bodbod.png", "Bodhead.png", 1 , 1, world);
		this.player = person.getPlayer();
		weapon = person.getWeapon();

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
		renderer.render();
		renderer.setView(cam);
		debugRenderer.render(world, cam.combined);
		world.step(1/60f, 8, 3);
        if (bodiesToDestroy.size > 0) {
            destroyBodies();
        }
		handleInput(delta);
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
			} else if (body.getUserData() != null && body.getUserData() instanceof ProjectileLauncher.Bullet) {
                ((ProjectileLauncher.Bullet)(body.getUserData())).updateWaitTime();
            }
		}

		person.drawHeadOnBody(sb);

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

	private void handleInput(float delta) {
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

		if(person.getWeapon().isReloading) {
			person.getWeapon().timeElapsed += delta;
			if(person.getWeapon().timeElapsed >= person.getWeapon().reloadTime) {
				person.getWeapon().reload();
			}
		}


		if(!person.getWeapon().isReloading && InputManager.E) {
			person.getWeapon().startReload();
		}

		if(Gdx.input.justTouched()) {
			weapon.fire(player, world, bulletRad);
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
		person.getPlayerSprite().getTexture().dispose();
		person.getPlayerHead().getTexture().dispose();
		map.dispose();
		renderer.dispose();
	}

    private void destroyBodies() {
        for (int i = 0; i < bodiesToDestroy.size; i++) {
            world.destroyBody(bodiesToDestroy.get(i));
            bodiesToDestroy.get(i).setUserData(null);
            bodiesToDestroy.removeIndex(i);
        }
    }
}

