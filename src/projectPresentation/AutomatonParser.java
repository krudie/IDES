package projectPresentation;

import projectModel.*;

import java.io.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * 
 * @author agmi02
 *
 */
public class AutomatonParser extends AbstractParser{
    private int state = 0;
    
    private static final int STATE_IDLE = 0,
                             STATE_DOCUMENT = 1,
                             STATE_AUTOMATON = 2,
                             STATE_STATE = 3,
                             STATE_TRANSITION = 4,
                             STATE_EVENT = 5;
    
    private static final String ELEMENT_AUTOMATON = "automaton",
                                ELEMENT_STATE = "state",
                                ELEMENT_TRANSITION = "transition",
                                ELEMENT_EVENT = "event";
    
    private static final String ATTRIBUTE_ID = "id",
                                ATTRIBUTE_SOURCE_ID = "source",
                                ATTRIBUTE_TARGET_ID = "target",
                                ATTRIBUTE_EVENT = "event";
    
    private XMLReader xr;

    private File file;
    private Automaton a;
    private AutomatonElement ae;
    
    public AutomatonParser(){
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
        state = STATE_IDLE;
        a = null;
        file = f;
        xr.parse(new InputSource(new FileInputStream(f)));
        state = STATE_IDLE;
        return a;
    }
    
    public void startDocument(){
        if(state != STATE_IDLE)
            System.err.println("AutomatonParser: wrong state at start of document.");
        state = STATE_DOCUMENT;
    }
    public void endDocument(){
        if(state != STATE_DOCUMENT)
            System.err.println("AutomatonParser: wrong state at end of document.");
        state = STATE_IDLE;
    }
    
    public void startElement(String uri, String localName, String qName, Attributes atts){
        switch(state){
        case(STATE_IDLE):
            System.err.println("AutomatonParser: in state idle at start of element.");
            break;
        case(STATE_DOCUMENT):
            if(!qName.equals(ELEMENT_AUTOMATON)){
                System.err.println("AutomatonParser: encountered wrong start of element in state document.");
                break;
            }
            else{
                a = new Automaton(ParsingToolbox.removeFileType(file.getName()));
                state = STATE_AUTOMATON;
            }
            break;
        case(STATE_AUTOMATON):
            if(qName.equals(ELEMENT_STATE)){
                ae = new State(Integer.parseInt(atts.getValue(ATTRIBUTE_ID)));
                a.addState((State)ae);
                state = STATE_STATE;
            }
            else if(qName.equals(ELEMENT_EVENT)){
                ae = new Event(Integer.parseInt(atts.getValue(ATTRIBUTE_ID)));
                a.addEvent((Event)ae);
                state = STATE_EVENT;
            }
            else if(qName.equals(ELEMENT_TRANSITION)){
                //test code..... make it better!
                if(atts.getValue(ATTRIBUTE_EVENT) == null){
                    ae = new Transition(Integer.parseInt(atts.getValue(ATTRIBUTE_ID)),
                            a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_SOURCE_ID))),
                            a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_TARGET_ID))));
                }
                else{
                    ae = new Transition(Integer.parseInt(atts.getValue(ATTRIBUTE_ID)),
                            a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_SOURCE_ID))),
                            a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_TARGET_ID))),
                            a.getEvent(Integer.parseInt(atts.getValue(ATTRIBUTE_EVENT))));
                }
                a.addTransition((Transition)ae);
                state = STATE_TRANSITION;
            }
            break;
        case(STATE_STATE):
        case(STATE_TRANSITION):
        case(STATE_EVENT):
            parseSubElement();
            break;
        default:
            System.err.println("AutomatonParser: encountered wrong state at beginning of element.");
            break;
        }
    }
    
    public void endElement(String uri, String localName, String qName){
        switch(state){
        case(STATE_AUTOMATON):
            if(qName.equals(ELEMENT_AUTOMATON)){
                state = STATE_DOCUMENT;
            }
            else{
                System.err.println("AutomatonParser: Wrong element endend while in state automaton.");
            }
            break;
        case(STATE_STATE):
            if(qName.equals(ELEMENT_STATE)) state = STATE_AUTOMATON;
            else System.err.println("AutomatonParser: Wrong element endend while in state state.");
            break;
        case(STATE_TRANSITION):
            if(qName.equals(ELEMENT_TRANSITION)) state = STATE_AUTOMATON;
            else System.err.println("AutomatonParser: Wrong element endend while in state transition.");
            break;
        case(STATE_EVENT):
            if(qName.equals(ELEMENT_EVENT)) state = STATE_AUTOMATON;
            else System.err.println("AutomatonParser: Wrong element endend while in state event.");
            break;
        default:
            System.err.println("AutomatonParser: encountered wrong state at end of element.");
            break;
        }
    }    

    public void parseSubElement(){
    }
}
