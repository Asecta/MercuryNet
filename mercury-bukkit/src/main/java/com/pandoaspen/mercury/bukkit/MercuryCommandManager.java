package com.pandoaspen.mercury.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.pandoaspen.mercury.bukkit.command.*;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerInfo;
import com.pandoaspen.mercury.common.service.redirect.RedirectService;
import com.pandoaspen.mercury.common.service.redirect.model.RedirectReplyMessage;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class MercuryCommandManager implements Listener {

    private final MercuryBukkitPlugin plugin;

    private static SimpleCommandMap commandMap;
    private static Map<String, Command> knownCommands;

    public void initialize() {
        PaperCommandManager commandManager = new PaperCommandManager(plugin);
        commandManager.enableUnstableAPI("help");

        commandManager.registerCommand(new AlertCommand(plugin));
        commandManager.registerCommand(new FindCommand(plugin));
        commandManager.registerCommand(new JoinCommand(plugin));
        commandManager.registerCommand(new PlayerListCommand(plugin));
        commandManager.registerCommand(new RedirectCommand(plugin));
        commandManager.registerCommand(new RestartCommand(plugin));
        commandManager.registerCommand(new StaffChatCommand(plugin));
        commandManager.registerCommand(new WhereAmICommand(plugin));
        commandManager.registerCommand(new DispatchCommand(plugin));
        commandManager.registerCommand(new PingCommand(plugin));
        commandManager.registerCommand(new MercuryCommand(plugin));

        Command actualCommand = new Command("restart") {
            @Override
            public boolean execute(CommandSender commandSender, String s, String[] strings) {
                String commandStr = "stop " + String.join(" ", strings);
                return plugin.getServer().dispatchCommand(commandSender, commandStr);
            }
        };
        getKnownCommands().put("restart", actualCommand);
        commandMap.register("restart", "mercury", actualCommand);

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();

        if (command.contains(" ")) {
            return;
        }

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        if (!event.getPlayer().hasPermission("mercury.server." + command)) {
            return;
        }

        AbstractHealthCheckService healthcheckService =
                plugin.getMercuryService().getSubService(AbstractHealthCheckService.class);
        List<ServerInfo> serverInfos = healthcheckService.getServersInGroup(command);

        if (serverInfos == null || serverInfos.isEmpty()) {
            return;
        }

        ServerInfo targetServer = serverInfos.get((int) (serverInfos.size() * Math.random()));

        RedirectService redirectService = plugin.getMercuryService().getSubService(RedirectService.class);
        CompletableFuture<RedirectReplyMessage> future =
                redirectService.redirect(event.getPlayer().getUniqueId(), targetServer.getServerId(),
                        event.getPlayer().getUniqueId());

        event.setCancelled(true);
    }

    public SimpleCommandMap getCommandMap() {
        if (commandMap != null) {
            return commandMap;
        }
        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (SimpleCommandMap) field.get(Bukkit.getPluginManager());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return commandMap;
    }

    public Map<String, Command> getKnownCommands() {
        if (knownCommands != null) {
            return knownCommands;
        }
        try {
            Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);
            knownCommands = (Map) field.get(getCommandMap());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return knownCommands;
    }

}
