package model.fsa.ver1;

import io.fsa.ver1.SubElement;

import java.awt.Point;
import java.awt.geom.Point2D;

import model.DESElement;
import model.fsa.FSAMetaData;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import presentation.fsa.Edge;
import presentation.fsa.StateLayout;
import presentation.fsa.TransitionLayout;

/**
 * Store and extracts the metadata for a given Automaton.
 * TODO  If layout information is missing, call GraphViz to layout the graph
 * representing the Automaton. 
 * 
 * @author helen bretzke
 *
 */
public class MetaData implements FSAMetaData {
	
	Automaton automaton;
	
	public MetaData(Automaton automaton){
		this.automaton = automaton;
	}
	
	/* (non-Javadoc)
	 * @see model.fsa.FSAMetaData#getData(java.lang.String)
	 */
	public Object getData(String moduleID, DESElement el) {
		if(moduleID.equals("graphic")){
			try {
				if(el.getClass().equals(Class.forName("State"))){
					return getLayoutData((State)el);
				}else if(el.getClass().equals(Class.forName("Transition"))){
					return getLayoutData((Transition)el);
				}else{
					return null;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see model.fsa.FSAMetaData#setData(java.lang.Object)
	 */
	public void setData(Object data) {
		this.automaton = (Automaton)data;
	}

	
	/**
	 * TODO delete metadata from State 
	 * (so no synchronization problems since only write it back before writing to file).
	 * 
	 * @return an object encapsulating all of the graphical layout 
	 * 	information required to display this state.
	 */
	public StateLayout getLayoutData(FSAState state) {
		State s = (State)state;
		
		// radius, centre point, label text and arrow vector (if initial)
		SubElement layout = s.getSubElement("graphic").getSubElement("circle");
		int radius = Integer.parseInt(layout.getAttribute("r"));
		Point centre = new Point(Integer.parseInt(layout.getAttribute("x")),
								 Integer.parseInt(layout.getAttribute("y")));
				
		SubElement name = s.getSubElement("name");
        String n = (name.getChars() != null) ? name.getChars() : "";
        
        if(s.isInitial()) {
        	SubElement a = s.getSubElement("graphic").getSubElement("arrow");
        	Point arrow = new Point((int)Float.parseFloat(a.getAttribute("x")),
								 (int)Float.parseFloat(a.getAttribute("y")));
        	return new StateLayout(centre, radius, n, arrow);
        } else {
		 	return new StateLayout(centre, radius, n);
        }	
	}

	/**
	 * TODO Implement
	 */
	public TransitionLayout getLayoutData(FSATransition transition){
		Transition t = (Transition)transition;
		SubElement layout = t.getSubElement("graphic").getSubElement("bezier");
		Point2D.Float[] controls = new Point2D.Float[4];
		controls[Edge.P1] = new Point2D.Float(Float.parseFloat(layout.getAttribute("x1")),
				Float.parseFloat(layout.getAttribute("y1")));
		controls[Edge.P2] = new Point2D.Float(Float.parseFloat(layout.getAttribute("x2")),
				Float.parseFloat(layout.getAttribute("y2")));
		controls[Edge.CTRL1] = new Point2D.Float(Float.parseFloat(layout.getAttribute("ctrlx1")),
				Float.parseFloat(layout.getAttribute("ctrly1")));
		controls[Edge.CTRL2] = new Point2D.Float(Float.parseFloat(layout.getAttribute("ctrlx2")),
				Float.parseFloat(layout.getAttribute("ctrly2")));
						
		// How many events can fire this transition?
		// Version 2 has a single event per transition.
		// Kluge around it.
		Event e = (Event) t.getEvent();
		String[] edgeLabels = new String[1];
		if(e != null){
			edgeLabels[0] = e.getSymbol();
		}else{
			edgeLabels[0] = "";
		}
		
		return new TransitionLayout(controls, edgeLabels);
	}	
}
