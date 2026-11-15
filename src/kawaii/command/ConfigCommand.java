/*
 * Decompiled with CFR 0.152.
 */
package kawaii.command;

import kawaii.Kawaii;
import kawaii.command.Command;
import kawaii.config.ConfigFile;

public class ConfigCommand
extends Command {
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            this.sendMessage("Invalid Arguments");
            return;
        }
        switch (args[1]) {
            case "load": {
                Kawaii.INSTANCE.getConfigManager().loadConfig(args[2], false);
                return;
            }
            case "save": {
                Kawaii.INSTANCE.getConfigManager().saveConfig(args[2]);
                return;
            }
            case "list": {
                this.sendMessage("+ Configs: [" + Kawaii.INSTANCE.getConfigManager().getConfigs().size() + "]");
                for (ConfigFile configFile : Kawaii.INSTANCE.getConfigManager().getConfigs()) {
                    this.sendMessage("\u00a73" + configFile.getName());
                }
                return;
            }
        }
        this.sendMessage("Invalid Arguments");
    }

    public ConfigCommand() {
        super("Config", "config", "cfg");
    }
}

