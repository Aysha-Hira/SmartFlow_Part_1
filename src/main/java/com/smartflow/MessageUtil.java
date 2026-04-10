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

    public static void writeText(OutputStream output, String text) throws IOException {
        // Create writer to send text using UTF-8 encoding
        PrintWriter writer = new PrintWriter(output, false, StandardCharsets.UTF_8);

        writer.println(text); // Send the text (adds newline at the end)
        writer.flush(); // Make sure data is actually sent
        // output.close(); // Close the connection/output stream
    }

    // Checks if the message is PUBLIC or SUBSCRIBE or UNSUBSCRIBE, and sends it to
    public static String classifyMessage(String message) {

        if (message.contains("PUBLISH"))
            return "PUBLISH";

        if (message.contains("SUBSCRIBE"))
            return "SUBSCRIBE";

        if (message.contains("UNSUBSCRIBE"))
            return "UNSUBSCRIBE";

        return "UNKNOWN REQUEST"; // If no known pattern is found
    }

    // Extracts topic from message
    // "SUBSCRIBE TRAFFIC.accident" → "TRAFFIC.accident"
    // "PUBLISH TRAFFIC.accident | Location | Message" → "TRAFFIC.accident"
    public static String getTopic(String message) {
        String[] parts = message.trim().split(" ");
        if (parts.length >= 2)
            return parts[1].split("\\|")[0].trim(); // gets the first part (topic) regardless of the request
        return "";
    }
}