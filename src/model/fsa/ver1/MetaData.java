package model.fsa.ver1;

import java.awt.Point;

import presentation.fsa.StateLayout;
import presentation.fsa.TransitionLayout;
import ui.Publisher;
import io.fsa.ver1.SubElement;
import model.DESElement;
import model.fsa.FSAMetaData;
import model.fsa.FSAState;
import model.fsa.FSATransition;

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

	public TransitionLayout getLayoutData(FSATransition t){
		
		return null;
	}	
}
