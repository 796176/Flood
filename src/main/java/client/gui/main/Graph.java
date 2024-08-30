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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Graph draws the given records. The amount of displayed records is based on the width.
 */
class Graph extends JPanel {
	/**
	 * Stores the information about the bar shown on the screen.
	 * @param time time to be shown when the bar is pointed at
	 * @param speed speed to be shown when the bar is pointed at
	 * @param coord coordinates of the shown bar
	 * @param info addition information to be shown when the bar is pointed at
	 */
	protected record Bar(String time, String speed, Rectangle coord, String info) {}

	private final FRecord[] records;
	private final Color barColor = Color.red;
	private final Color barColorBright = new Color(
		Math.min(255, barColor.getRed() + 64),
		Math.min(255, barColor.getGreen() + 64),
		Math.min(255, barColor.getBlue() + 64)
	);

	/**
	 * Stores the information about the bars in the same order they are shown.
	 */
	protected Bar[] shownBars;
	/**
	 * The width of the bar.
	 */
	protected double barWidth;
	/**
	 * The width of the space between bars.
	 */
	protected double gapWidth;
	/**
	 * The starting position of the first bar.
	 */
	protected int leftOffset = 0;
	/**
	 * The space on the right reserved for displaying the speed labels.
	 */
	protected int rightOffset;
	/**
	 * Reserved.
	 */
	protected int topOffset = 0;
	/**
	 * The minimum height of the bar which is equals to the height of the lowest speed label.
	 */
	protected int bottomOffset;

	/**
	 * Creates an instance of Graph.
	 * @param records array of records to display on the graph. The amount of displayed records depends on the size of the graph.
	 *                The height precision of the displayed records isn't guaranteed.
	 * @param width width of the graph
	 * @param height height of the graph
	 */
	Graph(FRecord[] records, int width, int height){
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		this.records = records;
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Graphics gc = Graph.this.getGraphics();
				paintComponent(gc);
				if (isBar(e.getX(), e.getY())) {
					Rectangle barBorders = getBarBorders(e.getX(), e.getY());
					gc.setColor(barColorBright);
					gc.fillRect(barBorders.x, barBorders.y, barBorders.width, barBorders.height);

					Bar bar = shownBars[getBarIndex(e.getX(), e.getY())];
					showHint(bar, gc, e.getX(), e.getY());
				}
			}
		});
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
		bottomOffset = gc.getFontMetrics().getHeight();
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

		drawSpeedBars(gc, lowestBar, highestBar);

		double scaleY = (double) getHeight() / (highestBar - lowestBar);
		double scaleX = (double) maxSpeedBarsAmount / visibleRecords.length;

		shownBars = new Bar[visibleRecords.length];
		barWidth = barMinWidth * scaleX;
		gapWidth = gapMinWidth * scaleX;
		for (int index = 0; index < visibleRecords.length; index++) {
			FRecord record = visibleRecords[index];
			int x = (int) ((barWidth + gapWidth) * index);
			int barHeight = (int) ((record.speed() - lowestBar ) * scaleY) + bottomOffset;
			int y = getHeight() - barHeight;

			Rectangle coordinates = new Rectangle(x, y, (int) barWidth, barHeight);
			String time = SimpleDateFormat
				.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
				.format(new Date(record.time()));
			String speedHuman = human(record.speed());

			shownBars[index] = new Bar(time, speedHuman, coordinates, record.info());
		}
		drawRecordBars(gc, shownBars);
	}

	private void drawSpeedBars(Graphics gc, long lowestBar, long highestBar) {
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

	private void drawRecordBars(Graphics gc, Bar[] bars) {
		gc.setColor(barColor);
		for (Bar b: bars) {
			gc.fillRect(b.coord().x, b.coord().y, b.coord().width, b.coord().height);
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

	/**
	 * Returns true if the specified coordinates points on the bar, otherwise returns false.
	 * @param x horizontal position from left to right
	 * @param y vertical position from up to down
	 * @return true if the specified coordinates points on the bar or false otherwise
	 */
	protected boolean isBar(int x, int y) {
		if (x < leftOffset || x > getWidth() - rightOffset) return false;

		return (double) x % (barWidth + gapWidth + leftOffset) <= barWidth &&
			y >= shownBars[(int) Math.floor((double) x / (barWidth + gapWidth + leftOffset))].coord().y;
	}

	/**
	 * Returns the index of the pointed at bar in {@link Graph#shownBars}.
	 * If the coordinates don't point at any bar, the method returns a negative value.
	 * @param x horizontal position from left to right
	 * @param y vertical position from up to down
	 * @return index of the pointed at bar
	 */
	protected int getBarIndex(int x, int y) {
		if (!isBar(x, y)) return -1;

		return (int) Math.floor((double) x / (barWidth + gapWidth + leftOffset));
	}

	/**
	 * Returns the coordinates of the pointed at bar.
	 * If the coordinates don't point at any bar, the method returns null.
	 * @param x horizontal position from left to right
	 * @param y vertical position from up to down
	 * @return coordinates of the pointed at bar
	 */
	protected Rectangle getBarBorders(int x, int y) {
		if (!isBar(x, y)) return null;

		return shownBars[getBarIndex(x, y)].coord();
	}

	private String human(long bits) {
		int siPrefix = 0;
		while (bits >= 1000) {
			bits /= 1000;
			siPrefix++;
		}
		return bits + " " + " KMG".charAt(siPrefix) + "bits/s";
	}

	private void showHint(Bar bar, Graphics gc, int x, int y) {
		String[] lines = new String[] {
			bar.time(),
			bar.speed(),
			bar.info()
		};

		FontMetrics fm = gc.getFontMetrics();
		int padH = (int) Arrays.stream(lines).filter(l -> !l.isEmpty()).count() * gc.getFontMetrics().getHeight();
		int padW = Arrays.stream(lines).mapToInt(fm::stringWidth).max().getAsInt();
		gc.setColor(new Color(0, 0, 0, 170));
		gc.fillRect(x, y - padH, padW, padH);

		int lineY = y + gc.getFontMetrics().getHeight() - gc.getFontMetrics().getDescent() - padH;
		gc.setColor(Color.white);
		for (String line: lines) {
			if (line.isEmpty()) continue;
			gc.drawString(line, x, lineY);
			lineY += gc.getFontMetrics().getHeight();
		}
	}
}
