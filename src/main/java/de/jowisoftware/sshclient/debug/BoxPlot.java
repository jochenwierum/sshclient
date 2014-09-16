package de.jowisoftware.sshclient.debug;

import java.awt.*;

class BoxPlot {
    private long min;
    private long max;
    private Rectangle area;
    private Graphics drawArea;

    public void plot(final Graphics g, final Quantile data,
            final long globalMin, final long globalMax,
            final Rectangle areaInGraphics) {

        this.drawArea = g.create(areaInGraphics.x, areaInGraphics.y,
                areaInGraphics.width, areaInGraphics.height);
        this.min = globalMin;
        this.max = globalMax;
        this.area = areaInGraphics;

        drawLine(convertToYPos(data.getMedian()));

        final int q25Y = convertToYPos((long) data.getQuantile(.25));
        final int q75Y = convertToYPos((long) data.getQuantile(.75));

        drawQuantile(q25Y, q75Y);

        final int maxY = convertToYPos(data.getMaxValueWithoutAntenna());
        final int minY = convertToYPos(data.getMinValueWithoutAntenna());
        drawLine(maxY);
        drawLine(minY);

        drawVerticalLine(maxY, q75Y);
        drawVerticalLine(minY, q25Y);

        drawAntennas(data);
    }

    private void drawAntennas(final Quantile data) {
        for (final long value : data.getBottomAntennas()) {
            drawCircle(convertToYPos(value));
        }
        for (final long value : data.getTopAntennas()) {
            drawCircle(convertToYPos(value));
        }
    }

    private void drawCircle(final int y) {
        final int radius = 2;
        drawArea.drawOval(area.width / 2 - radius, y - radius,
                2 * radius, 2 * radius);
    }

    private void drawVerticalLine(final int topY, final int bottomY) {
        drawArea.drawLine(area.width / 2, topY,
                area.width / 2, bottomY);
    }

    private void drawQuantile(final int q25Y, final int q75Y) {
        drawLine(q25Y);
        drawLine(q75Y);
        drawArea.drawLine(1, q25Y, 1, q75Y);
        drawArea.drawLine(area.width - 1, q25Y, area.width - 1, q75Y);
    }

    private int convertToYPos(final long value) {
        final double scaledMedian = scale(value, min, max);
        final int y = (int) (scaledMedian * (area.height - 2)) + 1;
        return area.height - y;
    }

    private void drawLine(final int y) {
        drawArea.drawLine(1, y, area.width - 2, y);
    }

    private double scale(final long value, final long globalMin, final long globalMax) {
        return 1.0 * (value - globalMin) / (globalMax - globalMin);
    }
}
