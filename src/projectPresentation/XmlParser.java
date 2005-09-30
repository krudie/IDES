package projectPresentation;

import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import projectModel.*;

public class XmlParser implements ContentHandler{
    private XMLReader xr;
    private Automaton a;
    private AutomatonElement le;
    
    public XmlParser(){
        try{
            xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            xr.setContentHandler(this);
        }
        catch(ParserConfigurationException pce){
            System.err.println("XmlParser: could not configure parser, message: "+ pce.getMessage());
        }
        catch(SAXException se){
            System.err.println("XmlParser: could not do something, message: "+se.getMessage());
        }
    }

    public Automaton parse(File f) throws FileNotFoundException, IOException, SAXException{
        xr.parse(new InputSource(new FileInputStream(f)));
        return a;
    }

    private String attribute = null;
    public void	characters(char[] ch, int start, int length){
        String t = "";
        for(int i = 0; i<length; i++){
            t += ch[start+i];
        }
        if(t.trim().equals("")) return;
        attribute = attribute == null ? t:attribute+t;
    }
    public void endDocument(){
    }
    public void	endPrefixMapping(String prefix){
    }
    public void ignorableWhitespace(char[] ch, int start, int length){
    }
    public void processingInstruction(String target, String data){
    }
    public void setDocumentLocator(Locator locator){
    }
    public void skippedEntity(String name){
    }
    public void startDocument(){
            a = new Automaton();
    }
    public void startElement(String uri, String localName, String qName, Attributes atts){
        if(qName.equals(State.class.getName())){
            if(atts.getQName(1).equals("id"))
                a.addState((State)(le = new State(Integer.getInteger(atts.getValue(1)))));
        }
        System.out.println("qName "+qName);        
        for(int i = 0; i < atts.getLength(); i++){
            System.out.println("attribute: "+atts.getQName(i)
                    +" value: "+atts.getValue(i));
        }
    }
    public void endElement(String uri, String localName, String qName){
        if(attribute != null){
            if(le == null){
                System.out.println("fejl!");
                return;
            }
            if(le.getClass().getName().equals(qName));
            System.out.println("text: ["+attribute+"]");
        }
        attribute = null;
    }
    public void startPrefixMapping(String prefix, String uri){
    }
    
    
    public static void main(String args[]){
        XmlParser x = new XmlParser();
        try{
            x.parse(new File("/home/agmi02/code/test.xml"));
        }
        catch(SAXException SAXE){
        }
        catch(FileNotFoundException fnfe){            
        }
        catch(IOException ioe){   
        }
        System.out.println("done!");
    }
}
