package cast.trainer.utils;

import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cast.trainer.GUI;
import cast.trainer.GestureGroup;

@SuppressWarnings("serial")
public class Groups extends Panel implements ActionListener {

	private Button addBtn;
	private Button renameBtn;
	private Button daleteBtn;
	private Button saveBtn;
	private TextField groupName;
	private List groupsList;
	private GUI gui;
	private Button clearBtn;

	public Groups(GUI gui) {
		this.gui = gui;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		groupName = new TextField("");
		addBtn = new Button("Add");
		renameBtn = new Button("Rename");
		daleteBtn = new Button("Delete");
		saveBtn = new Button("Save");
		clearBtn = new Button("Clear");
		groupsList = new List(10);

		addBtn.addActionListener(this);
		renameBtn.addActionListener(this);
		daleteBtn.addActionListener(this);
		saveBtn.addActionListener(this);
		clearBtn.addActionListener(this);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 1;
		add(groupName, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		add(addBtn, c);
		c.gridx = 1;
		add(renameBtn, c);
		c.gridx = 2;
		add(daleteBtn, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		c.gridheight = 4;
		add(groupsList, c);

		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		add(saveBtn, c);

		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		add(clearBtn, c);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addBtn && !groupName.getText().isEmpty()) {
			groupsList.add(groupName.getText());
			gui.gestureGroups.put(groupName.getText(), new GestureGroup(groupName.getText()));
			groupName.setText("");
		}
		if (e.getSource() == renameBtn && groupsList.getSelectedIndex() >= 0 && !groupName.getText().isEmpty()) {
			int index = groupsList.getSelectedIndex();
			groupsList.remove(index);
			groupsList.add(groupName.getText(), index);
			gui.gestureGroups.get(index).rename(groupName.getText());
			groupName.setText("");
		}
		if (e.getSource() == daleteBtn && groupsList.getSelectedIndex() >= 0) {
			groupsList.remove(groupsList.getSelectedIndex());
			gui.gestureGroups.remove(groupsList.getSelectedIndex());
			groupName.setText("");
		}

		if (e.getSource() == saveBtn && groupsList.getSelectedIndex() >= 0) {
			gui.saveGesture(groupsList.getSelectedItem());
		}

		if (e.getSource() == clearBtn) {
			gui.center.reset();
		}
	}

	public String getSelected() {
		return groupsList.getSelectedItem();
	}

}
