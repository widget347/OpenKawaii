/*
 * Decompiled with CFR 0.152.
 */
package kawaii.render;

import java.util.Map;
import kawaii.event.HandlerMap;
import org.lwjgl.opengl.GL20;

public abstract class ShaderProgram {
    public int programId;
    public Map<String, Integer> uniformCache = new HandlerMap();

    public int compileShader(String source, int shaderType) {
        int shaderId = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(shaderId, source);
        GL20.glCompileShader(shaderId);
        return shaderId;
    }

    public int getUniformLocation(String name) {
        if (this.uniformCache.isEmpty()) {
            System.out.println("Crashed due to uniformMap moment :(");
        }
        return this.uniformCache.get(name);
    }

    public ShaderProgram(String vertexSource, String fragmentSource) {
        this.programId = GL20.glCreateProgram();
        GL20.glAttachShader(this.programId, this.compileShader(vertexSource, 35633));
        GL20.glAttachShader(this.programId, this.compileShader(fragmentSource, 35632));
        GL20.glLinkProgram(this.programId);
        this.initUniforms();
    }

    public void use() {
        GL20.glUseProgram(this.programId);
        this.updateUniforms();
    }

    public void registerUniform(String name) {
        this.uniformCache.put(name, GL20.glGetUniformLocation(this.programId, name));
    }

    public abstract void updateUniforms();

    public abstract void initUniforms();
}

