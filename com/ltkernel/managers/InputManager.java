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

	public static boolean W;
	public static boolean A;
	public static boolean S;
	public static boolean D;
	public static boolean SPACE;

	public InputManager(Body body) {
		this.body = body;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
			case Input.Keys.W:
				W = true;
				break;
			case Input.Keys.A:
				A = true;
				break;
			case Input.Keys.S:
				S = true;
				break;
			case Input.Keys.D:
				D = true;
				break;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode) {
			case Input.Keys.W:
				W = false;
				break;
			case Input.Keys.A:
				A = false;
				break;
			case Input.Keys.S:
				S = false;
				break;
			case Input.Keys.D:
				D = false;
				break;
		}
		return true;
	}

}

