package com.pandoaspen.mercury.common.utils;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Duration {

    private static final String regex = "((?<units>[0-9]+)(?<token>[wdhmst]))\\s*";
    private static final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private static final char YEARS = 'Y';
    private static final char WEEKS = 'W';
    private static final char DAYS = 'D';
    private static final char HOURS = 'H';
    private static final char MINUTES = 'M';
    private static final char SECONDS = 'S';
    private static final char TICKS = 'T';

    private static long getTokenMillis(String token) {
        if (token == null || token.length() <= 0) return 0;

        switch (Character.toUpperCase(token.charAt(0))) {
            case YEARS: return 31536000000L;
            case WEEKS: return 604800000L;
            case DAYS: return 86400000L;
            case HOURS: return 3600000L;
            case MINUTES: return 60000L;
            case SECONDS: return 1000L;
            case TICKS: return 50L;
            default: return 0L;
        }
    }

    public static long parseDuration(String timeString) {
        final Matcher matcher = pattern.matcher(timeString);

        long value = 0;

        while (matcher.find()) {
            String units = matcher.group("units");
            String token = matcher.group("token");

            long unitInt = Integer.parseInt(units);
            long tokenMillis = getTokenMillis(token);
            value += unitInt * tokenMillis;
        }

        return value;
    }

    private final String durationString;
    private long millis;

    public Duration(String durationString) {
        this.durationString = durationString;
        this.millis = parseDuration(durationString);
    }
}
