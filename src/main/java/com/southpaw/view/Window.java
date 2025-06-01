package com.southpaw.view;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
	private int width, height;
	private long glfwWindow;
	private String title;
	private static Window window = null;

	private Window() {
		this.width = 1920;
		this.height = 1080;
		this.title = "The Game";
	}

	public void run() {
		System.out.println("LWJGL v" + Version.getVersion());
		init();
		loop();
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
		/* Create the window */
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		if (glfwWindow == NULL)
			throw new IllegalStateException("Failed to create the GLFW Window.");

		glfwMakeContextCurrent(glfwWindow);    // Make OpenGL context current
		glfwSwapInterval(1);    // Enable V-Sync
		glfwShowWindow(glfwWindow);    // Make the window visible

		GL.createCapabilities();    // MUST HAVE THIS LINE
	}

	public void loop() {
		while (!glfwWindowShouldClose(glfwWindow)) {
			glfwPollEvents();
			glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT);
			glfwSwapBuffers(glfwWindow);
		}
	}
}