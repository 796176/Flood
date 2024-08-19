package server.bandwidth;

public class DefaultFloodSchedulerTests extends FloodSchedulerTests {
	@Override
	FloodScheduler getInstance() {
		return new DefaultFloodScheduler();
	}
}
