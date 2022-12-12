package com.pandoaspen.mercury.bukkit.configuration;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public final class LangConfig {

    @SerializedName("find") private FindLang findLang;
    @SerializedName("alert") private AlertLang alertLang;
    @SerializedName("list") private ListLang listLang;
    @SerializedName("join") private JoinLang joinLang;
    @SerializedName("restart") private RestartLang restartLang;
    @SerializedName("whereami") private WhereAmILang whereAmILang;
    @SerializedName("dispatch") private DispatchLang dispatchLang;
    @SerializedName("ping") private PingLang pingLang;


    @Getter
    public static final class FindLang {
        @SerializedName("online") private String online;
        @SerializedName("offline ") private String offline;
        @SerializedName("not-found") private String notFound;
        @SerializedName("denied") private String denied;
        @SerializedName("self") private String self;
        @SerializedName("usage") private String usage;
    }

    @Getter
    public static final class AlertLang {
        @SerializedName("alert") private String alert;
    }

    @Getter
    public static final class ListLang {
        @SerializedName("total") private String total;
        @SerializedName("server") private String server;
        @SerializedName("staff") private String staff;
        @SerializedName("staffmember") private String staffmember;
        @SerializedName("nostaff") private String nostaff;
        @SerializedName("online") private String online;
    }

    @Getter
    public static final class JoinLang {
        @SerializedName("lookup") private String lookup;
        @SerializedName("join") private String join;
        @SerializedName("self") private String self;
        @SerializedName("denied") private String denied;
        @SerializedName("not-found") private String notFound;
        @SerializedName("errors") private ErrorLang errorLang;

        @Getter
        public static final class ErrorLang {
            @SerializedName("error") private String errorError;
            @SerializedName("offline") private String errorOffline;
            @SerializedName("whitelist") private String errorWhitelist;
            @SerializedName("restarting") private String errorRestarting;
            @SerializedName("same-server") private String errorSameServer;
            @SerializedName("unknown") private String errorUnknown;
        }
    }


    @Getter
    public static final class RestartLang {
        @SerializedName("rescheduled") private String rescheduled;
        @SerializedName("time") private String time;
        @SerializedName("notify") private NotifyLang notify;
        @SerializedName("countdown") private CountdownLang countdown;

        @Getter
        public static final class NotifyLang {
            @SerializedName("chat") private String chat;
            @SerializedName("title") private String title;
            @SerializedName("sub-title") private String subTitle;
        }

        @Getter
        public static final class CountdownLang {
            @SerializedName("chat") private String chat;
            @SerializedName("title") private String title;
            @SerializedName("sub-title") private String subTitle;
        }
    }

    @Getter
    public static final class WhereAmILang {
        @SerializedName("whereami") private String whereami;
    }

    @Getter
    public static final class DispatchLang {
        @SerializedName("dispatch") private String dispatch;
    }

    @Getter
    public static final class PingLang {
        @SerializedName("ping") private String ping;
    }
}
