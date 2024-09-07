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
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
			try {
				URI uri = URI.create(
					SettingManipulator.getValue(SettingManipulator.Parameter.REMOTE_URL).orElse("") +
					"/" +
					requestType +
					parameters
				);
				HttpClient client = HttpClient.newBuilder().build();
				HttpRequest request =
					HttpRequest.newBuilder().uri(uri).timeout(Duration.of(15, ChronoUnit.SECONDS)).build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				client.close();
				if (response.statusCode() != 200)
					throw new ConnectException("Failed to fetch data. HTTP code: " + response.statusCode());

				FRecord[] records = parseResponseBody(response.body());
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
	}
}
