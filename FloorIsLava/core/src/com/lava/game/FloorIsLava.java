package com.lava.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lava.game.states.GameStateManager;
import com.lava.game.states.MenuState;
import com.lava.game.utils.SimpleDirectionGestureDetector;

public class FloorIsLava extends ApplicationAdapter {
	// 1280x720
	public static final int WIDTH = 720;//480;
	public static final int HEIGHT = 1280;//800;
	public static final String TITLE = "Lava Game";
	private GameStateManager gsm;
	private SpriteBatch batch;


	public static PlayServices playServices;


	public FloorIsLava(PlayServices playServices) {
		this.playServices = playServices;
	}

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameStateManager();

		// Set background color
		Gdx.gl.glClearColor(1, 0, 0, 1);

		gsm.push(new MenuState(gsm, this));


	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}



}
