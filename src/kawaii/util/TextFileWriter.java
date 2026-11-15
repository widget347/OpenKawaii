/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

public class TextFileWriter
extends FileWriter {
    public void writeString(String text) throws IOException {
        super.write(text);
    }

    public TextFileWriter(File file) throws IOException {
        super(file);
    }

    public void closeFile() throws IOException {
        super.close();
    }
}

