package com.pandoaspen.mercury.bukkit.services;

import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.scheduler.BukkitMercuryScheduler;
import com.pandoaspen.mercury.bukkit.utils.NettyUtils;
import com.pandoaspen.mercury.common.scheduler.IMercuryScheduler;
import com.pandoaspen.mercury.common.service.AbstractMercuryService;
import lombok.Getter;

import java.io.File;
import java.net.InetSocketAddress;

public class BukkitMercuryService extends AbstractMercuryService {

    private final MercuryBukkitPlugin plugin;

    @Getter private IMercuryScheduler scheduler;

    public BukkitMercuryService(String serverId, MercuryBukkitPlugin plugin, File distributionConfigFile) {
        super(serverId, plugin.getLogger(), distributionConfigFile);
        this.plugin = plugin;
        this.scheduler = new BukkitMercuryScheduler(plugin, plugin.getServer().getScheduler());
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getAddress() {
        try {
            InetSocketAddress socketAddress = NettyUtils.findMinecraftPort(plugin.getServer());
            String address = socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();

            System.out.println(address);

            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }
}
