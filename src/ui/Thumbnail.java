package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.UIManager;

import main.Hub;
import presentation.Presentation;
import presentation.fsa.GraphView;

/**
 * @author chris mcaloney
 */
@SuppressWarnings("serial")
public class Thumbnail extends JPanel {
	
	protected Action closeButtonListener = new AbstractAction()
	{
		public void actionPerformed(ActionEvent event) {
			Thumbnail parent = (Thumbnail) ( (JButton) event.getSource() ).getParent();
			Hub.getWorkspace().removeModel(parent.getGraphModelName());
		}
	};
	
	private Presentation view;
	private static final int DEFAULT_ICON_SIZE = 16;
	private static int cbWidth = DEFAULT_ICON_SIZE;
	private static int cbHeight = DEFAULT_ICON_SIZE;
	private JButton closeButton;
	/**
	 * @param layout
	 */
	public Thumbnail(FilmStrip parent, LayoutManager layout) {
		super(layout);
		closeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(DEFAULT_ICON_SIZE));
		
//		// GTKLookAndFeel doesn't provide a FrameCloseIcon, so if we're running on GTK,
//		// take the window close icon from the MetalLookAndFeel -- CLM
//		if (UIManager.getSystemLookAndFeelClassName() == "com.sun.java.swing.plaf.gtk.GTKLookAndFeel") {
//			closeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(DEFAULT_ICON_SIZE));
//		} else {
//			Icon cbIcon = UIManager.getIcon("InternalFrame.closeIcon");
//			cbWidth = cbIcon.getIconWidth();
//			cbHeight = cbIcon.getIconHeight();
//			closeButton = new JButton(cbIcon);
//		}
		
		closeButton.addActionListener(closeButtonListener);
		closeButton.addMouseListener(parent);

		add(closeButton);
		closeButton.setVisible(false);
	}

	public void handleMouseEntered(MouseEvent arg0) {
		closeButton.setBounds(this.getWidth()-cbWidth-1,0,cbWidth,cbHeight);
		closeButton.setVisible(true);
	}
	
	public void handleMouseExited(MouseEvent arg0) {
		closeButton.setVisible(false);
	}
	
	public Component add(Component gv) {
		if (gv instanceof Presentation) {
			view = (Presentation) gv;
		}
		return super.add(gv);
	}
	
	public Presentation getPresentation()
	{
		return view;
	}
	
	public String getGraphModelName() {
		return view.getLayoutShell().getModel().getName();
	}
}
