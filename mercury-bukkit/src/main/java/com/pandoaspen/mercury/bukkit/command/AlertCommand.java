package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.services.alert.AlertService;
import com.pandoaspen.mercury.bukkit.services.alert.model.AlertMessage;
import com.pandoaspen.mercury.bukkit.services.alert.model.AlertType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


@CommandAlias("sendalert")
@CommandPermission("mercury.alert")
public class AlertCommand extends AbstractMercuryCommand {

    private final AlertService alertService;

    public AlertCommand(MercuryBukkitPlugin plugin) {
        super(plugin);
        this.alertService = getSubService(AlertService.class);
    }

    @Subcommand("chat")
    @CommandAlias("alert")
    public void cmdAlertChat(CommandSender sender, String groups, String message) {
        String senderName = sender.getName();
        UUID senderUUID = getSenderUUID(sender);
        AlertType alertType = AlertType.CHAT;

        String[] groupArr = groups.split(",");

        AlertMessage alertMessage = new AlertMessage(senderName, senderUUID, alertType, message, null, groupArr);

        alertService.sendAlert(alertMessage);
    }

    @Subcommand("title")
    @CommandAlias("talert")
    public void cmdAlertTitle(CommandSender sender, String groups, String message) {
        String senderName = sender.getName();
        UUID senderUUID = getSenderUUID(sender);
        AlertType alertType = AlertType.TITLE;

        String[] groupArr = groups.split(",");

        String subtitle = "";

        if (message.contains("|")) {
            subtitle = message.split("\\|")[1];
            message = message.split("\\|")[0];
        }

        message = getLangConfig().getAlertLang().getAlert().replaceAll("%message%", message);
        subtitle = getLangConfig().getAlertLang().getAlert().replaceAll("%message%", subtitle);

        AlertMessage alertMessage = new AlertMessage(senderName, senderUUID, alertType, message, subtitle, groupArr);

        alertService.sendAlert(alertMessage);
    }

    private UUID getSenderUUID(CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getUniqueId();
        } else {
            return new UUID(0, 0);
        }
    }
}
