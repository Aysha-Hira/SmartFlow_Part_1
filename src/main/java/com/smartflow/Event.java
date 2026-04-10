/**
 * Section: 104
 * Group number: 4
 * Student IDs and names: 
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */

package com.smartflow;

/**
 * Event Class
 * 
 * id - Id of the event
 * topic - type of the event (flood, etc)
 * location - location where the event is
 * message - desscription of the event
 * startTime - starting time of the event
 * updateTime - time when updating details of the event
 * status - if the event has been resolved or pending
 * 
 */
public class Event {
    private int id;
    private String topic;
    private String location;
    private String message;
    private long publishTime;
    private long updateTime;
    private String status = "Pending"; // default value

    // New Event
    public Event(int id, String topic, String location, String message) {
        this.id = id;
        this.topic = topic;
        this.location = location;
        this.message = message;
        this.publishTime = System.currentTimeMillis();
        this.updateTime = publishTime;
    }

    public int getID() {
        return this.id;
    }

    public String getTopic() {
        return this.topic;
    }

    public String getMessage() {
        return this.message;
    }

    public long getPublishTime() {
        return this.publishTime;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public String getLocation() {
        return this.location;
    }

    public String getStatus() {
        return this.status;
    }

    public String setAsPending() {
        return this.status = "Pending";
    }

    public String setAsResolved() {
        return this.status = "Resolved";
    }

    public void updateTime() {
        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Event Details: " +
                "Event Id: " + this.id +
                "Topic:" + this.topic +
                "Location: " + this.location +
                "Started Time: " + this.publishTime +
                "Last updated Time: " + this.updateTime;
    }

    // Convert event to string for sending over network
    public String serialize() {
        return id + "|"
                + topic + "|"
                + location + "|"
                + message + "|"
                + status + "|"
                + publishTime + "|"
                + updateTime;
    }

    // Rebuild event from received string
    public static Event deserialize(String data) {
        String[] parts = data.split("\\|");
        Event event = new Event(
                Integer.parseInt(parts[0]), // id
                parts[1], // topic
                parts[2], // location
                parts[3] // message
        );
        event.status = parts[4];
        event.publishTime = Long.parseLong(parts[5]);
        event.updateTime = Long.parseLong(parts[6]);
        return event;
    }
}