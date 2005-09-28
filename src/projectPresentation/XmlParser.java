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

    public void	characters(char[] ch, int start, int length){
    }
    public void endDocument(){
    }
    public void	endElement(String uri, String localName, String qName){
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
    }
    public void startPrefixMapping(String prefix, String uri){
    }
}
