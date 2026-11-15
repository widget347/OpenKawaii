/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import kawaii.module.render.Blur;
import kawaii.render.BlurRenderer;
import kawaii.util.ClipboardSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tv.twitch.chat.ChatUserInfo;

public abstract class GuiScreen
extends Gui
implements GuiYesNoCallback {
    public Minecraft mc;
    public static Set<String> PROTOCOLS;
    public long lastMouseEvent;
    public List<GuiLabel> labelList;
    public GuiButton selectedButton;
    public FontRenderer fontRendererObj;
    public int height;
    public static Logger LOGGER;
    public int width;
    public static Splitter NEWLINE_SPLITTER;
    public URI clickedLinkURI;
    public int eventButton;
    public boolean allowUserInput;
    public int touchValue;
    public List<GuiButton> buttonList = Lists.newArrayList();
    public RenderItem itemRender;

    public void handleComponentHover(IChatComponent iChatComponent, int mouseX, int mouseY) {
        if (iChatComponent != null && iChatComponent.getChatStyle().getChatHoverEvent() != null) {
            block21: {
                HoverEvent hoverEvent = iChatComponent.getChatStyle().getChatHoverEvent();
                if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
                    ItemStack itemStack = null;
                    try {
                        NBTTagCompound nBTTagCompound = JsonToNBT.getTagFromJson(hoverEvent.getValue().getUnformattedText());
                        if (nBTTagCompound instanceof NBTTagCompound) {
                            itemStack = ItemStack.loadItemStackFromNBT(nBTTagCompound);
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    if (itemStack != null) {
                        this.renderToolTip(itemStack, mouseX, mouseY);
                    } else {
                        this.drawCreativeTabHoveringText((Object)((Object)EnumChatFormatting.RED) + "Invalid Item!", mouseX, mouseY);
                    }
                } else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
                    if (this.mc.gameSettings.advancedItemTooltips) {
                        try {
                            NBTTagCompound nBTTagCompound = JsonToNBT.getTagFromJson(hoverEvent.getValue().getUnformattedText());
                            if (nBTTagCompound instanceof NBTTagCompound) {
                                ArrayList<String> tooltipLines = Lists.newArrayList();
                                NBTTagCompound entityTag = nBTTagCompound;
                                tooltipLines.add(entityTag.getString("name"));
                                if (entityTag.hasKey("type", 8)) {
                                    String entityType = entityTag.getString("type");
                                    tooltipLines.add("Type: " + entityType + " (" + EntityList.getIDFromString(entityType) + ")");
                                }
                                tooltipLines.add(entityTag.getString("id"));
                                this.drawHoveringText(tooltipLines, mouseX, mouseY);
                                break block21;
                            }
                            this.drawCreativeTabHoveringText((Object)((Object)EnumChatFormatting.RED) + "Invalid Entity!", mouseX, mouseY);
                        }
                        catch (Exception exception) {
                            this.drawCreativeTabHoveringText((Object)((Object)EnumChatFormatting.RED) + "Invalid Entity!", mouseX, mouseY);
                        }
                    }
                } else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                    this.drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverEvent.getValue().getFormattedText()), mouseX, mouseY);
                } else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
                    StatBase statBase = StatList.getOneShotStat(hoverEvent.getValue().getUnformattedText());
                    if (statBase != null) {
                        IChatComponent statName = statBase.getStatName();
                        ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("stats.tooltip.type." + (statBase.isAchievement() ? "achievement" : "statistic"), new Object[0]);
                        chatComponentTranslation.getChatStyle().zK(true);
                        String achievementDescription = statBase instanceof Achievement ? ((Achievement)statBase).getDescription() : null;
                        ArrayList<String> tooltipLines = Lists.newArrayList(statName.getFormattedText(), chatComponentTranslation.getFormattedText());
                        if (achievementDescription != null) {
                            tooltipLines.addAll(this.fontRendererObj.listFormattedStringToWidth(achievementDescription, 150));
                        }
                        this.drawHoveringText(tooltipLines, mouseX, mouseY);
                    } else {
                        this.drawCreativeTabHoveringText((Object)((Object)EnumChatFormatting.RED) + "Invalid statistic/achievement!", mouseX, mouseY);
                    }
                }
            }
            GlStateManager.disableLighting();
        }
    }

    public void onGuiClosed() {
    }

    @Override
    public void confirmClicked(boolean confirmed, int buttonId) {
        if (buttonId == 31102009) {
            if (confirmed) {
                this.openWebLink(this.clickedLinkURI);
            }
            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    public void openWebLink(URI uri) {
        try {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object desktop = clazz.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            clazz.getMethod("browse", URI.class).invoke(desktop, uri);
        }
        catch (Throwable throwable) {
            LOGGER.error("Couldn't open link", throwable);
        }
    }

    public void handleMouseInput() {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int mouseButton = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
                return;
            }
            this.eventButton = mouseButton;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(mouseX, mouseY, this.eventButton);
        } else if (mouseButton != -1) {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
                return;
            }
            this.eventButton = -1;
            this.mouseReleased(mouseX, mouseY, mouseButton);
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
            long timeSinceLastClick = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(mouseX, mouseY, this.eventButton, timeSinceLastClick);
        }
    }

    public GuiScreen() {
        this.labelList = Lists.newArrayList();
    }

    public static boolean isKeyComboCtrlV(int keyCode) {
        return keyCode == 47 && GuiScreen.isCtrlDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown();
    }

    public void handleInput() {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }
        if (Keyboard.isCreated()) {
            while (Keyboard.next()) {
                this.handleKeyboardInput();
            }
        }
    }

    public static boolean isCtrlKeyDown() {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public void setText(String text, boolean flag) {
    }

    public static void setClipboardString(String text) {
        if (!StringUtils.isEmpty(text)) {
            try {
                ClipboardSelection clipboardSelection = new ClipboardSelection(text);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clipboardSelection, null);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public void drawDefaultBackground() {
        if (Blur.blurActive.booleanValue() && this.mc.thePlayer != null) {
            BlurRenderer.queueRect(0.0, 0.0, this.width, this.height);
            Gui.drawRect(0.0, 0.0, this.width, this.height, -2145509857);
        } else {
            this.drawWorldBackground(0);
        }
    }

    public void onResize(Minecraft minecraft, int newWidth, int newHeight) {
        this.setWorldAndResolution(minecraft, newWidth, newHeight);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (int i = 0; i < this.buttonList.size(); ++i) {
                GuiButton guiButton = this.buttonList.get(i);
                if (!guiButton.mousePressed(this.mc, mouseX, mouseY)) continue;
                this.selectedButton = guiButton;
                guiButton.playPressSound(this.mc.getSoundHandler());
                this.actionPerformed(guiButton);
            }
        }
    }

    public void sendChatMessage(String message) {
        this.sendChatMessage(message, true);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int i;
        for (i = 0; i < this.buttonList.size(); i += 4) {
            this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY);
            i -= 3;
        }
        for (i = 0; i < this.labelList.size(); ++i) {
            this.labelList.get(i).drawLabel(this.mc, mouseX, mouseY);
        }
    }

    public void drawBackground(int vOffset) {
        float textureSize = 32.0f;
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(optionsBackground);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float textureScale = textureSize;
        worldRenderer.begin(7, DefaultVertexFormats.Rgp);
        worldRenderer.pos(0.0, this.height, 0.0).tex(0.0, (float)this.height / textureSize + (float)vOffset).color(64, 64, 64, 255).endVertex();
        worldRenderer.pos(this.width, this.height, 0.0).tex((float)this.width / textureSize, (float)this.height / textureSize + (float)vOffset).color(64, 64, 64, 255).endVertex();
        worldRenderer.pos(this.width, 0.0, 0.0).tex((float)this.width / textureSize, vOffset).color(64, 64, 64, 255).endVertex();
        worldRenderer.pos(0.0, 0.0, 0.0).tex(0.0, vOffset).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public void drawHoveringText(List<String> list, int mouseX, int mouseY) {
        if (!list.isEmpty()) {
            int size;
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int maxTextWidth = 0;
            for (String line : list) {
                size = this.fontRendererObj.getStringWidth(line);
                if (size <= maxTextWidth) continue;
                maxTextWidth = size;
            }
            int tooltipX = mouseX + 12;
            int tooltipY = mouseY - 12;
            size = 8;
            if (list.size() > 1) {
                size += 2 + (list.size() - 1) * 10;
            }
            if (tooltipX + maxTextWidth > this.width) {
                tooltipX -= 28 + maxTextWidth;
            }
            if (tooltipY + size + 6 > this.height) {
                tooltipY = this.height - size - 6;
            }
            zLevel = 300.0f;
            this.itemRender.zLevel = 300.0f;
            GuiScreen.drawGradientRect(tooltipX - 3, tooltipY - 4, tooltipX + maxTextWidth + 3, tooltipY - 3, -267386864, -267386864);
            GuiScreen.drawGradientRect(tooltipX - 3, tooltipY + size + 3, tooltipX + maxTextWidth + 3, tooltipY + size + 4, -267386864, -267386864);
            GuiScreen.drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + maxTextWidth + 3, tooltipY + size + 3, -267386864, -267386864);
            GuiScreen.drawGradientRect(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + size + 3, -267386864, -267386864);
            GuiScreen.drawGradientRect(tooltipX + maxTextWidth + 3, tooltipY - 3, tooltipX + maxTextWidth + 4, tooltipY + size + 3, -267386864, -267386864);
            int borderColor = 1344798847;
            GuiScreen.drawGradientRect(tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + size + 3 - 1, 0x505000FF, borderColor);
            GuiScreen.drawGradientRect(tooltipX + maxTextWidth + 2, tooltipY - 3 + 1, tooltipX + maxTextWidth + 3, tooltipY + size + 3 - 1, 0x505000FF, borderColor);
            GuiScreen.drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + maxTextWidth + 3, tooltipY - 3 + 1, 0x505000FF, 0x505000FF);
            GuiScreen.drawGradientRect(tooltipX - 3, tooltipY + size + 2, tooltipX + maxTextWidth + 3, tooltipY + size + 3, borderColor, borderColor);
            for (int i = 0; i < list.size(); ++i) {
                String line = list.get(i);
                this.fontRendererObj.drawStringWithShadow(line, tooltipX, tooltipY, -1);
                if (i == 0) {
                    tooltipY += 2;
                }
                tooltipY += 10;
            }
            zLevel = 0.0f;
            this.itemRender.zLevel = 0.0f;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public static boolean isKeyComboCtrlX(int keyCode) {
        return keyCode == 45 && GuiScreen.isCtrlDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown();
    }

    public void initGui() {
    }

    public static boolean isKeyComboCtrlC(int keyCode) {
        return keyCode == 46 && GuiScreen.isCtrlDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown();
    }

    public static boolean isCtrlDown() {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public void actionPerformed(GuiButton guiButton) {
    }

    public void setWorldAndResolution(Minecraft minecraft, int newWidth, int newHeight) {
        this.mc = minecraft;
        this.itemRender = minecraft.getRenderItem();
        this.fontRendererObj = minecraft.fontRendererObj;
        this.width = newWidth;
        this.height = newHeight;
        this.buttonList.clear();
        this.initGui();
    }

    public void sendChatMessage(String message, boolean addToHistory) {
        if (addToHistory) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(message);
        }
        this.mc.thePlayer.sendChatMessage(message);
    }

    public void renderToolTip(ItemStack itemStack, int mouseX, int mouseY) {
        List<String> list = itemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, (Object)((Object)itemStack.getRarity().rarityColor) + list.get(i));
                continue;
            }
            list.set(i, (Object)((Object)EnumChatFormatting.GRAY) + list.get(i));
        }
        this.drawHoveringText(list, mouseX, mouseY);
    }

    public void handleKeyboardInput() {
        if (Keyboard.getEventKeyState()) {
            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
        this.mc.dispatchKeypresses();
    }

    static {
        LOGGER = LogManager.getLogger();
        PROTOCOLS = Sets.newHashSet("http", "https");
        NEWLINE_SPLITTER = Splitter.on('\n');
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    public boolean doesGuiPauseGame() {
        return true;
    }

    public void drawCreativeTabHoveringText(String text, int mouseX, int mouseY) {
        this.drawHoveringText(Arrays.asList(text), mouseX, mouseY);
    }

    public void updateScreen() {
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.selectedButton != null && mouseButton == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    public boolean handleComponentClick(IChatComponent iChatComponent) {
        if (iChatComponent == null) {
            return false;
        }
        ClickEvent clickEvent = iChatComponent.getChatStyle().getChatClickEvent();
        if (GuiScreen.isShiftKeyDown()) {
            if (iChatComponent.getChatStyle().getInsertion() != null) {
                this.setText(iChatComponent.getChatStyle().getInsertion(), false);
            }
        } else if (clickEvent != null) {
            block23: {
                if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.mc.gameSettings.chatLinks) {
                        return false;
                    }
                    try {
                        URI uri = new URI(clickEvent.avo());
                        String scheme = uri.getScheme();
                        if (scheme == null) {
                            throw new URISyntaxException(clickEvent.avo(), "Missing protocol");
                        }
                        if (!PROTOCOLS.contains(scheme.toLowerCase())) {
                            throw new URISyntaxException(clickEvent.avo(), "Unsupported protocol: " + scheme.toLowerCase());
                        }
                        if (this.mc.gameSettings.chatLinksPrompt) {
                            this.clickedLinkURI = uri;
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink((GuiYesNoCallback)this, clickEvent.avo(), 31102009, false));
                            break block23;
                        }
                        this.openWebLink(uri);
                    }
                    catch (URISyntaxException syntaxException) {
                        LOGGER.error("Can't open url for " + clickEvent, (Throwable)syntaxException);
                    }
                } else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uri = new File(clickEvent.avo()).toURI();
                    this.openWebLink(uri);
                } else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.setText(clickEvent.avo(), true);
                } else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendChatMessage(clickEvent.avo(), false);
                } else if (clickEvent.getAction() == ClickEvent.Action.TWITCH_USER_INFO) {
                    ChatUserInfo chatUserInfo = this.mc.getTwitchStream().getChatUserInfo(clickEvent.avo());
                    if (chatUserInfo != null) {
                        this.mc.displayGuiScreen(new GuiTwitchUserMode(this.mc.getTwitchStream(), chatUserInfo));
                    } else {
                        LOGGER.error("Tried to handle twitch user but couldn't find them!");
                    }
                } else {
                    LOGGER.error("Don't know how to handle " + clickEvent);
                }
            }
            return true;
        }
        return false;
    }

    public void drawWorldBackground(int vOffset) {
        if (this.mc.theWorld != null) {
            GuiScreen.drawGradientRect(0.0, 0.0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.drawBackground(vOffset);
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }

    public static String getClipboardString() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return "";
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    public static boolean isKeyComboCtrlA(int keyCode) {
        return keyCode == 30 && GuiScreen.isCtrlDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isCtrlKeyDown();
    }
}

