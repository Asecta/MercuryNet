package com.pandoaspen.mercury.bukkit.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public class MessageUtils {

    public static void broadcastTitle(String title, String subTitle) {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(title, subTitle));
    }

    public static void broadcast(String msg, String... args) {
        broadcast(parseMessage(msg, args));
    }

    public static void broadcast(BaseComponent[] message) {
        Bukkit.spigot().broadcast(message);
    }

    public static void sendMessage(CommandSender receiver, String msg, String... args) {
        sendMessage(receiver, parseMessage(msg, args));
    }

    public static void sendMessage(CommandSender receiver, BaseComponent[] message) {
        if (receiver instanceof Player) {
            ((Player) receiver).spigot().sendMessage(message);
        } else {
            receiver.sendMessage(BaseComponent.toLegacyText(message));
        }
    }


    public static void sendMessage(CommandSender receiver, Collection<BaseComponent[]> message) {
        sendMessage(receiver, flattenMessage(message));
    }

    public static BaseComponent[] parseMessage(String msg, String... args) {
        if (msg.startsWith("[JSON] ")) {
            msg = msg.substring(7);
            msg = combine(msg, args);
            return ComponentSerializer.parse(msg);
        }
        msg = colorize(msg);
        msg = combine(msg, args);
        return TextComponent.fromLegacyText(msg);
    }


    public static String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static BaseComponent[] parseMessage(String msg, Object... args) {
        return parseMessage(msg, Arrays.stream(args).map(Object::toString).toArray(String[]::new));
    }

    public static String parseMessageToString(String msg, Object... args) {
        BaseComponent[] baseComponents = parseMessage(msg, Arrays.stream(args).map(Object::toString).toArray(String[]::new));
        return TextComponent.toLegacyText(baseComponents);
    }

    public static String combine(String msg, String... args) {
        for (int i = 0; i < args.length - 1; i += 2) {
            msg = msg.replaceAll("%" + args[i] + "%", args[i + 1]);
        }
        return msg;
    }

    public static BaseComponent[] flattenMessage(Collection<BaseComponent[]> collection) {
        return collection.stream().flatMap(bc -> Arrays.stream(bc)).toArray(BaseComponent[]::new);
    }


}
