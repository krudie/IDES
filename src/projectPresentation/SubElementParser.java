package projectPresentation;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import projectModel.SubElement;

public class SubElementParser extends AbstractParser{
    private static final int STATE_IDLE = 0,
                             STATE_WORKING = 1,
                             STATE_WAITING = 2;
    
    private int state = STATE_IDLE;
    private String chars;
    
    public SubElementParser(){
        
    }
    
    public SubElement parse() {
        parsingErrors = "";
        state = STATE_IDLE;
        return null;
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        switch(state){
        case(STATE_IDLE):
            break;
        case(STATE_WORKING):
            break;
        case(STATE_WAITING):
            break;
        default:
            break;
        }
    }
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch(state){
        case(STATE_IDLE):
            break;
        case(STATE_WORKING):
            break;
        case(STATE_WAITING):
            break;
        default:
            break;
        }
    }
    public void characters(char[] ch, int start, int length) throws SAXException {
    }
}
