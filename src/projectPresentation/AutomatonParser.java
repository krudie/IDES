package projectPresentation;

import projectModel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * 
 * @author agmi02
 *
 */
public class AutomatonParser extends AbstractFileParser{
    private int state = STATE_IDLE;
    
    private static final int STATE_IDLE = 0,
                             STATE_DOCUMENT = 1,
                             STATE_AUTOMATON = 2,
                             STATE_STATE = 3,
                             STATE_TRANSITION = 4,
                             STATE_EVENT = 5;

    private File file;
    private Automaton a;
    private SubElementContainer sec;
    
    public AutomatonParser(){
        super();
    }
    
    public Automaton parse(File f){
        state = STATE_IDLE;
        a = null;
        file = f;
        parsingErrors = "";
        try{
            xr.parse(new InputSource(new FileInputStream(f)));
        }
        catch(FileNotFoundException fnfe){
            parsingErrors += file.getName()+": "+fnfe.getMessage()+"\n";
        }
        catch(IOException ioe){
            parsingErrors += file.getName()+": "+ioe.getMessage()+"\n";
        }
        catch(SAXException saxe){
            parsingErrors += file.getName()+": "+saxe.getMessage()+"\n";
        }
        state = STATE_IDLE;
        return a;
    }
    
    public void startDocument(){
        if(state != STATE_IDLE)
            parsingErrors += file.getName()+": wrong state at start of document.";
        state = STATE_DOCUMENT;
    }
    public void endDocument(){
        if(state != STATE_DOCUMENT)
            parsingErrors += file.getName()+": wrong state at end of document.\n";
        state = STATE_IDLE;
    }
    
    public void startElement(String uri, String localName, String qName, Attributes atts){
        switch(state){
        case(STATE_IDLE):
            parsingErrors += file.getName()+": in state idle at start of element.\n";
            break;
        case(STATE_DOCUMENT):
            if(!qName.equals(ELEMENT_AUTOMATON)){
                parsingErrors += file.getName()+": encountered wrong start of element in state document.\n";
                break;
            }
            else{
                a = new Automaton(ParsingToolbox.removeFileType(file.getName()));
                state = STATE_AUTOMATON;
            }
            break;
        case(STATE_AUTOMATON):
            if(qName.equals(ELEMENT_STATE)){
                sec = new State(Integer.parseInt(atts.getValue(ATTRIBUTE_ID)));
                a.addState((State)sec);
                state = STATE_STATE;
            }
            else if(qName.equals(ELEMENT_EVENT)){
                sec = new Event(Integer.parseInt(atts.getValue(ATTRIBUTE_ID)));
                a.addEvent((Event)sec);
                state = STATE_EVENT;
            }
            else if(qName.equals(ELEMENT_TRANSITION)){
                //test code..... make it better!
                if(atts.getValue(ATTRIBUTE_EVENT) == null){
                    sec = new Transition(Integer.parseInt(atts.getValue(ATTRIBUTE_ID)),
                            a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_SOURCE_ID))),
                            a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_TARGET_ID))));
                }
                else{
                    sec = new Transition(Integer.parseInt(atts.getValue(ATTRIBUTE_ID)),
                            a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_SOURCE_ID))),
                            a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_TARGET_ID))),
                            a.getEvent(Integer.parseInt(atts.getValue(ATTRIBUTE_EVENT))));
                }
                a.addTransition((Transition)sec);
                state = STATE_TRANSITION;
            }
            break;
        case(STATE_STATE):
        case(STATE_TRANSITION):
        case(STATE_EVENT):
            parseSubElement();
            break;
        default:
            parsingErrors += file.getName()+": encountered wrong state at beginning of element.\n";
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
                parsingErrors += file.getName()+": Wrong element endend while in state automaton.\n";
            }
            break;
        case(STATE_STATE):
            if(qName.equals(ELEMENT_STATE)) state = STATE_AUTOMATON;
            else parsingErrors += file.getName()+": Wrong element endend while in state state.\n";
            break;
        case(STATE_TRANSITION):
            if(qName.equals(ELEMENT_TRANSITION)) state = STATE_AUTOMATON;
            else parsingErrors += file.getName()+": Wrong element endend while in state transition.\n";
            break;
        case(STATE_EVENT):
            if(qName.equals(ELEMENT_EVENT)) state = STATE_AUTOMATON;
            else parsingErrors += file.getName()+": Wrong element endend while in state event.\n";
            break;
        default:
            parsingErrors += file.getName()+": encountered wrong state at end of element.\n";
            break;
        }
    }    

    public void parseSubElement(){
    }
}
