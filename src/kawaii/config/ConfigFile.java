/*
 * Decompiled with CFR 0.152.
 */
package kawaii.config;

import java.io.File;

public class ConfigFile {
    public String name;
    public File file;

    public void setName(String newName) {
        this.name = newName;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ConfigFile(String configName, File file) {
        this.name = configName;
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public String getName() {
        return this.name;
    }
}

