package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.IDESWorkspace;
import model.Subscriber;

/**
 * A panel of graph thumbnail views that highlights the border
 * on the graph that has focus.
 * 
 * @author Helen Bretzke
 *
 */
public class FilmStrip extends JPanel implements ActionListener, Subscriber {
	
	protected GraphView activeView;
	protected IDESWorkspace workspace;
	
	public FilmStrip(){
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setPreferredSize(new Dimension(750, 100));
	}
	
	public void add(GraphView gv){
		
		// Set active MODEL in the workspace, don't bother keeping a reference here.
		activeView = gv;
		// FIXME why is the border not visible?
		gv.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		gv.setToolTipText(gv.getName());
		super.add(gv);
		gv.repaint();		
	}

	/**
	 * Figure out which graph was selected, toggle it's border (clear all others)
	 * and make it known to the UIStateModel as the currently active graph. 
	 * 
	 * @param arg0
	 */
	public void actionPerformed(ActionEvent arg0) {
		try{
			Component[] components = this.getComponents();
			
			// TODO Factor out toggleBorders code and call from add(gv)
			if(components != null){
				int n = components.length;
				for(int i=0; i<n; i++){
					((JComponent)components[i]).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				}				
			}
			activeView = (GraphView)arg0.getSource();
			workspace.setActiveModel(activeView.getName());
			activeView.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Unable to select and highlight graph.", "FilmStrip Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public GraphView getActiveView() {
		return activeView;
	}

	public void update() {
		// TODO get all graph models from the workspace and render them here,
		// each in its own GraphView object.  NOTE add() method will change.
		Iterator iter = workspace.getGraphModels();
		
	}

	public void setWorkspace(IDESWorkspace workspace) {
		this.workspace = workspace;
		workspace.attach(this);
	}
}
