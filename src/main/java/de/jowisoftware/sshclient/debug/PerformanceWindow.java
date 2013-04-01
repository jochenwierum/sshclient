package de.jowisoftware.sshclient.debug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.Timer;

import de.jowisoftware.sshclient.util.RingBuffer;

public class PerformanceWindow extends JFrame implements ActionListener, WindowListener {
    private static final long serialVersionUID = -1674919267554894420L;

    private static final int X_GAP = 80;
    private static final int Y_GAP = 40;
    private static final int BOX_WIDTH = 50;
    private static final int BOX_HEIGHT = 250;

    private static final int TICK_COUNT = 10;
    private static final int AXIS_X_GAP = 10;

    private static final Color[] colors = new Color[] {
        Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW
    };

    private transient Image image;
    private final Object lock = new Object();

    private final Timer timer = new Timer(1000, this);
    private final Map<PerformanceType, RingBuffer<Long>> recentTimings;

    private final BoxPlot plot = new BoxPlot();

    private long max;
    private final Map<PerformanceType, Quantile> quantiles = new HashMap<>();

    public PerformanceWindow(final Map<PerformanceType, RingBuffer<Long>> recentTimings) {
        super("Performance");
        setResizable(false);

        this.recentTimings = recentTimings;

        prepareDrawing();
        timer.start();

        addWindowListener(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    public void paint(final Graphics g) {
        synchronized(lock) {
            if (image != null) {
                g.drawImage(image, 0, 0, this);
            }
        }
    }

    private void drawLegend(final Graphics g) {
        final int x = quantiles.size() * BOX_WIDTH + X_GAP + 2 * AXIS_X_GAP;
        int y = 2 * Y_GAP;
        int color = 0;
        for (final PerformanceType type : PerformanceType.values()) {
            g.setColor(colors[color++]);
            if (quantiles.containsKey(type)) {
                final FontMetrics metrics = g.getFontMetrics();
                final int textHeight = metrics.getHeight();
                final int xOffset = textHeight + 4;

                //noinspection SuspiciousNameCombination
                g.fillRect(x, y, textHeight, textHeight);
                drawString(g, metrics, type.niceName, x + xOffset,
                        y + metrics.getAscent(), 2 * BOX_WIDTH - xOffset);
                y += textHeight * 2;
            }
        }
    }

    private void drawString(final Graphics g, final FontMetrics metrics,
            final String text, final int x, final int y, final int width) {
        final String[] parts = text.split(" ");
        final StringBuilder builder = new StringBuilder();

        int lineY = y;
        int i = 0;
        while(i < parts.length) {
            do {
                builder.append(parts[i++]).append(" ");
                if (i < parts.length - 1 &&
                        metrics.stringWidth(builder + parts[i + 1]) >= width) {
                    break;
                }
            } while(i < parts.length);

            g.drawString(builder.toString(), x, lineY);
            builder.delete(0, builder.length());
            lineY += metrics.getHeight();
        }
    }

    private void drawAxis(final Graphics g) {
        g.setColor(Color.WHITE);
        g.drawLine(X_GAP - AXIS_X_GAP, (int)(Y_GAP * .75),
                X_GAP - AXIS_X_GAP, Y_GAP + BOX_HEIGHT);
        final float step = 1f * max / TICK_COUNT;
        final int tickHeight = BOX_HEIGHT / TICK_COUNT;

        for (int i = 0; i <= TICK_COUNT; ++i) {
            final int y = Y_GAP + BOX_HEIGHT - i * tickHeight;
            final String text = String.format("%.1f", step * i);

            g.drawLine(X_GAP - AXIS_X_GAP, y, X_GAP - AXIS_X_GAP - 3, y);
            g.drawString(text, X_GAP - AXIS_X_GAP - 5 -
                    g.getFontMetrics().stringWidth(text), y);
        }

        g.drawLine(X_GAP - AXIS_X_GAP, Y_GAP + BOX_HEIGHT, X_GAP +
                (int) (quantiles.size() * 1.25 * BOX_WIDTH), Y_GAP + BOX_HEIGHT);
    }

    private void drawBoxPlots(final Graphics g) {
        int position = 0;
        int color = 0;
        for (final PerformanceType type : PerformanceType.values()) {
            g.setColor(colors[color++]);
            if (quantiles.containsKey(type)) {
                final Rectangle rect = new Rectangle(position * BOX_WIDTH + X_GAP, Y_GAP,
                        BOX_WIDTH, BOX_HEIGHT);
                plot.plot(g, quantiles.get(type), 0, max, rect);
                ++position;
            }
        }
    }

    private void calculateMaximumValue() {
        max = 0;
        for (final Quantile quantile : quantiles.values()) {
            max = Math.max(max, quantile.getMaxValue());
        }
    }

    private void calculateQuantiles() {
        quantiles.clear();
        for (final PerformanceType type : PerformanceType.values()) {
            final Long[] timings = recentTimings.get(type).toArray(new Long[0]);
            final Quantile quantile;
            if (timings.length > 0) {
                quantile = new Quantile(timings);
                quantiles.put(type, quantile);
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        prepareDrawing();
        repaint();
    }

    private void prepareDrawing() {
        final Dimension d;
        calculateQuantiles();
        calculateMaximumValue();
        d = new Dimension(
                (2 + quantiles.size()) * BOX_WIDTH + 2 * X_GAP,
                BOX_HEIGHT + 2 * Y_GAP);

        synchronized(lock) {
            if (d.width != getWidth() || image == null) {
                this.setSize(d);
                this.setPreferredSize(d);
                this.setMinimumSize(d);
                this.setMaximumSize(d);

                image = new BufferedImage(d.width, d.height,
                        BufferedImage.TYPE_INT_RGB);
            }

            final Graphics g = image.getGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            drawAxis(g);
            drawLegend(g);
            drawBoxPlots(g);
        }
    }

    @Override public void windowClosed(final WindowEvent e) {
        timer.stop();
    }

    @Override public void windowClosing(final WindowEvent e) { /* ignored */ }
    @Override public void windowOpened(final WindowEvent e) { /* ignored */ }
    @Override public void windowIconified(final WindowEvent e) { /* ignored */ }
    @Override public void windowDeiconified(final WindowEvent e) { /* ignored */ }
    @Override public void windowActivated(final WindowEvent e) { /* ignored */ }
    @Override public void windowDeactivated(final WindowEvent e) { /* ignored */ }
}

