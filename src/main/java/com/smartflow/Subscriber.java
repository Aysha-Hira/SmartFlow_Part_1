<<<<<<< Updated upstream
package com.smartflow;

public class Subscriber {

}
=======
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import tech.kwik.core.QuicClientConnection;
import tech.kwik.core.QuicStream;
import tech.kwik.core.log.SysOutLogger;

public class Subscriber {

    protected String subscriberId;
    protected QuicClientConnection connection;
    protected SysOutLogger logger;
    
    // Track subscribed topics and their corresponding streams
    protected Set<String> subscribedTopics = new CopyOnWriteArraySet<>();
    protected Map<String, QuicStream> topicStreams = new ConcurrentHashMap<>();
    protected Map<String, Thread> listenerThreads = new ConcurrentHashMap<>();
    
    // Latency tracker
    protected LatencyTracker latencyTracker;
    
    // Connection status
    protected AtomicBoolean isConnected = new AtomicBoolean(false);
    protected AtomicBoolean isRunning = new AtomicBoolean(true);

    public Subscriber(String id) {
        this.subscriberId = id;
        this.latencyTracker = new LatencyTracker();
    }

    /**
     * Connect to the SmartFlow broker
     */
    public void connect() {
        logger = new SysOutLogger();
        logger.logInfo(false);
        logger.logWarning(false);

        try {
			connection = QuicClientConnection.newBuilder()
			        .uri(URI.create("https://localhost:" + EventBroker.PORT))
			        .applicationProtocol(Protocol.PROTOCOL)
			        .noServerCertificateCheck()
			        .logger(logger)
			        .build();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("--------------------------------------------------------------- ");
        System.out.println(subscriberId + " connecting to Smart Flow Smart City System...");
        System.out.println("--------------------------------------------------------------- ");

        try {
            connection.connect();
            isConnected.set(true);
        } catch (IOException e) {
            e.printStackTrace();
            isConnected.set(false);
        }

        System.out.println(subscriberId + " connected successfully.");
        System.out.println("--------------------------------------------------------------- ");
    }

    /**
     * Subscribe to a topic - creates a persistent stream for receiving events
     */
    public void subscribe(String topic) {
        if (!isConnected.get()) {
            System.err.println(subscriberId + ": Not connected to broker!");
            return;
        }

        if (subscribedTopics.contains(topic)) {
            System.out.println(subscriberId + " already subscribed to: " + topic);
            return;
        }

        System.out.println(subscriberId + " subscribing to: " + topic);

        try {
            // Create a new stream for this subscription (bidirectional)
            QuicStream stream = connection.createStream(true);
            
            // Create subscription message
            String msg = "SUBSCRIBE " + topic;
            String msgWithHMAC = SecurityUtils.addHMACValue(msg);
            
            System.out.println("Sending subscription: " + msgWithHMAC);
            MessageUtil.writeText(stream.getOutputStream(), msgWithHMAC);
            
            // Read ACK response
            String response = MessageUtil.readLine(stream.getInputStream());
            System.out.println("Received from server: " + response);
            
            if (response != null && response.equals("ACK")) {
                subscribedTopics.add(topic);
                topicStreams.put(topic, stream);
                
                // Start listener thread for this stream
                startListener(stream, topic);
                
                System.out.println(subscriberId + " successfully subscribed to: " + topic);
            } else {
                System.err.println(subscriberId + " subscription failed for: " + topic);
                stream.resetStream(0);
            }
            
        } catch (IOException e) {
            System.out.println(subscriberId + " subscribe error: " + e.getMessage());
        }
    }

    /**
     * Subscribe to multiple topics at once
     */
    public void subscribeAll(String[] topics) {
        for (String topic : topics) {
            subscribe(topic);
            // Small delay to avoid overwhelming the broker
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Start listener thread for a subscribed stream
     */
    private void startListener(QuicStream stream, String topic) {
        Thread listenerThread = new Thread(() -> {
            System.out.println(subscriberId + " started listening for: " + topic);
            
            try {
                // Keep reading events as long as we're connected and subscribed
                while (isConnected.get() && isRunning.get() && subscribedTopics.contains(topic)) {
                    // This blocks until an event arrives or stream closes
                    String eventData = MessageUtil.readLine(stream.getInputStream());
                    
                    if (eventData == null || eventData.isEmpty()) {
                        // Stream closed or end of stream
                        break;
                    }
                    
                    // Process the received event
                    processEvent(eventData, topic);
                }
            } catch (IOException e) {
                if (isConnected.get() && isRunning.get()) {
                    System.err.println(subscriberId + " listener error for " + topic + ": " + e.getMessage());
                }
            } finally {
                System.out.println(subscriberId + " stopped listening for: " + topic);
            }
        });
        
        listenerThread.setDaemon(true);
        listenerThread.start();
        listenerThreads.put(topic, listenerThread);
    }

    /**
     * Process received event and track latency
     */
    private void processEvent(String eventData, String topic) {
        try {
            long receiveTimeNanos = System.nanoTime();
            long receiveTimeMs = System.currentTimeMillis();
            
            // Deserialize event using your Event class
            Event event = Event.deserialize(eventData);
            
            // Calculate latency in microseconds
            long publishTimeMs = event.getPublishTime();
            long latencyMs = receiveTimeMs - publishTimeMs;
            long latencyNanos = latencyMs * 1_000_000;
            
            // Record latency
            latencyTracker.recordLatency(topic, latencyNanos);
            
            // Print event details (can be disabled in simulation for performance)
            System.out.println("\n[Event Received] " + subscriberId + " on [" + topic + "]:");
            System.out.println("  ID: " + event.getID());
            System.out.println("  Location: " + event.getLocation());
            System.out.println("  Message: " + event.getMessage());
            System.out.println("  Status: " + event.getStatus());
            System.out.println("  Latency: " + latencyMs + " ms");
            
        } catch (Exception e) {
            System.err.println(subscriberId + " failed to process event: " + e.getMessage());
        }
    }

    /**
     * Unsubscribe from a topic
     */
    public void unsubscribe(String topic) {
        if (!subscribedTopics.contains(topic)) {
            return;
        }
        
        System.out.println(subscriberId + " unsubscribing from: " + topic);
        
        QuicStream stream = topicStreams.get(topic);
        if (stream != null) {
            try {
                String msg = "UNSUBSCRIBE " + topic;
                String msgWithHMAC = SecurityUtils.addHMACValue(msg);
                MessageUtil.writeText(stream.getOutputStream(), msgWithHMAC);
                
                String response = MessageUtil.readLine(stream.getInputStream());
                System.out.println("Unsubscribe response: " + response);
                
                stream.resetStream(0);
            } catch (IOException e) {
                System.out.println(subscriberId + " unsubscribe error: " + e.getMessage());
            }
        }
        
        // Stop listener thread
        Thread listener = listenerThreads.remove(topic);
        if (listener != null) {
            listener.interrupt();
        }
        
        subscribedTopics.remove(topic);
        topicStreams.remove(topic);
        
        System.out.println(subscriberId + " unsubscribed from: " + topic);
    }

    /**
     * Get latency tracker for statistics
     */
    public LatencyTracker getLatencyTracker() {
        return latencyTracker;
    }

    /**
     * Print latency report
     */
    public void printLatencyReport() {
        System.out.println(latencyTracker.getStatsReport());
    }

    /**
     * Get all subscribed topics
     */
    public Set<String> getSubscribedTopics() {
        return new CopyOnWriteArraySet<>(subscribedTopics);
    }

    /**
     * Get subscriber ID
     */
    public String getSubscriberId() {
        return subscriberId;
    }

    /**
     * Check if connected
     */
    public boolean isConnected() {
        return isConnected.get();
    }

    /**
     * Disconnect from broker (clean shutdown)
     */
    public void disconnect() {
        if (!isConnected.get()) {
            return;
        }
        
        System.out.println(subscriberId + " disconnecting...");
        isRunning.set(false);
        
        // Unsubscribe from all topics
        for (String topic : new CopyOnWriteArraySet<>(subscribedTopics)) {
            unsubscribe(topic);
        }
        
        // Close connection
        if (connection != null) {
            connection.close();
        }
        
        isConnected.set(false);
        System.out.println(subscriberId + " disconnected.");
    }
}
>>>>>>> Stashed changes
