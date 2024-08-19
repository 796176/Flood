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

package server.bandwidth;

import global.SettingLoader;

import java.util.Timer;

/**
 * DefaultFloodScheduler is a default implementation of {@link FloodScheduler}.
 * The pause time is acquired through {@link SettingLoader#IDLE_TIME}.
 * The default {@link DownloadTest} instance is chosen according to a protocol, which is acquired though {@link SettingLoader#PROTOCOL}.
 */
public class DefaultFloodScheduler implements FloodScheduler {
	private final Timer timer = new Timer();
	private long delay;
	private DownloadTest dt;
	public DefaultFloodScheduler() {
		delay = Long.parseLong(SettingLoader.getValue(SettingLoader.Parameter.IDLE_TIME));
		dt = switch (SettingLoader.getValue(SettingLoader.Parameter.PROTOCOL)) {
			case "http" -> new HTTPDownloadTest();
			default -> null;
		};
	}
	@Override
	public void execute() {
		timer.scheduleAtFixedRate(dt.toTimerTask(), 0, delay);
	}

	@Override
	public void stopExecution() {
		timer.cancel();
	}

	@Override
	public void setDelay(long delay) {
		assert delay >= 0;
		this.delay = delay;
	}

	@Override
	public long getDelay() {
		return delay;
	}

	@Override
	public DownloadTest getDownloadTest() {
		return dt;
	}

	@Override
	public void setDownloadTest(DownloadTest downloadTest) {
		assert downloadTest != null;

		dt = downloadTest;
	}
}
