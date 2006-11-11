/**
 * 
 */
package ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 * @author chris mcaloney
 *
 * TODO COMMENT
 */
@SuppressWarnings("serial")
public class Thumbnail extends JPanel implements MouseListener {
	
	private static final int BUTTON_SIZE = 18;
	private static final JButton closeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(BUTTON_SIZE-2));

	/**
	 * @param layout
	 */
	public Thumbnail(LayoutManager layout) {
		super(layout);
		add(closeButton);
		addMouseListener(this);
//		closeButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
//		addMouseListener(this);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public Thumbnail() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void mousePressed(MouseEvent arg0) {}
	
	public void mouseReleased(MouseEvent arg0) {}
	
	public void mouseClicked(MouseEvent arg0) {}
	
	public void mouseEntered(MouseEvent arg0) {
//		Point p = arg0.getComponent().getLocationOnScreen();
//		System.out.println(arg0.getComponent().toString());
//		closePopup = popupFactory.getPopup(this, closeButton, p.x+this.getWidth()-BUTTON_SIZE, p.y);
		closeButton.setVisible(true);
	}
	
	public void mouseExited(MouseEvent arg0) {
		if (!this.getBounds().contains(arg0.getPoint())) {
			closeButton.setVisible(false);
		}
	}
	
	protected void paintChildren(Graphics g)
	{
		super.paintChildren(g);
		closeButton.setBounds(85,0,15,15);
	}

}
