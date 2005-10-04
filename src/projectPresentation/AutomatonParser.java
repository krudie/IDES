package projectPresentation;

import projectModel.*;

import java.io.*;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

/**
 * 
 * @author agmi02
 *
 */
public class AutomatonParser implements ContentHandler{
    public Automaton parse(File f){
        System.out.println("AutomatonParser: ordered to parse file: "+f.getName());
        return new Automaton();
    }
    
    public void characters(char[] ch, int start, int length){
    }
    public void endPrefixMapping(String prefix){
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
    public void endDocument(){
    }
    public void startElement(String uri, String localName, String qName, Attributes atts){
    }

    
    public void endElement(String uri, String localName, String qName){
    }

    public void startPrefixMapping(String prefix, String uri){
    }

}
