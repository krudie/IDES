package presentation.fsa;

import java.util.Iterator;

import presentation.PresentationElement;

public class SelectionGroup extends GraphElement {

	/**	
	 * @return a shallow copy of references to all children
	 * in this group.
	 */
	public SelectionGroup copy(){
		SelectionGroup copy = new SelectionGroup();
		Iterator children = children();
		while(children.hasNext()){
			copy.insert((PresentationElement)children.next());
		}
		return copy;
	}
	
	/**
	 * Test and flag edges before moving the group.
	 * 
	 * For all e in the selection group
	 * If both s and t are in this group, flag e as a simple translation
	 * otherwise e will need to be recomputed to preserve its shape when
	 * only one of its edges is moved.
	 * 
	 * Then translates all nodes (nodes will see to moving their edges...
	 * ??? If nodes are responsible for moving their edges, then why not have them check
	 * the edges for ... 
	 * A: because nodes don't know about the group to which they belong!
	 * 
	 */
	public void translate(float x, float y){
		Iterator children = children();
		while(children.hasNext()){
			Object o = children.next();
			if(o instanceof Edge){  // YUCK evil kluge indicative of poor design and looming deadline.
				Edge e = (Edge)o;
				if( contains(e.getSource()) && contains(e.getTarget()) ){
					((EdgeLayout)e.getLayout()).setRigidTranslation(true);  
				}else{
					((EdgeLayout)e.getLayout()).setRigidTranslation(false);
				}
				// ??? What about Edges adjacent to translated Nodes that are 
				// NOT in the selection group?  Default to FALSE (assume recomputation required).
			}	
		}
		super.translate(x,y);
	}

	/**
	 * @return
	 */
	public boolean hasMultipleElements() {
		if(hasChildren()){
			return this.child(1) != null;
		}
		return false;
	}
}
