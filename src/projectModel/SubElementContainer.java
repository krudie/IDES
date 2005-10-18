package projectModel;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Axel Gottlieb Michelsen
 * 
 * This class is the superclass of elements in an automaton (suprise suprise) At
 * the moment it holds functionality for manipulating attributes of an element,
 * e.g. observable, controlable. (Just make sure you type the names of the
 * attributes in the same every time.)
 * 
 */
public class SubElementContainer{
    private Hashtable<String, SubElement> subElementList;

    public SubElementContainer(){
        subElementList = new Hashtable<String, SubElement>();
    }

    public Enumeration<SubElement> getSubElements(){
        return subElementList.elements();
    }

    public SubElement getSubElement(String aName){
        return subElementList.get(aName);
    }

    public void addSubElement(SubElement s){
        subElementList.put(s.getName(), s);
    }

    public void removeSubElement(String aName){
        subElementList.remove(aName);
    }

    public void hasSubElement(String aName){
        subElementList.containsKey(aName);
    }

    public boolean isEmpty(){
        return subElementList.isEmpty();
    }

    public void toXML(PrintStream ps, String indent){
        Enumeration<SubElement> see = subElementList.elements();
        while(see.hasMoreElements())
            see.nextElement().toXML(ps, indent);
    }
}
