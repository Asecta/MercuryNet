package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.configuration.LangConfig;
import com.pandoaspen.mercury.common.service.healthcheck.model.PlayerInfo;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerInfo;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerStatus;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;

@CommandAlias("who|list|online")
public class PlayerListCommand extends AbstractMercuryCommand {

    public PlayerListCommand(MercuryBukkitPlugin plugin) {
        super(plugin);
    }

    @Default
    public void defaultCommand(CommandSender sender) {
        runTaskAsynchronously(() -> runListCommand(sender));
    }

    private void runListCommand(CommandSender sender) {
        LangConfig.ListLang listLang = getLangConfig().getListLang();

        Map<PlayerInfo, ServerInfo> playerMap = getPlayerMap();
        ServerInfo localServer = getHealthCheckService().getLocalServerInfo();

        int totalPlayerCount = playerMap.size();

        Set<BaseComponent[]> staffOnline = new HashSet<>();
        Set<String> playersOnline = new HashSet<>();

        for (Map.Entry<PlayerInfo, ServerInfo> entry : playerMap.entrySet()) {
            PlayerInfo playerInfo = entry.getKey();
            ServerInfo serverInfo = entry.getValue();

            if (!playerInfo.isVisible()) continue;

            if (playerInfo.isStaff()) {
                BaseComponent[] msg = parseMessage(listLang.getStaffmember(), "player", playerInfo.getName(), "server", serverInfo.getServerId());
                staffOnline.add(msg);
            }

            if (serverInfo.getServerId().equals(localServer.getServerId())) {
                playersOnline.add(playerInfo.getName());
            }
        }


        List<BaseComponent[]> response = new ArrayList<>();

        response.add(parseMessage(listLang.getTotal() + "\n",
                "count", Integer.toString(totalPlayerCount)));

        response.add(parseMessage(listLang.getServer() + "\n",
                "count", playersOnline.size(),
                      "server", localServer.getServerId()));


        if (staffOnline.isEmpty()) {
            response.add(parseMessage(listLang.getNostaff() + "\n"));
        } else {
            response.add(parseMessage(listLang.getStaff()));
            response.add(staffOnline.stream().flatMap(comp -> Arrays.stream(comp)).toArray(BaseComponent[]::new));
            response.add(new BaseComponent[]{new TextComponent("\n")});
        }

        response.add(parseMessage(listLang.getOnline(),
                "players", String.join(", ", playersOnline)));

        sendMessage(sender, response);
    }



    private Map<PlayerInfo, ServerInfo> getPlayerMap() {
        Map<PlayerInfo, ServerInfo> map = new HashMap<>();

        for (ServerInfo serverInfo : getHealthCheckService().getServerInfoMap().values()) {

            if (serverInfo.getServerType() != ServerType.BUKKIT) continue;
            if (serverInfo.getServerStatus() == ServerStatus.STOPPED) continue;

            for (PlayerInfo onlinePlayer : serverInfo.getOnlinePlayers()) {
                map.put(onlinePlayer, serverInfo);
            }
        }

        return map;
    }

    private StringBuilder appendf(StringBuilder stringBuilder, String template, Object... args) {
        return stringBuilder.append(ChatColor.translateAlternateColorCodes('&', String.format(template, args)));
    }
}
