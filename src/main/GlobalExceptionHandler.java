/**
 * 
 */
package main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;


/**
 *
 * @author Lenko Grigorov
 */
public class GlobalExceptionHandler extends JDialog implements UncaughtExceptionHandler {

	private JTextArea messageArea;
	private JTextArea labelArea;
	private JLabel iconLabel;
	
	public GlobalExceptionHandler()
	{
		super((java.awt.Frame)null,"Error");
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	quit();
		    }
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Box mainBox=Box.createVerticalBox();
		
		Box messageBox=Box.createHorizontalBox();
		iconLabel=new JLabel();
		messageBox.add(iconLabel);
		messageBox.add(Box.createRigidArea(new Dimension(10,0)));
		labelArea=new JTextArea("A serious error occurred in the IDES software.\nYou can check if IDES is still responsive and choose to continue your work.\nIf you press on the quit button, IDES will terminate.");
		labelArea.setEditable(false);
		labelArea.setBackground(mainBox.getBackground());
		messageBox.add(labelArea);
		messageBox.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
		mainBox.add(messageBox);
		
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		messageArea=new JTextArea();
		messageArea.setEditable(false);
		JScrollPane sPane=new JScrollPane(messageArea);
		sPane.setPreferredSize(new Dimension(200,300));
		sPane.setBorder(BorderFactory.createTitledBorder("Error details"));
		mainBox.add(sPane);

		mainBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		JButton continueButton=new JButton("Continue");
		continueButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				goOn();
			}
		});
		buttonBox.add(continueButton);
		buttonBox.add(Box.createRigidArea(new Dimension(5,0)));
		JButton quitButton=new JButton("Quit");
		quitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				quit();
			}
		});
		buttonBox.add(quitButton);
		mainBox.add(buttonBox);
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		getContentPane().add(mainBox);
		//pack();
	}
	/* (non-Javadoc)
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	public void uncaughtException(Thread arg0, Throwable arg1) {
		messageArea.setText("Message: "+arg1.getMessage()+"\n");
		messageArea.append("Exception in thread \""+arg0.getName()+"\" "+arg1.getClass().getName()+"\n");
		StackTraceElement[] elements=arg1.getStackTrace();
		for(int i=0;i<elements.length;++i)
			messageArea.append("    at "+elements[i].toString()+"\n");
		javax.swing.SwingUtilities.updateComponentTreeUI(this);
		iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
		labelArea.setBackground(getBackground());
		labelArea.setFont(new JLabel().getFont());
		pack();
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-getWidth())/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getHeight())/2);
		setVisible(true);
	}

	protected void goOn()
	{
		setVisible(false);
	}
	
	protected void quit()
	{
		System.exit(2);
	}
}
