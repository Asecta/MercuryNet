package com.pandoaspen.mercury.bukkit.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.pandoaspen.mercury.bukkit.MercuryBukkitPlugin;
import com.pandoaspen.mercury.bukkit.services.staffchat.StaffChatService;
import org.bukkit.command.CommandSender;

@CommandAlias("staffchat|sc")
public class StaffChatCommand extends AbstractMercuryCommand {

    private final StaffChatService staffChatService;

    public StaffChatCommand(MercuryBukkitPlugin plugin) {
        super(plugin);
        staffChatService = getSubService(StaffChatService.class);
    }

    @Default
    @CommandPermission("mercury.staff")
    public void defaultCommand(CommandSender sender, String message) {
        staffChatService.sendStaffMessage(sender, message);
    }
}
