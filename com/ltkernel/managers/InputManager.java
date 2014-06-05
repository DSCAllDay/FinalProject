package com.ltkernel.managers;

import com.badlogic.gdx.*;

/**
 * Created by esauKang on 5/24/14.
 */
public class InputManager extends InputAdapter {

	public static boolean W;
	public static boolean A;
	public static boolean S;
	public static boolean D;
	public static boolean R;
	public static boolean SHIFT;

	public InputManager() {
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
			case Input.Keys.R:
				R = true;
				break;
			case Input.Keys.SHIFT_LEFT:
				SHIFT = true;
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
			case Input.Keys.R:
				R = false;
				break;
			case Input.Keys.SHIFT_LEFT:
				SHIFT = false;
				break;
		}
		return true;
	}
}

