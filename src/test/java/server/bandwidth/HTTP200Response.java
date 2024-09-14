package server.bandwidth;

import java.io.IOException;
import java.net.*;

public class HTTP200Response extends Thread{
	private final int port;
	private final String payload;
	private boolean interrupted = false;
	private int acceptCount = 0;

	public HTTP200Response(int port, String data){
		assert port >= 0 && port <= Math.pow(2, 16) - 1;

		this.port = port;
		payload = data == null ? "" : data;

	}

	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			serverSocket.setSoTimeout(100);
			while (!interrupted) {
				try (Socket clientSocket = serverSocket.accept()) {
					acceptCount++;
					String mes =
						"HTTP/1.1 200 OK\n" +
							"Content-Length: " + payload.length() + "\n" +
							"\n" +
							payload;
					clientSocket.getOutputStream().write(mes.getBytes());
				} catch (Exception ignored) {}
			}
		} catch (IOException ignored) {}
	}

	@Override
	public void interrupt() {
		interrupted = true;
	}

	public int getAcceptedConnections() {
		return acceptCount;
	}
}
