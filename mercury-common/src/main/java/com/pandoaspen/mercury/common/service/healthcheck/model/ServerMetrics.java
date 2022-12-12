package com.pandoaspen.mercury.common.service.healthcheck.model;

import lombok.Data;

@Data
public class ServerMetrics {


    private OSMetrics systemMetrics = new OSMetrics();

    private MemoryMetrics onHeapMemoryMetrics = new MemoryMetrics();
    private MemoryMetrics offHeapMemoryMetrics = new MemoryMetrics();

    @Data
    public static class OSMetrics {
        private long committedVirtualMemory;
        private long freePhysicalMemory;
        private long freeSwapSpaceSize;
        private double processCpuLoad;
        private long processCpuTime;
        private double systemCpuLoad;
        private long totalPhysicalMemorySize;
        private long totalSwapSpaceSize;
    }

    @Data
    public static class MemoryMetrics {
        private long committed;
        private long initial;
        private long max;
        private long used;
    }
}
