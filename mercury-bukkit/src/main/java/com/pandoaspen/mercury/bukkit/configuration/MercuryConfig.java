package com.pandoaspen.mercury.bukkit.configuration;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class MercuryConfig {

    @SerializedName("groups") private Map<String, GroupsConf> groups;
    @SerializedName("auto-restart") private AutoRestartConf autoRestart;
    @SerializedName("dispatch") private DispatchConf dispatch;
    @SerializedName("scheduled-messages") private ScheduledMessagesConf scheduledMessagesConfig;


    @Getter
    public static class GroupsConf {
        @SerializedName("aliases") private Set<String> aliases;
        @SerializedName("servers") private Set<String> servers;
    }

    @Getter
    public static class AutoRestartConf {

        @SerializedName("scheduler") private SchedulerConf scheduler;
        @SerializedName("reminder") private ReminderConf reminder;
        @SerializedName("redirectall") private RedirectConf redirect;
        @SerializedName("redirect-back") private boolean redirectBack;
        @SerializedName("commands") private CommandsConf commands;

        @Getter
        public static class SchedulerConf {
            @SerializedName("enabled") private boolean enabled;
            @SerializedName("interval") private String interval;
            @SerializedName("randomize") private String randomize;
        }

        @Getter
        public static class ReminderConf {
            @SerializedName("chat") private boolean chat;
            @SerializedName("title") private boolean title;
            @SerializedName("minutes") private Set<Integer> minutes;
            @SerializedName("seconds") private int seconds;
        }

        @Getter
        public static class RedirectConf {
            @SerializedName("group") private String group;
            @SerializedName("timeout") private String timeout;
        }

        @Getter
        public static class CommandsConf {
            @SerializedName("time") private String time;
            @SerializedName("commands") private List<String> commands;
        }
    }

    @Getter
    public static class DispatchConf {
        @SerializedName("enabled") private boolean enabled;
        @SerializedName("players") private Set<String> players;
        @SerializedName("blacklist") private Set<String> blacklist;
    }


    @Getter
    public static class ScheduledMessagesConf {

        @SerializedName("enabled") private boolean enabled;
        @SerializedName("scheduler") private SchedulerConf schedulerConfig;
        @SerializedName("messages") private List<MessageConf> messages;

        @Getter
        public static class SchedulerConf {
            @SerializedName("delay") private String delay;
            @SerializedName("messages") private int chatMessages;
            @SerializedName("messages-timeout") private String chatMessagesTimeout;
        }

        @Getter
        public static class MessageConf {
            @SerializedName("messages") private List<String> messages;
            @SerializedName("group") private String group;
        }
    }
}