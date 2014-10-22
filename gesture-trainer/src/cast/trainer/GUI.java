package cast.trainer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;

import cast.trainer.utils.Groups;

@SuppressWarnings("serial")
public class GUI extends Frame {

	public GestureGrid center;
	public Groups groupsList;
	private GestureExamples bottom;
	public LinkedHashMap<String, GestureGroup> gestureGroups = new LinkedHashMap<>();
	private GUIMenu menu;

	public GUI() {
		center = new GestureGrid();
		groupsList = new Groups(this);
		bottom = new GestureExamples(this);
		menu = new GUIMenu(this);

		add(center, BorderLayout.CENTER);
		add(groupsList, BorderLayout.WEST);
		add(bottom, BorderLayout.SOUTH);

		setMenuBar(menu);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				// ConfirmDialog dialog = new ConfirmDialog(GUI.this, "Quit?");
				// dialog.setVisible(true);
				System.exit(0);
			}
		});

		setTitle("Cast Trainer");
		setSize(600, 500);
		setBackground(new Color(230, 230, 230));
		setVisible(true);
	}

	public void saveGesture(String groupName) {
		GestureGroup gestureGroup = gestureGroups.get(groupName);
		gestureGroup.gestures.add(center.getGesture());
		center.reset();
		bottom.setGroup(gestureGroup);
		bottom.refreshGroup();
	}

}
