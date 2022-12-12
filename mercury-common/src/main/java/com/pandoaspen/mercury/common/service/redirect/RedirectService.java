package com.pandoaspen.mercury.common.service.redirect;

import com.hazelcast.config.SerializerConfig;
import com.hazelcast.topic.ITopic;
import com.pandoaspen.mercury.common.serializer.HzGsonSerializer;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.MercurySubService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import com.pandoaspen.mercury.common.service.healthcheck.PlayerQuery;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerInfo;
import com.pandoaspen.mercury.common.service.healthcheck.model.ServerType;
import com.pandoaspen.mercury.common.service.redirect.model.RedirectMessage;
import com.pandoaspen.mercury.common.service.redirect.model.RedirectReplyMessage;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RedirectService extends MercurySubService {

    private final IRedirectHandler redirectHandler;

    private final AbstractHealthCheckService healthCheckService;

    private final Map<UUID, CompletableFuture<RedirectReplyMessage>> replyMap;

    private ITopic<RedirectMessage> redirectTopic;
    private ITopic<RedirectReplyMessage> replyTopic;

    public RedirectService(IMercuryService mercuryService, IRedirectHandler redirectHandler) {
        super(mercuryService);
        this.replyMap = new HashMap<>();
        this.redirectHandler = redirectHandler;
        this.healthCheckService = mercuryService.getSubService(AbstractHealthCheckService.class);
    }

    @Override
    public void startup() {
        this.redirectTopic = getHazelcast().getTopic("redirecttopic");
        this.replyTopic = getHazelcast().getTopic("redirectreplytopic");

        if (this.redirectHandler != null) {
            this.redirectTopic.addMessageListener(msg -> onRedirectMessage(msg.getMessageObject()));
        }

        this.replyTopic.addMessageListener(msg -> onRedirectReply(msg.getMessageObject()));
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<SerializerConfig> getSerializerConfigs() {
        return Arrays.asList(
                new SerializerConfig().setTypeClass(RedirectMessage.class).setImplementation(new HzGsonSerializer<>(7, RedirectMessage.class)),
                new SerializerConfig().setTypeClass(RedirectReplyMessage.class).setImplementation(new HzGsonSerializer<>(8, RedirectReplyMessage.class)));
    }

    @Override
    public String getName() {
        return "RedirectService";
    }

    public CompletableFuture<RedirectReplyMessage> redirect(UUID commandSender, String destinationGroup, UUID... uuidsToSend) {
        UUID requestUUID = UUID.randomUUID();

        RedirectMessage redirectMessage = new RedirectMessage(requestUUID);
        redirectMessage.setSenderUUID(commandSender);
        redirectMessage.setDestinationGroup(destinationGroup);
        redirectMessage.setSendingUUIDs(uuidsToSend);
        redirectTopic.publishAsync(redirectMessage);

        CompletableFuture<RedirectReplyMessage> future = new CompletableFuture<>();
        replyMap.put(requestUUID, future);
        return future;
    }

    private void onRedirectMessage(RedirectMessage redirectMessage) {
        UUID[] playerUUIDS = Arrays.asList(redirectMessage.getSendingUUIDs()).stream().filter(redirectHandler::isConnected).toArray(UUID[]::new);
        Map<UUID, PlayerQuery> playerServers = new HashMap<>();
        for (UUID uuid : playerUUIDS) {
            playerServers.put(uuid, healthCheckService.findPlayerInfo(uuid));
        }

        String[] servers = healthCheckService.getServersInGroup(redirectMessage.getDestinationGroup()).stream().map(ServerInfo::getServerId).toArray(String[]::new);

        int serversSize = servers.length;
        int playersSize = playerUUIDS.length;

        System.out.println("got redirect message");
        System.out.println(redirectMessage.getDestinationGroup());
        System.out.println(Arrays.asList(servers));

        int serverIdx = 0;

        for (int i = 0; i < playersSize; i++) {
            UUID uuid = playerUUIDS[i];

            PlayerQuery playerQuery = playerServers.get(uuid);
            if (playerQuery == null) {
                System.out.println("failed to redirect, player not found");
                continue;
            }

            String server = null;
            for (int attempt = 0; attempt < serversSize; attempt++) {
                String attemptedServer = servers[serverIdx++ % serversSize];
                if (playerQuery.getServerInfo().getServerId().equalsIgnoreCase(attemptedServer)) {
                    System.out.println("failed redirect " + playerQuery.getPlayerInfo().getName() + " too " + attemptedServer);
                    continue;
                } else {
                    System.out.println("player is not on " + attemptedServer +". Redirecting. Current server: " + playerQuery.getServerInfo().getServerId());
                }
                server = attemptedServer;
                break;
            }

            if (server == null) {
                System.out.println("failed to redirect, no servers");
                continue;
            }

            String finalServer = server;
            redirectHandler.redirect(uuid, server).thenAccept((status) -> {
                replyTopic.publishAsync(new RedirectReplyMessage(redirectMessage.getRequestUUID(), redirectMessage.getSenderUUID(), uuid, finalServer, status));
            });
        }

    }

    private void onRedirectReply(RedirectReplyMessage redirectReply) {
        if (!replyMap.containsKey(redirectReply.getRequestUUID())) {
            return;
        }
        ServerInfo localServer = healthCheckService.getLocalServerInfo();
        if (localServer.getServerType() != ServerType.BUKKIT) return;
        boolean isSenderOnline = localServer.getOnlinePlayers().stream().anyMatch(info -> info.getUuid().equals(redirectReply.getSenderUUID()));
        if (!isSenderOnline) return;

        CompletableFuture<RedirectReplyMessage> future = replyMap.get(redirectReply.getRequestUUID());
        future.complete(redirectReply);
        replyMap.remove(redirectReply.getRequestUUID());
    }
}