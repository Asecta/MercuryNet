package com.pandoaspen.mercury.bukkit.placeholder;

import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.placeholder.papi.PapiPlaceholderHook;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerInfo;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class PlaceholderManager implements PlaceholderHook {

    private final MercuryBukkitPlugin plugin;
    private Set<PlaceholderHook> hooks;

    @Override
    public void startup() {
        this.hooks = new HashSet<>();

        this.tryEnableHook("PlaceholderAPI", () -> new PapiPlaceholderHook(plugin));

        this.hooks.forEach(PlaceholderHook::startup);

        this.setupDefaultPlaceholders();
    }


    private void tryEnableHook(String pluginName, Supplier<? extends PlaceholderHook> placeholderHookSupplier) {
        if (plugin.getServer().getPluginManager().getPlugin(pluginName) != null) {
            this.hooks.add(placeholderHookSupplier.get());
            plugin.getLogger().info("Hooking into placeholder provider: " + pluginName);
        } else {
            plugin.getLogger().info("Could not hook into placeholder provider: " + pluginName);
        }
    }


    @Override
    public void registerPlaceholder(String placeholder, PlaceholderProvider provider) {
        for (PlaceholderHook hook : hooks) {
            hook.registerPlaceholder(placeholder, provider);
        }
    }

    @Override
    public void shutdown() {
        hooks.forEach(PlaceholderHook::shutdown);
    }

    private void setupDefaultPlaceholders() {
        registerPlaceholder("server", (player, label) -> {

            String[] args = label.split("\\.");

            if (args.length == 0) {
                return "null";
            }

            String serverName = args[0];

            AbstractHealthCheckService healthCheckService =
                    plugin.getMercuryService().getSubService(AbstractHealthCheckService.class);
            List<ServerInfo> serverInfos = healthCheckService.getServersInGroup(serverName);

            if (serverInfos == null || serverInfos.isEmpty()) {
                return "offline";
            }

            if (args[1].equals("playercount")) {
                int playerCount = 0;

                for (ServerInfo serverInfo : serverInfos) {
                    playerCount += serverInfo.getOnlinePlayers().size();
                }

                return Integer.toString(playerCount);
            }

            return "nan";
        });

    }
}