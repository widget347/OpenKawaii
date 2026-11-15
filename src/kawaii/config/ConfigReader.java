/*
 * Decompiled with CFR 0.152.
 */
package kawaii.config;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileReader;

public class ConfigReader
extends FileReader {
    public ConfigReader(File file) throws FileNotFoundException {
        super(file);
    }
}

