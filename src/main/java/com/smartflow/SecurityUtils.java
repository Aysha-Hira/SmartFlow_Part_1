/**
 * Section: 104
 * Group number: 4
 * Student IDs and names: 
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */

package com.smartflow;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecurityUtils {

    // Secret key shared between publisher and broker
    private static final String SECRET_KEY = "SmartFlowSecretKey123!";

    // Adds HMAC to end of message before sending
    // "PUBLISH 1|TRAFFIC.accident|Dubai|Accident" -> "PUBLISH
    // 1|TRAFFIC.accident|Dubai|Accident|HMAC=xyz789"
    public static String addHMACValue(String message) {
        try {
            String hmac = generateHMAC(message);
            return message + "|HMAC=" + hmac;
        } catch (Exception e) {
            e.printStackTrace();
            return message; // If HMAC generation fails, return original message
        }

    }

    // Generate HMAC for a message
    public static String generateHMAC(String message) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        mac.init(keySpec);
        byte[] hmac = mac.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(hmac);
    }

    // Verify HMAC of received message
    public static boolean verifyHMACValue(String message, String receivedHMAC) throws Exception {
        String expectedHMAC = generateHMAC(message);
        return expectedHMAC.equals(receivedHMAC);
    }

    public static String verifyAndStrip(String message) throws Exception {
        int hmacIndex = message.lastIndexOf("|HMAC=");
        if (hmacIndex == -1)
            return null;
        String content = message.substring(0, hmacIndex);
        String receivedHMAC = message.substring(hmacIndex + 6);
        if (generateHMAC(content).equals(receivedHMAC))
            return content;
        return null;
    }
}