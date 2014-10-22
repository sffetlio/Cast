package cast.trainer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;

import cast.trainer.utils.Gesture;

@SuppressWarnings("serial")
public class GestureGrid extends Canvas implements MouseListener, MouseMotionListener, MouseWheelListener {

	private volatile int interval = 10;
	private volatile float scale = 1;
	private Gesture gesture = new Gesture();

	public GestureGrid() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int width = 480;
		int height = 320;
		int verticalLines = (int) (width / (interval * scale));
		int horizontallLines = (int) (height / (interval * scale));

		g2d.setColor(new Color(200, 200, 200));
		for (int i = 0; i <= verticalLines; i++) {
			g2d.drawLine((int) (i * interval * scale), 0, (int) (i * interval * scale), height);
		}
		for (int i = 0; i <= horizontallLines; i++) {
			g2d.drawLine(0, (int) (i * interval * scale), width, (int) (i * interval * scale));
		}

		g2d.setColor(new Color(50, 50, 50));
		Point prevPoint = null;
		for (Iterator<Point> pointsIterator = gesture.getPoints().iterator(); pointsIterator.hasNext();) {
			Point point = (Point) pointsIterator.next();
			if (prevPoint != null) {
				g2d.drawLine((int) (prevPoint.x * scale), (int) (prevPoint.y * scale), (int) (point.x * scale), (int) (point.y * scale));
			}
			prevPoint = point;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Point point = e.getPoint();
			if (gesture.getPoints().size() >= 1) {
				Point lastPoint = gesture.getPoints().getLast();
				double distance = Math.sqrt((point.x - lastPoint.x) * (point.x - lastPoint.x) + (point.y - lastPoint.y) * (point.y - lastPoint.y));
				if (distance < interval) {
					return;
				}
			}
			gesture.getPoints().add(point);
			repaint();
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			if (gesture.getPoints().size() > 0) {
				gesture.getPoints().removeLast();
				repaint();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point point = e.getPoint();
		if (gesture.getPoints().size() >= 1) {
			Point lastPoint = gesture.getPoints().getLast();
			double distance = Math.sqrt((point.x - lastPoint.x) * (point.x - lastPoint.x) + (point.y - lastPoint.y) * (point.y - lastPoint.y));
			if (distance < interval) {
				return;
			}
		}
		gesture.getPoints().add(point);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scale += e.getWheelRotation() * 0.1;
		if (scale < 1) {
			scale = 1;
			return;
		}
		if (scale > 3) {
			scale = 3;
			return;
		}
		repaint();
	}

	public Gesture getGesture() {
		return gesture;
	}

	public void reset() {
		gesture = new Gesture();
		repaint();
	}
}
