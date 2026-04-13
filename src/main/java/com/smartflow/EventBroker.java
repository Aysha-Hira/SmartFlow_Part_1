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
	private static Map<String, List<OutputStream>> subscriptionDB = new ConcurrentHashMap<>();

	public static void main(String[] args) throws Exception {

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
		System.out.println(" Broker started on port " + PORT);
		// Print status messages to show server is running
		System.out.println("""
				===============================================================
				                      Welcome to SMARTFLOW
				===============================================================
				""");

	}

	// Adds a new subscriber for a specific topic by storing their output stream in
	// the subscribers map
	public static void addSubscriber(String topic, OutputStream out) {
		subscriptionDB.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(out);
		System.out.println("Subscriber added for topic: " + topic);
	}

	// Delivers an event to all subscribers of the event's topic by writing the
	// event
	public static void deliverEvent(Event event) {
		CopyOnWriteArrayList<OutputStream> list = (CopyOnWriteArrayList<OutputStream>) subscriptionDB
				.get(event.getTopic());

		if (list == null || list.isEmpty()) {
			System.out.println("No subscribers for: " + event.getTopic());
			return;
		}
		else {
			System.out.println(list.size() + " subscribers found for: " + event.getTopic());
		}
		for (OutputStream out : list) {
			try {
				MessageUtil.writeText(out, event.serialize());
			} catch (IOException e) {
				list.remove(out);
			}
		}

	}

	// Removes a subscriber from a specific topic by removing their output stream
	// from the subscribers map
	public static void removeSubscriber(String topic, OutputStream out) {

		try {
			subscriptionDB.get(topic).remove(out);
			System.out.println("Subscriber removed for topic: " + topic);
		} catch (Exception e) {
			System.out.println("Error removing subscriber for topic: " + topic);
		}

	}

	public static void addTopic(String topic) {
		subscriptionDB.putIfAbsent(topic, new CopyOnWriteArrayList<>());
		System.out.println("Topic added: " + topic);
	}

}