/*
 * Decompiled with CFR 0.152.
 */
package kawaii.command;

import java.util.ArrayList;
import kawaii.command.BindCommand;
import kawaii.command.Command;
import kawaii.command.ConfigCommand;
import kawaii.command.FriendCommand;
import kawaii.command.TestNotificationCommand;
import kawaii.command.VClipCommand;
import kawaii.util.ObjectList;

public class CommandManager {
    public ArrayList<Command> commands = new ObjectList();

    public void addCommand(Command command) {
        this.commands.add(command);
    }

    public boolean dispatch(String input) {
        if (!input.startsWith(".")) {
            return false;
        }
        String commandLine = input.substring(1);
        for (Command command : this.commands) {
            for (String alias : command.getAliases()) {
                if (!commandLine.split(" ")[0].equalsIgnoreCase(alias)) continue;
                command.execute(commandLine.split(" "));
                return true;
            }
        }
        return input.startsWith(".");
    }

    public CommandManager() {
        this.addCommand(new TestNotificationCommand());
        this.addCommand(new VClipCommand());
        this.addCommand(new FriendCommand());
        this.addCommand(new ConfigCommand());
        this.addCommand(new BindCommand());
    }
}

