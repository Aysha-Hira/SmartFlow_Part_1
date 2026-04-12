/**
 * Section: 104

 * Group number: 4
 * Student IDs and names: 
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */


package com.smartflow;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LatencyTracker - Tracks event delivery latency for SmartFlow subscribers
 * Optimized for simulation with high-frequency events
 */
public class LatencyTracker {
    
    // Statistics counters (using primitives for better performance)
    private final AtomicLong totalEventsReceived = new AtomicLong(0);
    private final AtomicLong totalLatencySumNanos = new AtomicLong(0);
    private long minLatencyNanos = Long.MAX_VALUE;
    private long maxLatencyNanos = 0;
    
    // Per-topic statistics
    private final Map<String, TopicStats> topicStats = new ConcurrentHashMap<>();
    
    // For percentile calculations (keep last N measurements)
    private final List<Long> latencyBuffer = new CopyOnWriteArrayList<>();
    private final int maxBufferSize;
    
    // Constants
    private static final long NANOS_TO_MICROS = 1000;
    
    // Internal class for per-topic stats
    private static class TopicStats {
        AtomicLong count = new AtomicLong(0);
        AtomicLong totalLatency = new AtomicLong(0);
        
        void addLatency(long latencyNanos) {
            count.incrementAndGet();
            totalLatency.addAndGet(latencyNanos);
        }
        
        double getAverageMicros() {
            long c = count.get();
            return c == 0 ? 0 : (totalLatency.get() / (double) c) / NANOS_TO_MICROS;
        }
        
        long getCount() {
            return count.get();
        }
    }
    
    public LatencyTracker() {
        this(10000); // Keep last 10,000 measurements
    }
    
    public LatencyTracker(int bufferSize) {
        this.maxBufferSize = bufferSize;
    }
    
    /**
     * Record latency in nanoseconds
     */
    public void recordLatency(String topic, long latencyNanos) {
        // Update global stats
        totalEventsReceived.incrementAndGet();
        totalLatencySumNanos.addAndGet(latencyNanos);
        
        // Update min/max (synchronized for thread safety)
        synchronized (this) {
            if (latencyNanos < minLatencyNanos) minLatencyNanos = latencyNanos;
            if (latencyNanos > maxLatencyNanos) maxLatencyNanos = latencyNanos;
        }
        
        // Update per-topic stats
        topicStats.computeIfAbsent(topic, k -> new TopicStats()).addLatency(latencyNanos);
        
        // Update buffer for percentiles
        synchronized (latencyBuffer) {
            latencyBuffer.add(latencyNanos);
            while (latencyBuffer.size() > maxBufferSize) {
                latencyBuffer.remove(0);
            }
        }
    }
    
    /**
     * Record latency using publish and receive times (both in milliseconds)
     */
    public void recordLatency(String topic, long publishTimeMs, long receiveTimeMs) {
        long latencyNanos = (receiveTimeMs - publishTimeMs) * 1_000_000;
        recordLatency(topic, latencyNanos);
    }
    
    /**
     * Get average latency in microseconds
     */
    public double getAverageLatencyMicros() {
        long total = totalEventsReceived.get();
        return total == 0 ? 0 : (totalLatencySumNanos.get() / (double) total) / NANOS_TO_MICROS;
    }
    
    /**
     * Get average latency in milliseconds
     */
    public double getAverageLatencyMillis() {
        return getAverageLatencyMicros() / 1000;
    }
    
    /**
     * Get average latency for a specific topic (in microseconds)
     */
    public double getAverageLatencyForTopicMicros(String topic) {
        TopicStats stats = topicStats.get(topic);
        return stats == null ? 0 : stats.getAverageMicros();
    }
    
    /**
     * Get min latency in microseconds
     */
    public long getMinLatencyMicros() {
        synchronized (this) {
            return minLatencyNanos == Long.MAX_VALUE ? 0 : minLatencyNanos / NANOS_TO_MICROS;
        }
    }
    
    /**
     * Get max latency in microseconds
     */
    public long getMaxLatencyMicros() {
        synchronized (this) {
            return maxLatencyNanos / NANOS_TO_MICROS;
        }
    }
    
    /**
     * Get total events received
     */
    public long getTotalEventsReceived() {
        return totalEventsReceived.get();
    }
    
    /**
     * Calculate percentile latency (in microseconds)
     */
    public long getPercentileLatencyMicros(double percentile) {
        synchronized (latencyBuffer) {
            if (latencyBuffer.isEmpty()) return 0;
            
            List<Long> sorted = new ArrayList<>(latencyBuffer);
            Collections.sort(sorted);
            
            int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
            index = Math.max(0, Math.min(index, sorted.size() - 1));
            
            return sorted.get(index) / NANOS_TO_MICROS;
        }
    }
    
    /**
     * Get all topics
     */
    public Set<String> getTopics() {
        return topicStats.keySet();
    }
    
    /**
     * Get event count for a topic
     */
    public long getEventCountForTopic(String topic) {
        TopicStats stats = topicStats.get(topic);
        return stats == null ? 0 : stats.getCount();
    }
    
    /**
     * Get complete statistics report
     */
    public String getStatsReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(repeat("=", 70)).append("\n");
        sb.append("LATENCY STATISTICS REPORT\n");
        sb.append(repeat("=", 70)).append("\n");
        sb.append(String.format("Total Events Received: %,d\n", totalEventsReceived.get()));
        sb.append(String.format("Average Latency:        %,10.2f μs (%,8.2f ms)\n", 
            getAverageLatencyMicros(), getAverageLatencyMillis()));
        sb.append(String.format("Min Latency:            %,10d μs\n", getMinLatencyMicros()));
        sb.append(String.format("Max Latency:            %,10d μs\n", getMaxLatencyMicros()));
        sb.append(String.format("p50 Latency:            %,10d μs\n", getPercentileLatencyMicros(50)));
        sb.append(String.format("p95 Latency:            %,10d μs\n", getPercentileLatencyMicros(95)));
        sb.append(String.format("p99 Latency:            %,10d μs\n", getPercentileLatencyMicros(99)));
        sb.append(String.format("p99.9 Latency:          %,10d μs\n", getPercentileLatencyMicros(99.9)));
        sb.append(repeat("-", 70)).append("\n");
        sb.append("PER-TOPIC STATISTICS:\n");
        
        for (String topic : getTopics()) {
            sb.append(String.format("  %-30s: count=%,-8d avg=%,10.2f μs\n", 
                topic, getEventCountForTopic(topic), getAverageLatencyForTopicMicros(topic)));
        }
        
        sb.append(repeat("=", 70)).append("\n");
        return sb.toString();
    }
    
    /**
     * Get CSV format for easy analysis
     */
    public String getCSVReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Metric,Value\n");
        sb.append(String.format("Total Events,%,d\n", totalEventsReceived.get()));
        sb.append(String.format("Average Latency (μs),%.2f\n", getAverageLatencyMicros()));
        sb.append(String.format("Min Latency (μs),%d\n", getMinLatencyMicros()));
        sb.append(String.format("Max Latency (μs),%d\n", getMaxLatencyMicros()));
        sb.append(String.format("p50 Latency (μs),%d\n", getPercentileLatencyMicros(50)));
        sb.append(String.format("p95 Latency (μs),%d\n", getPercentileLatencyMicros(95)));
        sb.append(String.format("p99 Latency (μs),%d\n", getPercentileLatencyMicros(99)));
        
        for (String topic : getTopics()) {
            sb.append(String.format("%s Count,%,d\n", topic, getEventCountForTopic(topic)));
            sb.append(String.format("%s Avg (μs),%.2f\n", topic, getAverageLatencyForTopicMicros(topic)));
        }
        
        return sb.toString();
    }
    
    /**
     * Reset all statistics
     */
    public void reset() {
        totalEventsReceived.set(0);
        totalLatencySumNanos.set(0);
        synchronized (this) {
            minLatencyNanos = Long.MAX_VALUE;
            maxLatencyNanos = 0;
        }
        topicStats.clear();
        synchronized (latencyBuffer) {
            latencyBuffer.clear();
        }
    }
    
    private String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}