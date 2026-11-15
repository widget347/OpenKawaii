/*
 * Decompiled with CFR 0.152.
 */
package kawaii.render;

import java.util.List;
import kawaii.module.render.Blur;
import kawaii.render.BlurShader;
import kawaii.render.RenderUtils;
import kawaii.render.ShaderProgram;
import kawaii.util.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class BlurRenderer {
    public static ScaledResolution scaledResolution;
    public static String blurFragmentSource;
    public static List<double[]> queue;
    public static Framebuffer blurFramebufferA;
    public static String blurVertexSource;
    public static ShaderProgram shader;
    public static Minecraft mc;
    public static Framebuffer blurFramebufferB;

    public static void setDirection(int directionY) {
        GL20.glUniform2f(shader.getUniformLocation("direction"), 1 - directionY, directionY);
    }

    public static float gaussian(float x, float sigma) {
        float ratio = x / sigma;
        return (float)(1.0 / ((double)Math.abs(sigma) * 2.50662827463) * Math.exp(-0.5 * (double)ratio * (double)ratio));
    }

    public static void queueBlur(double x, double y, double width, double height, double radius) {
        if (Blur.blurActive.booleanValue() && !queue.contains(new double[]{x, y, width, height, radius})) {
            queue.add(new double[]{x, y, width, height, radius});
        }
    }

    public static float getBlurRadius() {
        return (float)Blur.blurRadius.getValue();
    }

    static {
        blurVertexSource = "#version 120 \n\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}";
        blurFragmentSource = "#version 120\n\nuniform sampler2D texture;\nuniform sampler2D texture2;\nuniform vec2 texelSize;\nuniform vec2 direction;\nuniform float radius;\nuniform float weights[256];\n\nvoid main() {\n    vec4 color = vec4(0.0);\n    vec2 texCoord = gl_TexCoord[0].st;\n    if (direction.y == 0)\n        if (texture2D(texture2, texCoord).a == 0.0) discard;\n    for (float f = -radius; f <= radius; f++) {\n        color += texture2D(texture, texCoord + f * texelSize * direction) * (weights[int(abs(f))]);\n    }\n    gl_FragColor = vec4(color.rgb, 1.0);\n}";
        mc = Minecraft.getMinecraft();
        queue = new ObjectList();
        shader = new BlurShader("#version 120 \n\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}", "#version 120\n\nuniform sampler2D texture;\nuniform sampler2D texture2;\nuniform vec2 texelSize;\nuniform vec2 direction;\nuniform float radius;\nuniform float weights[256];\n\nvoid main() {\n    vec4 color = vec4(0.0);\n    vec2 texCoord = gl_TexCoord[0].st;\n    if (direction.y == 0)\n        if (texture2D(texture2, texCoord).a == 0.0) discard;\n    for (float f = -radius; f <= radius; f++) {\n        color += texture2D(texture, texCoord + f * texelSize * direction) * (weights[int(abs(f))]);\n    }\n    gl_FragColor = vec4(color.rgb, 1.0);\n}");
    }

    public static float gaussianWeight(float x, float sigma) {
        return BlurRenderer.gaussian(x, sigma);
    }

    public static float getRadius() {
        return BlurRenderer.getBlurRadius();
    }

    public static void recreateFramebuffers(int width, int height) {
        if (blurFramebufferA != null) {
            blurFramebufferA.deleteFramebuffer();
        }
        if (blurFramebufferB != null) {
            blurFramebufferB.deleteFramebuffer();
        }
        blurFramebufferA = new Framebuffer(width, height, false);
        blurFramebufferB = new Framebuffer(width, height, false);
    }

    public static void drawFramebuffer(ScaledResolution scaledResolution, Framebuffer framebuffer) {
        GL11.glBindTexture(3553, framebuffer.framebufferTexture);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2i(0, 0);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2i(0, scaledResolution.getScaledHeight());
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2i(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2i(scaledResolution.getScaledWidth(), 0);
        GL11.glEnd();
    }

    public static void renderQueued() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        if (BlurRenderer.scaledResolution == null || scaledResolution.getScaledWidth() != BlurRenderer.scaledResolution.getScaledWidth() || scaledResolution.getScaledHeight() != BlurRenderer.scaledResolution.getScaledHeight()) {
            BlurRenderer.scaledResolution = scaledResolution;
            BlurRenderer.recreateFramebuffers(BlurRenderer.mc.displayWidth, BlurRenderer.mc.displayHeight);
        }
        if (blurFramebufferA == null || queue.isEmpty() || blurFramebufferB == null) {
            return;
        }
        Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
        blurFramebufferB.framebufferClear();
        blurFramebufferB.bindFramebuffer(false);
        for (double[] blurRect : queue) {
            RenderUtils.drawRoundedRectCorners(blurRect[0], blurRect[1], blurRect[2], blurRect[3], blurRect[4], -1);
        }
        queue.clear();
        GL11.glEnable(3042);
        blurFramebufferA.bindFramebuffer(false);
        shader.use();
        BlurRenderer.setDirection(1);
        BlurRenderer.drawFramebuffer(scaledResolution, framebuffer);
        GL20.glUseProgram(0);
        framebuffer.bindFramebuffer(false);
        shader.use();
        BlurRenderer.setDirection(0);
        GL13.glActiveTexture(34004);
        GL11.glBindTexture(3553, BlurRenderer.blurFramebufferB.framebufferTexture);
        GL13.glActiveTexture(33984);
        BlurRenderer.drawFramebuffer(scaledResolution, blurFramebufferA);
        GL20.glUseProgram(0);
    }

    public static void queueRect(double x, double y, double width, double height) {
        if (Blur.blurActive.booleanValue() && !queue.contains(new double[]{x, y, width, height, 0.0})) {
            queue.add(new double[]{x, y, width, height, 0.0});
        }
    }
}

