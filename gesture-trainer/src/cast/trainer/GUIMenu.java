package cast.trainer;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import cast.trainer.utils.Gesture;

@SuppressWarnings("serial")
public class GUIMenu extends MenuBar implements ActionListener {
	private MenuItem getSql;
	private GUI gui;
	private MenuItem getGroupInfo;

	public GUIMenu(GUI gui) {
		this.gui = gui;
		Menu fileMenu = new Menu("File");

		getSql = new MenuItem("Get sql");
		getSql.addActionListener(this);

		getGroupInfo = new MenuItem("Get group coordinates");
		getGroupInfo.addActionListener(this);

		fileMenu.add(getSql);
		fileMenu.add(getGroupInfo);
		add(fileMenu);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getSql) {
			String text = "insert into spells";
			for (Iterator<GestureGroup> iterator = gui.gestureGroups.values().iterator(); iterator.hasNext();) {
				GestureGroup gestureGroup = (GestureGroup) iterator.next();
				text += gestureGroup.name;
				for (Iterator<Gesture> iterator2 = gestureGroup.gestures.iterator(); iterator2.hasNext();) {
					Gesture gesture = (Gesture) iterator2.next();
					for (Iterator<Point> iterator3 = gesture.getPoints().iterator(); iterator3.hasNext();) {
						Point point = (Point) iterator3.next();
						text += point.x + " " + point.y;
					}
				}
			}
			System.out.println(text);
		}

		if (e.getSource() == getGroupInfo) {
			GestureGroup gestureGroup = gui.gestureGroups.get(gui.groupsList.getSelected());
			for (Iterator<Gesture> iterator2 = gestureGroup.gestures.iterator(); iterator2.hasNext();) {
				String pointsStr = "";
				Gesture gesture = (Gesture) iterator2.next();
				for (Iterator<Point> iterator3 = gesture.getPoints().iterator(); iterator3.hasNext();) {
					Point point = (Point) iterator3.next();
					pointsStr += point.x + " " + point.y + ",";
				}
				System.out.println(pointsStr.substring(0, pointsStr.length() - 1));
			}

		}
	}
}
