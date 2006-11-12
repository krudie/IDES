/**
 * 
 */
package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.UIManager;

import main.Hub;
import presentation.fsa.GraphView;
import presentation.fsa.FSAGraph;

/**
 * @author chris mcaloney
 *
 * TODO COMMENT
 */
@SuppressWarnings("serial")
public class Thumbnail extends JPanel implements MouseListener {
	
	protected Action closeButtonListener = new AbstractAction()
	{
		public void actionPerformed(ActionEvent event) {
			Thumbnail parent = (Thumbnail) ( (JButton) event.getSource() ).getParent();
			Hub.getWorkspace().removeFSAModel(parent.getGraphModelName());
		}
	};
	
	private GraphView graphView;
	private static final int BUTTON_SIZE = 16;
	private JButton closeButton;
	/**
	 * @param layout
	 */
	public Thumbnail(LayoutManager layout) {
		super(layout);
		closeButton = new JButton(UIManager.getIcon("InternalFrame.closeIcon"));
		
		// GTKLookAndFeel doesn't provide a FrameCloseIcon, so if we're running on GTK,
		// take the window close icon from the MetalLookAndFeel -- CLM
		if (UIManager.getSystemLookAndFeelClassName() == "com.sun.java.swing.plaf.gtk.GTKLookAndFeel") {
			closeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(BUTTON_SIZE));
		} else {
			closeButton = new JButton(UIManager.getIcon("InternalFrame.closeIcon"));
		}
		closeButton.addActionListener(closeButtonListener);
		add(closeButton);
		closeButton.setVisible(false);
		//addMouseListener(this);
	}

	/**
	 * 
	 */
	public Thumbnail() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void handleMouseEntered(MouseEvent arg0) {
		closeButton.setVisible(true);
	}
	
	public void handleMouseExited(MouseEvent arg0) {
		closeButton.setVisible(false);
	}
	
	public void mousePressed(MouseEvent arg0) {}
	
	public void mouseReleased(MouseEvent arg0) {}
	
	public void mouseClicked(MouseEvent arg0) {}
	
	// TODO if the mouse is moved too quickly, we will miss mouseEntered or
	// mouseExited events.  The boundary check below corrects for this in
	// some, but not all cases. -- CLM
	public void mouseEntered(MouseEvent arg0) {
//		closeButton.setVisible(true);
	}
	
	public void mouseExited(MouseEvent arg0) {
//		if (this.getBounds().contains(arg0.getPoint())) {
//			closeButton.setVisible(true);
//		} else {
//			closeButton.setVisible(false);
//		}
	}
	
	protected void paintChildren(Graphics g)
	{
		super.paintChildren(g);
		closeButton.setBounds(this.getWidth()-BUTTON_SIZE,0,BUTTON_SIZE,BUTTON_SIZE);
	}

	public Component add(Component gv) {
		if (gv instanceof GraphView) {
			graphView = (GraphView) gv;
		}
		return super.add(gv);
	}
	
	public String getGraphModelName() {
		return graphView.getGraphModel().getName();
	}
}
