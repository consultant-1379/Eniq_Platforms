package com.ericsson.eniq.techpacksdk;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

// TODO modify this to Ericsson style

/*
 * This component is intended to be used as a GlassPane. It's start method
 * makes this component visible, consumes mouse and keyboard input, and
 * displays a spinning activity indicator animation. The stop method makes
 * the component not visible. The code for rendering the animation was
 * lifted from org.jdesktop.swingx.painter.BusyPainter. I've made some
 * simplifications to keep the example small.
 */
@SuppressWarnings("serial")
public class BusyIndicator extends JComponent implements ActionListener {

  private int frame = -1; // animation frame index
  private static final int nBars = 8;
  private static final float barWidth = 6;
  private static final float outerRadius = 28;
  private static final float innerRadius = 12;
  private static final int trailLength = 4;
  private static final float barGray = 200f; // shade of gray, 0-255
  private final Timer timer = new Timer(65, this); // 65ms = animation
                            // rate

  public BusyIndicator() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    final MouseInputListener blockMouseEvents = new MouseInputAdapter() {
    };
    addMouseMotionListener(blockMouseEvents);
    addMouseListener(blockMouseEvents);
    final InputVerifier retainFocusWhileVisible = new InputVerifier() {
      public boolean verify(final JComponent c) {
        return !c.isVisible();
      }
    };
    setInputVerifier(retainFocusWhileVisible);
  }

  public void actionPerformed(final ActionEvent ignored) {
    frame += 1;
    repaint();
  }

  void start() {
    setVisible(true);
    requestFocusInWindow();
    timer.start();
  }

  void stop() {
    setVisible(false);
    timer.stop();
  }

  @Override
  protected void paintComponent(final Graphics g) {
    final RoundRectangle2D bar = new RoundRectangle2D.Float(innerRadius,
        -barWidth / 2, outerRadius, barWidth, barWidth, barWidth);
    // x, y, width, height, arc width,arc height
    final double angle = Math.PI * 2.0 / (double) nBars; // between bars
    final Graphics2D g2d = (Graphics2D) g;
    g2d.translate(getWidth() / 2, getHeight() / 2);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    for (int i = 0; i < nBars; i++) {
      // compute bar i's color based on the frame index
      Color barColor = new Color((int) barGray, (int) barGray,
          (int) barGray);
      if (frame != -1) {
        for (int t = 0; t < trailLength; t++) {
          if (i == ((frame - t + nBars) % nBars)) {
            final float tlf = (float) trailLength;
            final float pct = 1.0f - ((tlf - t) / tlf);
            final int gray = (int) ((barGray - (pct * barGray)) + 0.5f);
            barColor = new Color(gray, gray, gray);
          }
        }
      }
      // draw the bar
      g2d.setColor(barColor);
      g2d.fill(bar);
      g2d.rotate(angle);
    }
  }

}
