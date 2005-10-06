package projectModel;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;

public class SubElement extends SubElementContainer{
    private Hashtable<String,String> attributeList;
    private String name;
    private String chars;
    
    public SubElement(String name){
        this.name = name;
        attributeList = new Hashtable<String,String>();
    }
    
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getChars(){
        return chars;
    }
    public void setChars(String chars){
        this.chars = chars;
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
    public Enumeration<String> getAttributeValues(){
        return attributeList.elements();
    }
    public Enumeration<String> getAttributeNames(){
        return attributeList.keys();
    }
    
    public void toXML(PrintStream ps, String indent){
        ps.print(indent+"<"+name);
        Enumeration<String> av = attributeList.elements();
        Enumeration<String> an = attributeList.keys();
        while(an.hasMoreElements()){
            ps.print(" "+an.nextElement()+"=\""+av.nextElement()+"\"");
        }
        if(super.isEmpty() && ((chars==null)?true:chars.trim().equals(""))){
            ps.println("/>");
            return;
        }
        ps.println(">");
        if(!super.isEmpty()){
            super.toXML(ps, indent+" ");
        }
        if(!chars.trim().equals("")){
            ps.println(chars);
        }
        ps.println(indent+"</"+name+">");
    }
    
    
    /**
     * Test
     */
    public static void main(String args[]){
        SubElement ae = new SubElement("subelement?");
        ae.setAttribute("integer", "42");
        ae.setAttribute("text"," is the answer to the universe, and all that: ");
        ae.setAttribute("bool", new Boolean(true).toString());

        System.out.println(ae.getAttribute("integer")
                +ae.getAttribute("text")
                +ae.getAttribute("bool"));
    }
}
