package server;

import server.backend.FloodWebServer;
import server.bandwidth.DefaultFloodScheduler;

public class FloodServer {
	public static void main(String[] args) {
		DefaultFloodScheduler defaultFloodScheduler = new DefaultFloodScheduler();
		defaultFloodScheduler.execute();
		FloodWebServer floodWebServer = new FloodWebServer();
		floodWebServer.start();
		while (true);
	}
}
