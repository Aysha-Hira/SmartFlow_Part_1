/**
 * Section: 104
 * Group number: 4
 * Student IDs and names: 
 * Laisa Sanjida Isra: 1089635
 * Fatima Syed Wasti: 1095190
 * Aysha Hira: 1088000
 */

package com.smartflow;

import java.net.SocketException;
import java.net.UnknownHostException;

public class SimulationRunner{
	public static void main(String args[]) throws SocketException, UnknownHostException {
		Traffic t1 = new Traffic();
		Weather w1 = new Weather();
		Utility u1 = new Utility();
		PublicTransport p1 = new PublicTransport();
		
		t1.connect();
		w1.connect();
		u1.connect();
		p1.connect();
		
		t1.startPublishingLoop(1000, 10);
		w1.startPublishingLoop(2000, 10);
		u1.startPublishingLoop(4000, 10);
		p1.startPublishingLoop(3000, 10);
		
		//TODO: not added anything related to subscribers and tracking latency/performance
	}
}