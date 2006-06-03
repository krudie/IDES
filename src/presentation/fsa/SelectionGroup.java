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
	
}
