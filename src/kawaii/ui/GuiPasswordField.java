/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import kawaii.ui.font.FontLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;

public class GuiPasswordField
extends Gui {
    public String label;
    public int height;
    public String text;
    public boolean enabled;
    public boolean editable;
    public FontRenderer fontRendererInstance;
    public int cursorCounter;
    public int lineScrollOffset;
    public int yPosition;
    public boolean canLoseFocus;
    public int cursorPosition;
    public boolean isFocused;
    public int maxStringLength;
    public boolean enableBackgroundDrawing;
    public String placeholder = "";
    public int selectionEnd;
    public int disabledColor;
    public int width;
    public int enabledColor;
    public int xPosition;

    public void setCursorPosition(int position) {
        this.selectionEnd = position;
        int textLength = this.text.length();
        if (this.selectionEnd < 0) {
            this.selectionEnd = 0;
        }
        if (this.selectionEnd > textLength) {
            this.selectionEnd = textLength;
        }
        this.setSelectionPos(this.selectionEnd);
    }

    public int getNthWordFromCursor(int wordCount) {
        return this.getNthWordFromPos(wordCount, this.getSelectionEnd());
    }

    public void setMaxStringLength(int maxLength) {
        this.maxStringLength = maxLength;
        if (this.text.length() > maxLength) {
            this.text = this.text.substring(0, maxLength);
        }
    }

    public int getNthWordFromPos(int wordCount, int startPos, boolean skipSpaces) {
        int pos = startPos;
        boolean backwards = wordCount < 0;
        int steps = Math.abs(wordCount);
        for (int i = 0; i < steps; ++i) {
            if (!backwards) {
                int textLength = this.text.length();
                if ((pos = this.text.indexOf(32, pos)) == -1) {
                    pos = textLength;
                    continue;
                }
                while (skipSpaces && pos < textLength && this.text.charAt(pos) == ' ') {
                    ++pos;
                }
                continue;
            }
            while (skipSpaces && pos > 0 && this.text.charAt(pos - 1) == ' ') {
                --pos;
            }
            while (pos > 0 && this.text.charAt(pos - 1) != ' ') {
                --pos;
            }
        }
        return pos;
    }

    public void drawTextBox() {
        if (this.isEnabled()) {
            if (this.getEnableBackgroundDrawing()) {
                Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, Integer.MIN_VALUE);
            }
            int textColor = this.editable ? this.enabledColor : this.disabledColor;
            int selectionEndInView = this.selectionEnd - this.lineScrollOffset;
            int cursorInView = this.cursorPosition - this.lineScrollOffset;
            String visibleText = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean selectionInView = selectionEndInView >= 0 && selectionEndInView <= visibleText.length();
            boolean showCursor = this.isFocused && this.cursorCounter / 6 % 2 == 0 && selectionInView;
            int textX = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int textY = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int textEndX = textX;
            if (cursorInView > visibleText.length()) {
                cursorInView = visibleText.length();
            }
            if (this.getTextWithoutSpaces().length() == 0) {
                FontLoader.popMed20.drawString(this.placeholder, textX, textY, -2138535800);
            }
            if (visibleText.length() > 0) {
                if (selectionInView) {
                    visibleText.substring(0, selectionEndInView);
                }
                textEndX = (int)FontLoader.popMed20.drawString(this.text.replaceAll("(?s).", "*"), textX, textY, textColor);
            }
            boolean drawBlockCursor = this.selectionEnd < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int cursorX = textEndX;
            if (!selectionInView) {
                cursorX = selectionEndInView > 0 ? textX + this.width : textX;
            } else if (drawBlockCursor) {
                cursorX = textEndX - 1;
                --textEndX;
            }
            if (visibleText.length() <= 0 || !selectionInView || selectionEndInView < visibleText.length()) {
                // empty if block
            }
            if (showCursor) {
                if (drawBlockCursor) {
                    Gui.drawRect(cursorX, textY - 1, cursorX + 1, textY + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                } else {
                    FontLoader.popMed20.drawString("_", cursorX, textY, textColor);
                }
            }
            if (cursorInView != selectionEndInView) {
                int selectionX = textX + this.fontRendererInstance.getStringWidth(visibleText.substring(0, cursorInView));
                this.drawCursorVertical(cursorX, textY - 1, selectionX - 1, textY + 1 + this.fontRendererInstance.FONT_HEIGHT);
            }
        }
    }

    public void setEnableBackgroundDrawing(boolean enableBackground) {
        this.enableBackgroundDrawing = enableBackground;
    }

    public String getSelectedText() {
        int start = this.selectionEnd < this.cursorPosition ? this.selectionEnd : this.cursorPosition;
        int end = this.selectionEnd < this.cursorPosition ? this.cursorPosition : this.selectionEnd;
        return this.text.substring(start, end);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public void setCanLoseFocus(boolean allowLoseFocus) {
        this.canLoseFocus = allowLoseFocus;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public GuiPasswordField(FontRenderer fontRenderer, int x, int y, int width, int height) {
        this.text = "";
        this.maxStringLength = 50;
        this.enableBackgroundDrawing = true;
        this.canLoseFocus = true;
        this.isFocused = false;
        this.editable = true;
        this.lineScrollOffset = 0;
        this.selectionEnd = 0;
        this.cursorPosition = 0;
        this.enabledColor = 0xE0E0E0;
        this.disabledColor = 0x707070;
        this.enabled = true;
        this.fontRendererInstance = fontRenderer;
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean mouseInside;
        boolean isInside = mouseInside = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
        if (this.canLoseFocus) {
            this.setFocused(this.editable && mouseInside);
        }
        if (this.isFocused && mouseButton == 0) {
            int relativeX = mouseX - this.xPosition;
            if (this.enableBackgroundDrawing) {
                relativeX -= 4;
            }
            String visibleText = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(visibleText, relativeX).length() + this.lineScrollOffset);
        }
    }

    public void setFocused(boolean focused) {
        if (focused && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = focused;
    }

    public void writeText(String textToWrite) {
        int insertedLength;
        String newText = "";
        String filteredText = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
        int selectionMin = Math.min(this.selectionEnd, this.cursorPosition);
        int selectionMax = Math.max(this.selectionEnd, this.cursorPosition);
        int remainingCapacity = this.maxStringLength - this.text.length() - (selectionMin - this.cursorPosition);
        if (this.text.length() > 0) {
            newText = newText + this.text.substring(0, selectionMin);
        }
        if (remainingCapacity < filteredText.length()) {
            newText = newText + filteredText.substring(0, remainingCapacity);
            insertedLength = remainingCapacity;
        } else {
            newText = newText + filteredText;
            insertedLength = filteredText.length();
        }
        if (this.text.length() > 0 && selectionMax < this.text.length()) {
            newText = newText + this.text.substring(selectionMax);
        }
        this.text = newText.replaceAll(" ", "");
        this.moveCursorBy(selectionMin - this.cursorPosition + insertedLength);
    }

    public void drawCursorVertical(int x1, int y1, int x2, int y2) {
        if (x1 < x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GL11.glColor4f(0.0f, 0.0f, 255.0f, 255.0f);
        GL11.glDisable(3553);
        GL11.glEnable(3058);
        GL11.glLogicOp(5387);
        GL11.glDisable(3058);
        GL11.glEnable(3553);
    }

    public int getNthWordFromPos(int wordCount, int position) {
        return this.getNthWordFromPos(wordCount, this.getSelectionEnd(), true);
    }

    public void setSelectionPos(int position) {
        int textLength = this.text.length();
        if (position > textLength) {
            position = textLength;
        }
        if (position < 0) {
            position = 0;
        }
        this.cursorPosition = position;
        if (this.fontRendererInstance != null) {
            if (this.lineScrollOffset > textLength) {
                this.lineScrollOffset = textLength;
            }
            int visibleWidth = this.getWidth();
            String visibleText = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), visibleWidth);
            int visibleEnd = visibleText.length() + this.lineScrollOffset;
            if (position == this.lineScrollOffset) {
                this.lineScrollOffset -= this.fontRendererInstance.trimStringToWidth(this.text, visibleWidth, true).length();
            }
            if (position > visibleEnd) {
                this.lineScrollOffset += position - visibleEnd;
            } else if (position <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - position;
            }
            if (this.lineScrollOffset < 0) {
                this.lineScrollOffset = 0;
            }
            if (this.lineScrollOffset > textLength) {
                this.lineScrollOffset = textLength;
            }
        }
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public GuiPasswordField(String string, int x, int y, int width, int height, String placeholderText) {
        this.label = string;
        this.placeholder = placeholderText;
        this.text = "";
        this.maxStringLength = 50;
        this.enableBackgroundDrawing = true;
        this.canLoseFocus = true;
        this.isFocused = false;
        this.editable = true;
        this.lineScrollOffset = 0;
        this.selectionEnd = 0;
        this.cursorPosition = 0;
        this.enabledColor = 0xE0E0E0;
        this.disabledColor = 0x707070;
        this.enabled = true;
        this.fontRendererInstance = Minecraft.getMinecraft().fontRendererObj;
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
    }

    public void setTextColor(int color) {
        this.enabledColor = color;
    }

    public void deleteWords(int wordCount) {
        if (this.text.length() != 0) {
            if (this.cursorPosition != this.selectionEnd) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(wordCount) - this.selectionEnd);
            }
        }
    }

    public void deleteFromCursor(int offset) {
        if (this.text.length() != 0) {
            if (this.cursorPosition != this.selectionEnd) {
                this.writeText("");
            } else {
                boolean backwards = offset < 0;
                int deleteStart = backwards ? this.selectionEnd + offset : this.selectionEnd;
                int deleteEnd = backwards ? this.selectionEnd : this.selectionEnd + offset;
                String newText = "";
                if (deleteStart >= 0) {
                    newText = this.text.substring(0, deleteStart);
                }
                if (deleteEnd < this.text.length()) {
                    newText = newText + this.text.substring(deleteEnd);
                }
                this.text = newText;
                if (backwards) {
                    this.moveCursorBy(offset);
                }
            }
        }
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setText(String newText) {
        this.text = newText.length() > this.maxStringLength ? newText.substring(0, this.maxStringLength) : newText;
        this.setCursorPositionEnd();
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        if (!this.editable || !this.isFocused) {
            return false;
        }
        switch (typedChar) {
            case '\u0001': {
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return true;
            }
            case '\u0003': {
                GuiScreen.setClipboardString(this.getSelectedText());
                return true;
            }
            case '\u0016': {
                this.writeText(GuiScreen.getClipboardString());
                return true;
            }
            case '\u0018': {
                GuiScreen.setClipboardString(this.getSelectedText());
                this.writeText("");
                return true;
            }
        }
        switch (keyCode) {
            case 14: {
                if (GuiScreen.isCtrlDown()) {
                    this.deleteWords(-1);
                } else {
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
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getCursorPosition()));
                    } else {
                        this.setSelectionPos(this.getCursorPosition() - 1);
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
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getCursorPosition()));
                    } else {
                        this.setSelectionPos(this.getCursorPosition() + 1);
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
                    this.deleteWords(1);
                } else {
                    this.deleteFromCursor(1);
                }
                return true;
            }
        }
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            this.writeText(Character.toString(typedChar));
            return true;
        }
        return false;
    }

    public void moveCursorBy(int offset) {
        this.setCursorPosition(this.cursorPosition + offset);
    }

    public String getTextWithoutSpaces() {
        String strippedText = this.text.replaceAll(" ", "");
        return strippedText;
    }
}

