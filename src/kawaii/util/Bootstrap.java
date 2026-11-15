/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

import kawaii.Kawaii;
import kawaii.auth.HwidWhitelist;

public class Bootstrap {
    public void bootstrap() {
        HwidWhitelist.checkWhitelist();
        Kawaii.initialize();
    }
}

