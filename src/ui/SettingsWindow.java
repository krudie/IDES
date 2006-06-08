package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import util.EscapeDialog;

import main.Hub;

public class SettingsWindow extends EscapeDialog {

	protected JList sectionList;
	
	public SettingsWindow()
	{
		super(Hub.getMainWindow(),Hub.string("settingsWindowTitle"),true);
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onClose();
		    }
		});
		this.setDefaultCloseOperation(this.DO_NOTHING_ON_CLOSE);
		
		sectionList=new JList();
		JSplitPane stuffPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane(sectionList),new JPanel());

		JPanel buttonPane=new JPanel();
		JButton cancelBut=new JButton("Cancel");
		cancelBut.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						onClose();
					}
				});
		buttonPane.add(cancelBut);

		getContentPane().add(stuffPane,BorderLayout.CENTER);
		getContentPane().add(buttonPane,BorderLayout.SOUTH);

		pack();
		setVisible(true);
	}
	
	protected void onClose()
	{
		dispose();
	}
	
	protected void onEscapeEvent()
	{
		onClose();
	}
}
