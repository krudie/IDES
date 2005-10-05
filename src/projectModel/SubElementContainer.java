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
public class SubElementContainer {
    private Hashtable<String,SubElement> subElementList;
    
    public SubElementContainer(){
        subElementList = new Hashtable<String,SubElement>();
    }
    
    public SubElement[] getSubElements(){
        SubElement[] s = new SubElement[subElementList.size()];
        Iterator<SubElement> sei = subElementList.values().iterator();
        for(int i = 0; i < subElementList.size(); i++){
            s[i] = sei.next();
        }
        return s;
    }
    
    public SubElement getSubElement(String aName) throws NullPointerException{
        return subElementList.get(aName);
    }
    public void addSubElement(String aName, SubElement s) throws NullPointerException{
        subElementList.put(aName, s);
    }
    public void removeSubElement(String aName) throws NullPointerException{
        subElementList.remove(aName);
    }   
    public void hasSubElement(String aName){
        subElementList.containsKey(aName);
    }
}
