///**
// * Section: 104
// * Group number: 4
// * Student IDs and names:
// * Laisa Sanjida Isra: 1089635
// * Fatima Syed Wasti: 1095190
// * Aysha Hira: 1088000
// */
//
//package com.smartflow;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//
//public class SubscriberSimulation {
//
//    static String[] all_topics = {
//        "TRAFFIC.ACCIDENT", "TRAFFIC.CONGESTION", "TRAFFIC.HAZARD",
//        "TRAFFIC.ROAD_CLOSURE", "TRAFFIC.EMERGENCY_VEHICLES",
//        "WEATHER.RAIN", "WEATHER.STORM", "WEATHER.HURRICANE",
//        "WEATHER.EARTHQUAKE", "WEATHER.UV_DANGER",
//        "UTILITY.POWER_OUTAGE", "UTILITY.WATER_LEAK",
//        "UTILITY.GAS_LEAK", "UTILITY.GRID_OVERLOAD",
//        "PUBLIC_TRANSPORT.BUS_DELAY", "PUBLIC_TRANSPORT.METRO_OVERLOAD",
//        "PUBLIC_TRANSPORT.TRAM_BREAKDOWN", "PUBLIC_TRANSPORT.ROUTE_CHANGE"
//    };
//
//    public static Subscriber[] createSubscribers(int count) throws InterruptedException {
//        Subscriber[] subscribers = {};
//        Random rand = new Random();
//
//        for (int i = 0; i < count; i++) {
//            Subscriber s = new Subscriber("subscriber-" + (i + 1));
//            s.connect();
//
//            // Pick between 1 and 4 unique random indices from all_topics
//            int topicCount = rand.nextInt(4) + 1;
//            List<Integer> topic_indexes = new ArrayList<>();
//            while (topic_indexes.size() < topicCount) {
//                int topic_index = rand.nextInt(all_topics.length);
//                if (!topic_indexes.contains(topic_index)) {
//                	topic_indexes.add(topic_index);
//                }
//            }
//
//            String[] selectedTopics = new String[topicCount];
//            for (int j = 0; j < topicCount; j++) {
//                selectedTopics[j] = all_topics[topic_indexes.get(j)];
//            }
//
//            System.out.println(s.subscriberId + " subscribing to: " + selectedTopics.toString());
//            s.subscribeAll(selectedTopics);
//
//            subscribers[i] = s;
//            Thread.sleep(50);
//        }
//
//        return subscribers;
//    }
//
//    public static void main(String[] args) throws Exception {
//
//        System.out.println("=== SmartFlow Subscriber Simulation ===");
//        System.out.println("Creating and connecting subscribers...\n");
//
//        Subscriber[] subscribers = createSubscribers(10);
//
//        System.out.println("\nAll subscribers connected and subscribed.");
//        System.out.println("Waiting for events from publishers...\n");
//
//        // Adjust this based on how long your PublisherSimulation runs
//        Thread.sleep(20000);
//
//        // ── Per-subscriber latency reports ───────────────────────────────────
//        System.out.println("\n========== LATENCY REPORTS FOR EACH SUBSCRIBER ==========");
//        long totalEventsReceived = 0;
//        double totalLatencySum = 0;
//
//        for (Subscriber subscriber : subscribers) {
//            long count = subscriber.getLatencyTracker().getTotalEventsReceived();
//        	subscriber.printLatencyReport();
//            totalEventsReceived += count;
//            totalLatencySum += subscriber.getLatencyTracker().getAverageLatencyMillis() * count;
//        }
//
//        // ── Aggregate summary ─────────────────────────────────────────────────
//        System.out.println("\n========== AGGREGATE SUMMARY ==========");
//        System.out.println("Total subscribers:                " + subscribers.length);
//        System.out.println("Total events received (all subs): " + totalEventsReceived);
//        if (totalEventsReceived > 0) {
//            double overallAvgLatency = totalLatencySum / totalEventsReceived;
//            System.out.printf("Overall average latency:" + overallAvgLatency);
//        }
//        System.out.println("========================================");
//    }
//}






/**
 * Section: 104
 * Group number: 4
 * Student IDs and names:
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */

package com.smartflow;

import java.io.FileWriter;
import java.io.IOException;
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
        Subscriber[] subscribers = new Subscriber[count];
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            Subscriber s = new Subscriber("subscriber-" + (i + 1));
            s.connect();

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

            System.out.println(s.subscriberId + " subscribing to: " + Arrays.toString(selectedTopics));
            s.subscribeAll(selectedTopics);

            subscribers[i] = s;
            Thread.sleep(50);
        }

        return subscribers;
    }

    public static void main(String[] args) throws Exception {

        System.out.println("=== SmartFlow Part 1 - Subscriber Simulation ===");

        int[] loads = { 10, 25, 50 };

        // These will accumulate rows across all load levels for the combined CSVs
        StringBuilder summaryRows    = new StringBuilder();
        StringBuilder perTopicRows   = new StringBuilder();
        StringBuilder perSubRows     = new StringBuilder();

        for (int load : loads) {

            System.out.println("\n=== Running simulation with load: " + load + " events per publisher ===");

            // Step 1: create 10 subscribers
            Subscriber[] subscribers = createSubscribers(10);
            System.out.println("Subscribers ready. Starting publishers...");

            // Step 2: run publisher simulation
            PublisherSimulation.runSimulation(load);

            // Step 3: wait for events to be delivered
            // Slowest publisher is Utility at 4000ms interval * load events
            long waitMs = Math.min(4000L * load + 10000L, 120000L); // cap at 2 min
            System.out.println("Waiting " + waitMs / 1000 + "s for delivery...");
            Thread.sleep(waitMs);

            // Step 4: print per-subscriber reports
            System.out.println("\n========== LATENCY REPORTS (load=" + load + ") ==========");
            long totalEventsReceived = 0;
            double totalLatencySum   = 0;

            for (Subscriber s : subscribers) {
                long count = s.getLatencyTracker().getTotalEventsReceived();
                s.printLatencyReport();
                totalEventsReceived += count;
                totalLatencySum     += s.getLatencyTracker().getAverageLatencyMillis() * count;
            }

            double overallAvg = totalEventsReceived == 0 ? 0 : totalLatencySum / totalEventsReceived;

            System.out.println("\n========== AGGREGATE SUMMARY (load=" + load + ") ==========");
            System.out.println("Total subscribers:                " + subscribers.length);
            System.out.println("Total events received (all subs): " + totalEventsReceived);
            System.out.printf( "Overall average latency:          %.3f ms%n", overallAvg);

            // Step 5: build aggregate tracker for this load level
            LatencyTracker aggregate = buildAggregateTracker(subscribers);

            // Step 6: write per-load CSV files
            writeLoadCSV(aggregate, subscribers, load);

            // Step 7: accumulate rows for the combined multi-load CSVs
            summaryRows.append(buildSummaryRow(aggregate, subscribers, load));
            perTopicRows.append(buildPerTopicRows(aggregate, load));
            perSubRows.append(buildPerSubscriberRows(subscribers, load));
        }

        // Step 8: write combined multi-load CSV files (useful for charts)
        writeCombinedCSVs(summaryRows.toString(), perTopicRows.toString(), perSubRows.toString());

        System.out.println("\n=== All simulations complete. CSV files written. ===");
    }

    // ── Aggregate helper ──────────────────────────────────────────────────────

    private static LatencyTracker buildAggregateTracker(Subscriber[] subscribers) {
        LatencyTracker aggregate = new LatencyTracker();
        for (Subscriber s : subscribers) {
            LatencyTracker lt = s.getLatencyTracker();
            for (String topic : lt.getTopics()) {
                long count    = lt.getEventCountForTopic(topic);
                double avgMicros = lt.getAverageLatencyForTopicMicros(topic);
                long avgNanos = (long)(avgMicros * 1000);
                for (long j = 0; j < count; j++) {
                    aggregate.recordLatency(topic, avgNanos);
                }
            }
        }
        return aggregate;
    }

    // ── Per-load CSV writer ───────────────────────────────────────────────────

    private static void writeLoadCSV(LatencyTracker aggregate, Subscriber[] subscribers, int load) {

        // File 1: results_summary_load{N}.csv
        String summaryFile = "results_summary_load" + load + ".csv";
        try (FileWriter fw = new FileWriter(summaryFile)) {
            fw.write("Metric,Value\n");
            fw.write("Load (events per publisher)," + load + "\n");
            fw.write("Total Subscribers," + subscribers.length + "\n");
            fw.write(String.format("Total Events Received,%d\n",   aggregate.getTotalEventsReceived()));
            fw.write(String.format("Average Latency (ms),%.3f\n",  aggregate.getAverageLatencyMillis()));
            fw.write(String.format("Min Latency (ms),%.3f\n",      aggregate.getMinLatencyMicros() / 1000.0));
            fw.write(String.format("Max Latency (ms),%.3f\n",      aggregate.getMaxLatencyMicros() / 1000.0));
            fw.write(String.format("p50 Latency (ms),%.3f\n",      aggregate.getPercentileLatencyMicros(50)   / 1000.0));
            fw.write(String.format("p95 Latency (ms),%.3f\n",      aggregate.getPercentileLatencyMicros(95)   / 1000.0));
            fw.write(String.format("p99 Latency (ms),%.3f\n",      aggregate.getPercentileLatencyMicros(99)   / 1000.0));
            fw.write(String.format("p99.9 Latency (ms),%.3f\n",    aggregate.getPercentileLatencyMicros(99.9) / 1000.0));
            System.out.println("[CSV] Written: " + summaryFile);
        } catch (IOException e) {
            System.err.println("Failed to write " + summaryFile + ": " + e.getMessage());
        }

        // File 2: results_per_topic_load{N}.csv
        String topicFile = "results_per_topic_load" + load + ".csv";
        try (FileWriter fw = new FileWriter(topicFile)) {
            fw.write("Topic,Events Received,Avg Latency (ms)\n");
            for (String topic : aggregate.getTopics()) {
                long count  = aggregate.getEventCountForTopic(topic);
                double avg  = aggregate.getAverageLatencyForTopicMicros(topic) / 1000.0;
                fw.write(String.format("%s,%d,%.3f\n", topic, count, avg));
            }
            System.out.println("[CSV] Written: " + topicFile);
        } catch (IOException e) {
            System.err.println("Failed to write " + topicFile + ": " + e.getMessage());
        }

        // File 3: results_per_subscriber_load{N}.csv
        String subFile = "results_per_subscriber_load" + load + ".csv";
        try (FileWriter fw = new FileWriter(subFile)) {
            fw.write("Subscriber,Events Received,Avg Latency (ms),Topics\n");
            for (Subscriber s : subscribers) {
                LatencyTracker lt = s.getLatencyTracker();
                fw.write(String.format("%s,%d,%.3f,\"%s\"\n",
                        s.getSubscriberId(),
                        lt.getTotalEventsReceived(),
                        lt.getAverageLatencyMillis(),
                        s.getSubscribedTopics().toString()));
            }
            System.out.println("[CSV] Written: " + subFile);
        } catch (IOException e) {
            System.err.println("Failed to write " + subFile + ": " + e.getMessage());
        }
    }

    // ── Row builders for combined CSVs ────────────────────────────────────────

    private static String buildSummaryRow(LatencyTracker agg, Subscriber[] subscribers, int load) {
        return String.format("%d,%d,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f\n",
                load,
                subscribers.length,
                agg.getTotalEventsReceived(),
                agg.getAverageLatencyMillis(),
                agg.getMinLatencyMicros()              / 1000.0,
                agg.getMaxLatencyMicros()              / 1000.0,
                agg.getPercentileLatencyMicros(50)     / 1000.0,
                agg.getPercentileLatencyMicros(95)     / 1000.0,
                agg.getPercentileLatencyMicros(99)     / 1000.0,
                agg.getPercentileLatencyMicros(99.9)   / 1000.0);
    }

    private static String buildPerTopicRows(LatencyTracker agg, int load) {
        StringBuilder sb = new StringBuilder();
        for (String topic : agg.getTopics()) {
            sb.append(String.format("%d,%s,%d,%.3f\n",
                    load,
                    topic,
                    agg.getEventCountForTopic(topic),
                    agg.getAverageLatencyForTopicMicros(topic) / 1000.0));
        }
        return sb.toString();
    }

    private static String buildPerSubscriberRows(Subscriber[] subscribers, int load) {
        StringBuilder sb = new StringBuilder();
        for (Subscriber s : subscribers) {
            LatencyTracker lt = s.getLatencyTracker();
            sb.append(String.format("%d,%s,%d,%.3f,\"%s\"\n",
                    load,
                    s.getSubscriberId(),
                    lt.getTotalEventsReceived(),
                    lt.getAverageLatencyMillis(),
                    s.getSubscribedTopics().toString()));
        }
        return sb.toString();
    }

    // ── Combined multi-load CSV writer ────────────────────────────────────────

    private static void writeCombinedCSVs(String summaryRows, String perTopicRows, String perSubRows) {

        // Combined summary across all load levels — great for line charts
        try (FileWriter fw = new FileWriter("results_summary_all_loads.csv")) {
            fw.write("Load,Subscribers,Total Events,Avg Latency (ms),Min (ms),Max (ms),p50 (ms),p95 (ms),p99 (ms),p99.9 (ms)\n");
            fw.write(summaryRows);
            System.out.println("[CSV] Written: results_summary_all_loads.csv");
        } catch (IOException e) {
            System.err.println("Failed to write combined summary: " + e.getMessage());
        }

        // Combined per-topic across all load levels — great for grouped bar charts
        try (FileWriter fw = new FileWriter("results_per_topic_all_loads.csv")) {
            fw.write("Load,Topic,Events Received,Avg Latency (ms)\n");
            fw.write(perTopicRows);
            System.out.println("[CSV] Written: results_per_topic_all_loads.csv");
        } catch (IOException e) {
            System.err.println("Failed to write combined per-topic: " + e.getMessage());
        }

        // Combined per-subscriber across all load levels
        try (FileWriter fw = new FileWriter("results_per_subscriber_all_loads.csv")) {
            fw.write("Load,Subscriber,Events Received,Avg Latency (ms),Topics\n");
            fw.write(perSubRows);
            System.out.println("[CSV] Written: results_per_subscriber_all_loads.csv");
        } catch (IOException e) {
            System.err.println("Failed to write combined per-subscriber: " + e.getMessage());
        }
    }
}