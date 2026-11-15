/*
 * Decompiled with CFR 0.152.
 */
package kawaii.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class Command {
    public String[] aliases;
    public Minecraft mc = Minecraft.getMinecraft();
    public String name;

    public void execute(String[] args) {
    }

    public Command(String commandName, String ... aliasNames) {
        this.name = commandName;
        this.aliases = aliasNames;
    }

    public void sendMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00a7cKawaii \u00a77> \u00a76" + message));
    }

    public String getName() {
        return this.name;
    }

    public String[] getAliases() {
        return this.aliases;
    }
}

