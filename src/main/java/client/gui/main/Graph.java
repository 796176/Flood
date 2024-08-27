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
	protected double barWidth;
	protected double gapWidth;
	protected int leftOffset = 0;
	protected int rightOffset;
	protected int topOffset = 0;
	protected int bottomOffset;
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
		int gapMinWidth = 10;
		rightOffset = gc.getFontMetrics().stringWidth("   999 Mbit/s");
		int maxSpeedBarsAmount = (getWidth() - rightOffset + gapMinWidth) / (barMinWidth + gapMinWidth);

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

		visibleRecords = Arrays
			.stream(visibleRecords)
			.map(r -> new FRecord(r.time(), r.speed() - lowestBar, r.info()))
			.toArray(FRecord[]::new);
		double scaleY = (double) getHeight() / (highestBar - lowestBar);
		drawSpeedBars(gc, lowestBar, highestBar);

		double scaleX = (double) maxSpeedBarsAmount / visibleRecords.length;
		barWidth = barMinWidth * scaleX;
		gapWidth = gapMinWidth * scaleX;
		drawRecordBars(gc, visibleRecords, scaleY);
	}

	private void drawSpeedBars(Graphics gc, long lowestBar, long highestBar) {
		FontMetrics fm = gc.getFontMetrics();
		bottomOffset = fm.getHeight();
		int barAmount = 5;
		int barWidth = 4;
		int currentHeight = 0;
		long currentSpeed = highestBar;
		for (int i = 0; i < barAmount; i++) {
			gc.setColor(Color.gray);
			gc.fillRect(0, currentHeight, getWidth(), barWidth);

			gc.setColor(Color.white);
			String text = human(currentSpeed);
			gc.drawString(
				text,
				getWidth() - gc.getFontMetrics().stringWidth(text),
				currentHeight + gc.getFontMetrics().getHeight()
			);

			currentHeight += (getHeight() - barWidth - bottomOffset) / (barAmount - 1);
			currentSpeed -= (highestBar - lowestBar) / (barAmount - 1);
		}
	}

	private void drawRecordBars(Graphics gc, FRecord[] records, double scaleY) {
		gc.setColor(Color.red);
		for (int recordIndex = 0; recordIndex < records.length; recordIndex++) {
			int height = (int) (records[recordIndex].speed() * scaleY);
			int xPos = (int) (recordIndex * barWidth + recordIndex * gapWidth);
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

	private String human(long bits) {
		int siPrefix = 0;
		while (bits >= 1000) {
			bits /= 1000;
			siPrefix++;
		}
		return bits + " " + " KMG".charAt(siPrefix) + "bits/s";
	}
}
