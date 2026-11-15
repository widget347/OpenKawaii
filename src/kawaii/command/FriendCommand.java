/*
 * Decompiled with CFR 0.152.
 */
package kawaii.command;

import java.util.ArrayList;
import kawaii.command.Command;
import kawaii.util.ObjectList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class FriendCommand
extends Command {
    public static ArrayList<String> friends = new ObjectList();

    public FriendCommand() {
        super("Friend", "f", "friend", "friends");
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("clear")) {
                friends.clear();
                this.sendMessage("Cleared friends list!");
                return;
            }
            if (args[1].equalsIgnoreCase("list")) {
                StringBuilder stringBuilder = new StringBuilder("Friends:\n");
                for (String friend : friends) {
                    stringBuilder.append("- ").append(friend).append("\n");
                }
                this.sendMessage(stringBuilder.toString());
                return;
            }
            if (args.length > 2) {
                this.sendMessage("Please enter a valid username.");
                return;
            }
            for (EntityPlayer entityPlayer : this.mc.theWorld.playerEntities) {
                if (!entityPlayer.getName().toLowerCase().startsWith(args[1].toLowerCase())) continue;
                if (entityPlayer == this.mc.thePlayer) {
                    this.sendMessage("We know you lack friends, but you can't be your own friend. :(");
                    return;
                }
                if (friends.contains(entityPlayer.getName())) {
                    friends.remove(entityPlayer.getName());
                    this.sendMessage("Removed " + entityPlayer.getName() + " from friends!");
                    return;
                }
                friends.add(entityPlayer.getName());
                this.sendMessage("Added " + entityPlayer.getName() + " to friends!");
                return;
            }
            this.sendMessage("Couldn't Find Player '" + args[1]);
            return;
        }
        this.sendMessage("Invalid Arguments for [Friend]!");
        super.execute(args);
    }

    public static boolean isFriend(EntityLivingBase entityLivingBase) {
        if (entityLivingBase instanceof EntityPlayer) {
            return friends.contains(entityLivingBase.getName());
        }
        return false;
    }
}

