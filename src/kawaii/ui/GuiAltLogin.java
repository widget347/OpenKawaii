/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import java.io.IOException;
import kawaii.render.RenderUtils;
import kawaii.ui.Button;
import kawaii.ui.GuiPasswordField;
import kawaii.ui.GuiTheme;
import kawaii.ui.Widget;
import kawaii.ui.WidgetList;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

public class GuiAltLogin
extends GuiScreen
implements WidgetList {
    public MicrosoftAuthenticator microsoftAuth = new MicrosoftAuthenticator();
    public String status = (Object)((Object)EnumChatFormatting.GRAY) + "Idle..";
    public GuiScreen parentScreen;
    public GuiPasswordField passwordField;
    public GuiTextField usernameField;

    public void goBack() {
        this.mc.displayGuiScreen(this.parentScreen);
    }

    @Override
    public void updateScreen() {
        this.usernameField.updateCursorCounter();
        this.passwordField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float halfDivisor = 2.0f;
        this.drawDefaultBackground();
        RenderUtils.drawImageWithAlpha(0.0, 0.0, this.width, this.height, new ResourceLocation("kawaii/images/background.png"), 1.0);
        this.usernameField.drawTextBox();
        this.passwordField.drawTextBox();
        FontLoader.sf24.drawCenteredString("Alt Login", (float)this.width / halfDivisor, (float)this.height / halfDivisor - 90.0f, -1);
        FontLoader.popMed20.drawCenteredString(this.status, (float)this.width / halfDivisor, (float)this.height / halfDivisor - 78.0f, -1);
        int entryIndex = 0;
        for (Widget widget : entries) {
            widget.onMouseDrag((double)((float)this.width / halfDivisor) - widget.getWidth() / 2.0, (double)((float)this.height / halfDivisor) + (double)entryIndex * (widget.getHeight() + 5.0), mouseX, mouseY);
            ++entryIndex;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void login() {
        if (this.passwordField.getTextWithoutSpaces().length() == 0) {
            if (this.usernameField.getText().length() > 0) {
                this.mc.session = new Session(this.usernameField.getText(), "", "", "MOJANG");
                this.status = "Logged into offline account \u00a77[ \u00a76" + this.usernameField.getText() + " \u00a77]";
            } else {
                this.status = "Invalid Username";
            }
            return;
        }
        try {
            MicrosoftAuthResult microsoftAuthResult = this.microsoftAuth.loginWithCredentials(this.usernameField.getText(), this.passwordField.getTextWithoutSpaces());
            this.status = "Logged into \u00a77[ \u00a76" + microsoftAuthResult.getProfile().getName() + "\u00a77 ]";
            this.mc.session = new Session(microsoftAuthResult.getProfile().getName(), microsoftAuthResult.getProfile().getId(), microsoftAuthResult.getAccessToken(), "MICROSOFT");
        }
        catch (MicrosoftAuthenticationException microsoftAuthenticationException) {
            this.status = microsoftAuthenticationException.getMessage();
            microsoftAuthenticationException.printStackTrace();
        }
        catch (RuntimeException runtimeException) {
            this.status = "Something went wrong with your account...";
            runtimeException.printStackTrace();
        }
        catch (Exception exception) {
            this.status = "Something went wrong...";
            exception.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            this.checkMouseOverAll(mouseX, mouseY);
        }
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        this.usernameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.passwordField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onClear() {
        entries.add(new Button("Login", 200.0, 30.0, GuiTheme.darkUnderline, this::login));
        entries.add(new Button("Login Via Clipboard", 200.0, 30.0, GuiTheme.darkUnderline, this::pasteFromClipboard));
        entries.add(new Button("Exit", 200.0, 30.0, GuiTheme.darkUnderline, this::goBack));
    }

    public GuiAltLogin(GuiScreen guiScreen) {
        this.parentScreen = guiScreen;
    }

    @Override
    public void initGui() {
        this.clearAll();
        int textFieldId = this.height / 2 + 24;
        this.usernameField = new GuiTextField(textFieldId, this.fontRendererObj, this.width / 2 - 100, (int)((float)this.height / 2.0f - 30.0f - 30.0f), 200, 20, "Username | Email");
        this.passwordField = new GuiPasswordField("", (int)((float)this.width / 2.0f - 100.0f), (int)((float)this.height / 2.0f + 25.0f - 25.0f - 30.0f), 200, 20, "Password");
        this.usernameField.setFocused(true);
        if (this.mc.getSession().getUsername() != null) {
            this.usernameField.setText(this.mc.getSession().getUsername());
        }
        Keyboard.enableRepeatEvents(true);
    }

    public void pasteFromClipboard() {
        if (GuiAltLogin.getClipboardString() != null && GuiAltLogin.getClipboardString().split(":").length > 1) {
            String[] stringArray = GuiAltLogin.getClipboardString().split(":");
            this.usernameField.setText(stringArray[0]);
            this.passwordField.setText(stringArray[1]);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        try {
            super.keyTyped(typedChar, keyCode);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (typedChar == '\t') {
            if (!this.usernameField.isFocused() && !this.passwordField.isFocused()) {
                this.usernameField.setFocused(true);
            } else {
                this.usernameField.setFocused(this.passwordField.isFocused());
                this.passwordField.setFocused(!this.usernameField.isFocused());
            }
        }
        if (typedChar == '\r') {
            this.runButtonAt(0);
        }
        this.usernameField.textboxKeyTyped(typedChar, keyCode);
        this.passwordField.textboxKeyTyped(typedChar, keyCode);
    }
}

