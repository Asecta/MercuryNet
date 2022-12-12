package com.pandoaspen.mercury.bukkit.services.staffchat;

import com.hazelcast.config.SerializerConfig;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.services.staffchat.model.StaffChatMessage;
import com.pandoaspen.mercury.bukkit.services.staffchat.model.StaffChatMessageSerializer;
import com.pandoaspen.mercury.common.service.IMercuryService;
import com.pandoaspen.mercury.common.service.MercurySubService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StaffChatService extends MercurySubService {

    private static final UUID BLANK_UUID = new UUID(0, 0);

    private MercuryBukkitPlugin plugin;

    private ITopic<StaffChatMessage> topic;

    public StaffChatService(IMercuryService mercuryService, MercuryBukkitPlugin plugin) {
        super(mercuryService);
        this.plugin = plugin;
    }

    @Override
    public void startup() {
        this.topic = getHazelcast().getTopic("staffchat");
        this.topic.addMessageListener(this::onMessageReceived);
    }

    private void onMessageReceived(Message<StaffChatMessage> wrappedMessage) {
        StaffChatMessage object = wrappedMessage.getMessageObject();

        // Parse message
        String message = "&e(%s) &c%s: &f%s";
        message = String.format(message, object.getServerId(), object.getPlayerName(), object.getMessage());
        message = ChatColor.translateAlternateColorCodes('&', message);

        plugin.getServer().getConsoleSender().sendMessage(message);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.isOp() || player.hasPermission("staffchat.use")) {
                player.sendMessage(message);
            }
        }
    }

    public void sendStaffMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sendStaffMessage(new StaffChatMessage(getServerId(), ((Player) sender).getUniqueId(), sender.getName(), message));
        } else {
            sendStaffMessage(new StaffChatMessage(getServerId(), BLANK_UUID, "CONSOLE", message));
        }
    }

    public void sendStaffMessage(StaffChatMessage message) {
        this.topic.publishAsync(message);
    }

    @Override
    public void shutdown() {
    }

    @Override
    public List<SerializerConfig> getSerializerConfigs() {
        return Arrays.asList(new SerializerConfig().setTypeClass(StaffChatMessage.class).setImplementation(new StaffChatMessageSerializer()));
    }

    @Override
    public String getName() {
        return "Staffchat";
    }
}
