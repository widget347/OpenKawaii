/*
 * Decompiled with CFR 0.152.
 */
package kawaii.render;

import java.nio.FloatBuffer;
import kawaii.render.BlurRenderer;
import kawaii.render.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;

public class BlurShader
extends ShaderProgram {
    @Override
    public void initUniforms() {
        this.registerUniform("texture");
        this.registerUniform("texture2");
        this.registerUniform("radius");
        this.registerUniform("texelSize");
        this.registerUniform("direction");
        this.registerUniform("weights");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(this.getUniformLocation("texture"), 0);
        GL20.glUniform1i(this.getUniformLocation("texture2"), 20);
        GL20.glUniform1f(this.getUniformLocation("radius"), BlurRenderer.getRadius());
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(256);
        float halfRadius = BlurRenderer.getRadius() / 2.0f;
        int i = 0;
        while ((float)i <= halfRadius) {
            floatBuffer.put(BlurRenderer.gaussianWeight(i, BlurRenderer.getRadius() / 4.0f));
            ++i;
        }
        floatBuffer.rewind();
        GL20.glUniform1(this.getUniformLocation("weights"), floatBuffer);
        GL20.glUniform2f(this.getUniformLocation("texelSize"), 1.0f / (float)Display.getWidth(), 1.0f / (float)Display.getHeight());
    }

    public BlurShader(String vertexShaderSource, String fragmentShaderSource) {
        super(vertexShaderSource, fragmentShaderSource);
    }
}

