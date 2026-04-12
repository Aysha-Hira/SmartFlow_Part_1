package com.smartflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class MessageUtil {

    private MessageUtil() {
    }

    public static String readAll(InputStream input) throws IOException {
        // Wrap input stream so we can read text line by line (UTF-8 encoding)
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder(); // Used to build the full message
        String line;
        boolean first = true; // Helps avoid adding extra newline at the start

        // Read input line by line until no more data
        while ((line = reader.readLine()) != null) {
            if (!first) {
                sb.append('\n'); // Add newline between lines (not before first)
            }
            sb.append(line); // Add current line to result
            first = false;
        }

        return sb.toString(); // Return complete message as a single string
    }

    public static String readLine(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        return reader.readLine(); // reads ONE line then returns
    }

    public static void writeText(OutputStream output, String text) throws IOException {
        // Create writer to send text using UTF-8 encoding
        PrintWriter writer = new PrintWriter(output, false, StandardCharsets.UTF_8);
        writer.println(text); // Send the text (adds newline at the end)
        writer.flush(); // Make sure data is actually sent
        // output.close(); // Close the connection/output stream
    }

    private static int eventIdCounter = 0;

    public static String format(String publisherId, String topic, String location, String payload) {
        long now = System.currentTimeMillis();
        String eventData = eventIdCounter++ + "|" // id
                + topic + "|" // topic
                + location + "|" // location
                + payload + "|" // message
                + "Pending|" // status
                + now + "|" // publishTime
                + now; // updateTime
        return "PUBLISH " + eventData;
    }

    // Checks if the message is PUBLIC or SUBSCRIBE or UNSUBSCRIBE, and sends it to
    public static String classifyMessage(String message) {

        if (message.startsWith("PUBLISH"))
            return "PUBLISH";

        if (message.startsWith("UNSUBSCRIBE"))
            return "UNSUBSCRIBE";

        if (message.startsWith("SUBSCRIBE"))
            return "SUBSCRIBE";

        return "UNKNOWN REQUEST"; // If no known pattern is found
    }

    // Extracts topic from message
    // "SUBSCRIBE TRAFFIC.accident" → "TRAFFIC.accident"
    // "PUBLISH | 101 | TRAFFIC.accident | Location | Message" → "TRAFFIC.accident"
    public static String getTopic(String message, int requestType) {
        // Remove the request type (PUBLISH, SUBSCRIBE, UNSUBSCRIBE) from the message
        String messageWithoutRequest = message.substring(requestType + 1).trim();

        // Split the remaining message by '|'
        String[] parts = messageWithoutRequest.trim().split("\\|");

        // For Publsh request
        if (message.startsWith("PUBLISH"))
            // Since sending an event, we need id, so the first part is the id and the
            // second part is the topic
            return parts[1].trim();
        return parts[0].trim();
    }

}