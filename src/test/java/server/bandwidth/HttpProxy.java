/*
 * Flood is a network inspection tool
 * Copyright (C) 2024 Yegore Vlussove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package server.bandwidth;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpProxy extends Thread {
	private final int port;
	private boolean interrupted = false;
	private int acceptCount = 0;

	public HttpProxy(int port) {
		assert port >= 0 && port < Math.pow(2, 16);

		this.port = port;
	}

	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			serverSocket.setSoTimeout(100);
			while (!interrupted) {
				try (Socket client = serverSocket.accept()) {
					acceptCount++;
					byte[] mesBuffer = new byte[1024 * 10];
					int size = client.getInputStream().read(mesBuffer);
					String message = new String(mesBuffer, 0, size).replace("\r\n", "\n");
					String requestLine = message.substring(0, message.indexOf('\n'));
					URI uri = URI.create(requestLine.split(" ")[1]);

					HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
					try (HttpClient httpClient = HttpClient.newBuilder().build()) {
						HttpResponse<InputStream> httpResponse =
							httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
						try (InputStream is = httpResponse.body()) {
							is.transferTo(client.getOutputStream());
						}
					}
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
