/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.movement;

import kawaii.Kawaii;
import kawaii.event.CancellableEvent;
import kawaii.event.EventType;
import kawaii.event.ImageInitEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.world.Scaffold;
import kawaii.setting.BooleanSetting;
import kawaii.util.MovementUtil;

public class Sprint
extends Module {
    public BooleanSetting omniSprintSetting = new BooleanSetting("Omni-Sprint", false);

    public Sprint() {
        super("Sprint", "Automatically sprints for you", Category.player);
        this.addSettings(this.omniSprintSetting);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (cancellableEvent instanceof ImageInitEvent && cancellableEvent.getEventType() == EventType.pre && (this.omniSprintSetting.getValue() != false ? MovementUtil.isMoving() : MovementUtil.isMovingForward()) && !Sprint.mc.thePlayer.isEating() && !Kawaii.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled()) {
            Sprint.mc.thePlayer.setSprinting(true);
        }
    }
}

