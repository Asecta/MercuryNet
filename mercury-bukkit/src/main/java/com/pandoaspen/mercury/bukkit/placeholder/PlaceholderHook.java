package com.pandoaspen.mercury.bukkit.placeholder;

import com.pandoaspen.mercury.common.api.Startable;

public interface PlaceholderHook extends Startable {
    public void registerPlaceholder(String placeholder, PlaceholderProvider placeholderProvider);
}
