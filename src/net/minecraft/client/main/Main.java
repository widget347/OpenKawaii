/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.Proxy;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import kawaii.Kawaii;
import kawaii.network.CustomProxy;
import kawaii.network.ProxyAddress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.main.Main$1;
import net.minecraft.client.main.Main$2;
import net.minecraft.util.Session;

public class Main {
    public static boolean isNullOrEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("fullscreen");
        optionParser.accepts("checkGlErrors");
        ArgumentAcceptingOptionSpec<String> serverSpec = optionParser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> portSpec = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<File> gameDirSpec = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), (File[])new File[0]);
        ArgumentAcceptingOptionSpec<File> assetsDirSpec = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> resourcePackDirSpec = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<String> proxyHostSpec = optionParser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec<Integer> proxyPortSpec = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", (String[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> proxyUserSpec = optionParser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> proxyPassSpec = optionParser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> usernameSpec = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L, (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> uuidSpec = optionParser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> accessTokenSpec = optionParser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<String> versionSpec = optionParser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec<Integer> widthSpec = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<Integer> heightSpec = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, (Integer[])new Integer[0]);
        ArgumentAcceptingOptionSpec<String> userPropertiesSpec = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> profilePropertiesSpec = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> assetIndexSpec = optionParser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec<String> userTypeSpec = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy", (String[])new String[0]);
        NonOptionArgumentSpec<String> nonOptionArgumentSpec = optionParser.nonOptions();
        ArgumentAcceptingOptionSpec<String> intentApiKeySpec = optionParser.accepts("intentApiKey").withRequiredArg();
        OptionSet optionSet = optionParser.parse(args);
        Kawaii.apiKey = optionSet.valueOf(intentApiKeySpec);
        List<String> ignoredArguments = optionSet.valuesOf(nonOptionArgumentSpec);
        if (!ignoredArguments.isEmpty()) {
            System.out.println("Completely ignored arguments: " + ignoredArguments);
        }
        String proxyHost = optionSet.valueOf(proxyHostSpec);
        Proxy proxy = Proxy.NO_PROXY;
        if (proxyHost != null) {
            try {
                proxy = new CustomProxy(Proxy.Type.SOCKS, new ProxyAddress(proxyHost, (int)optionSet.valueOf(proxyPortSpec)));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        String proxyUser = optionSet.valueOf(proxyUserSpec);
        String proxyPass = optionSet.valueOf(proxyPassSpec);
        if (!proxy.equals(Proxy.NO_PROXY) && Main.isNullOrEmpty(proxyUser) && Main.isNullOrEmpty(proxyPass)) {
            Authenticator.setDefault(new Main$1(proxyUser, proxyPass));
        }
        int width = optionSet.valueOf(widthSpec);
        int height = optionSet.valueOf(heightSpec);
        boolean isFullscreen = optionSet.has("fullscreen");
        boolean shouldCheckGlErrors = optionSet.has("checkGlErrors");
        boolean isDemo = optionSet.has("demo");
        String version = optionSet.valueOf(versionSpec);
        Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)PropertyMap.class), new PropertyMap.Serializer()).create();
        PropertyMap userPropertyMap = gson.fromJson(optionSet.valueOf(userPropertiesSpec), PropertyMap.class);
        PropertyMap profilePropertyMap = gson.fromJson(optionSet.valueOf(profilePropertiesSpec), PropertyMap.class);
        File gameDir = optionSet.valueOf(gameDirSpec);
        File assetsDir = optionSet.has(assetsDirSpec) ? optionSet.valueOf(assetsDirSpec) : new File(gameDir, "assets/");
        File resourcePackDir = optionSet.has(resourcePackDirSpec) ? optionSet.valueOf(resourcePackDirSpec) : new File(gameDir, "resourcepacks/");
        String uuid = optionSet.has(uuidSpec) ? (String)uuidSpec.value(optionSet) : (String)usernameSpec.value(optionSet);
        String assetIndex = optionSet.has(assetIndexSpec) ? (String)assetIndexSpec.value(optionSet) : null;
        String server = optionSet.valueOf(serverSpec);
        Integer port = optionSet.valueOf(portSpec);
        Session session = new Session((String)usernameSpec.value(optionSet), uuid, (String)accessTokenSpec.value(optionSet), (String)userTypeSpec.value(optionSet));
        GameConfiguration gameConfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, userPropertyMap, profilePropertyMap, proxy), new GameConfiguration.DisplayInformation(width, height, isFullscreen, shouldCheckGlErrors), new GameConfiguration.FolderInformation(gameDir, resourcePackDir, assetsDir, assetIndex), new GameConfiguration.GameInformation(isDemo, version), new GameConfiguration.ServerInformation(server, port));
        Runtime.getRuntime().addShutdownHook(new Main$2("Client Shutdown Thread"));
        Thread.currentThread().setName("Client thread");
        new Minecraft(gameConfiguration).run();
    }
}

