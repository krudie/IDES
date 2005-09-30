package projectModel;

import java.util.*;

/**
 * @author Axel Gottlieb Michelsen
 * 
 * This class is the superclass of elements in an automaton (suprise suprise)
 * At the moment it holds functionality for manipulating attributes of
 * an element, e.g. observable, controlable. 
 * (Just make sure you type the names of the attributes in the same every time.)
 *
 */
public class AutomatonElement {
	private Hashtable<String,String> attributeList;
	public AutomatonElement(){
		attributeList = new Hashtable<String,String>();
	}
	public String getAttribute(String aName) throws NullPointerException{
		return attributeList.get(aName);
	}
	public void setAttribute(String aName, String attr) throws NullPointerException{
		attributeList.put(aName, attr);
	}
	public void removeAttribute(String aName) throws NullPointerException{
		attributeList.remove(aName);
	}	
	public void hasAttribute(String aName){
		attributeList.containsKey(aName);
	}
	
	/**
	 * Test
	 */
	public static void main(String args[]){
		AutomatonElement ae = new AutomatonElement();
		ae.setAttribute("integer", "42");
		ae.setAttribute("text"," is the answer to the universe, and all that: ");
		ae.setAttribute("bool", new Boolean(true).toString());

		System.out.println(ae.getAttribute("integer")
				+ae.getAttribute("text")
				+ae.getAttribute("bool"));
	}
}
