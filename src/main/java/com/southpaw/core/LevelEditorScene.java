package com.southpaw.core;

import com.southpaw.renderer.*;
import org.lwjgl.*;
import java.nio.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
public class LevelEditorScene extends Scene
{
	private String vertexShaderSrc = "#version 330 core\n" +
		"layout (location=0) in vec3 aPos;\n" +
		"layout (location=1) in vec4 aColor;\n" +
		"\n" +
		"out vec4 fColor;\n" +
		"\n" +
		"void main() {\n" +
		"    fColor = aColor;\n" +
		"    gl_Position = vec4(aPos, 1.0);\n" +
		"}", fragmentShaderSrc = "#version 330 core\n" +
		"\n" +
		"in vec4 fColor;\n" +
		"\n" +
		"out vec4 color;\n" +
		"\n" +
		"void main() {\n" +
		"    color = fColor;\n" +
		"}";

	private int vertexID, fragmentID, shaderProgram;
	private float[] vertexArray = {
		 0.5f, -0.5f, 0.0f,		1.0f, 0.0f, 0.0f, 1.0f,	// bottom-right	(0)
		-0.5f,  0.5f, 0.0f,		0.0f, 1.0f, 0.0f, 1.0f,	// top-left		(1)
		 0.5f,  0.5f, 0.0f,		0.0f, 0.0f, 1.0f, 1.0f,	// top-right	(2)
		-0.5f, -0.5f, 0.0f,		1.0f, 1.0f, 0.0f, 1.0f	// bottom-left	(3)
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
		defaultShader.use();
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
