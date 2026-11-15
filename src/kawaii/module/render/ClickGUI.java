/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.render;

import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.ModeSetting;
import kawaii.ui.clean.ClickGui;

public class ClickGUI
extends Module {
    public ModeSetting modeSetting = new ModeSetting("Mode", "Kawaii", "Clean", "Jello", "Astolfo");

    public ClickGUI() {
        super("ClickGUI", "You should know what this is already.", Category.visuals);
        this.setKeyBind(54);
        this.addSettings(this.modeSetting);
    }

    @Override
    public void onEnable() {
        switch (this.modeSetting.getMode()) {
            case "Jello": {
                kawaii.ui.jello.ClickGui.open();
                break;
            }
            case "Clean": {
                ClickGui.open();
                break;
            }
            case "Astolfo": {
                kawaii.ui.astolfo.ClickGui.open();
                break;
            }
            default: {
                kawaii.ui.kawaii.ClickGui.openClickGui();
            }
        }
        this.setEnabled(false);
    }
}

