/*
 * Decompiled with CFR 0.152.
 */
package kawaii.auth;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public class AltLoginThread
extends Thread {
    public Minecraft mc = Minecraft.getMinecraft();
    public String username;
    public String status;
    public String password;

    public AltLoginThread(String passwordInput, String usernameInput) {
        super("Alt Login Thread");
        this.password = passwordInput;
        this.username = usernameInput;
        this.status = (Object)((Object)EnumChatFormatting.GRAY) + "Waiting...";
    }

    public Session authenticate(String loginName, String loginPassword) {
        YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication yggdrasilUserAuthentication = (YggdrasilUserAuthentication)yggdrasilAuthenticationService.createUserAuthentication(Agent.MINECRAFT);
        yggdrasilUserAuthentication.setUsername(loginName);
        yggdrasilUserAuthentication.setPassword(loginPassword);
        try {
            yggdrasilUserAuthentication.logIn();
            return new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "mojang");
        }
        catch (AuthenticationException authenticationException) {
            authenticationException.printStackTrace();
            return null;
        }
    }

    @Override
    public void run() {
        if (this.username.length() < 1) {
            this.mc.session = new Session(this.password, "", "", "mojang");
            this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Logged in. (" + this.password + " - offline name)";
            return;
        }
        this.status = (Object)((Object)EnumChatFormatting.YELLOW) + "Logging in...";
        Session session = this.authenticate(this.password, this.username);
        if (session == null) {
            this.status = (Object)((Object)EnumChatFormatting.RED) + "Login failed!";
        } else {
            this.status = (Object)((Object)EnumChatFormatting.GREEN) + "Logged in. (" + session.getUsername() + ")";
            this.mc.session = session;
        }
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    public String getStatus() {
        return this.status;
    }
}

