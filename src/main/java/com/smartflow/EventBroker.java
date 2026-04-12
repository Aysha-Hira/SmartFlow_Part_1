/**
 * Section: 104
 * Group number: 4
 * Student IDs and names:
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */

package com.smartflow;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import tech.kwik.core.log.SysOutLogger;
import tech.kwik.core.server.ServerConnectionConfig;
import tech.kwik.core.server.ServerConnector;

public class EventBroker {

	// Port number where the QUIC server will listen for client connections
	public static final int PORT = 4433;

	// File that contains the server certificate
	private static final String KEYSTORE_FILE = "src/main/resources/cert.jks";

	// Password used to open the keystore file
	private static final String KEYSTORE_PASSWORD = "smartflow123";

	// Alias name of the certificate inside the keystore
	private static final String CERT_ALIAS = "smartflow";

	// for storring events
	private static Map<String, List<OutputStream>> subscribers = new ConcurrentHashMap<>();

	public static void main(String[] args) throws Exception {

		// subscribers.put("TRAFFIC.accident", null);
		// subscribers.put("WEATHER.sandstorm", null);
		// subscribers.put("ELECTRICITY.outage", null);
		// subscribers.put("OTHERS", null);

		// Create a keystore object to store the server certificate
		KeyStore keyStore = KeyStore.getInstance("JKS");

		// Load the certificate file into the keystore
		try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
			keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
		}

		// Create logger object for server logs
		SysOutLogger logger = new SysOutLogger();
		logger.logInfo(false); // Disable info messages
		logger.logWarning(false); // Disable warning messages

		// Set limits for incoming streams from clients
		ServerConnectionConfig config = ServerConnectionConfig.builder().maxOpenPeerInitiatedBidirectionalStreams(50)
				.maxOpenPeerInitiatedUnidirectionalStreams(0) // Do not allow one-way streams
				.build();

		// Build the QUIC server connector
		ServerConnector connector = ServerConnector.builder().withPort(PORT) // Server will run on port 4433
				.withKeyStore(keyStore, CERT_ALIAS, KEYSTORE_PASSWORD.toCharArray())
				// Attach certificate for secure QUIC communication
				.withConfiguration(config) // Apply stream settings
				.withLogger(logger) // Apply logger settings
				.build();

		// Register the application protocol and connection factory
		connector.registerApplicationProtocol(Protocol.PROTOCOL, new QuicProtocolFactory());

		// Start the QUIC server
		connector.start();

		// Print status messages to show server is running
		System.out.println("""
				===============================================================
				                      Welcome to SMARTFLOW
				===============================================================
				""");
		System.out.println("Abu Dhabi Smart Mobility Control Center QUIC server started on port " + PORT);

	}

	// // Based on the message recieved
	// public static void addSuscriber(String topic, OutputStream out) {
	// subscribers.computeIfAbsent(topic, k -> new
	// CopyOnWriteArrayList<>()).add(out);
	// sendEvents(topic);
	// }

	// // Example: "SUBSCRIBE TRAFFIC.accident"
	// public static List<OutputStream> sendEvents(String topic) {
	// return subscribers.get(topic);
	// }

	public static void addSubscriber(String topic, OutputStream out) {
		subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(out);
		System.out.println("Subscriber added for topic: " + topic);
	}

	public static void deliverEvent(Event event) {
		CopyOnWriteArrayList<OutputStream> list = (CopyOnWriteArrayList<OutputStream>) subscribers
				.get(event.getTopic());

		if (list == null || list.isEmpty()) {
			System.out.println("No subscribers for: " + event.getTopic());
			return;
		}
		for (OutputStream out : list) {
			try {
				MessageUtil.writeText(out, event.serialize());
			} catch (IOException e) {
				list.remove(out);
			}
		}

	}

	public static void removeSubscriber(String topic, OutputStream out) {

		try {
			subscribers.get(topic).remove(out);
			System.out.println("Subscriber removed for topic: " + topic);
		} catch (Exception e) {
			System.out.println("Error removing subscriber for topic: " + topic);
		}

	}

	public static void addTopic(String topic) {
		subscribers.putIfAbsent(topic, new CopyOnWriteArrayList<>());
		System.out.println("Topic added: " + topic);
	}

}