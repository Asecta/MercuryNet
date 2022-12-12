package com.pandoaspen.mercury.common.utils;

import java.util.Collections;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class TPSUtil implements Runnable {

    private long lastTick;
    private Deque<Long> tickIntervals;
    int resolution = 40;

    public TPSUtil() {
        lastTick = System.currentTimeMillis();
        tickIntervals = new ConcurrentLinkedDeque<>(Collections.nCopies(resolution, 50L));
    }

    @Override
    public void run() {
        long curr = System.currentTimeMillis();
        long delta = curr - lastTick;
        lastTick = curr;
        tickIntervals.removeFirst();
        tickIntervals.addLast(delta);
    }

    public double getTPS() {
        int base = 0;
        for (long delta : tickIntervals) {
            base += delta;
        }
        return 1000D / ((double) base / resolution);
    }
}