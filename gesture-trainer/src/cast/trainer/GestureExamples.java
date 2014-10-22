package cast.trainer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import cast.trainer.utils.Gesture;

@SuppressWarnings("serial")
public class GestureExamples extends Panel {

	private GestureGroup gestureGroup;

	private class GestureThumbnail extends Canvas implements MouseListener {

		private Gesture gesture;
		private final int scale = 4;

		public GestureThumbnail(Gesture gesture) {
			this.gesture = gesture;
			setPreferredSize(new Dimension(480 / scale, 320 / scale));
			setBackground(new Color(255, 255, 255));
			setForeground(new Color(0, 0, 0));
			addMouseListener(this);
		}

		@Override
		public void paint(Graphics g) {
			Point prevPoint = null;
			for (Iterator<Point> pointsIterator = gesture.getPoints().iterator(); pointsIterator.hasNext();) {
				Point point = (Point) pointsIterator.next();
				if (prevPoint != null) {
					g.drawLine(prevPoint.x / scale, prevPoint.y / scale, point.x / scale, point.y / scale);
				}
				prevPoint = point;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		// don't work
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				gestureGroup.gestures.remove(this.gesture);
				refreshGroup();
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
	}

	public GestureExamples(GUI gui) {
		setPreferredSize(new Dimension(100, 100));
	}

	public void removeExample() {

	}

	public void refreshGroup() {
		removeAll();
		for (Iterator<Gesture> iterator = gestureGroup.gestures.iterator(); iterator.hasNext();) {
			Gesture gesture = (Gesture) iterator.next();
			GestureThumbnail canvas = new GestureThumbnail(gesture);
			add(canvas);
			canvas.repaint();
		}
		validate();
	}

	public void setGroup(GestureGroup gestureGroup) {
		this.gestureGroup = gestureGroup;
	}

}
