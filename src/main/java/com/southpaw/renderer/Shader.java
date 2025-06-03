package com.southpaw.renderer;

import java.io.*;
import java.nio.file.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
public class Shader
{
	private int shaderProgramID;
	private String vertexSource, fragmentSource, filePath;

	public Shader(String filePath) {
		this.filePath = filePath;
		try {
			String source = new String(Files.readAllBytes(Paths.get(filePath)));
			String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");
			int index = source.indexOf("#type") + 6;
			int eol = source.indexOf("\r\n", index);
			String firstPattern = source.substring(index, eol).trim();
			index = source.indexOf("#type", eol) + 6;
			eol = source.indexOf("\r\n", index);
			String secondPattern = source.substring(index, eol).trim();

			if (firstPattern.equals("vertex"))
				vertexSource = splitString[1];
			else if (firstPattern.equals("fragment"))
				fragmentSource = splitString[1];
			else throw new IOException("Unexpected token '" + firstPattern + "'");

			if (secondPattern.equals("vertex"))
				vertexSource = splitString[2];
			else if (secondPattern.equals("fragment"))
				fragmentSource = splitString[2];
			else throw new IOException("Unexpected token '" + secondPattern + "'");
		} catch (IOException e) {
			e.printStackTrace();
			assert false: "Error: Could not open file for shader: '" + filePath + '\'';
		}
		System.out.println(vertexSource);
		System.out.println(fragmentSource);
	}

	public void compile() {
		/* Compile and link shaders */
		int vertexID, fragmentID;
		vertexID = glCreateShader(GL_VERTEX_SHADER);	// Load and compile the vertex shader
		glShaderSource(vertexID, vertexSource);	// Pass shader source to GPU
		glCompileShader(vertexID);
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filePath + "'\n\tVertex shader compilation failed.");
			System.out.println(glGetShaderInfoLog(vertexID, len));
			assert false : "";
		}

		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);	// Load and compile the vertex shader
		glShaderSource(fragmentID, fragmentSource);	// Pass shader source to GPU
		glCompileShader(fragmentID);
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filePath + "'\n\tFragment shader compilation failed.");
			System.out.println(glGetShaderInfoLog(fragmentID, len));
			assert false : "";
		}

		shaderProgramID = glCreateProgram();
		glAttachShader(shaderProgramID, vertexID);
		glAttachShader(shaderProgramID, fragmentID);
		glLinkProgram(shaderProgramID);
		success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filePath + "'\n\tFailed to link shaders.");
			System.out.println(glGetProgramInfoLog(shaderProgramID, len));
			assert false : "";
		}
	}

	public void use() {
		glUseProgram(shaderProgramID);		// bind shader program
	}

	public void detach() {
		glUseProgram(0);
	}
}