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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		try {
			URI uri = URI.create(
				SettingManipulator.getValue(SettingManipulator.Parameter.REMOTE_URL).orElse("") + "/" + rt + params
			);

			HttpClient client = HttpClient.newBuilder().build();
			HttpRequest request =
				HttpRequest.newBuilder().uri(uri).timeout(Duration.of(15, ChronoUnit.SECONDS)).build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				JOptionPane.showMessageDialog(
					this,
					"Server status: " + response.statusCode(),
					"NetworkException",
					JOptionPane.ERROR_MESSAGE
				);
			}
			JSONObject object = new JSONObject(response.body());
			JSONArray records = object.getJSONArray("records");
			ArrayList<FRecord> recordsArrayList = new ArrayList<>(1024 * 10);
			for (int i = 0; i < records.length(); i++) {
				List<Object> recordList = ((JSONArray) records.get(i)).toList();
				recordsArrayList.add(new FRecord(
					Long.parseLong(recordList.get(0).toString()),
					Long.parseLong(recordList.get(1).toString()),
					recordList.get(2).toString()
				));
			}
			FRecord[] recordsArray = recordsArrayList.toArray(new FRecord[]{});
			Arrays.sort(recordsArray);
			parent.setRecords(recordsArray);
		} catch (IOException exception) {
			JOptionPane.showMessageDialog(
				this,
				exception.getMessage(),
				"IOException",
				JOptionPane.ERROR_MESSAGE
			);
		} catch (InterruptedException exception) {
			JOptionPane.showMessageDialog(
				this,
				exception.getMessage(),
				"InterruptedException",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}
}
