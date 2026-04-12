package com.smartflow;

import java.util.Random;

public class Utility extends Publisher {
	private static int currentid = 1;
		
	static String topics[] = {"POWER_OUTAGE", "WATER_LEAK", "GAS_LEAK", "GRID_OVERLOAD", "MAINTENANCE_ALERT"};
	
		
	static String power_outage_payloads[] = {
		    "Localized power outage reported in sector 12",
		    "Unexpected blackout — technicians dispatched",
		    "Partial outage affecting residential blocks",
		    "Power interruption due to equipment failure",
		    "Scheduled outage for grid upgrades"
		};

	static String water_leak_payloads[] = {
		    "Water leak detected near main pipeline",
		    "Minor leak reported — maintenance en route",
		    "Significant water loss — pressure reduced temporarily",
		    "Leak affecting nearby buildings — repairs underway",
		    "Underground pipe leak identified — expect delays"
		};

	static String gas_leak_payloads[] = {
		    "Gas leak detected — evacuation recommended",
		    "Minor gas leak — technicians investigating",
		    "Emergency response team deployed for gas leak",
		    "Gas odor reported — safety checks in progress",
		    "Pipeline leak confirmed — area cordoned off"
		};

	static String grid_overload_payloads[] = {
		    "High demand causing grid overload",
		    "Energy consumption spike — load balancing active",
		    "Grid nearing capacity — reduce usage if possible",
		    "Overload warning — backup systems engaged",
		    "Peak load detected — expect temporary fluctuations"
		};

	static String maintenance_alert_payloads[] = {
		    "Routine maintenance scheduled for tonight",
		    "Utility service temporarily unavailable for repairs",
		    "Maintenance crew dispatched to affected area",
		    "System check in progress — minor disruptions expected",
		    "Preventive maintenance underway — service may slow"
		};


	
	Random rand = new Random();
	
	public Utility() {
		super("utility-"+ currentid);
		currentid++;
	}
	
	@Override
	protected String generateTopic() {
		String topic = "UTILITY." + topics[rand.nextInt(topics.length)];
		return topic;
	}

	@Override
	protected String generateContent(String topic) {
		String payload = "";
		
		switch(topic) { //randomize payload based on topic
        case "UTILITY.POWER_OUTAGE":
        	payload = power_outage_payloads[rand.nextInt(power_outage_payloads.length)];
        	break;
        case "UTILITY.WATER_LEAK":
        	payload = water_leak_payloads[rand.nextInt(water_leak_payloads.length)];
        	break;
        case "UTILITY.GAS_LEAK":
        	payload = gas_leak_payloads[rand.nextInt(gas_leak_payloads.length)];
        	break;
        case "UTILITY.GRID_OVERLOAD":
        	payload = grid_overload_payloads[rand.nextInt(grid_overload_payloads.length)];
        	break;
        case "UTILITY.MAINTENANCE_ALERT":
        	payload = maintenance_alert_payloads[rand.nextInt(maintenance_alert_payloads.length)];
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
