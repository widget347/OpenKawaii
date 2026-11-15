/*
 * Decompiled with CFR 0.152.
 */
package kawaii.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.Writer;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import kawaii.Kawaii;
import kawaii.config.ConfigFile;
import kawaii.config.ConfigReader;
import kawaii.module.Module;
import kawaii.ui.Notification;
import kawaii.util.ConcurrentArrayList;
import kawaii.util.TextFileWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import org.apache.commons.io.FilenameUtils;

public class ConfigManager {
    public File configDir;
    public CopyOnWriteArrayList<ConfigFile> configs;
    public static boolean $assertionsDisabled = !ConfigManager.class.desiredAssertionStatus();

    public void deleteConfig(String configName) {
        try {
            new File(this.configDir, configName + ".json").delete();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        this.reloadConfigs();
    }

    public ConfigManager() {
        this.configDir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Kawaii.INSTANCE.getName() + "/configs");
        this.configs = new ConcurrentArrayList();
        if (!this.configDir.exists()) {
            this.configDir.mkdirs();
        }
        this.reloadConfigs();
    }

    public CopyOnWriteArrayList<ConfigFile> getConfigs() {
        return this.configs;
    }

    public static ConfigFile createConfig(File file) {
        return new ConfigFile(FilenameUtils.removeExtension(file.getName()).replace(" ", ""), file);
    }

    public static boolean nameMatches(String configName, ConfigFile configFile) {
        return configFile.getName().equalsIgnoreCase(configName);
    }

    public void addConfig(ConfigFile configFile) {
        if (!Objects.equals(configFile.getName(), "$default.json")) {
            this.configs.add(configFile);
        }
    }

    public void reloadConfigs() {
        try {
            this.configs.clear();
            if (!this.configDir.exists()) {
                this.configDir.mkdirs();
            }
            if (this.configDir != null) {
                File[] configFiles = this.configDir.listFiles(ConfigManager::isConfigFile);
                if (!$assertionsDisabled && configFiles == null) {
                    throw new AssertionError();
                }
                Arrays.stream(configFiles).map(ConfigManager::createConfig).forEach(this::addConfig);
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void loadConfig(String configName, boolean flag) {
        try {
            ConfigReader configReader = new ConfigReader(new File(this.configDir, configName + ".json"));
            JsonElement jsonElement = new JsonParser().parse(configReader);
            if (!jsonElement.isJsonObject()) {
                return;
            }
            JsonArray jsonArray = jsonElement.getAsJsonObject().get("modules").getAsJsonArray();
            jsonArray.forEach(moduleElement -> ConfigManager.loadModuleConfig(flag, moduleElement));
            Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Config Loaded", "Loaded config '" + configName + "'", 3000.0, C19PacketResourcePackStatus.Action.SUCCESS));
        }
        catch (Exception exception) {
            exception.printStackTrace();
            Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Config Not Found", "Could not find config '" + configName + "'", 3000.0, C19PacketResourcePackStatus.Action.SUCCESS));
        }
    }

    public static void loadModuleConfig(boolean flag, JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String moduleName = jsonObject.get("name").getAsString();
        Module module = Kawaii.INSTANCE.getModuleManager().getModuleByName(moduleName);
        if (module != null) {
            module.fromJson(jsonObject, flag);
        }
    }

    public static void writeModuleConfig(JsonArray jsonArray, Module module) {
        jsonArray.add(module.toJson());
    }

    public ConfigFile findConfig(String configName) {
        return this.configs.stream().filter(config -> ConfigManager.nameMatches(configName, config)).findFirst().orElse(null);
    }

    public static boolean isConfigFile(File file) {
        return !file.isDirectory() && FilenameUtils.getExtension(file.getName()).equals("json");
    }

    public void saveConfig(String configName) {
        File file = new File(this.configDir, configName + ".json");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            TextFileWriter textFileWriter = new TextFileWriter(file);
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            Kawaii.INSTANCE.getModuleManager().getModules().forEach(module -> ConfigManager.writeModuleConfig(jsonArray, module));
            jsonObject.add("modules", jsonArray);
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
            System.out.println(json);
            textFileWriter.write(json);
            ((Writer)textFileWriter).close();
            Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Config Saved", "Saved config '" + configName + "'", 2500.0, C19PacketResourcePackStatus.Action.SUCCESS));
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            file.delete();
        }
        this.reloadConfigs();
    }
}

