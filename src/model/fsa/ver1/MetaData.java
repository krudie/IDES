package model.fsa.ver1;

import io.fsa.ver1.SubElement;

import java.awt.geom.Point2D;

import model.DESElement;
import model.fsa.FSAMetaData;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import presentation.GraphicalLayout;
import presentation.fsa.Edge;
import presentation.fsa.EdgeLayout;
import presentation.fsa.NodeLayout;

/**
 * Store and extracts the metadata for a given Automaton.
 * 
 * ??? Is this class a publisher or a subscriber?  
 * to Automaton (pushes and pulls)? 
 * to GraphModel (pushes and pulls)?
 * 
 * TODO  If layout information is missing, call GraphViz to layout the graph
 * representing the Automaton. 
 * 
 * @author helen bretzke
 *
 */
public class MetaData implements FSAMetaData {
	
	private Automaton automaton;
	
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
		String name;
		SubElement n = s.getSubElement("name");
		if(n != null){
			name = (n.getChars() != null) ? n.getChars() : "";
		}else{
			name = "";
		}
		
		if(s.isInitial()) {
        	SubElement a = s.getSubElement("graphic").getSubElement("arrow");
        	Point2D.Float arrow = new Point2D.Float(Float.parseFloat(a.getAttribute("x")),
								 					Float.parseFloat(a.getAttribute("y")));
        	
        	
        	return new NodeLayout(centre, radius, name, arrow);
        } else {
		 	return new NodeLayout(centre, radius, name);
        }	
	}

	/**
	 * Sets the graphical layout information required to display the given state
	 * and if it does not yet exist, adds the state to the FSAModel.
	 * 
	 * @param state the state
	 * @param layout the graphical layout data for a node
	 */
	public void setLayoutData(FSAState state, NodeLayout layout){
		// Set the layout data for state
		State s = (State)state;
		
		// ??? What if the state doesn't have a name?  Should we be creating this subelement?
		SubElement n = new SubElement("name");
		n.setChars(layout.getText());
		s.addSubElement(n);
		
		SubElement g = new SubElement("graphic");
		SubElement c = new SubElement("circle");
		c.setAttribute("r", Math.round(layout.getRadius()) + "");
		c.setAttribute("x", Math.round(layout.getLocation().x) + "");
		c.setAttribute("y", Math.round(layout.getLocation().y) + "");
		g.addSubElement(c);		
		if(s.isInitial()) {
        	SubElement a = new SubElement("arrow");
        	a.setAttribute("x", layout.getArrow().x + "");
        	a.setAttribute("y", layout.getArrow().y + "");
        	g.addSubElement(a);
		}
		s.addSubElement(g);		
	}
	
	
	
	/**	
	 * Sets the graphical layout information required to display the given transition
	 * and if it does not yet exist, adds the transition to the FSAModel.
	 * 
	 * @param transition the transition to be stored
	 * @param layout the graphical layout data for an edge
	 */
	public void setLayoutData(FSATransition transition, EdgeLayout layout){
		Transition t = (Transition)transition;
		SubElement g = new SubElement("graphic");
		SubElement b = new SubElement("bezier");
		SubElement l = new SubElement("label");
		b.setAttribute("x1", "" + layout.getCurve()[EdgeLayout.P1].x);
		b.setAttribute("y1", "" + layout.getCurve()[EdgeLayout.P1].y);
		b.setAttribute("x2", "" + layout.getCurve()[EdgeLayout.P2].x);
		b.setAttribute("y2", "" + layout.getCurve()[EdgeLayout.P2].y);
		b.setAttribute("ctrlx1", "" + layout.getCurve()[EdgeLayout.CTRL1].x);
		b.setAttribute("ctrly1", "" + layout.getCurve()[EdgeLayout.CTRL1].y);
		b.setAttribute("ctrlx2", "" + layout.getCurve()[EdgeLayout.CTRL2].x);
		b.setAttribute("ctrly2", "" + layout.getCurve()[EdgeLayout.CTRL2].y);
		l.setAttribute("x", "" + layout.getLabelOffset().x);
		l.setAttribute("y", "" + layout.getLabelOffset().y);
		g.addSubElement(b);
		g.addSubElement(l);
		t.addSubElement(g);
	}
	
	/**
	 * Extracts and returns the graphical layout for the Edge representing
	 * the given tranistion.
	 * 
	 * @return graphical layout for the Edge representing the given transition.
	 */
	public EdgeLayout getLayoutData(FSATransition transition){
		Transition t = (Transition)transition;
		SubElement layout = t.getSubElement("graphic");
		
		SubElement bezier = layout.getSubElement("bezier");
		Point2D.Float[] controls = new Point2D.Float[4];
		controls[EdgeLayout.P1] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("x1")),
				Float.parseFloat(bezier.getAttribute("y1")));
		controls[EdgeLayout.P2] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("x2")),
				Float.parseFloat(bezier.getAttribute("y2")));
		controls[EdgeLayout.CTRL1] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("ctrlx1")),
				Float.parseFloat(bezier.getAttribute("ctrly1")));
		controls[EdgeLayout.CTRL2] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("ctrlx2")),
				Float.parseFloat(bezier.getAttribute("ctrly2")));

		EdgeLayout edgeLayout = new EdgeLayout(controls);
		
		// extract label offset
		Point2D.Float offset = new Point2D.Float();
		SubElement label = layout.getSubElement("label");
		offset.setLocation(Float.parseFloat(label.getAttribute("x")), Float.parseFloat(label.getAttribute("y")));
		edgeLayout.setLabelOffset(offset);		
		
		// extract transition event symbol (if exists)
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
	 * Precondition: layout != null 
	 * and transition has same source and target nodes as those in layout.
	 * 
	 * @param transition
	 * @param layout
	 */
	public void addToLayout(FSATransition transition, EdgeLayout layout){
		Transition t = (Transition)transition;
		Event e = (Event) t.getEvent();
		if(e != null){			
			layout.addEventName(e.getSymbol());
		}
	}
	
	public void removeFromLayout(FSATransition transition, EdgeLayout layout){
		Transition t = (Transition)transition;
		Event e = (Event) t.getEvent();
		if(e != null){			
			layout.removeEventName(e.getSymbol());
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
