package com.pandoaspen.mercury.bukkit.services.alert;

import com.hazelcast.config.SerializerConfig;
import com.hazelcast.topic.ITopic;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.services.alert.model.AlertMessage;
import com.pandoaspen.mercury.bukkit.services.alert.model.AlertMessageSerializer;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.MercurySubService;
import com.pandoaspen.mercury.common.service.healthcheck.AbstractHealthCheckService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class AlertService extends MercurySubService {

    private final MercuryBukkitPlugin plugin;
    private final AbstractHealthCheckService healthCheckService;

    private ITopic<AlertMessage> alertTopic;

    public AlertService(IMercuryService mercuryService, MercuryBukkitPlugin plugin) {
        super(mercuryService);
        this.plugin = plugin;
        this.healthCheckService = mercuryService.getSubService(AbstractHealthCheckService.class);
    }

    @Override
    public String getName() {
        return "AlertService";
    }

    @Override
    public void startup() {
        this.alertTopic = getMercuryService().getHazelcast().getTopic("alertservice");
        this.alertTopic.addMessageListener(msg -> onAlertReceived(msg.getMessageObject()));
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<SerializerConfig> getSerializerConfigs() {
        return Arrays.asList(new SerializerConfig().setTypeClass(AlertMessage.class).setImplementation(new AlertMessageSerializer()));
    }


    public void sendAlert(AlertMessage alertMessage) {
        alertTopic.publishAsync(alertMessage);
    }

    private void onAlertReceived(AlertMessage alertMessage) {
        if (!canReceive(alertMessage)) return;

        switch (alertMessage.getAlertType()) {
            case CHAT:
                plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', alertMessage.getTitle()));
                break;
            case TITLE:
                for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                    onlinePlayer.sendTitle(ChatColor.translateAlternateColorCodes('&', alertMessage.getTitle()), ChatColor.translateAlternateColorCodes('&', alertMessage.getSubTitle()));
                }
                break;
        }
    }

    private boolean canReceive(AlertMessage alertMessage) {
        if (alertMessage.getGroups() == null) return true;
        if (alertMessage.getGroups().length == 0) return true;

        for (String group : alertMessage.getGroups()) {
            if (healthCheckService.isLocalServerInGroup(group)) {
                return true;
            }
        }

        return false;
    }
}
