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

import global.RequestType;
import org.json.JSONObject;
import server.bandwidth.BandwidthStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HTTPResponse extends Thread {
	private final Socket clientSocket;
	private final BandwidthStatus bs;

	HTTPResponse(BandwidthStatus bandwidthStatus, Socket socket) {
		assert bandwidthStatus != null && socket != null;

		bs = bandwidthStatus;
		clientSocket = socket;
	}


	@Override
	public void run() {
		String response = "";
		try {
			InputStream cis = clientSocket.getInputStream();
			StringBuilder mes = new StringBuilder();
			byte[] buffer = new byte[1024 * 8];
			int receivedBytes;
			do {
				receivedBytes = cis.read(buffer);
				mes.append(new String(buffer, 0, receivedBytes));
			} while (receivedBytes == buffer.length);

			String header = mes.substring(0, mes.indexOf("\n"));
			RequestType rt = RequestType.valueOf(header.substring(header.indexOf("/") + 1, header.indexOf("?")));
			String params = header.substring(header.indexOf("?") + 1, header.indexOf(" ", header.indexOf("?")));
			response = buildResponse(rt, params);
		} catch (IOException exception) {
			response = DefaultHTTPResponses._500;
		} catch (Exception exception) {
			response = DefaultHTTPResponses._400;
		} finally {
			try {
				clientSocket.getOutputStream().write(response.getBytes());
			} catch (IOException ignored) {}
		}
	}

	private String buildResponse(RequestType rq, String params) throws IOException {
			StringBuilder response = new StringBuilder(
				"""
					HTTP/1.1 200 OK
					Content-Type: text/html
					"""
			);

			switch (rq) {
				case retrieve_last -> {
					int unitAmount = Integer.parseInt(retrieve(params, "t=\\d+").substring("t=".length()));
					int timeUnit = Integer.parseInt(retrieve(params, "u=\\d+").substring("u=".length()));

					GregorianCalendar calendar = new GregorianCalendar();
					calendar.add(timeUnit, -unitAmount);
					LinkedList<String[]> records = bs.collect(calendar.toInstant().toEpochMilli());
					String responseBody = new JSONObject().put("records", records).toString();
					response.append("Content-Length: " + responseBody.length() + "\n\n" + responseBody);
				}
			}

			return response.toString();
	}

	private String retrieve(String input, String regex) {
		Matcher matcher = Pattern.compile(regex).matcher(input);
		matcher.find();
		return matcher.group();
	}
}
