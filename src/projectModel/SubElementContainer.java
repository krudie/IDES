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
    private boolean initialized = false;

    public SubElementContainer(){
    }
    
    public SubElementContainer(SubElementContainer sec){
        if(sec.initialized){
            this.initialize();
            subElementList = new Hashtable<String, SubElement>();
            Enumeration<SubElement> see = sec.getSubElements();
            while(see.hasMoreElements()){
                this.addSubElement(new SubElement(see.nextElement()));
            }
        }
    }
    
    public SubElementContainer clone(){
        return new SubElementContainer(this);
    }

    private void initialize(){
        initialized = true;
        subElementList = new Hashtable<String, SubElement>(5);
    }

    public Enumeration<SubElement> getSubElements(){
        return initialized ? subElementList.elements() : new Hashtable<String, SubElement>().elements();
    }

    public SubElement getSubElement(String aName){
        return initialized ? subElementList.get(aName) : null;
    }

    public void addSubElement(SubElement s){
        if(!initialized) initialize();
        subElementList.put(s.getName(), s);
    }

    public void removeSubElement(String aName){
        if(initialized) subElementList.remove(aName);
    }

    public boolean hasSubElement(String aName){
        return initialized && subElementList.containsKey(aName);
    }

    public boolean isEmpty(){
        return !initialized || subElementList.isEmpty();
    }

    public void toXML(PrintStream ps, String indent){
        if(!initialized) return;
        Enumeration<SubElement> see = subElementList.elements();
        while(see.hasMoreElements())
            see.nextElement().toXML(ps, indent);
    }
}
