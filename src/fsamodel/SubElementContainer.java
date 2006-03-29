package fsamodel;

import java.util.Enumeration;
import java.util.Hashtable;

/** 
 * This class is the superclass of elements in an automaton. 
 * It provides operations for manipulating subelements of an element,
 * e.g. observable, controllable. 
 * (Just make sure you type the names of the attributes in the same every time.)
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 * @author Helen Bretzke
 */
public class SubElementContainer implements Cloneable{

	public static final int DEFAULT_NUM_SUBELEMENTS = 5;
	
	// attributes of this element
    private Hashtable<String, SubElement> subElementList;
    private boolean initialized = false;

    /**
     * constructs a new empty subelementcontainer.
     */
    public SubElementContainer(){
    }
    
    /**
     * constructs a clone of the given subelementcontainer sec.
     * @param sec the subelementcontainer to clone.
     */
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
    
    
    /**
     * @see java.lang.Object#clone()
     */
    public SubElementContainer clone(){
        return new SubElementContainer(this);
    }

    private void initialize(){
        initialized = true;
        subElementList = new Hashtable<String, SubElement>(DEFAULT_NUM_SUBELEMENTS);
    }
    private void deinitialize(){
        initialized = false;
        subElementList = null;
    }
    
    /**
     * returns the subelements in this subelementcontainer.
     * @return all sublements in this subelementcontainer.
     */
    public Enumeration<SubElement> getSubElements(){
        return initialized ? subElementList.elements() : new Hashtable<String, SubElement>().elements();
    }

    /**
     * returns a subelement with the given name, or null if it is not
     * in this subelementcontainter.
     * @param aName the name of the subelement.
     * @return the subelement with the gibven name.
     */
    public SubElement getSubElement(String aName){
        return initialized ? subElementList.get(aName) : null;
    }

    /**
     * adds a subelement to the subelementcontainer.
     * If a subelement with the same name as the subelement that is about 
     * to be added allready exist in this subelementcontainer
     * the existing is overridden by the new.
     * @param s the subelement to add. 
     */
    public void addSubElement(SubElement s){
        if(!initialized) initialize();
        subElementList.put(s.getName(), s);
    }

    /**
     * removes a subelement from the subelementcontainer.
     * @param aName the name of the subelement to remove.
     */
    public void removeSubElement(String aName){
        if(initialized){
            subElementList.remove(aName);
            if(subElementList.size() == 0) deinitialize();
        }
    }

    /**
     * returns true if a subelement with the given name exist
     * in this subelementcontainer.
     * @param aName the name of the subelement.
     * @return true if this sublementcontainer contains a subelement with the given name.
     */
    public boolean hasSubElement(String aName){
        return initialized && subElementList.containsKey(aName);
    }

    /**
     * returns true if this subelementcontainer is empty.
     * @return true if this subelementcontainer is empty.
     */
    public boolean isEmpty(){
        return !initialized || subElementList.isEmpty();
    }
}
