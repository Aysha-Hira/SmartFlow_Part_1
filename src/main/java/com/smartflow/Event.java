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
 * eventType - type of the event (flood, etc)
 * location - location where the event is
 * startTime - starting time of the event
 * updateTime - time when updating details of the event
 * status - if the event has been resolved or pending
 * 
 */
public class Event {
    private int id;
    private String eventType;
    private String location;
    private long startTime;
    private long updateTime;
    private String status = "Pending"; // default value

    // New Event
    public Event(int id, String eventType, String location) {
        this.id = id;
        this.eventType = eventType;
        this.location = location;
        this.startTime = System.currentTimeMillis();
        this.updateTime = startTime;
    }

    public int getID() {
        return this.id;
    }

    public String getEventType() {
        return this.eventType;
    }

    public long getStartTime() {
        return this.startTime;
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
                "EventType:" + this.eventType +
                "Location: " + this.location +
                "Started Time: " + this.startTime +
                "Last updated Time: " + this.updateTime;
    }
}