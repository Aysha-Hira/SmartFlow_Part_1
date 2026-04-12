package com.smartflow;

import java.util.Random;

public class Weather extends Publisher {
	private static int currentid = 1;
	
	static String topics[] = {"RAIN", "STORM", "HURRICANE", "EARTHQUAKE", "UV_DANGER"};
	
	static String rain_payloads[] = {
		    "Light rain expected throughout the area",
		    "Moderate rainfall causing reduced visibility",
		    "Heavy rain — roads may become slippery",
		    "Intermittent showers reported across the city",
		    "Rainfall increasing — drive with caution"
		};

	static String storm_payloads[] = {
		    "Thunderstorm approaching — strong winds expected",
		    "Severe storm warning issued for the region",
		    "Lightning activity detected nearby",
		    "Heavy storm causing localized flooding",
		    "Storm system moving rapidly — stay indoors"
		};

	static String hurricane_payloads[] = {
		    "Hurricane alert — high wind speeds expected",
		    "Evacuation recommended in coastal areas",
		    "Hurricane approaching — secure outdoor items",
		    "Storm surge risk increasing due to hurricane",
		    "Hurricane conditions intensifying — stay updated"
		};

	static String earthquake_payloads[] = {
		    "Minor tremor detected — no damage reported",
		    "Moderate earthquake felt across the region",
		    "Seismic activity increasing — stay alert",
		    "Earthquake reported — structural checks advised",
		    "Aftershocks expected following earlier quake"
		};

	static String uv_danger_payloads[] = {
		    "High UV index — wear sunscreen",
		    "Extreme UV levels — limit outdoor exposure",
		    "UV alert — protective clothing recommended",
		    "Very high UV radiation detected",
		    "UV danger level rising — stay hydrated and protected"
		};

	
	Random rand = new Random();
	
	public Weather() {
		super("weather-" + currentid);
		currentid++;
		
	}

	@Override
	protected String generateTopic() {
		String topic = "WEATHER." + topics[rand.nextInt(topics.length)];
		return topic;
	}

	@Override
	protected String generateContent(String topic) {
		String payload = "";
		
		switch(topic) { //randomize payload based on topic
        case "WEATHER.RAIN":
        	payload = rain_payloads[rand.nextInt(rain_payloads.length)];
        	break;
        case "WEATHER.STORM":
        	payload = storm_payloads[rand.nextInt(storm_payloads.length)];
        	break;
        case "WEATHER.HURRICANE":
        	payload = hurricane_payloads[rand.nextInt(hurricane_payloads.length)];
        	break;
        case "WEATHER.EARTHQUAKE":
        	payload = earthquake_payloads[rand.nextInt(earthquake_payloads.length)];
        	break;
        case "WEATHER.UV_DANGER":
        	payload = uv_danger_payloads[rand.nextInt(uv_danger_payloads.length)];
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
