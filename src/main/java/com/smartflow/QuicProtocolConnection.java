/**
 * Section: 104

 * Group number: 4
 * Student IDs and names: 
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */

package com.smartflow;

import tech.kwik.core.QuicStream;
import tech.kwik.core.server.ApplicationProtocolConnection;

public class QuicProtocolConnection implements ApplicationProtocolConnection {

    @SuppressWarnings("unused") // just doesn't show unused error (the orange line) for the client id
    private final int clientId;

    public QuicProtocolConnection(int clientId) {
        this.clientId = clientId;
        // Store client ID (used to identify which client this connection belongs to)
    }

    // When a new stream arrives from the client
    @Override
    public void acceptPeerInitiatedStream(QuicStream stream) {

        // Create a new thread for each client connection
        Thread worker = new Thread(() -> handleStream(stream));

        // Start processing the stream in parallel
        worker.start();

    }

    // reads the message recieved, identifies the type
    private void handleStream(QuicStream stream) {
        try {
            // Read the full message sent by the client through the stream
            String message = (MessageUtil.readLine(stream.getInputStream()));
            String cleanMessage = SecurityUtils.verifyAndStrip(message);
            if (cleanMessage == null) {
                System.out.println("HMAC verification failed!");
                MessageUtil.writeText(stream.getOutputStream(), "INVALID HMAC");
                stream.resetStream(1);
                return;
            }
            message = cleanMessage;
            // Gets the type of the message (PUBLISH, SUBSCRIBE, UNSUBSCRIBE) and the topic
            // from the message
            String requestType = MessageUtil.classifyMessage(message);

            // Extract the topic from the message based on the request type (PUBLISH,
            // SUBSCRIBE, UNSUBSCRIBE)
            String topic = MessageUtil.getTopic(message, requestType.length());

            switch (requestType) {
                case "PUBLISH":
                    Event event = extractEventDetailsFromMessage(message, requestType.length());

                    EventBroker.deliverEvent(event);

                    MessageUtil.writeText(stream.getOutputStream(), "ACK");

                    Thread.sleep(100);
                    System.err.println();
                    stream.resetStream(0);
                    break;

                case "SUBSCRIBE":
                    EventBroker.addSubscriber(topic, stream.getOutputStream());
                    MessageUtil.writeText(stream.getOutputStream(), "ACK");
                    break;

                case "UNSUBSCRIBE":
                    EventBroker.removeSubscriber(topic, stream.getOutputStream());
                    MessageUtil.writeText(stream.getOutputStream(), "ACK");
                    break;

                default:
                    MessageUtil.writeText(stream.getOutputStream(), "UNKNOWN REQUEST");
                    break;
            }

        } catch (Exception e) {
            // If an error happens while processing the stream
            System.err.println("Error handling traffic light stream: " + e.getMessage());

            try {
                // Reset the stream with an error code
                stream.resetStream(1);
            } catch (Exception ignored) {
                // Ignore reset errors
            }
        }
    }

    public Event extractEventDetailsFromMessage(String message, int requestType) {
        String messageWithoutRequestType = message.substring(++requestType).trim(); // Remove the request type from the
                                                                                    // message
        String[] parts = messageWithoutRequestType.split("\\|");
        Event event = new Event(Integer.parseInt(parts[0]), // id
                parts[1], // topic
                parts[2], // location
                parts[3] // message
        );

        if (parts[4].equals("Pending"))
            event.setAsPending();

        else if (parts[4].equals("Resolved"))
            event.setAsResolved();

        event.setStartTime(Long.parseLong(parts[5]));
        event.setLastUpdatedTime(Long.parseLong(parts[6]));
        return event;
    }
}