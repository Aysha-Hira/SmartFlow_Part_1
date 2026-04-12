/**
 * Section: 104
 * Group number: 4
 * Student IDs and names: 
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */

package com.smartflow;

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;

import tech.kwik.core.QuicClientConnection;
import tech.kwik.core.QuicStream;
import tech.kwik.core.log.SysOutLogger;

public abstract class Publisher {

	protected String publisher_id;
	protected QuicClientConnection connection;
	protected SysOutLogger logger;

	public Publisher(String id) {
		this.publisher_id = id;
	}

	public void connect() throws SocketException, UnknownHostException {
		logger = new SysOutLogger();
		logger.logInfo(false);
		logger.logWarning(false);

		connection = QuicClientConnection.newBuilder().uri(URI.create("https://localhost:" + EventBroker.PORT))
				.applicationProtocol(Protocol.PROTOCOL).noServerCertificateCheck().logger(logger).build();

		System.out.println("--------------------------------------------------------------- ");
		System.out.println(publisher_id + " connecting to Smart Flow Smart City System...");
		System.out.println("--------------------------------------------------------------- ");

		try {
			connection.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(publisher_id + " connected successfully.");
		System.out.println("--------------------------------------------------------------- ");
	}

	public void publish() {
		String topic = generateTopic();
		String payload = generateContent(topic);
		String location = getLocation();

		QuicStream stream;
		try {
			stream = connection.createStream(true);

			// Need location of publisher
			String msg = MessageUtil.format(publisher_id, topic, location, payload); // create message
			System.out.println("Message before HMAC: " + msg + "\n");

			String msgWithHMAC = SecurityUtils.addHMACValue(msg); // add HMAC to message
			System.out.println("Message after HMAC: " + msgWithHMAC);
			System.out.println();
			System.out.println("Sending message -->" + msg);
			MessageUtil.writeText(stream.getOutputStream(), msgWithHMAC); // sends encrypted message

			System.out.println();
			System.out.println("after writing");
			String response = MessageUtil.readLine(stream.getInputStream()); // get ACK
			System.out.println("Recieved from server: " + response);
			// stream.resetStream(0);

		} catch (IOException e) {
			System.out.println(publisher_id + ": " + e.getMessage());
		}

	}

	public void startPublishingLoop(int interval, int maxRequests) {
		int requestCount = 0;
		while (requestCount < maxRequests) {
			publish();
			try {
				requestCount++;
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				System.out.println(publisher_id + ": " + e.getMessage());
			}
		}
	}

	protected abstract String generateTopic();

	protected abstract String generateContent(String topic);

	protected abstract String getLocation();
}
