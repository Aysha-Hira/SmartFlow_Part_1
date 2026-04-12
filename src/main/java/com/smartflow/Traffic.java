package com.smartflow;

import java.util.Random;

public class Traffic extends Publisher {
	private static int currentid = 0;
	
	static String topics[] = {"ACCIDENT", "HAZARD", "CONGESTION", "ROAD_CLOSURE", "EMERGENCY_VEHICLES"};
	
	static String accident_payloads[] = {
		    "Minor rear-end collision reported",
		    "Two-car accident blocking right lane",
		    "Multi-vehicle crash causing delays",
		    "Accident near intersection, expect slowdowns",
		    "Vehicle rollover reported, emergency crews on site"
		};
	static String hazard_payloads[] = {
		    "Debris detected on roadway",
		    "Oil spill causing slippery conditions",
		    "Fallen object blocking lane",
		    "Pothole hazard reported",
		    "Construction materials scattered on road"
		};
	static String congestion_payloads[] = {
		    "Heavy traffic due to peak hours",
		    "Slow-moving traffic ahead",
		    "Severe congestion near city center",
		    "Traffic buildup reported on main highway",
		    "Delays expected due to high volume"
		};
	static String road_closure_payloads[] = {
		    "Road closed for maintenance work",
		    "Temporary closure due to flooding",
		    "Lane closure for construction",
		    "Road blocked due to earlier accident",
		    "Detour in place — follow signs"
		};
	static String emergency_vehicles_payloads[] = {
		    "Ambulance en route — clear the way",
		    "Fire truck responding to incident",
		    "Police vehicles approaching area",
		    "Emergency convoy passing through",
		    "Rescue team deployed to nearby location"
		};
	
	Random rand = new Random();

	public Traffic() {
		super("traffic-" + currentid);
		currentid++;
	}

	@Override
	protected String generateTopic() {
		String topic = "TRAFFIC." + topics[rand.nextInt(topics.length)];
		return topic;
	}

	@Override
	protected String generateContent(String topic) {
		String payload = "";

		switch (topic) { // randomize payload based on topic
			case "TRAFFIC.ACCIDENT":
				payload = accident_payloads[rand.nextInt(accident_payloads.length)];
				break;
			case "TRAFFIC.HAZARD":
				payload = hazard_payloads[rand.nextInt(hazard_payloads.length)];
				break;
			case "TRAFFIC.CONGESTION":
				payload = congestion_payloads[rand.nextInt(congestion_payloads.length)];
				break;
			case "TRAFFIC.ROAD_CLOSURE":
				payload = road_closure_payloads[rand.nextInt(road_closure_payloads.length)];
				break;
			case "TRAFFIC.EMERGENCY_VEHICLES":
				payload = emergency_vehicles_payloads[rand.nextInt(emergency_vehicles_payloads.length)];
				break;
		}

		return payload;
	}

	@Override
	protected String getLocation() {
		String[] locations = { "Downtown", "Highway 101", "Main Street", "5th Avenue", "Broadway" };
		return locations[rand.nextInt(locations.length)];
	}

}
