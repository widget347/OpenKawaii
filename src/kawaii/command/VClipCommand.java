/*
 * Decompiled with CFR 0.152.
 */
package kawaii.command;

import kawaii.command.Command;
import kawaii.util.NumberParser;

public class VClipCommand
extends Command {
    public VClipCommand() {
        super("VClip", "vclip");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 1 || NumberParser.parseDouble(args[1]) == null) {
            this.sendMessage("Invalid Arguments");
            return;
        }
        this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + NumberParser.parseDouble(args[1]), this.mc.thePlayer.posZ);
        this.sendMessage("Set PosY to " + this.mc.thePlayer.posY);
    }
}

