package model.fsa.ver1;

import io.fsa.ver1.SubElement;

import java.awt.geom.Point2D;

import model.DESElement;
import model.fsa.FSAMetaData;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import presentation.fsa.BezierLayout;
import presentation.fsa.CircleNodeLayout;

/**
 * Store and extracts the metadata for a given Automaton.
 * 
 * @author Helen Bretzke
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
	public CircleNodeLayout getLayoutData(FSAState state) {
		State s = (State)state;
		
		// radius, centre point, label text and arrow vector (if initial)
		SubElement layout = s.getSubElement("graphic").getSubElement("circle");
		int radius = Integer.parseInt(layout.getAttribute("r"));
		Point2D.Float centre = new Point2D.Float(Integer.parseInt(layout.getAttribute("x")),
								 				Integer.parseInt(layout.getAttribute("y")));
		String name;
		SubElement n = s.getSubElement("name");
		if(n != null) {
			name = (n.getChars() != null) ? n.getChars() : "";
		} else {
			name = "";
		}
		
		if(s.isInitial()) {
        	SubElement a = s.getSubElement("graphic").getSubElement("arrow");
        	Point2D.Float arrow = new Point2D.Float(Float.parseFloat(a.getAttribute("x")),
								 					Float.parseFloat(a.getAttribute("y")));
        	
        	
        	return new CircleNodeLayout(centre, radius, name, arrow);
        } else {
		 	return new CircleNodeLayout(centre, radius, name);
        }	
	}

	/**
	 * Sets the graphical layout information required to display the given state
	 * and if it does not yet exist, adds the state to the FSAModel.
	 * 
	 * @param state the state
	 * @param layout the graphical layout data for a node
	 */
	public void setLayoutData( FSAState state, CircleNodeLayout layout ) {
		// Set the layout data for state
		State s = (State)state;
				
		SubElement n = s.getSubElement("name");
		if( n == null ) {
			n = new SubElement("name");
			s.addSubElement(n);
		}
		n.setChars(layout.getText());		
		
		SubElement g = s.getSubElement("graphic");
		if( g == null ) {
			g = new SubElement("graphic");
			s.addSubElement(g);
		}
		
		SubElement c = g.getSubElement("circle");
		if(c == null){
			c = new SubElement("circle");
			g.addSubElement(c);
		}
		c.setAttribute("r", Math.round(layout.getRadius()) + "");
		c.setAttribute("x", Math.round(layout.getLocation().x) + "");
		c.setAttribute("y", Math.round(layout.getLocation().y) + "");

    	SubElement a = g.getSubElement("arrow");
		if(s.isInitial()) {
        	if(a == null){
        		a = new SubElement("arrow");
            	g.addSubElement(a);
        	}
        	a.setAttribute("x", layout.getArrow().x + "");
        	a.setAttribute("y", layout.getArrow().y + "");        	
		}else{
			if(a != null){
				g.removeSubElement("arrow");
			}
		}
	}
	
	
	
	/**	
	 * Sets the graphical layout information required to display the given transition
	 * and packs it into a subelement of <code>transition</code>.
	 * 
	 * @param transition the transition to be stored
	 * @param layout the graphical layout data for an edge
	 */
	public void setLayoutData(FSATransition transition, BezierLayout layout){
		Transition t = (Transition)transition;
		SubElement g = new SubElement("graphic");
		SubElement b = new SubElement("bezier");
		SubElement l = new SubElement("label");
		b.setAttribute("x1", "" + layout.getCurve().getX1());
		b.setAttribute("y1", "" + layout.getCurve().getY1());
		b.setAttribute("x2", "" + layout.getCurve().getX2());
		b.setAttribute("y2", "" + layout.getCurve().getY2());
		b.setAttribute("ctrlx1", "" + layout.getCurve().getCtrlX1());
		b.setAttribute("ctrly1", "" + layout.getCurve().getCtrlY1());
		b.setAttribute("ctrlx2", "" + layout.getCurve().getCtrlX2());
		b.setAttribute("ctrly2", "" + layout.getCurve().getCtrlY2());
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
	public BezierLayout getLayoutData(FSATransition transition){
		Transition t = (Transition)transition;
		SubElement layout = t.getSubElement("graphic");
		
		SubElement bezier = layout.getSubElement("bezier");
		Point2D.Float[] controls = new Point2D.Float[4];
		controls[BezierLayout.P1] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("x1")),
				Float.parseFloat(bezier.getAttribute("y1")));
		controls[BezierLayout.P2] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("x2")),
				Float.parseFloat(bezier.getAttribute("y2")));
		controls[BezierLayout.CTRL1] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("ctrlx1")),
				Float.parseFloat(bezier.getAttribute("ctrly1")));
		controls[BezierLayout.CTRL2] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("ctrlx2")),
				Float.parseFloat(bezier.getAttribute("ctrly2")));

		// FIXME edgeLayout constructor needs to know about source and target
		// 		Node layouts.
		// Need to know if this is a self-loop before calling this constructor 
		// (since constructor calls updateAnglesEtc...)
		BezierLayout edgeLayout = new BezierLayout(controls); //, t.getSource().equals(t.getTarget()));		
		
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
	
	public void removeFromLayout(FSATransition transition, BezierLayout layout){
		Transition t = (Transition)transition;
		Event e = (Event) t.getEvent();
		if(e != null){			
			layout.removeEventName(e.getSymbol());
		}
	}

	public float getFontSize(model.fsa.FSAModel a) {
		SubElement se=a.getMeta();
		if(se==null)
			return 12;
		String fontSize=se.getAttribute("size");
		try
		{
			return Float.parseFloat(fontSize);
		}catch(NumberFormatException e)
		{
			return 12;
		}
	}

	public void setFontSize(model.fsa.FSAModel a, float fontSize) {
		SubElement se=a.getMeta();
		if(se==null)
			se=new SubElement("font");
		se.setAttribute("size",Float.toString(fontSize));
		a.setMeta(se);
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
		return null;
	}
	////////////////////////////////////////////////////////////////////////
}
