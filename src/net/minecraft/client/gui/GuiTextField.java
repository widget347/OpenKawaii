/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

public class GuiTextField
extends Gui {
    public boolean isEnabled = true;
    public boolean visible = true;
    public int maxStringLength = 32;
    public String placeholder = "";
    public int cursorCounter;
    public Predicate<String> field_175209_y = Predicates.alwaysTrue();
    public boolean enableBackgroundDrawing = true;
    public int id;
    public GuiPageButtonList.GuiResponder field_175210_x;
    public String text = "";
    public FontRenderer fontRendererInstance;
    public boolean canLoseFocus = true;
    public int selectionEnd;
    public int height;
    public int yPosition;
    public int disabledColor = 0x707070;
    public int lineScrollOffset;
    public int cursorPosition;
    public int enabledColor = 0xE0E0E0;
    public int width;
    public boolean isFocused;
    public int xPosition;

    public int getId() {
        return this.id;
    }

    public void setEnableBackgroundDrawing(boolean enableBackground) {
        this.enableBackgroundDrawing = enableBackground;
    }

    public String getText() {
        return this.text;
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public int getNthWordFromPos(int wordCount, int position) {
        return this.getNthWordPos(wordCount, position, true);
    }

    public void deleteFromCursor(int count) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean deletingBackward = count < 0;
                int startIndex = deletingBackward ? this.cursorPosition + count : this.cursorPosition;
                int endIndex = deletingBackward ? this.cursorPosition : this.cursorPosition + count;
                String newText = "";
                if (startIndex >= 0) {
                    newText = this.text.substring(0, startIndex);
                }
                if (endIndex < this.text.length()) {
                    newText = newText + this.text.substring(endIndex);
                }
                if (this.field_175209_y.apply(newText)) {
                    this.text = newText;
                    if (deletingBackward) {
                        this.moveCursorBy(count);
                    }
                    if (this.field_175210_x != null) {
                        this.field_175210_x.setEntryValue(this.id, this.text);
                    }
                }
            }
        }
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void deleteWords(int wordCount) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(wordCount) - this.cursorPosition);
            }
        }
    }

    public void setMaxStringLength(int maxLength) {
        this.maxStringLength = maxLength;
        if (this.text.length() > maxLength) {
            this.text = this.text.substring(0, maxLength);
        }
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public void setCanLoseFocus(boolean allowLoseFocus) {
        this.canLoseFocus = allowLoseFocus;
    }

    public void setValidator(Predicate predicate) {
        this.field_175209_y = predicate;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void setText(String newText) {
        if (this.field_175209_y.apply(newText)) {
            this.text = newText.length() > this.maxStringLength ? newText.substring(0, this.maxStringLength) : newText;
            this.setCursorPositionEnd();
        }
    }

    public String getSelectedText() {
        int startIndex = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int endIndex = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(startIndex, endIndex);
    }

    public void setSelectionPos(int position) {
        int textLength = this.text.length();
        if (position > textLength) {
            position = textLength;
        }
        if (position < 0) {
            position = 0;
        }
        this.selectionEnd = position;
        if (this.fontRendererInstance != null) {
            if (this.lineScrollOffset > textLength) {
                this.lineScrollOffset = textLength;
            }
            int availableWidth = this.getWidth();
            String visibleText = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), availableWidth);
            int visibleEnd = visibleText.length() + this.lineScrollOffset;
            if (position == this.lineScrollOffset) {
                this.lineScrollOffset -= this.fontRendererInstance.trimStringToWidth(this.text, availableWidth, true).length();
            }
            if (position > visibleEnd) {
                this.lineScrollOffset += position - visibleEnd;
            } else if (position <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - position;
            }
            this.lineScrollOffset = MathHelper.Zog(this.lineScrollOffset, 0, textLength);
        }
    }

    public void setVisible(boolean shouldBeVisible) {
        this.visible = shouldBeVisible;
    }

    public GuiTextField(int componentId, FontRenderer fontRenderer, int x, int y, int boxWidth, int boxHeight, String placeholderText) {
        this.placeholder = placeholderText;
        this.id = componentId;
        this.fontRendererInstance = fontRenderer;
        this.xPosition = x;
        this.yPosition = y;
        this.width = boxWidth;
        this.height = boxHeight;
    }

    public void setTextColor(int textColor) {
        this.enabledColor = textColor;
    }

    public void setCursorPosition(int position) {
        this.cursorPosition = position;
        int textLength = this.text.length();
        this.cursorPosition = MathHelper.Zog(this.cursorPosition, 0, textLength);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setFocused(boolean focused) {
        if (focused && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = focused;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setResponder(GuiPageButtonList.GuiResponder guiResponder) {
        this.field_175210_x = guiResponder;
    }

    public void drawCursorVertical(int x1, int y1, int x2, int y2) {
        int swapValue;
        if (x1 < x2) {
            swapValue = x1;
            x1 = x2;
            x2 = swapValue;
        }
        if (y1 < y2) {
            swapValue = y1;
            y1 = y2;
            y2 = swapValue;
        }
        if (x2 > this.xPosition + this.width) {
            x2 = this.xPosition + this.width;
        }
        if (x1 > this.xPosition + this.width) {
            x1 = this.xPosition + this.width;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldRenderer.begin(7, DefaultVertexFormats.jXa);
        worldRenderer.pos(x1, y2, 0.0).endVertex();
        worldRenderer.pos(x2, y2, 0.0).endVertex();
        worldRenderer.pos(x2, y1, 0.0).endVertex();
        worldRenderer.pos(x1, y1, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public GuiTextField(int componentId, FontRenderer fontRenderer, int x, int y, int boxWidth, int boxHeight) {
        this.id = componentId;
        this.fontRendererInstance = fontRenderer;
        this.xPosition = x;
        this.yPosition = y;
        this.width = boxWidth;
        this.height = boxHeight;
    }

    public void setDisabledTextColour(int disabledTextColor) {
        this.disabledColor = disabledTextColor;
    }

    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, Integer.MIN_VALUE);
            }
            int textColor = this.isEnabled ? this.enabledColor : this.disabledColor;
            int visibleCursor = this.cursorPosition - this.lineScrollOffset;
            int visibleSelectionEnd = this.selectionEnd - this.lineScrollOffset;
            String visibleText = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean cursorInView = visibleCursor >= 0 && visibleCursor <= visibleText.length();
            boolean drawCursor = this.isFocused && this.cursorCounter / 6 % 2 == 0 && cursorInView;
            int textX = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int textY = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int nextX = textX;
            if (this.getText().length() == 0) {
                FontLoader.popMed20.drawString(this.placeholder, textX, textY, -259487608);
            }
            if (visibleSelectionEnd > visibleText.length()) {
                visibleSelectionEnd = visibleText.length();
            }
            if (visibleText.length() > 0) {
                String beforeCursor = cursorInView ? visibleText.substring(0, visibleCursor) : visibleText;
                nextX = (int)FontLoader.popMed20.drawStringWithShadow(beforeCursor, textX, textY, textColor);
            }
            boolean drawLineCursor = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int cursorX = nextX;
            if (visibleText.length() > 0 && cursorInView && visibleCursor < visibleText.length()) {
                nextX = (int)FontLoader.popMed20.drawStringWithShadow(visibleText.substring(visibleCursor), nextX, textY, textColor);
            }
            if (drawCursor) {
                if (drawLineCursor) {
                    Gui.drawRect(cursorX, textY - 1, cursorX + 1, textY + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                } else {
                    FontLoader.popMed20.drawStringWithShadow("_", cursorX, (float)textY + 1.0f, textColor);
                }
            }
            if (visibleSelectionEnd != visibleCursor) {
                int selectionX = textX + this.fontRendererInstance.getStringWidth(visibleText.substring(0, visibleSelectionEnd));
                this.drawCursorVertical(cursorX, textY - 1, selectionX - 1, textY + 1 + this.fontRendererInstance.FONT_HEIGHT);
            }
        }
    }

    public void moveCursorBy(int offset) {
        this.setCursorPosition(this.selectionEnd + offset);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean mouseInside;
        boolean insideCopy = mouseInside = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
        if (this.canLoseFocus) {
            this.setFocused(mouseInside);
        }
        if (this.isFocused && mouseInside && mouseButton == 0) {
            int relativeX = mouseX - this.xPosition;
            if (this.enableBackgroundDrawing) {
                relativeX -= 4;
            }
            String visibleText = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(visibleText, relativeX).length() + this.lineScrollOffset);
        }
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        if (!this.isFocused) {
            return false;
        }
        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        }
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            if (this.isEnabled) {
                this.writeText(GuiScreen.getClipboardString());
            }
            return true;
        }
        if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            if (this.isEnabled) {
                this.writeText("");
            }
            return true;
        }
        switch (keyCode) {
            case 14: {
                if (GuiScreen.isCtrlDown()) {
                    if (this.isEnabled) {
                        this.deleteWords(-1);
                    }
                } else if (this.isEnabled) {
                    this.deleteFromCursor(-1);
                }
                return true;
            }
            case 199: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(0);
                } else {
                    this.setCursorPositionZero();
                }
                return true;
            }
            case 203: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() - 1);
                    }
                } else if (GuiScreen.isCtrlDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                } else {
                    this.moveCursorBy(-1);
                }
                return true;
            }
            case 205: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() + 1);
                    }
                } else if (GuiScreen.isCtrlDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(1));
                } else {
                    this.moveCursorBy(1);
                }
                return true;
            }
            case 207: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(this.text.length());
                } else {
                    this.setCursorPositionEnd();
                }
                return true;
            }
            case 211: {
                if (GuiScreen.isCtrlDown()) {
                    if (this.isEnabled) {
                        this.deleteWords(1);
                    }
                } else if (this.isEnabled) {
                    this.deleteFromCursor(1);
                }
                return true;
            }
        }
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            if (this.isEnabled) {
                this.writeText(Character.toString(typedChar));
            }
            return true;
        }
        return false;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public int getNthWordPos(int wordCount, int position, boolean skipSpaces) {
        int index = position;
        boolean backward = wordCount < 0;
        int absWordCount = Math.abs(wordCount);
        for (int i = 0; i < absWordCount; i -= 47) {
            if (!backward) {
                int textLength = this.text.length();
                if ((index = this.text.indexOf(32, index)) == -1) {
                    index = textLength;
                } else {
                    while (skipSpaces && index < textLength && this.text.charAt(index) == ' ') {
                        ++index;
                    }
                }
            } else {
                while (skipSpaces && index > 0 && this.text.charAt(index - 1) == ' ') {
                    --index;
                }
                while (index > 0 && this.text.charAt(index - 1) != ' ') {
                    --index;
                }
            }
            i += 48;
        }
        return index;
    }

    public int getNthWordFromCursor(int wordCount) {
        return this.getNthWordFromPos(wordCount, this.getCursorPosition());
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public void writeText(String textToWrite) {
        String newText = "";
        String filteredText = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
        int startIndex = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int endIndex = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int remainingSpace = this.maxStringLength - this.text.length() - (startIndex - endIndex);
        int insertedLength = 0;
        if (this.text.length() > 0) {
            newText = newText + this.text.substring(0, startIndex);
        }
        if (remainingSpace < filteredText.length()) {
            newText = newText + filteredText.substring(0, remainingSpace);
            insertedLength = remainingSpace;
        } else {
            newText = newText + filteredText;
            insertedLength = filteredText.length();
        }
        if (this.text.length() > 0 && endIndex < this.text.length()) {
            newText = newText + this.text.substring(endIndex);
        }
        if (this.field_175209_y.apply(newText)) {
            this.text = newText;
            this.moveCursorBy(startIndex - this.selectionEnd + insertedLength);
            if (this.field_175210_x != null) {
                this.field_175210_x.setEntryValue(this.id, this.text);
            }
        }
    }
}

