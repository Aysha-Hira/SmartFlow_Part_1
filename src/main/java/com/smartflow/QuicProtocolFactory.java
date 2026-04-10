package com.smartflow;

import tech.kwik.core.QuicConnection;
import tech.kwik.core.server.ApplicationProtocolConnection;
import tech.kwik.core.server.ApplicationProtocolConnectionFactory;

public class QuicProtocolFactory implements ApplicationProtocolConnectionFactory {
	private static int clientIdCounter = 1;

	@Override
	public ApplicationProtocolConnection createConnection(String protocol, QuicConnection quicConnection) {

		int clientId = clientIdCounter++;
		// Assign a unique ID to each new client (increments every time)

		// Print connection info when a new client connects
		System.out.println("\n========================================");
		System.out.println(" Smartflow Client Connected: Client-" + clientId);
		System.out.println("========================================");

		// Create and return a new connection handler for this client
		return new QuicProtocolConnection(clientId);
	}

	@Override
	public int maxConcurrentPeerInitiatedBidirectionalStreams() {
		return 50;
		// Allow up to 50 streams from the client at the same time

	}

	@Override
	public int maxConcurrentPeerInitiatedUnidirectionalStreams() {
		return 0;
		// Do NOT allow unidirectional streams (only bidirectional communication)

	}
}