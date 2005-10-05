package projectModel;

import java.util.Hashtable;

public class SubElement extends SubElementContainer{
    private Hashtable<String,String> attributeList;
    
    public SubElement(){
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
        SubElement ae = new SubElement();
        ae.setAttribute("integer", "42");
        ae.setAttribute("text"," is the answer to the universe, and all that: ");
        ae.setAttribute("bool", new Boolean(true).toString());

        System.out.println(ae.getAttribute("integer")
                +ae.getAttribute("text")
                +ae.getAttribute("bool"));
    }
}
