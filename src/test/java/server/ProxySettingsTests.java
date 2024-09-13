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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxySettingsTests {
	Path newConfigFile;
	String oldConfigFile;
	Field configField;

	@BeforeEach
	void beforeEach() throws NoSuchFieldException, IllegalAccessException, IOException {
		newConfigFile =  Files.createTempFile(null, null);

		configField = SettingLoader.class.getDeclaredField("configPath");
		configField.trySetAccessible();
		oldConfigFile = (String) configField.get(null);
		configField.set(null, newConfigFile.toString());
	}

	@AfterEach
	void afterEach() throws IOException, IllegalAccessException {
		Files.delete(newConfigFile);

		configField.set(null, oldConfigFile);
	}

	@Test
	void getDefaultProxy() throws IOException {
		assertEquals(Proxy.NO_PROXY, ProxySettings.getProxy(), "The default proxy isn't a direct connection");

		try (FileWriter fw = new FileWriter(newConfigFile.toString())) {
			fw.write(SettingLoader.PROXY_PROTOCOL + " FINGER\n");
			fw.write(SettingLoader.PROXY_URL + " finger.example.com\n");
			fw.write(SettingLoader.PROXY_PORT + " 79");
		}
		assertEquals(Proxy.NO_PROXY, ProxySettings.getProxy(), "Errors in the config aren't handled correctly");
	}

	@Test
	void getModifiedProxy() throws IOException{
		try (FileWriter fw = new FileWriter(newConfigFile.toString())) {
			fw.write(SettingLoader.PROXY_PROTOCOL + " SOCKS\n");
			fw.write(SettingLoader.PROXY_URL + " socks.example.com\n");
			fw.write(SettingLoader.PROXY_PORT + " 1080");
		}
		SocketAddress address = new InetSocketAddress("socks.example.com", 1080);
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, address);
		assertEquals(proxy, ProxySettings.getProxy());
	}
}
