/*
 * Decompiled with CFR 0.152.
 */
package kawaii.command;

import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import kawaii.Kawaii;
import kawaii.command.Command;
import kawaii.ui.Notification;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

public class TestNotificationCommand
extends Command {
    public TestNotificationCommand() {
        super("TestNotifications", "notify", "testnotify");
    }

    public static Collector toCollection(Supplier supplier, int size) {
        return Collectors.toCollection(supplier);
    }

    @Override
    public void execute(String[] stringArray) {
        System.out.println("hi");
        Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Test Notification", "This is a test notification!", 4000.0, C19PacketResourcePackStatus.Action.SUCCESS));
        Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Test Notification", "This is a test notification!", 5000.0, C19PacketResourcePackStatus.Action.WARNING));
        Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Test Notification", "This is a test notification!", 6000.0, C19PacketResourcePackStatus.Action.ERROR));
        Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Test Notification", "This is a test notification!", 7000.0, C19PacketResourcePackStatus.Action.INFORMATION));
    }
}

