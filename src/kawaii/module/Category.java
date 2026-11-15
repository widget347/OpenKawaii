/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module;

public enum Category {
    combat("Combat", -44462),
    movement("Movement", -8519834),
    player("Player", -5356033),
    visuals("Visuals", -7247361),
    exploit("Exploit", -11229953);

    public String displayName;
    public int id;

    public int getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    private Category(java.lang.String displayName, int id) {
        this.displayName = displayName;
        this.id = id;
    }
}

