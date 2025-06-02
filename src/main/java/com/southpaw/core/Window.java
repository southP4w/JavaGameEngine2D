package com.southpaw.core;
import com.southpaw.utils.*;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
	private int width, height;
	private long glfwWindow;
	public float r, g, b, a;
	private String title;
	private boolean fadeToBlack = false;
	private static Window window = null;
	private static Scene currentScene;

	private Window() {
		this.width = 1920;
		this.height = 1080;
		this.title = "The Game";
		this.r = 1;
		this.g = 1;
		this.b = 1;
		this.a = 1;
	}

	public static void changeScene(int newScene) {
		switch (newScene) {
			case 0:
				currentScene = new LevelEditorScene();
//				currentScene.init();
				break;
			case 1:
				currentScene = new LevelScene();
				break;
			default:
				assert false : "Unknown scene '" + newScene + '\'';
				break;
		}
	}

	public void run() {
		System.out.println("LWJGL v" + Version.getVersion());
		init();
		loop();
		/* Free up the memory (not required, but good housekeeping) */
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		glfwTerminate();    // Terminate GLFW
		glfwSetErrorCallback(null).free();    // Free the error callback
	}

	public static Window get() {
		if (Window.window == null)
			Window.window = new Window();
		return Window.window;
	}

	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();    // Set up an error callback
		if (!glfwInit())    // Initialize GLFW
			throw new IllegalStateException("Unable to initialize GLFW.");
		/* Configure GLFW */
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);    // Create the window
		if (glfwWindow == NULL)
			throw new IllegalStateException("Failed to create the GLFW Window.");
		/* Listeners */
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

		glfwMakeContextCurrent(glfwWindow);    // Make OpenGL context current
		glfwSwapInterval(1);    // Enable V-Sync
		glfwShowWindow(glfwWindow);    // Make the window visible

		GL.createCapabilities();    // MUST HAVE THIS LINE

		Window.changeScene(0);
	}

	public void loop() {
		float beginTime = Time.getTime(), endTime = beginTime, deltaTime = -1f;
		while (!glfwWindowShouldClose(glfwWindow)) {
			glfwPollEvents();
			glClearColor(r, g, b, a);
			glClear(GL_COLOR_BUFFER_BIT);
			if (deltaTime >=0)
				currentScene.update(deltaTime);
			glfwSwapBuffers(glfwWindow);
			endTime = Time.getTime();
			deltaTime = endTime - beginTime;
			beginTime = endTime;
		}
	}
}