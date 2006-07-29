/**
 * 
 */
package ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.Hub;

import util.EscapeDialog;

/**
 * @author Lenko Grigorov
 *
 */
public class AboutDialog extends EscapeDialog {

	/**
	 * @throws HeadlessException
	 */
	public AboutDialog() throws HeadlessException {
		super(Hub.getMainWindow(),Hub.string("about"),true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
        
		Box mainBox=Box.createVerticalBox();
		
		Font bigBoldFont=new Font("Sans Serif",Font.BOLD,16);

		JLabel titleLabel=new JLabel(Hub.string("IDES_LONG_NAME"));
		titleLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		titleLabel.setFont(bigBoldFont);
		mainBox.add(titleLabel);

		JLabel versionLabel=new JLabel(Hub.string("version")+" "+Hub.string("IDES_VER"));
		versionLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		versionLabel.setFont(bigBoldFont);
		mainBox.add(versionLabel);
		
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		JLabel releaseLabel=new JLabel(Hub.string("RELEASE"));
		releaseLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		mainBox.add(releaseLabel);
		
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		JLabel iconLabel=new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/big_logo.gif"))));
		iconLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		mainBox.add(iconLabel);

		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		JLabel whereLabel1=new JLabel(Hub.string("PLACE1"));
		whereLabel1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		mainBox.add(whereLabel1);
		JLabel whereLabel2=new JLabel(Hub.string("PLACE2"));
		whereLabel2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		mainBox.add(whereLabel2);

		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		JTextField urlLabel=new JTextField(Hub.string("IDES_URL"));
		urlLabel.setEditable(false);
		urlLabel.setBackground(releaseLabel.getBackground());
		urlLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		urlLabel.setHorizontalAlignment(JTextField.CENTER);
		mainBox.add(urlLabel);

		mainBox.add(Box.createRigidArea(new Dimension(0,5)));

		JTextArea developers=new JTextArea();
		developers.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		developers.setEditable(false);
		developers.setFont(releaseLabel.getFont());
		developers.setBackground(releaseLabel.getBackground());
		developers.setLineWrap(true);
		developers.setText(Hub.string("DEVELOPERS"));
		developers.setBorder(BorderFactory.createTitledBorder(Hub.string("devolopersTitle")));
		mainBox.add(developers);

		String s="";
		BufferedReader in=null;
		try
		{
			in=new BufferedReader(new FileReader("license.txt"));
			String line;
			while((line=in.readLine())!=null)
				s+=line+"\n";
			in.close();
		}catch(IOException e)
		{
			s=Hub.string("licenseMissing");
		}
		finally
		{
			try{if(in!=null) in.close();}catch(IOException e){}
		}
		JTextArea license=new JTextArea(9,51);
		license.setEditable(false);
		JScrollPane sp=new JScrollPane(license);
		sp.setBorder(BorderFactory.createTitledBorder(Hub.string("licenseTitle")));
		sp.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		mainBox.add(sp);

		mainBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		JButton closeButton=new JButton(Hub.string("close"));
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				onEscapeEvent();
			}
		});
		closeButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		mainBox.add(closeButton);

		getContentPane().add(mainBox);
		rootPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		pack();
		license.setText(s);
		license.setCaretPosition(0);
		setLocation(Hub.getCenteredLocationForDialog(this.getSize()));
	}
	
	public void onEscapeEvent()
	{
		dispose();
	}

}