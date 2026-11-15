/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kawaii.event.HandlerMap;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.combat.AimAssist;
import kawaii.module.combat.AutoClicker;
import kawaii.module.combat.Killaura;
import kawaii.module.combat.Reach;
import kawaii.module.combat.Velocity;
import kawaii.module.misc.ConfigGui;
import kawaii.module.misc.Disabler;
import kawaii.module.movement.AntiVoid;
import kawaii.module.movement.Flight;
import kawaii.module.movement.Fly;
import kawaii.module.movement.InvMove;
import kawaii.module.movement.SafeWalk;
import kawaii.module.movement.Speed;
import kawaii.module.movement.Sprint;
import kawaii.module.player.Blink;
import kawaii.module.player.ChestStealer;
import kawaii.module.player.FastPlace;
import kawaii.module.player.InvManager;
import kawaii.module.player.NoFall;
import kawaii.module.render.Blur;
import kawaii.module.render.ClickGUI;
import kawaii.module.render.ESP;
import kawaii.module.render.FullBright;
import kawaii.module.render.Hud;
import kawaii.module.render.NameTags;
import kawaii.module.render.Radar;
import kawaii.module.render.TabGui;
import kawaii.module.world.CustomTime;
import kawaii.module.world.FastMine;
import kawaii.module.world.Scaffold;
import kawaii.ui.font.CFontRenderer;
import kawaii.util.ObjectList;

public class ModuleManager {
    public Map<Class<? extends Module>, Module> modulesByClass = new HandlerMap();

    public Module getModule(Class clazz) {
        return this.modulesByClass.get(clazz);
    }

    public static boolean isDisabled(Module module) {
        return !module.isEnabled();
    }

    public ModuleManager() {
        this.registerModule(new Disabler());
        this.registerModule(new AutoClicker());
        this.registerModule(new Fly());
        this.registerModule(new Killaura());
        this.registerModule(new Scaffold());
        this.registerModule(new ConfigGui());
        this.registerModule(new FullBright());
        this.registerModule(new SafeWalk());
        this.registerModule(new CustomTime());
        this.registerModule(new Blink());
        this.registerModule(new Blur());
        this.registerModule(new FastPlace());
        this.registerModule(new InvMove());
        this.registerModule(new NoFall());
        this.registerModule(new Radar());
        this.registerModule(new FastMine());
        this.registerModule(new ESP());
        this.registerModule(new Reach());
        this.registerModule(new TabGui());
        this.registerModule(new NameTags());
        this.registerModule(new AimAssist());
        this.registerModule(new InvManager());
        this.registerModule(new AntiVoid());
        this.registerModule(new ChestStealer());
        this.registerModule(new Hud());
        this.registerModule(new Sprint());
        this.registerModule(new Flight());
        this.registerModule(new Speed());
        this.registerModule(new Velocity());
        this.registerModule(new ClickGUI());
    }

    public Module getModuleByName(String moduleName) {
        try {
            for (Module module : this.modulesByClass.values()) {
                if (!module.getName().equalsIgnoreCase(moduleName)) continue;
                return module;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public static Integer getNameWidth(CFontRenderer cFontRenderer, Object module) {
        return cFontRenderer.getStringWidth(((Module)module).getName());
    }

    public List<Module> getModules() {
        return new ObjectList(Arrays.asList(this.modulesByClass.values().toArray(new Module[0])));
    }

    public static boolean isInCategory(Category category, Module module) {
        return module.getCategory() == category;
    }

    public void registerModule(Module module) {
        this.modulesByClass.put(module.getClass(), module);
    }

    public List<Module> getSortedModules(CFontRenderer cFontRenderer) {
        List<Module> modules = this.getModules();
        modules.sort(Comparator.comparing(module -> ModuleManager.getNameWidth(cFontRenderer, module)).reversed());
        return modules;
    }

    public static Integer getNameWithSuffixWidth(CFontRenderer cFontRenderer, Object module) {
        return cFontRenderer.getStringWidth(((Module)module).getName() + (((Module)module).getSuffix() != null ? ((Module)module).getSuffix() + "[] " : ""));
    }

    public ArrayList<Module> getModulesInCategory(Category category, CFontRenderer cFontRenderer) {
        return this.getSortedModules(cFontRenderer).stream().filter(module -> ModuleManager.isInCategory(category, module)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Module> getSortedEnabledModules(CFontRenderer cFontRenderer) {
        List<Module> modules = this.getModules();
        modules.removeIf(ModuleManager::isDisabled);
        modules.sort(Comparator.comparing(module -> ModuleManager.getNameWithSuffixWidth(cFontRenderer, module)).reversed());
        return modules;
    }
}

