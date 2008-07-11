package services.notice;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.plaf.metal.MetalIconFactory;

import main.Hub;

/**
 * This text area can be used inside scroll panes that may be sized down.
 * The text area will automatically get more narrow as needed. 
 * @author Lenko Grigorov
 */
public class ContractableTextArea extends JTextArea
{
	public ContractableTextArea(String s)
	{
		super(s);
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(10,super.getPreferredSize().height);
	}
}
