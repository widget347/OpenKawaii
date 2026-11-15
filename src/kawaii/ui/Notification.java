/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import kawaii.network.ResourcePackActionMap;
import kawaii.render.BlurRenderer;
import kawaii.render.ColorThemes;
import kawaii.render.RenderUtils;
import kawaii.ui.Animation;
import kawaii.ui.UiElement;
import kawaii.ui.font.CFontRenderer;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import org.lwjgl.opengl.GL11;

public class Notification
implements UiElement {
    public C19PacketResourcePackStatus.Action type;
    public Animation slideAnimation = new Animation();
    public long startTime;
    public long endTime;
    public String title;
    public String message;
    public double duration;

    public void render(ScaledResolution scaledResolution, int index) {
        float halfDivisor = 2.0f;
        int notificationWidth = 200;
        notificationWidth = Math.max(notificationWidth, FontLoader.popMed20.getStringWidth(this.message) + 24 + 30);
        int notificationX = scaledResolution.getScaledWidth() - 20 - notificationWidth;
        int notificationY = scaledResolution.getScaledHeight() - 5 - 65 - index * 50;
        CFontRenderer titleFont = FontLoader.popBold20;
        CFontRenderer messageFont = FontLoader.popMed20;
        this.slideAnimation.setEasing(400).setEnd(1.0).setReversed(System.currentTimeMillis() > this.endTime - 400L).update();
        if (!this.slideAnimation.isFinished()) {
            GL11.glEnable(3089);
            RenderUtils.applyScissor((double)(notificationX + notificationWidth) - this.slideAnimation.getValue() / 100.0 * (double)notificationWidth, notificationY, notificationX + notificationWidth, notificationY + 40);
        }
        BlurRenderer.queueBlur((double)(notificationX + notificationWidth) - this.slideAnimation.getValue() / 100.0 * (double)notificationWidth, notificationY, notificationX + notificationWidth, notificationY + 40, 2.0);
        int accentColor = -6504193;
        switch (ResourcePackActionMap.resourcePackActionMap[this.type.ordinal()]) {
            case 1: {
                accentColor = -9699450;
                break;
            }
            case 2: {
                accentColor = -1721;
                break;
            }
            case 3: {
                accentColor = -47289;
            }
        }
        RenderUtils.drawRoundedRect(notificationX, notificationY, notificationWidth, 40.0, 2.0, Integer.MIN_VALUE);
        RenderUtils.drawRoundedRect(notificationX + 5, notificationY + 5, 5.0, 5.0, 5.0, accentColor);
        titleFont.drawString(Math.floor((float)this.getRemainingTime() / 100.0f) / 10.0 + "s", notificationX + notificationWidth - titleFont.getStringWidth(Math.floor((float)this.getRemainingTime() / 100.0f) / 10.0 + "s") - 5, notificationY + 5, -1862270977);
        titleFont.drawString(this.title, (float)notificationX + (float)notificationWidth / halfDivisor - (float)titleFont.getStringWidth(this.title) / halfDivisor, notificationY + 5, -1);
        Gui.drawRect((float)notificationX + (float)notificationWidth / halfDivisor - (float)titleFont.getStringWidth(this.title) / halfDivisor, notificationY + 5 + titleFont.getHeight(), (float)notificationX + (float)notificationWidth / halfDivisor + (float)titleFont.getStringWidth(this.title) / halfDivisor, notificationY + 5 + titleFont.getHeight() + 1, -1);
        messageFont.drawString(this.message, notificationX + 5, notificationY + 5 + titleFont.getHeight() + 7, -251658241);
        RenderUtils.drawGradientRect(notificationX, notificationY + 40 - 1, (long)notificationX + this.startTime / this.endTime * (long)notificationWidth, notificationY + 40, ColorThemes.getStaticThemeColor(notificationX), ColorThemes.getStaticThemeColor(notificationX + notificationWidth));
        if (!this.slideAnimation.isFinished()) {
            GL11.glDisable(3089);
        }
    }

    public int getRemainingTime() {
        return (int)(this.endTime - System.currentTimeMillis());
    }

    public Notification(String notificationTitle, String notificationMessage, double durationMs, C19PacketResourcePackStatus.Action action) {
        this.title = notificationTitle;
        this.message = notificationMessage;
        this.duration = durationMs;
        this.type = action;
        this.startTime = System.currentTimeMillis();
        this.endTime = (long)((double)this.startTime + durationMs);
        this.slideAnimation.restart();
    }
}

