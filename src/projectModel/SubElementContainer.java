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
public class SubElementContainer implements Cloneable{
    private Hashtable<String, SubElement> subElementList;

    public SubElementContainer(){
        subElementList = new Hashtable<String, SubElement>();
    }
    
    public SubElementContainer(SubElementContainer sec){
        subElementList = new Hashtable<String, SubElement>();
        Enumeration<SubElement> see = sec.getSubElements();
        while(see.hasMoreElements()){
            this.addSubElement(new SubElement(see.nextElement()));
        }
    }
    
    public SubElementContainer clone(){
        return new SubElementContainer(this);
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

    public boolean hasSubElement(String aName){
        return subElementList.containsKey(aName);
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
