package util;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import main.Hub;

public abstract class InterruptableProgressDialog extends EscapeDialog
		implements Runnable {
	
	protected JProgressBar progressBar;

	protected InterruptableProgressDialog(Frame owner, String title)
	{
		super(owner, title);
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onEscapeEvent();
		    }
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		progressBar=new JProgressBar();
		JButton cancelButton=new JButton(Hub.string("cancel"));
		cancelButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						onEscapeEvent();
					}
				});
		JPanel pane=new JPanel();
		pane.add(progressBar);
		pane.add(cancelButton);
		getContentPane().add(pane);
		pack();
	}
	
	protected void onEscapeEvent()
	{
		interrupt();
	}
	
	public abstract void interrupt();
	public abstract void run(); 

}
