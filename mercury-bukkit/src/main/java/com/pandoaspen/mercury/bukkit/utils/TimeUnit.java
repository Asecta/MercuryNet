package com.pandoaspen.mercury.bukkit.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TimeUnit {
    MS("millisecond", 1L, "ms"), S("second", 1000L, "s"), MIN("minute", 60000L, "m"), HOUR("hour", 3600000L, "h"), DAY("day", 86400000L, "d"), WEEK("week", 604800000L, "w"), YEAR("year", 31536000000L, "y");

    private final String name;
    private final long ms;
    private final String unit;
    private static final Map<String, Long> convertion;
    private static final TimeUnit[] order;
    private static final Pattern isLong;
    private static final Pattern isTimePattern;
    private static final Pattern splitTime;

    TimeUnit(String name, long ms, String unit) {
        this.name = name;
        this.ms = ms;
        this.unit = unit;
    }

    public String toString(int x) {
        return this.toString(x, false);
    }

    public String toString(int x, boolean small) {
        String r = this.name;
        if (small) {
            r = this.unit;
        }
        if (!small && x > 1) {
            r = r + "s";
        }
        return x + (small ? "" : " ") + r;
    }

    private static long convert(String c) {
        if (!convertion.containsKey(c)) {
            return 0L;
        }
        return convertion.get(c);
    }

    public static String longToString(long time) {
        return TimeUnit.longToString(time, false);
    }

    public static String longToString(long time, boolean small) {
        StringBuilder sb = new StringBuilder();
        int units = 0;
        for (TimeUnit unit : order) {
            if (time <= unit.ms || units >= 3) continue;
            int t = (int) Math.floor(time / unit.ms);
            sb.append(unit.toString(t, small)).append(" ");
            time -= (long) t * unit.ms;
            ++units;
        }
        return sb.toString().trim();
    }

    public static String toString(long time, boolean small) {
        final StringBuilder sb = new StringBuilder();
        int units = 0;
        for (final TimeUnit unit : TimeUnit.order) {
            if (time > unit.ms && units < 3) {
                final int t = (int) Math.floor(time / unit.ms);
                sb.append(unit.toString(t, small)).append(" ");
                time -= t * unit.ms;
                units++;
            }
        }
        return sb.toString().trim();
    }

    public static String ticksToString(long ticks) {
        return TimeUnit.ticksToString(ticks, false);
    }

    public static String ticksToString(long ticks, boolean small) {
        return TimeUnit.longToString(ticks * 50L, small);
    }

    public static long toLong(String s) {
        if (s == null) {
            return 0L;
        }
        long cooldown = 0L;
        for (String t : TimeUnit.prepare(s)) {
            Matcher matcher = splitTime.matcher(t);
            if (!matcher.matches()) continue;
            cooldown += Long.parseLong(matcher.group(2)) * TimeUnit.convert(matcher.group(3));
        }
        return cooldown;
    }

    public static long toTicks(String s) {
        return TimeUnit.toLong(s) / 50L;
    }

    private static String[] prepare(String s) {
        return splitTime.matcher(s).replaceAll("$1 ").trim().split(" ");
    }

    public static boolean isTimeString(String s) {
        return isTimePattern.matcher(s).matches();
    }

    static {
        convertion = new HashMap<String, Long>();
        order = new TimeUnit[]{YEAR, WEEK, DAY, HOUR, MIN, S, MS};
        isLong = Pattern.compile("[0-9]+");
        isTimePattern = Pattern.compile("(([1-9][0-9]*)(ms|[smhdwy]))+");
        splitTime = Pattern.compile("(([1-9][0-9]*)(ms|[smhdwy]))");
        for (TimeUnit unit : TimeUnit.values()) {
            convertion.put(unit.unit, unit.ms);
        }
    }
}

