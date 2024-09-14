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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
	private final ExecutorService threadPool;
	private BandwidthStatus bs;

	public FloodWebServer() {
		bs = new DefaultBandwidthStatus();
		port = Integer.parseInt(SettingLoader.getValue(SettingLoader.Parameter.PORT));
		int availableThreadAmount = Integer.parseInt(SettingLoader.getValue(SettingLoader.Parameter.MAX_THREADS));
		threadPool = Executors.newFixedThreadPool(availableThreadAmount);
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
		threadPool.shutdownNow();
		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
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

	private void processSocket(Socket socket) {
		threadPool.execute(new HTTPResponse(bs, socket));
	}

	public static void main(String[] args) throws InterruptedException {
		FloodWebServer floodWebServer = new FloodWebServer();
		floodWebServer.start();
		while (true) {}
	}
}
