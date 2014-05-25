package com.ltkernel.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ltkernel.screens.*;

public class FinalProject extends Game {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		setScreen(new Play());
	}
}
