package projectPresentation;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class AbstractParser implements ContentHandler{

    protected static final String ELEMENT_AUTOMATON = "automaton",
                                  ELEMENT_PROJECT = "project",
                                  ELEMENT_STATE = "state",    
                                  ELEMENT_TRANSITION = "transition",
                                  ELEMENT_EVENT = "event";

    protected static final String ATTRIBUTE_ID = "id",
                                  ATTRIBUTE_SOURCE_ID = "source",
                                  ATTRIBUTE_TARGET_ID = "target",
                                  ATTRIBUTE_EVENT = "event",
                                  ATTRIBUTE_FILE = "file";

    protected String parsingErrors = "";
    protected XMLReader xmlr;   
    
    public String getParsingErrors(){
        return parsingErrors;
    }
       
    public void setDocumentLocator(Locator locator){
    }
    public void startDocument() throws SAXException {
    }
    public void endDocument() throws SAXException {
    }
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }
    public void endPrefixMapping(String prefix) throws SAXException {
    }
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    }
    public void endElement(String uri, String localName, String qName) throws SAXException {
    }
    public void characters(char[] ch, int start, int length) throws SAXException {
    }
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }
    public void processingInstruction(String target, String data) throws SAXException {
    }  
    public void skippedEntity(String name) throws SAXException {
    }
}
