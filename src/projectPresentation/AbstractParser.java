package projectPresentation;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class AbstractParser implements ContentHandler {

    protected String parsingErrors = "";

    public String getParsingErrors(){
        return parsingErrors;
    }
    
    public abstract Object parse(File f);
    
   
    public void setDocumentLocator(Locator locator){
    }
    public void startDocument() throws SAXException {
    }
    public void endDocument() throws SAXException {
    }
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }
    public void endPrefixMapping(String prefix) throws SAXException {
    }
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
    }
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    }
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    }
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
    }
    public void processingInstruction(String target, String data)
            throws SAXException {
    }  
    public void skippedEntity(String name) throws SAXException {
    }
}
