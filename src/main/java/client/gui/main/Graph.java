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

package client.gui.main;

import client.gui.FRecord;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Graph draws the given records. The amount of displayed records is based on the width.
 */
class Graph extends JPanel {
	private final FRecord[] records;
	Graph(FRecord[] records, int width, int height){
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		this.records = records;
	}

	@Override
	protected void paintComponent(Graphics gc) {
		super.paintComponent(gc);

		if (records.length == 0) {
			gc.setFont(new Font(Font.SERIF, Font.PLAIN, 30));
			String mes = "No data available";
			gc.drawString(mes, getWidth() / 2 - gc.getFontMetrics().stringWidth(mes) / 2, getHeight() / 2);
			return;
		}

		gc.setFont(new Font(Font.SERIF, Font.BOLD, 18));

		int barMinWidth = 15;
		int barMinOffset = 10;
		int rightOffset = gc.getFontMetrics().stringWidth("   999 Mbit/s");
		int maxSpeedBarsAmount = (getWidth() - rightOffset + barMinOffset) / (barMinWidth + barMinOffset);

		FRecord[] visibleRecords =
			records.length > maxSpeedBarsAmount ?
				smear(records, (int) Math.ceil((double) records.length / maxSpeedBarsAmount)) :
				records;

		long minSpeed = Arrays.stream(visibleRecords).min(Comparator.comparingLong(FRecord::speed)).get().speed();
		double minSpeedExponent = Math.pow(10, Math.floor(Math.log10(minSpeed)));
		// lowestBar is the largest number that is lower than minSpeed and equals to a natural single-digit number times 10^N where N is a natural number
		// for example for minSpeed 98 lowestBar is 90
		long lowestBar = (long) (Math.floor((double) minSpeed / minSpeedExponent) * minSpeedExponent);

		long maxSpeed = Arrays.stream(visibleRecords).max(Comparator.comparingLong(FRecord::speed)).get().speed();
		double maxSpeedExponent = Math.pow(10, Math.floor(Math.log10(maxSpeed)));
		// highestBar is the smallest number that is more than maxSpeed and equals to a natural single-digit number times 10^N where N is a natural number
		// for example for maxSpeed 537 highestBar is 600
		long highestBar = (long) (Math.ceil((double) maxSpeed / maxSpeedExponent) * maxSpeedExponent);

		double scaleY = (double) getHeight() / highestBar;
		drawSpeedBars(gc, lowestBar, highestBar, scaleY);

		double scaleX = (double) maxSpeedBarsAmount / visibleRecords.length;
		drawRecordBars(gc, visibleRecords, barMinWidth * scaleX, barMinOffset * scaleX, scaleY);
	}

	private void drawSpeedBars(Graphics gc, long lowestBar, long highestBar, double scaleY) {
		gc.setColor(Color.gray);
		FontMetrics fm = gc.getFontMetrics();
		gc.fillRect(0, 0, getWidth(), 4);
		gc.fillRect(0, (int) (getHeight() - lowestBar * scaleY), getWidth(), 4);

		gc.setColor(Color.white);
		String highText = highestBar / 1_000_000 + " Mbit/s";
		gc.drawString(
			highText,
			getWidth() - gc.getFontMetrics().stringWidth(highText),
			fm.getHeight()
		);

		String lowText = lowestBar / 1_000_000 + " Mbit/s";
		gc.drawString(
			lowText,
			getWidth() - gc.getFontMetrics().stringWidth(lowText),
			(int) (getHeight() - lowestBar * scaleY + gc.getFontMetrics().getHeight())
		);
	}

	private void drawRecordBars(Graphics gc, FRecord[] records, double barWidth, double barOffsetWidth, double scaleY) {
		gc.setColor(Color.red);
		for (int recordIndex = 0; recordIndex < records.length; recordIndex++) {
			int height = (int) (records[recordIndex].speed() * scaleY);
			int xPos = (int) (recordIndex * barWidth + recordIndex * barOffsetWidth);
			int yPos = getHeight() - height;
			gc.fillRect(xPos, yPos, (int) barWidth, height);
		}
	}

	private FRecord[] smear(FRecord[] records, int span) {
		FRecord[] smearedRecs = new FRecord[(int) Math.ceil((double) records.length / span)];
		Arrays.fill(smearedRecs, new FRecord(0, 0, ""));
		for (int i = 0; i < records.length; i++) {
			int smearedRecsIndex = i / span;
			if (i < records.length - records.length % span) {
				smearedRecs[smearedRecsIndex] = new FRecord(
					records[i].time(),
					smearedRecs[smearedRecsIndex].speed() + records[i].speed() / span,
					records[i].info()
				);
			} else {
				smearedRecs[smearedRecsIndex] = new FRecord(
					records[i].time(),
					smearedRecs[smearedRecsIndex].speed() + records[i].speed() / (records.length % span),
					records[i].info()
				);
			}
		}

		return smearedRecs;
	}
}
