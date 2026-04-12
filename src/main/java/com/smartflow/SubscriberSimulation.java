/**
 * Section: 104
 * Group number: 4
 * Student IDs and names:
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */

package com.smartflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SubscriberSimulation {

    static String[] all_topics = {
        "TRAFFIC.ACCIDENT", "TRAFFIC.CONGESTION", "TRAFFIC.HAZARD",
        "TRAFFIC.ROAD_CLOSURE", "TRAFFIC.EMERGENCY_VEHICLES",
        "WEATHER.RAIN", "WEATHER.STORM", "WEATHER.HURRICANE",
        "WEATHER.EARTHQUAKE", "WEATHER.UV_DANGER",
        "UTILITY.POWER_OUTAGE", "UTILITY.WATER_LEAK",
        "UTILITY.GAS_LEAK", "UTILITY.GRID_OVERLOAD",
        "PUBLIC_TRANSPORT.BUS_DELAY", "PUBLIC_TRANSPORT.METRO_OVERLOAD",
        "PUBLIC_TRANSPORT.TRAM_BREAKDOWN", "PUBLIC_TRANSPORT.ROUTE_CHANGE"
    };

    public static Subscriber[] createSubscribers(int count) throws InterruptedException {
        Subscriber[] subscribers = {};
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            Subscriber s = new Subscriber("subscriber-" + (i + 1));
            s.connect();

            // Pick between 1 and 4 unique random indices from all_topics
            int topicCount = rand.nextInt(4) + 1;
            List<Integer> topic_indexes = new ArrayList<>();
            while (topic_indexes.size() < topicCount) {
                int topic_index = rand.nextInt(all_topics.length);
                if (!topic_indexes.contains(topic_index)) {
                	topic_indexes.add(topic_index);
                }
            }

            String[] selectedTopics = new String[topicCount];
            for (int j = 0; j < topicCount; j++) {
                selectedTopics[j] = all_topics[topic_indexes.get(j)];
            }

            System.out.println(s.subscriberId + " subscribing to: " + selectedTopics.toString());
            s.subscribeAll(selectedTopics);

            subscribers[i] = s;
            Thread.sleep(50);
        }

        return subscribers;
    }

    public static void main(String[] args) throws Exception {

        System.out.println("=== SmartFlow Subscriber Simulation ===");
        System.out.println("Creating and connecting subscribers...\n");

        Subscriber[] subscribers = createSubscribers(10);

        System.out.println("\nAll subscribers connected and subscribed.");
        System.out.println("Waiting for events from publishers...\n");

        // Adjust this based on how long your PublisherSimulation runs
        Thread.sleep(20000);

        // ── Per-subscriber latency reports ───────────────────────────────────
        System.out.println("\n========== LATENCY REPORTS FOR EACH SUBSCRIBER ==========");
        long totalEventsReceived = 0;
        double totalLatencySum = 0;

        for (Subscriber subscriber : subscribers) {
            long count = subscriber.getLatencyTracker().getTotalEventsReceived();
        	subscriber.printLatencyReport();
            totalEventsReceived += count;
            totalLatencySum += subscriber.getLatencyTracker().getAverageLatencyMillis() * count;
        }

        // ── Aggregate summary ─────────────────────────────────────────────────
        System.out.println("\n========== AGGREGATE SUMMARY ==========");
        System.out.println("Total subscribers:                " + subscribers.length);
        System.out.println("Total events received (all subs): " + totalEventsReceived);
        if (totalEventsReceived > 0) {
            double overallAvgLatency = totalLatencySum / totalEventsReceived;
            System.out.printf("Overall average latency:" + overallAvgLatency);
        }
        System.out.println("========================================");
    }
}