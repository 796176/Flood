package server.backend;

public class FloodWebServerTests extends WebServerTests {
	@Override
	WebServer getInstance() {
		return new FloodWebServer();
	}
}
