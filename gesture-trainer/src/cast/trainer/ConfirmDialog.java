package cast.trainer;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class ConfirmDialog extends Dialog implements ActionListener {

	private Button yes, no;

	public ConfirmDialog(Frame owner, String title) {
		super(owner, title, true);
		setLayout(new FlowLayout());

		yes = new Button("Yes");
		no = new Button("No");
		yes.addActionListener(this);
		no.addActionListener(this);

		add(new Label("Really quit?"));
		add(yes);
		add(no);
		pack();
		setLocationRelativeTo(owner);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				dispose();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == yes)
			System.exit(0);
		else
			dispose();
	}

}
