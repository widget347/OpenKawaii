/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.movement;

import java.util.Arrays;
import kawaii.event.CancellableEvent;
import kawaii.event.EventType;
import kawaii.module.Category;
import kawaii.module.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

public class InvMove
extends Module {
    public static void updateKeyBindState(KeyBinding keyBinding) {
        KeyBinding.setKeyBindState(keyBinding.getKeyCode(), Keyboard.isKeyDown(keyBinding.getKeyCode()));
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (InvMove.mc.currentScreen != null && cancellableEvent.getEventType() == EventType.pre && !(InvMove.mc.currentScreen instanceof GuiChat)) {
            MovementInput.moveForward = 1.0f;
            if (Keyboard.isKeyDown(208)) {
                InvMove.mc.thePlayer.rotationPitch += 2.0f;
            }
            if (Keyboard.isKeyDown(200)) {
                InvMove.mc.thePlayer.rotationPitch -= 2.0f;
            }
            if (Keyboard.isKeyDown(205)) {
                InvMove.mc.thePlayer.rotationYaw += 2.0f;
            }
            if (Keyboard.isKeyDown(203)) {
                InvMove.mc.thePlayer.rotationYaw -= 2.0f;
            }
            KeyBinding[] keyBindingArray = new KeyBinding[]{InvMove.mc.gameSettings.keyBindForward, InvMove.mc.gameSettings.keyBindBack, InvMove.mc.gameSettings.keyBindLeft, InvMove.mc.gameSettings.keyBindRight, InvMove.mc.gameSettings.keyBindJump};
            Arrays.stream(keyBindingArray).forEach(InvMove::updateKeyBindState);
        }
    }

    public InvMove() {
        super("InvMove", "Allows you to move while in containers", Category.player);
    }
}

