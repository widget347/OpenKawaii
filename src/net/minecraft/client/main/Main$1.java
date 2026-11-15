/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.main;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class Main$1
extends Authenticator {
    public String field_178749_a;
    public String field_178748_b;

    public Main$1(String username, String password) {
        this.field_178748_b = username;
        this.field_178749_a = password;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.field_178748_b, this.field_178749_a.toCharArray());
    }
}

