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

package client.gui.update;

import client.SettingManipulator;
import client.gui.FRecord;
import client.gui.main.MainFrame;
import global.RequestType;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * UpdateDialog provides the GUI interface to get records from Flood Backend.
 */
public class UpdateDialog extends JDialog{
	private final MainFrame parent;
	public UpdateDialog(MainFrame parent) {
		super(parent, "Update", true);
		assert parent != null;
		this.parent = parent;

		setSize(600, 500);
		int x = parent.getLocation().x + (parent.getWidth() - getWidth()) / 2;
		int y = parent.getLocation().y + (parent.getHeight() - getHeight()) / 2;
		setLocation(x, y);
		setContentPane(new UpdateContentPane(this));
		setVisible(true);
	}

	/**
	 * Builds an HTTP request, receives the records from the response, and updates the records in {@link MainFrame}.
	 * @param rt type of HTTP request.
	 * @param params HTTP parameters
	 */
	void updateRequest(RequestType rt, String params) {
		assert rt != null && params != null;

		CancelDialog cancelDialog = new CancelDialog(parent);
		GraphUpdater graphUpdater = new GraphUpdater(rt, params, parent, cancelDialog);
		cancelDialog.setRunnableThread(graphUpdater);
		cancelDialog.setVisible(true);
		setVisible(false);
		graphUpdater.start();
	}

	private static class GraphUpdater extends Thread {
		private final RequestType requestType;
		private final String parameters;
		private final MainFrame mf;
		private final CancelDialog cd;
		private GraphUpdater(RequestType rt, String params, MainFrame mainFrame, CancelDialog cancelDialog) {
			assert rt != null && params != null;

			requestType = rt;
			parameters = params;
			mf = mainFrame;
			cd = cancelDialog;
		}

		@Override
		public void run() {
			HttpURLConnection connection = null;
			try {
				URL url = URI.create(
					SettingManipulator.getValue(SettingManipulator.Parameter.REMOTE_URL).orElse("") +
					"/" +
					requestType +
					parameters
				).toURL();
				connection = buildConnection(url);
				try (OutputStream os = connection.getOutputStream()) {
					os.write(buildGetRequest("/" + requestType + parameters).getBytes());
				}
				int bodySize = connection.getContentLength();
				if (bodySize == -1)
					throw new ConnectException("Failed to establish connection");
				byte[] responseBody = new byte[bodySize];
				try (InputStream is = connection.getInputStream()) {
					is.read(responseBody);
				}
				String responseMes = new String(responseBody);

				int statusCode = connection.getResponseCode();
				if (statusCode != 200)
					throw new ConnectException("Failed to fetch data. HTTP code: " + statusCode);

				FRecord[] records = parseResponseBody(responseMes);
				SwingUtilities.invokeLater(() -> {
					mf.setRecords(records);
					cd.setVisible(false);
				});
			} catch (Exception exception) {
				cd.setVisible(false);
				JOptionPane.showMessageDialog(
					mf,
					exception.getMessage(),
					exception.getClass().getSimpleName(),
					JOptionPane.ERROR_MESSAGE
				);
			}
			finally {
				if (connection != null) connection.disconnect();
			}
		}

		private FRecord[] parseResponseBody(String body) {
			JSONArray jsonArray = new JSONObject(body).getJSONArray("records");
			Function<JSONArray, FRecord> function = jsonArr ->
				new FRecord(
					jsonArr.getLong(0),
					jsonArr.getLong(1),
					jsonArr.getString(2)
				);
			Stream.Builder<FRecord> stream = Stream.builder();
			jsonArray.forEach(object -> stream.accept(function.apply((JSONArray) object)));
			return stream.build().sorted().toArray(FRecord[]::new);
		}

		private HttpURLConnection buildConnection(URL url) throws IOException {
			URLConnection urlConnection;
			Optional<String> proxyType = SettingManipulator.getValue(SettingManipulator.Parameter.PROXY_PROTOCOL);
			if (proxyType.isPresent()) {
				SocketAddress address = new InetSocketAddress(
					SettingManipulator.getValue(SettingManipulator.Parameter.PROXY_URL).get(),
					Integer.parseInt(SettingManipulator.getValue(SettingManipulator.Parameter.PROXY_PORT).get())
				);
				Proxy proxy = new Proxy(Proxy.Type.valueOf(proxyType.get()), address);
				urlConnection = url.openConnection(proxy);
			} else {
				urlConnection = url.openConnection();
			}
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setConnectTimeout(15_000);
			urlConnection.connect();

			return (HttpURLConnection) urlConnection;
		}

		private String buildGetRequest(String request) {
			return """
				GET %s HTTP/1.1
				""".formatted(request);
		}
	}
}
