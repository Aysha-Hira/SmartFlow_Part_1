///**
// * Section: 104
// * Group number: 4
// * Student IDs and names:
// * Laisa Sanjida Isra: 1089635
// * Fatima Syed Wasti: 1095190
// * Aysha Hira: 1088000
// */
//package com.smartflow;
//
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PublisherSimulation {
//
//    public static void main(String[] args) throws SocketException, UnknownHostException, InterruptedException {
//
//        System.out.println("=== SmartFlow Publisher Simulation ===");
//        System.out.println("Creating and connecting publishers...\n");
//
//        Traffic t1 = new Traffic();
//        Weather w1 = new Weather();
//        Utility u1 = new Utility();
//        PublicTransport p1 = new PublicTransport();
//
//        t1.connect();
//        w1.connect();
//        u1.connect();
//        p1.connect();
//
//        System.out.println("All publishers connected!");
//        System.out.println("=======================================");
//
//        // ── Launch all publishers in parallel ───────────────────────────────
//        List<Thread> threads = new ArrayList<>();
//
//        threads.add(new Thread(() -> {
//            System.out.println("Publishing loop for Traffic service...");
//            long start = System.currentTimeMillis();
//            t1.startPublishingLoop(1000, 10);
//            long duration = System.currentTimeMillis() - start;
//            System.out.println("=======================================");
//            System.out.println("Traffic publishing done.");
//            System.out.println("  Total time:       " + duration + " ms");
//            System.out.println("  Events published: 10");
//            System.out.printf( "  Avg time/event:   %.2f ms%n", duration / 10.0);
//            System.out.println("=======================================");
//        }));
//
//        threads.add(new Thread(() -> {
//            System.out.println("Publishing loop for Weather service...");
//            long start = System.currentTimeMillis();
//            w1.startPublishingLoop(2000, 10);
//            long duration = System.currentTimeMillis() - start;
//            System.out.println("=======================================");
//            System.out.println("Weather publishing done.");
//            System.out.println("  Total time:       " + duration + " ms");
//            System.out.println("  Events published: 10");
//            System.out.printf( "  Avg time/event:   %.2f ms%n", duration / 10.0);
//            System.out.println("=======================================");
//        }));
//
//        threads.add(new Thread(() -> {
//            System.out.println("Publishing loop for Utility service...");
//            long start = System.currentTimeMillis();
//            u1.startPublishingLoop(4000, 10);
//            long duration = System.currentTimeMillis() - start;
//            System.out.println("=======================================");
//            System.out.println("Utility publishing done.");
//            System.out.println("  Total time:       " + duration + " ms");
//            System.out.println("  Events published: 10");
//            System.out.printf( "  Avg time/event:   %.2f ms%n", duration / 10.0);
//            System.out.println("=======================================");
//        }));
//
//        threads.add(new Thread(() -> {
//            System.out.println("Publishing loop for Public Transport service...");
//            long start = System.currentTimeMillis();
//            p1.startPublishingLoop(3000, 10);
//            long duration = System.currentTimeMillis() - start;
//            System.out.println("=======================================");
//            System.out.println("Public Transport publishing done.");
//            System.out.println("  Total time:       " + duration + " ms");
//            System.out.println("  Events published: 10");
//            System.out.printf( "  Avg time/event:   %.2f ms%n", duration / 10.0);
//            System.out.println("=======================================");
//        }));
//
//        // Start all threads
//        long simulation_start = System.currentTimeMillis();
//        threads.forEach(Thread::start);
//
//        // Wait for all publishers to finish
//        for (Thread thread : threads) {
//        	thread.join();
//        }
//
//        long sim_duration = System.currentTimeMillis() - simulation_start;
//
//        // ── Overall summary ───────────────────────────────────────────────────
//        int totalEvents = 40; // 4 publishers x 10 events each
//        System.out.println("\n========== PUBLISHER SIMULATION SUMMARY ==========");
//        System.out.println("Total events published:   " + totalEvents);
//        System.out.println("Total simulation time:    " + sim_duration + " ms");
//        System.out.printf( "Overall throughput:       %.2f events/sec%n", totalEvents / (sim_duration / 1000.0));
//        System.out.println("==================================================");
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

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PublisherSimulation {

	/**
	 * This method is used by SubscriberSimulation to run publishers with a given
	 * load.
	 */
	public static void runSimulation(int eventsPerPublisher)
			throws SocketException, UnknownHostException, InterruptedException {

		System.out.println("\n=== SmartFlow Publisher Simulation ===");
		System.out.println("Events per publisher: " + eventsPerPublisher);
		System.out.println("Creating and connecting publishers...\n");

		// Create publishers
		Traffic t1 = new Traffic();
		Weather w1 = new Weather();
		Utility u1 = new Utility();
		PublicTransport p1 = new PublicTransport();

		// Connect publishers
		t1.connect();
		w1.connect();
		u1.connect();
		p1.connect();

		System.out.println("All publishers connected!");
		System.out.println("=======================================");

		// Run all publishers in parallel
		List<Thread> threads = new ArrayList<>();

		threads.add(new Thread(() -> {
			System.out.println("Traffic publishing started...");
			t1.startPublishingLoop(1000, eventsPerPublisher);
			System.out.println("Traffic publishing finished.");
		}));

		threads.add(new Thread(() -> {
			System.out.println("Weather publishing started...");
			w1.startPublishingLoop(2000, eventsPerPublisher);
			System.out.println("Weather publishing finished.");
		}));

		threads.add(new Thread(() -> {
			System.out.println("Utility publishing started...");
			u1.startPublishingLoop(4000, eventsPerPublisher);
			System.out.println("Utility publishing finished.");
		}));

		threads.add(new Thread(() -> {
			System.out.println("Public Transport publishing started...");
			p1.startPublishingLoop(3000, eventsPerPublisher);
			System.out.println("Public Transport publishing finished.");
		}));

		// Start all threads
		long startTime = System.currentTimeMillis();
		threads.forEach(Thread::start);

		// Wait for all to finish
		for (Thread thread : threads) {
			thread.join();
		}

		long duration = System.currentTimeMillis() - startTime;

		// Summary
		int totalEvents = eventsPerPublisher * 4;

		System.out.println("\n========== PUBLISHER SUMMARY ==========");
		System.out.println("Total events published:   " + totalEvents);
		System.out.println("Total simulation time:    " + duration + " ms");
		System.out.printf("Overall throughput:       %.2f events/sec%n", totalEvents / (duration / 1000.0));
		System.out.println("=======================================");
	}

	/**
	 * Optional: standalone test
	 */
	public static void main(String[] args) throws SocketException, UnknownHostException, InterruptedException {

		// Default test run
		runSimulation(10);
	}
}