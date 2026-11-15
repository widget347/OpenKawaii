/*
 * Decompiled with CFR 0.152.
 */
package kawaii;

import kawaii.command.CommandManager;
import kawaii.config.ConfigManager;
import kawaii.event.ModuleKeyHandler;
import kawaii.module.ModuleManager;
import kawaii.ui.InfoElementManager;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.Minecraft;
import store.intent.api.account.GetUserInfo;
import store.intent.api.account.IntentAccount;
import store.intent.intentguard.annotation.ApiKey;

public enum Kawaii {
    INSTANCE;

    public InfoElementManager infoElementManager;
    public String version = "v1.0";
    public CommandManager commandManager;
    public boolean initialized = false;
    public ModuleKeyHandler moduleKeyHandler;
    public IntentAccount intentAccount;
    public ConfigManager configManager;
    public String name = "Kawaii";
    public ModuleManager moduleManager;
    @ApiKey
    public static String apiKey;

    public static void printMessage(String message) {
        System.out.println(INSTANCE.getName() + " > " + message);
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public String getName() {
        return this.name;
    }

    static {
        apiKey = "";
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public static void initialize() {
        Minecraft.logger.info("Setting Up Kawaii");
        if (INSTANCE.isInitialized()) {
            return;
        }
        Minecraft.logger.info("0x53");
        System.out.println("Initializing Managers");
        Kawaii.INSTANCE.infoElementManager = new InfoElementManager();
        Kawaii.INSTANCE.commandManager = new CommandManager();
        Kawaii.INSTANCE.moduleKeyHandler = new ModuleKeyHandler();
        Kawaii.INSTANCE.configManager = new ConfigManager();
        Kawaii.INSTANCE.moduleManager = new ModuleManager();
        Minecraft.logger.info("0x62");
        try {
            Kawaii.INSTANCE.intentAccount = new GetUserInfo().getIntentAccount(apiKey);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (Kawaii.INSTANCE.intentAccount == null) {
            Kawaii.INSTANCE.intentAccount = new IntentAccount();
            Kawaii.INSTANCE.intentAccount.client_uid = 0;
            Kawaii.INSTANCE.intentAccount.username = "null";
        }
        Minecraft.logger.info("0x13");
        System.out.println("Initializing Fonts");
        FontLoader.initialize();
        Minecraft.logger.info("0x91");
        System.out.println("Loading Default Config");
        if (Kawaii.INSTANCE.configManager.findConfig("$default") != null) {
            Kawaii.INSTANCE.configManager.loadConfig("$default", true);
        }
        System.out.println("Welcome to " + Kawaii.INSTANCE.name);
        Minecraft.logger.info("Welcome to " + Kawaii.INSTANCE.name);
    }

    public static void markInitialized() {
        Kawaii.INSTANCE.infoElementManager = null;
        Kawaii.INSTANCE.initialized = true;
    }

    public IntentAccount getIntentAccount() {
        return this.intentAccount;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public InfoElementManager getInfoElementManager() {
        return this.infoElementManager;
    }

    public String getVersion() {
        return this.version;
    }

    public ModuleKeyHandler getModuleKeyHandler() {
        return this.moduleKeyHandler;
    }
}

