/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.misc;

import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.ui.ConfigManagerGui;

public class ConfigGui
extends Module {
    @Override
    public void onEnable() {
        mc.displayGuiScreen(new ConfigManagerGui());
        this.setEnabled(false);
    }

    public ConfigGui() {
        super("ConfigGUI", "Configuration Manager", Category.visuals);
        this.setKeyBind(37);
    }
}

