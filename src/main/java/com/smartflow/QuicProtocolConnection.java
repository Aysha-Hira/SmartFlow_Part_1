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
            String message = MessageUtil.readAll(stream.getInputStream());

            String topic = MessageUtil.getTopic(message);

            switch (MessageUtil.classifyMessage(message)) {
                case "PUBLISH":
                    Event event = extractEventDetailsFromMessage(message);
                    EventBroker.deliverEvent(event);
                    MessageUtil.writeText(stream.getOutputStream(), "ACK");
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

    public Event extractEventDetailsFromMessage(String message) {
        String[] parts = message.split("\\|");
        Event event = new Event(
                Integer.parseInt(parts[0]), // id
                parts[1], // topic
                parts[2], // location
                parts[3] // message
        );

        if (parts[4] == "Pending")
            event.setAsPending();

        else if (parts[4] == "Resolved")
            event.setAsResolved();

        event.setStartTime(Long.parseLong(parts[5]));
        event.setLeastUpdatedTime(Long.parseLong(parts[6]));
        return event;
    }
}