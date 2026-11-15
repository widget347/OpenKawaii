/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.font;

import java.awt.Font;
import kawaii.ui.font.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontLoader {
    public static CFontRenderer sf20;
    public static CFontRenderer roboReg24;
    public static CFontRenderer val24;
    public static CFontRenderer sf18;
    public static CFontRenderer popMed20;
    public static CFontRenderer popMed25;
    public static CFontRenderer latoBlack40;
    public static CFontRenderer val20;
    public static CFontRenderer val16;
    public static CFontRenderer popMed30;
    public static CFontRenderer popMed40;
    public static CFontRenderer sfLight22;
    public static CFontRenderer val32;
    public static CFontRenderer popBold20;
    public static CFontRenderer popMed15;
    public static CFontRenderer sf24;

    static {
        sf18 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/sf.ttf"), 18.0f), true, true);
        sf20 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/sf.ttf"), 20.0f), true, true);
        sf24 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/sf.ttf"), 24.0f), true, true);
        sfLight22 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/sflight.ttf"), 22.0f), true, false);
        val32 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/val.ttf"), 32.0f), true, false);
        val16 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/val.ttf"), 16.0f), true, false);
        roboReg24 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/roboreg.ttf"), 24.0f), true, false);
        val20 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/val.ttf"), 20.0f), true, true);
        val24 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/val.ttf"), 24.0f), true, true);
        popMed30 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/popmed.ttf"), 30.0f), true, true);
        popMed40 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/popmed.ttf"), 40.0f), true, true);
        popMed15 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/popmed.ttf"), 15.0f), true, true);
        popMed20 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/popmed.ttf"), 20.0f), true, true);
        popMed25 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/popmed.ttf"), 25.0f), true, true);
        popBold20 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/popbold.ttf"), 20.0f), true, true);
        latoBlack40 = new CFontRenderer(FontLoader.loadFont(new ResourceLocation("kawaii/fonts/latoblack.ttf"), 40.0f), true, true);
    }

    public static Font loadFont(ResourceLocation resourceLocation, float size) {
        System.out.println("Loading font: " + resourceLocation.toString());
        Font font = null;
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream());
            font = font.deriveFont(size);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return font;
    }

    public static void initialize() {
    }
}

