package com.pandoaspen.mercury.velocity.service.healthcheck;

import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthcheckHeartbeat;
import com.pandoaspen.mercury.common.service.healthcheck.model.PlayerInfo;
import com.pandoaspen.mercury.velocity.MercuryPlugin;
import com.velocitypowered.api.proxy.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class VelocityHealthCheckHeartbeat extends AbstractHealthcheckHeartbeat {

    private final MercuryPlugin plugin;

    public VelocityHealthCheckHeartbeat(AbstractHealthCheckService healthCheckService, MercuryPlugin plugin) {
        super(healthCheckService);
        this.plugin = plugin;
    }

    @Override
    protected boolean updateOnlinePlayers() {
        Set<UUID> onlineUUIDS =
                plugin.getServer().getAllPlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
        Set<UUID> trackedUUIDS = this.getLocalServerInfo().getOnlinePlayers().stream().map(PlayerInfo::getUuid)
                .collect(Collectors.toSet());

        for (PlayerInfo playerInfo : this.getLocalServerInfo().getOnlinePlayers()) {
            if (!onlineUUIDS.contains(playerInfo.getUuid())) {
                this.getLocalServerInfo().getOnlinePlayers().remove(playerInfo);
            }
        }

        for (Player player : plugin.getServer().getAllPlayers()) {
            if (!trackedUUIDS.contains(player.getUniqueId())) {
                this.getLocalServerInfo().getOnlinePlayers()
                        .add(new PlayerInfo(player.getUniqueId(), player.getUsername(), System.currentTimeMillis()));
            }
        }

        for (PlayerInfo playerInfo : this.getLocalServerInfo().getOnlinePlayers()) {
            Optional<Player> playerOptional = plugin.getServer().getPlayer(playerInfo.getUuid());
            if (playerOptional.isPresent()) {
                playerInfo.setPing(playerOptional.get().getPing());
            }
        }

        return true;
    }
}
