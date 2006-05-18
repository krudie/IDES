package model.fsa.ver1;

import io.fsa.ver1.SubElement;

import java.awt.geom.Point2D;

import model.DESElement;
import model.fsa.FSAMetaData;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import presentation.fsa.Edge;
import presentation.fsa.EdgeLayout;
import presentation.fsa.NodeLayout;

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
	 * 	information required to display the given state.
	 */
	public NodeLayout getLayoutData(FSAState state) {
		State s = (State)state;
		
		// radius, centre point, label text and arrow vector (if initial)
		SubElement layout = s.getSubElement("graphic").getSubElement("circle");
		int radius = Integer.parseInt(layout.getAttribute("r"));
		Point2D.Float centre = new Point2D.Float(Integer.parseInt(layout.getAttribute("x")),
								 Integer.parseInt(layout.getAttribute("y")));
		if(s.isInitial()) {
        	SubElement a = s.getSubElement("graphic").getSubElement("arrow");
        	Point2D.Float arrow = new Point2D.Float(Float.parseFloat(a.getAttribute("x")),
								 					Float.parseFloat(a.getAttribute("y")));
        	return new NodeLayout(centre, radius, s.getName(), arrow);
        } else {
		 	return new NodeLayout(centre, radius, s.getName());
        }	
	}

	/**
	 * Sets the graphical layout information required to display the given state
	 * and if it does not yet exist, adds the state to the FSAModel.
	 * 
	 * TODO Implement !!!
	 * 
	 * @param state the state
	 * @param layout the graphical layout data for a node
	 */
	public void setLayoutData(FSAState state, NodeLayout layout){
		// Set the layout data for state
		State s = (State)state;
		SubElement g = new SubElement("graphic");
		SubElement c = new SubElement("circle");
		c.setAttribute("r", layout.getRadius()+"");
		c.setAttribute("x", layout.getLocation().x+"");
		c.setAttribute("y", layout.getLocation().y+"");
		g.addSubElement(c);		
		if(s.isInitial()) {
        	SubElement a = new SubElement("arrow");
        	a.setAttribute("x", layout.getArrow().x+"");
        	a.setAttribute("y", layout.getArrow().y+"");
        	g.addSubElement(a);
		}
		s.addSubElement(g);
	}
	
	
	
	/**
	 * TODO Implement
	 * 
	 * Sets the graphical layout information required to display the given transition
	 * and if it does not yet exist, adds the transition to the FSAModel.
	 * 
	 * @param transition the transition to be stored
	 * @param layout the graphical layout data for an edge
	 */
	public void setLayoutData(FSATransition transition, EdgeLayout layout){
		
	}
	
	public EdgeLayout getLayoutData(FSATransition transition){
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

		// TODO extract label offset
		
		EdgeLayout edgeLayout = new EdgeLayout(controls);
		Event e = (Event) t.getEvent();		
		if(e != null){
			edgeLayout.addEventName(e.getSymbol());
		}		
		return edgeLayout;
	}

	/**
	 * Adds edge layout data (event name if exists) from the given transition 
	 * to the given layout.
	 * 
	 * Precondition: layout != null and transition has same source and target nodes as those in layout.
	 * 
	 * @param transition
	 * @param layout
	 */
	public void augmentLayoutData(FSATransition transition, EdgeLayout layout){
		Transition t = (Transition)transition;
		Event e = (Event) t.getEvent();
		if(e != null){			
			layout.addEventName(e.getSymbol());
		}
	}
	
	////////////////////////////////////////////////////////////////////////
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
	////////////////////////////////////////////////////////////////////////
}
