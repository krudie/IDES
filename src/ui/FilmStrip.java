package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import javax.swing.JPanel;

/**
 * A panel of graph thumbnail views that highlights the border
 * on the graph that has focus.
 * 
 * @author Helen Bretzke
 *
 */
public class FilmStrip extends JPanel implements ActionListener {

	// This may be unnecessary since can simply use my contentpane
	protected ArrayList<GraphView> miniGraphs;
	protected GraphView activeView;
	
	public FilmStrip(){
		miniGraphs = new ArrayList<GraphView>(5);
		
	}
	
	public void add(GraphView gv){
		miniGraphs.add(gv);
		super.add(gv);
		this.setPreferredSize(new Dimension(100, 100));
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
			if(components != null){
				int n = components.length;
				for(int i=0; i<n; i++){
					((JComponent)components[i]).setBorder(BorderFactory.createEmptyBorder());
				}				
			}
			activeView = (GraphView)arg0.getSource();
			activeView.setBorder(BorderFactory.createLoweredBevelBorder());
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Unable to select and highlight graph.", "FilmStrip Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public GraphView getActiveView() {
		return activeView;
	}
}
