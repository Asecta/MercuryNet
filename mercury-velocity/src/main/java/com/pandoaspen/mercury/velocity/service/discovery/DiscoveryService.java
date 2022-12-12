package com.pandoaspen.mercury.velocity.service.discovery;

import com.hazelcast.config.SerializerConfig;
import com.pandoaspen.mercury.common.scheduler.IMercuryTask;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.MercurySubService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerInfo;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerStatus;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerType;
import com.pandoaspen.mercury.velocity.MercuryPlugin;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscoveryService extends MercurySubService {

    private final MercuryPlugin plugin;
    private final AbstractHealthCheckService healthCheckService;

    private IMercuryTask discoveryTask;

    public DiscoveryService(IMercuryService mercuryService, MercuryPlugin plugin) {
        super(mercuryService);
        this.plugin = plugin;
        this.healthCheckService = mercuryService.getSubService(AbstractHealthCheckService.class);
    }

    @Override
    public void startup() {
        discoveryTask = getSchedular().runTaskTimer(this::tickDiscovery, 20, 20);
    }

    @Override
    public void shutdown() {
        this.discoveryTask.cancel();
    }

    @Override
    public List<SerializerConfig> getSerializerConfigs() {
        return null;
    }

    @Override
    public String getName() {
        return "DiscoveryService";
    }

    private boolean isOnline(Map<String, ServerInfo> trackedServers, RegisteredServer registeredServer) {
        String name = registeredServer.getServerInfo().getName();

        if (!trackedServers.containsKey(name)) return false;

        ServerInfo trackedServer = trackedServers.get(name);
        if (trackedServer.getServerStatus() == ServerStatus.STOPPED) return false;

        InetSocketAddress registeredAddr = registeredServer.getServerInfo().getAddress();
        InetSocketAddress trackedAddr = getServerAddress(trackedServer);
        if (!registeredAddr.equals(trackedAddr)) return false;

        return true;
    }

    private void tickDiscovery() {
        Collection<RegisteredServer> registeredServers = plugin.getServer().getAllServers();
        Map<String, ServerInfo> trackedServers = healthCheckService.getServerInfoMap();

        // Remove servers no longer running
        for (RegisteredServer registeredServer : registeredServers) {
            if (isOnline(trackedServers, registeredServer)) continue;
            plugin.getServer().unregisterServer(registeredServer.getServerInfo());
            getLogger().info("Unregistering server: " + registeredServer.getServerInfo().getName() + " @ " +
                    registeredServer.getServerInfo().getAddress());
        }

        Set<String> registeredNames = plugin.getServer().getAllServers().stream().map(s -> s.getServerInfo().getName())
                .collect(Collectors.toSet());

        for (ServerInfo serverInfo : trackedServers.values()) {
            if (serverInfo.getServerStatus() == ServerStatus.STOPPED) continue;
            if (serverInfo.getServerType() != ServerType.BUKKIT) continue;

            getLogger().fine("Checking discovery status of " + serverInfo.getServerId());

            if (registeredNames.contains(serverInfo.getServerId())) continue;

            getLogger().fine("Parsing server info for " + serverInfo.getServerId());

            getLogger().info(serverInfo.toString());

            InetSocketAddress inetSocketAddress = getServerAddress(serverInfo);
            if (inetSocketAddress == null) {
                getLogger().severe("Discovery error - Couldn't parse server info for: " + serverInfo.getAddress());
                continue;
            }

            getLogger().info("Registering server: " + serverInfo.getServerId() + " @ " + inetSocketAddress);
            plugin.getServer().registerServer(
                    new com.velocitypowered.api.proxy.server.ServerInfo(serverInfo.getServerId(), inetSocketAddress));
        }
    }

    public InetSocketAddress getServerAddress(ServerInfo serverInfo) {
        if (serverInfo == null || serverInfo.getAddress() == null) {
            getLogger().severe("Discovery Error - Invalid Server Record");
            return null;
        }

        if (!serverInfo.getAddress().contains(":")) {
            getLogger().severe("Discovery error - Invalid server address: " + serverInfo.getAddress());
            getLogger().severe("Discovery error - Server Record: " + serverInfo);
            return null;
        }

        String address = serverInfo.getAddress();
        int idx = address.lastIndexOf(":");
        String ip = address.substring(0, idx);
        int port = Integer.parseInt(address.substring(idx + 1));

        return new InetSocketAddress(ip, port);
    }

}
