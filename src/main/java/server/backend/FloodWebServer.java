/* Flood is a network inspection tool
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
 */

package server.backend;

import global.SettingLoader;
import server.bandwidth.BandwidthStatus;
import server.bandwidth.DefaultBandwidthStatus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * FloodWebServer is a default implementation of {@link WebServer} interface.
 * FloodWebServer utilizes multi-threading and performs connection listening and packet processing and sending in separate threads.
 * The maximum number of available threads is acquired through {@link SettingLoader#MAX_THREADS}.
 * {@link DefaultBandwidthStatus} is the default instance of {@link BandwidthStatus} FloodWebServer uses.
 * The default port is acquired through {@link SettingLoader#PORT}.
 */
public class FloodWebServer extends Thread implements WebServer {
	private boolean interrupted = false;
	private int port;
	private final HTTPResponse[] threads;
	private BandwidthStatus bs;

	public FloodWebServer() {
		bs = new DefaultBandwidthStatus();
		port = Integer.parseInt(SettingLoader.getValue(SettingLoader.Parameter.PORT));
		threads = new HTTPResponse[Integer.parseInt(SettingLoader.getValue(SettingLoader.Parameter.MAX_THREADS))];
		Arrays.fill(threads, new HTTPResponse(bs, new Socket()));
	}

	@Override
	public void run() {
		try (ServerSocket socket = new ServerSocket(port)) {
			while (!interrupted) {
				Socket client = socket.accept();
				processSocket(client);
			}
		} catch (IOException ignored) { }
	}

	@Override
	public void interrupt() {
		interrupted = true;
		try {
			for (HTTPResponse t : threads) {
				t.join(1000);
			}
			super.interrupt();
		} catch (InterruptedException ignored) {}
	}

	@Override
	public void setPort(int port) {
		assert port >= 0 && port <= Math.pow(2, 16) - 1;
		this.port = port;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public BandwidthStatus getBandwidthStatus() {
		return bs;
	}

	@Override
	public void setBandwidthStatus(BandwidthStatus bandwidthStatus) {
		assert  bandwidthStatus != null;

		bs = bandwidthStatus;
	}

	private int getAvailableThreadIndex() {
		long waitTime = 500;
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < waitTime) {
			for (int index = 0; index < threads.length; index++) {
				if (!threads[index].isAlive()) return index;
			}
		}
		return -1;
	}

	private void processSocket(Socket socket) {
		int availableThreadIndex = getAvailableThreadIndex();
		if (availableThreadIndex < 0) {
			try {
				socket.getOutputStream().write(DefaultHTTPResponses._503.getBytes());
			} catch (IOException ignored) {}
		} else {
			threads[availableThreadIndex] = new HTTPResponse(bs, socket);
			threads[availableThreadIndex].start();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		FloodWebServer floodWebServer = new FloodWebServer();
		floodWebServer.start();
		while (true) {}
	}
}
