package com.smartflow;

import java.util.Random;

public class Traffic extends Publisher {
	private static int currentid = 0;
	
	static String topics[] = {"ACCIDENT", "HAZARD", "CONGESTION", "ROAD_CLOSURE", "EMERGENCY_VEHICLES"};
	
	static String accident_payloads[];
	static String hazard_payloads[];
	static String congestion_payloads[];
	static String road_closure_payloads[];
	static String emergency_vehicles_payloads[];
	
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
		String[] locations = { "Downtown", "Highway 101", "Main Street", "5th Avenue", "Broadway" }; // example
																										// locations
		return locations[rand.nextInt(locations.length)];
	}

}
