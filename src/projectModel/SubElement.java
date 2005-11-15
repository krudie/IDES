package projectModel;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author agmi02
 *
 * This class represents an element in an xml document.
 * It can hold attributes and character data.
 * 
 */
public class SubElement extends SubElementContainer implements Cloneable{
    private Hashtable<String, String> attributeList;

    private String name;

    private String chars;

    private boolean initialized = false;
    
    /**
     * constructs a subelement with the given name.
     * @param name the name of the subelement.
     */
    public SubElement(String name){
        this.name = name;
    }
    /**
     * constructs a clone of the subelement se.
     * @param se the subelement to be cloned.
     */
    public SubElement(SubElement se){
        super(se);
        this.name = se.name;
        this.chars = se.chars;
        if(se.initialized){
            initialize();
            Enumeration<String> names = se.getAttributeNames();
            Enumeration<String> values = se.getAttributeValues();
            while(names.hasMoreElements()){
                this.setAttribute(names.nextElement(), values.nextElement());
            }
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public SubElement clone(){
        return new SubElement(this);
    }
    
    private void initialize(){
        initialized = true;
        attributeList = new Hashtable<String, String>(5);
    }
    
    private void deinitialize(){
        initialized = false;
        attributeList = null;
    }

    /**
     * returns the name of the subelement.
     * @return the name of the subelement.
     */
    public String getName(){
        return name;
    }

    /**
     * set a new name for the subelement.
     * @param name the new name of the subelement.
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * returns the character data this subelement holds, or null if it is empty.
     * @return the character data of this subelement.
     */
    public String getChars(){
        return chars;
    }

    /**
     * sets the character data of this subelement.
     * @param chars the character data of this subelement.
     */
    public void setChars(String chars){
        this.chars = chars;
    }

    /**
     * returns an attribute of the subelement.
     * @param aName the name of the attribute
     * @return the attribute named name, null if it doesn't exit.
     */
    public String getAttribute(String aName){
        return initialized ? attributeList.get(aName) : null;
    }

    /**
     * sets an attribute of the subelement.
     * @param aName the name of the attribute.
     * @param attr the value of the attribute.
     */
    public void setAttribute(String aName, String attr){
        if(!initialized) initialize();
        attributeList.put(aName, attr);
    }

    /**
     * removes an attribute from the subelement.
     * @param aName the name of the attribute that should be removed.
     */
    public void removeAttribute(String aName){
        if(initialized){
            attributeList.remove(aName);
            if(attributeList.size() == 0) deinitialize();
        }
    }

    /**
     * returns true if this subelement has the attribute.
     * @param aName the name of the attribute.
     * @return true if the attribute exist.
     */
    public boolean hasAttribute(String aName){
        return initialized && attributeList.containsKey(aName);
    }

    /**
     * returns the values of all attributes in this subelelent,
     * ordered in the same way as the attribute names.
     * @return all values of attributes of this subelement.
     */
    public Enumeration<String> getAttributeValues(){
        return initialized ? attributeList.elements() : new Hashtable<String, String>().elements();
    }

    /**
     * returns the names of all attributes in this subelelent,
     * ordered in the same way as the attribute values.
     * @return all names of attributes of this subelement.
     */
    public Enumeration<String> getAttributeNames(){
        return initialized ? attributeList.keys() : new Hashtable<String, String>().keys();
    }   
}
