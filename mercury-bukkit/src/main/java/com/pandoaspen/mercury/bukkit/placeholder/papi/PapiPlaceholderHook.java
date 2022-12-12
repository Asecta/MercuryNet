package com.pandoaspen.mercury.bukkit.placeholder.papi;

import com.pandoaspen.mercury.bukkit.placeholder.PlaceholderHook;
import com.pandoaspen.mercury.bukkit.placeholder.PlaceholderProvider;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PapiPlaceholderHook extends PlaceholderExpansion implements PlaceholderHook {

    private final JavaPlugin plugin;
    private Map<String, PlaceholderProvider> placeholders;

    @Override
    public void startup() {
        this.placeholders = new HashMap<>();
        register();
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void registerPlaceholder(String placeholder, PlaceholderProvider placeholderProvider) {
        placeholders.put(placeholder, placeholderProvider);
    }

    @Override
    public String onPlaceholderRequest(Player player, String label) {
        String prefix = label;
        if (label.contains(".")) {
            prefix = label.split("\\.", 2)[0];
            label = label.split("\\.", 2)[1];
        }

        PlaceholderProvider handler = placeholders.get(prefix);
        if (handler == null) return null;
        return handler.provide(player, label);
    }

    @Override
    public String getIdentifier() {
        return plugin.getName();
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }


}
