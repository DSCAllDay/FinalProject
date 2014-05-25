package com.ltkernel.managers;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.ltkernel.screens.*;

/**
 * Created by esauKang on 5/24/14.
 */
public class InputManager extends InputAdapter {
	private Body body;
	private float speed = 500f;
	private int pKeycode;

	public InputManager(Body body) {
		this.body = body;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
			case Input.Keys.W:
				Play.movement.y = speed;
				break;
			case Input.Keys.A:
				Play.movement.x = -speed;
				break;
			case Input.Keys.S:
				Play.movement.y = -speed;
				break;
			case Input.Keys.D:
				Play.movement.x = speed;
				break;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if((keycode == Input.Keys.A && !(pKeycode == Input.Keys.D)) ||
				keycode == Input.Keys.D && !(pKeycode == Input.Keys.A)) {
			Play.movement.x = 0;
		} else if((keycode == Input.Keys.W && !(pKeycode == Input.Keys.S)) ||
				keycode == Input.Keys.S && !(pKeycode == Input.Keys.W)) {
			Play.movement.y = 0;
		}
		pKeycode = keycode;
		return true;
	}
}
