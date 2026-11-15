/*
 * Decompiled with CFR 0.152.
 */
package kawaii.command;

import kawaii.Kawaii;
import kawaii.command.Command;
import kawaii.module.Module;
import org.lwjgl.input.Keyboard;

public class BindCommand
extends Command {
    public BindCommand() {
        super("Bind", "bind");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 3) {
            this.sendMessage("Invalid Arguments");
            return;
        }
        Module module = Kawaii.INSTANCE.getModuleManager().getModuleByName(args[1]);
        if (module == null) {
            this.sendMessage("Could not find module. [" + args[1] + "]");
            return;
        }
        int keyCode = Keyboard.getKeyIndex(args[2].toUpperCase()) == 0 ? Keyboard.getKeyIndex(args[2]) : Keyboard.getKeyIndex(args[2].toUpperCase());
        module.setKeyBind(keyCode);
        this.sendMessage("Binded " + module.getName() + " to key " + Keyboard.getKeyName(keyCode) + " [" + module.getKeyBind() + "]");
    }
}

