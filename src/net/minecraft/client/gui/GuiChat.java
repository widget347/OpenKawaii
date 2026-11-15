/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import kawaii.module.render.Blur;
import kawaii.render.BlurRenderer;
import kawaii.ui.ModulePanel;
import kawaii.ui.PanelRegistry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChat
extends GuiScreen {
    public List<String> foundPlayerNames = Lists.newArrayList();
    public String historyBuffer = "";
    public int autocompleteIndex;
    public int sentHistoryCursor = -1;
    public GuiTextField inputField;
    public boolean playerNamesFound;
    public static Logger logger = LogManager.getLogger();
    public boolean waitingOnAutocomplete;
    public String defaultInputFieldText = "";

    @Override
    public void updateScreen() {
        this.inputField.updateCursorCounter();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public GuiChat(String defaultInputText) {
        this.defaultInputFieldText = defaultInputText;
    }

    public void getSentHistory(int offset) {
        int newIndex = this.sentHistoryCursor + offset;
        int sentCount = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        if ((newIndex = MathHelper.Zog(newIndex, 0, sentCount)) != this.sentHistoryCursor) {
            if (newIndex == sentCount) {
                this.sentHistoryCursor = sentCount;
                this.inputField.setText(this.historyBuffer);
            } else {
                if (this.sentHistoryCursor == sentCount) {
                    this.historyBuffer = this.inputField.getText();
                }
                this.inputField.setText(this.mc.ingameGUI.getChatGUI().getSentMessages().get(newIndex));
                this.sentHistoryCursor = newIndex;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Blur.blurActive.booleanValue()) {
            BlurRenderer.queueRect(2.0, this.height - 14, this.width - 2, this.height - 2);
        }
        GuiChat.drawRect(2.0, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        IChatComponent iChatComponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
        if (iChatComponent != null && iChatComponent.getChatStyle().getChatHoverEvent() != null) {
            this.handleComponentHover(iChatComponent, mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (ModulePanel modulePanel : PanelRegistry.getElements()) {
            modulePanel.isHovered(mouseX, mouseY);
        }
    }

    @Override
    public void setText(String text, boolean replace) {
        if (replace) {
            this.inputField.setText(text);
        } else {
            this.inputField.writeText(text);
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100);
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setText(this.defaultInputFieldText);
        this.inputField.setCanLoseFocus(false);
    }

    public void onAutocompleteResponse(String[] names) {
        if (this.waitingOnAutocomplete) {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();
            for (String playerName : names) {
                if (playerName.length() <= 0) continue;
                this.foundPlayerNames.add(playerName);
            }
            String partialWord = this.inputField.getText().substring(this.inputField.getNthWordPos(-1, this.inputField.getCursorPosition(), false));
            String commonPrefix = StringUtils.getCommonPrefix(names);
            if (commonPrefix.length() > 0 && !partialWord.equalsIgnoreCase(commonPrefix)) {
                this.inputField.deleteFromCursor(this.inputField.getNthWordPos(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(commonPrefix);
            } else if (this.foundPlayerNames.size() > 0) {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheelDelta = Mouse.getEventDWheel();
        if (wheelDelta != 0) {
            if (wheelDelta > 1) {
                wheelDelta = 1;
            }
            if (wheelDelta < -1) {
                wheelDelta = -1;
            }
            if (!GuiChat.isShiftKeyDown()) {
                wheelDelta *= 7;
            }
            this.mc.ingameGUI.getChatGUI().scroll(wheelDelta);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        Object component;
        if (mouseButton == 0 && this.handleComponentClick((IChatComponent)(component = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY())))) {
            return;
        }
        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (ModulePanel modulePanel : PanelRegistry.getElements()) {
            modulePanel.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        this.waitingOnAutocomplete = false;
        if (keyCode == 15) {
            this.autocompletePlayerNames();
        } else {
            this.playerNamesFound = false;
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        } else if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 200) {
                this.getSentHistory(-1);
            } else if (keyCode == 208) {
                this.getSentHistory(1);
            } else if (keyCode == 201) {
                this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            } else {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            String message = this.inputField.getText().trim();
            if (message.length() > 0) {
                this.sendChatMessage(message);
            }
            this.mc.displayGuiScreen(null);
        }
    }

    public void autocompletePlayerNames() {
        if (this.playerNamesFound) {
            this.inputField.deleteFromCursor(this.inputField.getNthWordPos(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
            if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
                this.autocompleteIndex = 0;
            }
        } else {
            int wordStart = this.inputField.getNthWordPos(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String partialWord = this.inputField.getText().substring(wordStart).toLowerCase();
            String textBeforeCursor = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(textBeforeCursor, partialWord);
            if (this.foundPlayerNames.isEmpty()) {
                return;
            }
            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(wordStart - this.inputField.getCursorPosition());
        }
        if (this.foundPlayerNames.size() > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String foundName : this.foundPlayerNames) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(foundName);
            }
            this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringBuilder.toString()), 1);
        }
        this.inputField.writeText(this.foundPlayerNames.get(this.autocompleteIndex++));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (ModulePanel modulePanel : PanelRegistry.getElements()) {
            modulePanel.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public GuiChat() {
    }

    public void sendAutocompleteRequest(String textBeforeCursor, String partialWord) {
        if (textBeforeCursor.length() >= 1) {
            BlockPos blockPos = null;
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                blockPos = this.mc.objectMouseOver.getBlockPos();
            }
            this.mc.thePlayer.sendQueue.sendPacket(new C14PacketTabComplete(textBeforeCursor, blockPos));
            this.waitingOnAutocomplete = true;
        }
    }
}

