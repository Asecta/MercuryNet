package com.pandoaspen.mercury.bukkit.placeholder;

import org.bukkit.entity.Player;

public interface PlaceholderProvider {
    String provide(Player player, String label);
}
