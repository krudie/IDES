/**
 * 
 */
package io.fsa.ver1;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import model.fsa.ver1.Automaton;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;

import io.AbstractParser;

/**
 * 
 * @author Lenko Grigorov
 */
public class LayoutDataParser extends AbstractParser {

    private int state = STATE_IDLE;

    protected static final String ELEMENT_STATE = "state",
    ELEMENT_TRANSITION = "transition", ELEMENT_GRAPHIC = "graphic", ELEMENT_META = "meta",
    ELEMENT_FONT = "font";

    protected static final String ATTRIBUTE_ID = "id", ATTRIBUTE_SIZE = "size";

    private static final int STATE_IDLE = 0, STATE_META = 1,
    STATE_STATE = 2, STATE_TRANSITION = 3, STATE_FONT = 4;
    
	Automaton a=null;
	ContentHandler ch=null;
	
	public LayoutDataParser(Automaton a) {
		super();
		this.a=a;
	}

	/**
	 * @see io.AbstractFileParser#parse(java.io.File)
	 */
	public Object parse(XMLReader xmlr, String errors) {
		this.xmlReader = xmlr;
		parsingErrors=errors;
		ch=xmlr.getContentHandler();
		xmlr.setContentHandler(this);
		return null;
	}
	
    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
    	switch(state){
        case (STATE_IDLE):
            if(qName.equals(ELEMENT_STATE)){
                if(atts.getValue(ATTRIBUTE_ID) == null){
                    parsingErrors += "Unable to parse state with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                State s = (State)a.getState(id);
                if(s==null)
                {
                	parsingErrors += "Couldn't find state with given id.\n";
                    break;
                }
                SubElement graphic=new SubElement(ELEMENT_STATE);
                SubElementParser sep = new SubElementParser();
                sep.fill(graphic, xmlReader, parsingErrors);
                graphic.setName(ELEMENT_GRAPHIC);
                s.addSubElement(graphic);
                state = STATE_IDLE;
            }
            else if(qName.equals(ELEMENT_TRANSITION)){
                if(atts.getValue(ATTRIBUTE_ID) == null){
                    parsingErrors += "Unable to parse transition with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                Transition t=(Transition)a.getTransition(id);
                if(t==null)
                {
                	parsingErrors += "Couldn't find transition with given id.\n";
                    break;
                }
                SubElement graphic=new SubElement(ELEMENT_TRANSITION);
                SubElementParser sep = new SubElementParser();
                sep.fill(graphic, xmlReader, parsingErrors);
                graphic.setName(ELEMENT_GRAPHIC);
                t.addSubElement(graphic);
                state = STATE_IDLE;
            }
            else if(qName.equals(ELEMENT_FONT)){
                if(atts.getValue(ATTRIBUTE_SIZE) == null){
                    parsingErrors += "Font with undefined size.\n";
                    break;
                }
                SubElement font=new SubElement(ELEMENT_FONT);
                font.setAttribute(ATTRIBUTE_SIZE,atts.getValue(ATTRIBUTE_SIZE));
                SubElementParser sep = new SubElementParser();
                sep.fill(font, xmlReader, parsingErrors);
                a.setMeta(font);
                state = STATE_IDLE;
            }
            break;            
        default:
            parsingErrors += "Encountered wrong beginning of element.\n";
            break;
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        switch(state){
//        case (STATE_FONT):
//            if(!qName.equals(ELEMENT_FONT))
//            {
//            	parsingErrors += "Wrong element endend while parsing metadata.\n";
//            }
//        	state=STATE_IDLE;
//        	break;
        case (STATE_IDLE):
            if(!qName.equals(ELEMENT_META))
            {
            	parsingErrors += "Wrong element endend while parsing metadata.\n";
            }
            break;
        default:
            parsingErrors += "encountered wrong state at end of element.\n";
            break;
        }
    	xmlReader.setContentHandler(ch);
    }

}
