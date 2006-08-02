/**
 * 
 */
package presentation.fsa;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import model.fsa.FSATransition;
import model.fsa.ver1.Transition;

/**
 * 
 * @author helen bretzke
 *
 */
public abstract class Edge extends GraphElement{

	private ArrayList<FSATransition> transitions; // the transitions that this edge represents	
	private Node source, target;	
	private EdgeHandler handler; // Anchors for modifying the curve.
	private GraphLabel label;	 // extra pointer for O(1) access without instanceof
	
	public Edge(Node source)
	{
		this(source, null);
	}
	
	public Edge(Node source, Node target, FSATransition t)
	{
		this(source, target);	    
	    transitions.add(t);
	}	
	
	// ? Add a default transition? NO, since ID is missing.
	public Edge(Node source, Node target)  
	{
		transitions = new ArrayList<FSATransition>();
		this.source = source;
		this.target = target;
		this.label = new GraphLabel("");		
		insert(label);
	}
	
	/**
	 * TODO comment
	 * 
	 * @param selectionBox
	 * @param exportType
	 * @return
	 */
	public abstract String createExportString(Rectangle selectionBox, int exportType);
	
	
	/**
	 * Sets the handler for this edge with <code>handler</code>. 
	 * Replace the current edge handler (if exist) with the <code>handler</code>. 
	 * 
	 * @param handler
	 */
	public void setHandler(EdgeHandler handler) {
		if(this.handler != null)  
		{
			remove(this.handler);
		}
		this.handler = handler;
		this.insert(handler);
	}
	
	public EdgeHandler getHandler() {
		return handler;
	}	
	
	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;		
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}
	
	public void addTransition(Transition t) {
		transitions.add(t);		
	}
	
	public void removeTransition(Transition t){
		transitions.remove(t);
	}
	
	public Iterator<FSATransition> getTransitions() {		
		return transitions.iterator();
	}
	
	public boolean hasUncontrollableEvent()
	{
		boolean hasUE=false;
		for(Iterator<FSATransition> i=getTransitions();i.hasNext();)
		{
			FSATransition t=i.next();
			if(t.getEvent()!=null&&!t.getEvent().isControllable())
			{
				hasUE=true;
				break;
			}
		}
		return hasUE;
	}

	/**
	 * @return the number of transitions that this edge represents
	 */
	public int transitionCount() {		
		return transitions.size();
	}

	public Long getId(){
		if(!transitions.isEmpty()){
			return transitions.get(0).getId();
		}
		return super.getId();
	}
	
	public void showPopup(Component c){
		EdgePopup.showPopup((GraphDrawingView)c, this);
	}
	
	public GraphLabel getLabel() {
		return label;
	}

	public void setLabel(GraphLabel label) {
		this.remove(this.label);
		this.insert(label);
		this.label = label;
	}

	/**
	 * @param symbol
	 */
	public void addEventName(String symbol) {
		// TODO create an EdgeLayout class that will add the given symbol
		// to the layout and extend to BezierLayout.
		
	}
}
