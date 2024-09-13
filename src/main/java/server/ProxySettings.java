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

package server;

import global.SettingLoader;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

public class ProxySettings {
	static public Proxy getProxy() {
		try {
			SocketAddress address = new InetSocketAddress(
				SettingLoader.getValue(SettingLoader.PROXY_URL).get(),
				Integer.parseInt(SettingLoader.getValue(SettingLoader.PROXY_PORT).get())
			);
			return new Proxy(Proxy.Type.valueOf(SettingLoader.getValue(SettingLoader.PROXY_PROTOCOL).get()), address);
		} catch (Exception e) {
			return Proxy.NO_PROXY;
		}
	}
}
