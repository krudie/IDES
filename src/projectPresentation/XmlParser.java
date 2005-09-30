package projectPresentation;

import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class XmlParser implements ContentHandler{
    private XMLReader xr;
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

    public void parse(File f) throws FileNotFoundException, IOException, SAXException{
	xr.parse(new InputSource(new FileInputStream(f)));
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
    }
    public void startElement(String uri, String localName, String qName, Attributes atts){
        System.out.println("uri: "+uri
                +", localName: "+localName
                +"qName "+qName);
        for(int i = 0; i < atts.getLength(); i++){
            System.out.println("attribute: "+atts.getQName(i)
                    +" value: "+atts.getValue(i));
        }
    }
    public void endElement(String uri, String localName, String qName){
        if(attribute != null) System.out.println("text: ["+attribute+"]");
        attribute = null;
    }
    public void startPrefixMapping(String prefix, String uri){
    }
    
    
    public static void main(String args[]){
        XmlParser x = new XmlParser();
        try{
            x.parse(new File("/home/agmi02/code/test.xml"));
        }
        catch(Exception e){
            System.out.println("hov! "+e);
        }
        System.out.println("done!");
    }
}
