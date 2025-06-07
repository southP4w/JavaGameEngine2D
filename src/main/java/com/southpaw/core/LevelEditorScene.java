package com.southpaw.core;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import com.southpaw.renderer.Shader;
import com.southpaw.utils.Time;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.GL_FLOAT;
import static org.lwjgl.opengl.GL20.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL20.glBindBuffer;
import static org.lwjgl.opengl.GL20.glBufferData;
import static org.lwjgl.opengl.GL20.glDrawElements;
import static org.lwjgl.opengl.GL20.glGenBuffers;

public class LevelEditorScene extends Scene
{
	private int vertexID, fragmentID, shaderProgram;

	private float[] vertexArray = {
		 100.5f, .5f, 0.0f,		1.0f, 0.0f, 0.0f, 1.0f,	// bottom-right	(0)
		.5f,  100.5f, 0.0f,		0.0f, 1.0f, 0.0f, 1.0f,	// top-left		(1)
		 100.5f,  100.5f, 0.0f,		0.0f, 0.0f, 1.0f, 1.0f,	// top-right	(2)
		.5f, .5f, 0.0f,		1.0f, 1.0f, 0.0f, 1.0f	// bottom-left	(3)
	};
	private int[] elementArray = {
		/**
				x		x

				x		x
		*/
		2, 1, 0,
		0, 1, 3
	};

	private int vaoID, vboID, eboID;
	private Shader defaultShader;

	public LevelEditorScene() {

	}

	@Override
	public void init() {
		this.camera = new Camera(new Vector2f(-200, -300));
		defaultShader = new Shader("assets/shaders/default.glsl");
		defaultShader.compile();
		/* Generate VAO, VBO, EBO buffer objects and send them to the GPU */
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);	// create a float buffer of vertices
		vertexBuffer.put(vertexArray).flip();
		/* Create VBO and upload the vertex buffer */
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		/* Create indices and upload */
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
		elementBuffer.put(elementArray).flip();
		eboID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
		int positionsSize = 3, colorSize = 4, floatSizeInBytes = 4, vertexSizeInBytes = (positionsSize + colorSize)*floatSizeInBytes;
		glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeInBytes, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeInBytes, positionsSize*floatSizeInBytes);
		glEnableVertexAttribArray(1);
	}

	@Override
	public void update(float deltaTime) {
		camera.position.x -= deltaTime * 50f;
		camera.position.y -= deltaTime * 20f;
		defaultShader.use();
		defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
		defaultShader.uploadMat4f("uView", camera.getViewMatrix());
		defaultShader.uploadFloat("uTime", Time.getTime());
		glBindVertexArray(vaoID);			// bind the VAO we're using
		glEnableVertexAttribArray(0);	// enable the vertex attribute pointers
		glEnableVertexAttribArray(1);
		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);	// draw elements
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		defaultShader.detach();
	}
}
