/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import kawaii.module.render.Hud;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiNewChat
extends Gui {
    public List<String> sentMessages = Lists.newArrayList();
    public List<ChatLine> chatLines = Lists.newArrayList();
    public static Logger logger = LogManager.getLogger();
    public boolean isScrolled;
    public List<ChatLine> drawnChatLines = Lists.newArrayList();
    public int scrollPos;
    public Minecraft mc;

    public int getChatWidth() {
        return GuiNewChat.calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public void scroll(int scrollAmount) {
        this.scrollPos += scrollAmount;
        int drawnLineCount = this.drawnChatLines.size();
        if (this.scrollPos > drawnLineCount - this.getLineCount()) {
            this.scrollPos = drawnLineCount - this.getLineCount();
        }
        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    public void setChatLine(IChatComponent iChatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) {
            this.deleteChatLine(chatLineId);
        }
        int wrapWidth = MathHelper.zor((float)this.getChatWidth() / this.getChatScale());
        List<IChatComponent> wrappedComponents = GuiUtilRenderComponents.wrapChatComponent(iChatComponent, wrapWidth, this.mc.fontRendererObj, false, false);
        boolean chatOpen = this.getChatOpen();
        for (IChatComponent wrappedComponent : wrappedComponents) {
            if (chatOpen && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }
            this.drawnChatLines.add(0, new ChatLine(updateCounter, wrappedComponent, chatLineId));
        }
        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }
        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, iChatComponent, chatLineId));
            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public static int calculateChatboxWidth(float chatWidthFactor) {
        return MathHelper.zor(chatWidthFactor * 280.0f + 40.0f);
    }

    public GuiNewChat(Minecraft minecraft) {
        this.mc = minecraft;
    }

    public int getChatHeight() {
        return GuiNewChat.calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    public void clearChatMessages() {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    public void printChatMessageWithOptionalDeletion(IChatComponent iChatComponent, int chatLineId) {
        this.setChatLine(iChatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[CHAT] " + iChatComponent.getUnformattedText());
    }

    public void printChatMessage(IChatComponent iChatComponent) {
        this.printChatMessageWithOptionalDeletion(iChatComponent, 0);
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();
        for (int i = this.chatLines.size() - 1; i >= 0; --i) {
            ChatLine chatLine = this.chatLines.get(i);
            this.setChatLine(chatLine.getChatComponent(), chatLine.JFo(), chatLine.Uag(), true);
        }
    }

    public IChatComponent getChatComponent(int mouseX, int mouseY) {
        if (!this.getChatOpen()) {
            return null;
        }
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int scaleFactor = scaledResolution.getScaleFactor();
        float chatScale = this.getChatScale();
        int chatX = mouseX / scaleFactor - 3;
        int chatY = mouseY / scaleFactor - 27;
        chatX = MathHelper.zor((float)chatX / chatScale);
        chatY = MathHelper.zor((float)chatY / chatScale);
        if (chatX >= 0 && chatY >= 0) {
            int visibleLineCount = Math.min(this.getLineCount(), this.drawnChatLines.size());
            if (chatX <= MathHelper.zor((float)this.getChatWidth() / this.getChatScale()) && chatY < this.mc.fontRendererObj.FONT_HEIGHT * visibleLineCount + visibleLineCount) {
                int lineIndex = chatY / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
                if (lineIndex >= 0 && lineIndex < this.drawnChatLines.size()) {
                    ChatLine chatLine = this.drawnChatLines.get(lineIndex);
                    int runningWidth = 0;
                    for (IChatComponent iChatComponent : chatLine.getChatComponent()) {
                        if (!(iChatComponent instanceof ChatComponentText) || (runningWidth += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.formatTextForChat(((ChatComponentText)iChatComponent).getChatComponentText_TextValue(), false))) <= chatX) continue;
                        return iChatComponent;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void deleteChatLine(int chatLineId) {
        ChatLine chatLine;
        Iterator<ChatLine> chatLineIterator = this.drawnChatLines.iterator();
        while (chatLineIterator.hasNext()) {
            chatLine = chatLineIterator.next();
            if (chatLine.JFo() != chatLineId) continue;
            chatLineIterator.remove();
        }
        chatLineIterator = this.chatLines.iterator();
        while (chatLineIterator.hasNext()) {
            chatLine = chatLineIterator.next();
            if (chatLine.JFo() != chatLineId) continue;
            chatLineIterator.remove();
            break;
        }
    }

    public void addToSentMessages(String message) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    public void drawChat(int updateCounter) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int maxLines = this.getLineCount();
            boolean chatOpen = false;
            int visibleLineCount = 0;
            int totalLines = this.drawnChatLines.size();
            float opacityFactor = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
            if (totalLines > 0) {
                int temp;
                int alpha;
                int value;
                int size;
                if (this.getChatOpen()) {
                    chatOpen = true;
                }
                float chatScale = this.getChatScale();
                int scaledChatWidth = MathHelper.ceiling_float_int((float)this.getChatWidth() / chatScale);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0f, 20.0f, 0.0f);
                GlStateManager.scale(chatScale, chatScale, 1.0f);
                for (size = 0; size + this.scrollPos < this.drawnChatLines.size() && size < maxLines; ++size) {
                    ChatLine chatLine = this.drawnChatLines.get(size + this.scrollPos);
                    if (chatLine == null || (value = updateCounter - chatLine.Uag()) >= 200 && !chatOpen) continue;
                    double fadeFactor = (double)value / 200.0;
                    fadeFactor = 1.0 - fadeFactor;
                    fadeFactor *= 10.0;
                    fadeFactor = MathHelper.QH(fadeFactor, 0.0, 1.0);
                    fadeFactor *= fadeFactor;
                    alpha = (int)(255.0 * fadeFactor);
                    if (chatOpen) {
                        alpha = 255;
                    }
                    alpha = (int)((float)alpha * opacityFactor);
                    ++visibleLineCount;
                    if (alpha <= 3) continue;
                    temp = 0;
                    int lineY = -size * 10;
                    GuiNewChat.drawRect(temp, lineY - 9 - 1, temp + scaledChatWidth + 4, lineY, alpha / 2 << 24);
                    String formattedText = chatLine.getChatComponent().getFormattedText();
                    GlStateManager.enableBlend();
                    if (Hud.newChat.getValue().booleanValue()) {
                        FontLoader.val20.drawStringWithShadow(formattedText, temp, lineY - 8, 0xFFFFFF + (alpha << 24));
                    } else {
                        this.mc.fontRendererObj.drawStringWithShadow(formattedText, (float)temp + 1.0f, (float)((double)lineY - 8.5), 0xFFFFFF + (alpha << 24));
                    }
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                }
                if (chatOpen) {
                    size = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0f, 0.0f, 0.0f);
                    int totalChatHeight = totalLines * size + totalLines;
                    value = visibleLineCount * size + visibleLineCount;
                    int scrollBarOffset = this.scrollPos * value / totalLines;
                    int scrollBarHeight = value * value / totalChatHeight;
                    if (totalChatHeight != value) {
                        alpha = scrollBarOffset > 0 ? 170 : 96;
                        temp = this.isScrolled ? 0xCC3333 : 0x3333AA;
                        GuiNewChat.drawRect(0.0, -scrollBarOffset, 2.0, -scrollBarOffset - scrollBarHeight, temp + (alpha << 24));
                        GuiNewChat.drawRect(2.0, -scrollBarOffset, 1.0, -scrollBarOffset - scrollBarHeight, 0xCCCCCC + (alpha << 24));
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }

    public static int calculateChatboxHeight(float chatHeightFactor) {
        return MathHelper.zor(chatHeightFactor * 160.0f + 20.0f);
    }
}

