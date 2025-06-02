package com.southpaw.core;

import java.awt.event.*;
public class LevelEditorScene extends Scene
{
	private boolean changingScene = false;
	private float timeToChangeScene = 2.0f;

	public LevelEditorScene() {
		System.out.println("Inside level editor scene.");
	}

	@Override
	public void update(float deltaTime) {
		if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE))
			changingScene = true;
		if (changingScene && timeToChangeScene > 0) {
			timeToChangeScene -= deltaTime;
			Window.get().r -= deltaTime*5f;
			Window.get().g -= deltaTime*5f;
			Window.get().b -= deltaTime*5f;
		} else if (changingScene)
			Window.changeScene(1);
	}
}
