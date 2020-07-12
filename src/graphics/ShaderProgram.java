package graphics;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShaderProgram
{
	class Shader
	{
		private int id = 0;
		private int shaderType = 0;

		public Shader()
		{

		}

		public boolean compile(String fileName, int shaderType)
		{
			String source = this.fileToString(fileName);
			if(source == null)
			{
				System.out.println("Failed to load : " + fileName);
				return false;
			}

			this.shaderType = shaderType;
			this.id = glCreateShader(shaderType);
			glShaderSource(this.id, source);
			glCompileShader(this.id);

			if(glGetShaderi(this.id, GL_COMPILE_STATUS) != GL_TRUE)
			{
				System.out.println(glGetShaderInfoLog(this.id));
				return false;
			}

			return true;
		}

		public int getID()
		{
			return this.id;
		}

		public int getShaderType()
		{
			return this.shaderType;
		}

		public String getShaderTypeStr()
		{
			switch(this.shaderType)
			{
			case GL_VERTEX_SHADER:
				return "GL_VERTEX_SHADER";

			case GL_FRAGMENT_SHADER:
				return "GL_FRAGMENT_SHADER";

			default:
				return "Unknown Type";
			}
		}

		public void delete()
		{
			glDeleteShader(this.id);
		}

		private String fileToString(String fileName)
		{
			try(BufferedReader reader = Files.newBufferedReader(Paths.get(fileName)))
			{
				StringBuilder sb = new StringBuilder();
				String line = "";
				while((line = reader.readLine()) != null)
				{
					sb.append(line + "\n");
				}

				return sb.toString();
			}
			catch(IOException e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	private int id = 0;
	private ShaderProgram.Shader vertShader = new ShaderProgram.Shader();
	private ShaderProgram.Shader fragShader = new ShaderProgram.Shader();


	public ShaderProgram()
	{

	}

	public boolean load(String vertProgramFile, String fragProgramFile)
	{
		if(!this.vertShader.compile(vertProgramFile, GL_VERTEX_SHADER)
				|| !this.fragShader.compile(fragProgramFile, GL_FRAGMENT_SHADER))
		{
			return false;
		}

		this.id = glCreateProgram();
		glAttachShader(this.id, this.vertShader.getID());
		glAttachShader(this.id, this.fragShader.getID());
		glLinkProgram(this.id);

		if(glGetProgrami(this.id, GL_LINK_STATUS) != GL_TRUE)
		{
			System.out.println(glGetShaderInfoLog(this.id));
			return false;
		}

		return true;
	}

	public void begin()
	{
		glUseProgram(this.id);
	}

	public void end()
	{
		glUseProgram(0);
	}

	public void unload()
	{
		this.vertShader.delete();
		this.fragShader.delete();
		glDeleteProgram(this.id);
	}

}