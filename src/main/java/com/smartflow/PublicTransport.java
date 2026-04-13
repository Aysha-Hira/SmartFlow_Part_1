/**
 * Section: 104

 * Group number: 4
 * Student IDs and names: 
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */
package com.smartflow;

import java.util.Random;

public class PublicTransport extends Publisher {
	private static int currentid = 1;
	
	static String topics[] = {"BUS_DELAY", "TRAIN_MAINTENANCE", "METRO_OVERLOAD", "TRAM_BREAKDOWN", "ROUTE_CHANGE"};

	
	static String bus_delay_payloads[] = {
		    "Bus running 10 minutes late due to traffic",
		    "Unexpected delay — next bus arriving shortly",
		    "Heavy congestion causing bus schedule disruption",
		    "Bus delayed due to earlier incident on route",
		    "Minor delay — please expect slower service"
		};


	static String train_maintenance_payloads[] = {
		    "Scheduled track maintenance causing slower service",
		    "Maintenance work — expect extended travel times",
		    "Train frequency reduced due to system checks",
		    "Temporary maintenance on main line",
		    "Routine inspection causing minor delays"
		};


	static String metro_overload_payloads[] = {
		    "High passenger volume — trains operating at full capacity",
		    "Metro overcrowded during peak hours",
		    "Expect delays due to platform congestion",
		    "Heavy load — additional trains being deployed",
		    "Metro system experiencing high demand"
		};


	static String tram_breakdown_payloads[] = {
		    "Tram malfunction — service temporarily halted",
		    "Technical issue causing tram delay",
		    "Tram breakdown reported — maintenance team dispatched",
		    "Service disruption due to tram failure",
		    "Tram stopped unexpectedly — alternative routes advised"
		};


	static String route_change_payloads[] = {
		    "Temporary route change due to construction",
		    "Service rerouted — follow updated signs",
		    "Route adjustment in effect for today",
		    "Detour implemented due to road closure",
		    "Route change — expect different stop sequence"
		};


	
	Random rand = new Random();
	
	public PublicTransport() {
		super("public-transport-" + currentid);
		currentid++;
		
	}

	@Override
	protected String generateTopic() {
		String topic = "PUBLIC_TRANSPORT." + topics[rand.nextInt(topics.length)];
		return topic;
	}

	@Override
	protected String generateContent(String topic) {
		String payload = "";
		
		switch(topic) { //randomize payload based on topic
        case "PUBLIC_TRANSPORT.BUS_DELAY":
        	payload = bus_delay_payloads[rand.nextInt(bus_delay_payloads.length)];
        	break;
        case "PUBLIC_TRANSPORT.TRAIN_MAINTENANCE":
        	payload = train_maintenance_payloads[rand.nextInt(train_maintenance_payloads.length)];
        	break;
        case "PUBLIC_TRANSPORT.METRO_OVERLOAD":
        	payload = metro_overload_payloads[rand.nextInt(metro_overload_payloads.length)];
        	break;
        case "PUBLIC_TRANSPORT.TRAM_BREAKDOWN":
        	payload = tram_breakdown_payloads[rand.nextInt(tram_breakdown_payloads.length)];
        	break;
        case "PUBLIC_TRANSPORT.ROUTE_CHANGE":
        	payload = route_change_payloads[rand.nextInt(route_change_payloads.length)];
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
