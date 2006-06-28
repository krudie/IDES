package io.fsa.ver1;

import io.AbstractFileParser;
import io.ParsingToolbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import model.fsa.*;
import model.fsa.ver1.*;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Objects of this class parse a file into an automaton. 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class AutomatonParser extends AbstractFileParser{
    private int state = STATE_IDLE;

    protected static final String ELEMENT_MODEL = "model",
    ELEMENT_STATE = "state",
    ELEMENT_TRANSITION = "transition", ELEMENT_EVENT = "event",
    ELEMENT_DATA = "data", ELEMENT_META = "meta";

    protected static final String ATTRIBUTE_ID = "id",
    ATTRIBUTE_SOURCE_ID = "source", ATTRIBUTE_TARGET_ID = "target",
    ATTRIBUTE_EVENT = "event", ATTRIBUTE_VERSION = "version",
    ATTRIBUTE_TYPE = "type", ATTRIBUTE_TAG = "tag";

    private static final int STATE_IGNORE = -1, STATE_IDLE = 0, STATE_DOCUMENT = 1, STATE_MODEL = 2,
    		STATE_DATA = 3, STATE_STATE = 4, STATE_TRANSITION = 5, STATE_EVENT = 6,
    		STATE_META = 7;

    private File file;

    private Automaton a;

    private SubElementContainer sec;
    
    private boolean layoutRead=false;

    /**
     * creates an automatonParser.
     */
    public AutomatonParser(){
        super();
    }

    /**
     * @see projectPresentation.AbstractFileParser#parse(java.io.File)
     */
    public Automaton parse(File f){
        state = STATE_IDLE;
        a = null;
        file = f;
        parsingErrors = "";
        layoutRead=false;
        try{
            xmlReader.parse(new InputSource(new FileInputStream(f)));
        }
        catch(FileNotFoundException fnfe){
            parsingErrors += file.getName() + ": " + fnfe.getMessage() + "\n";
        }
        catch(IOException ioe){
            parsingErrors += file.getName() + ": " + ioe.getMessage() + "\n";
        }
        catch(SAXException saxe){
            parsingErrors += file.getName() + ": " + saxe.getMessage() + "\n";
        }
        state = STATE_IDLE;
        return a;
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument(){
        if(state != STATE_IDLE) parsingErrors += file.getName()
                + ": wrong state at start of document.";
        state = STATE_DOCUMENT;
    }

    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument(){
        if(state != STATE_DOCUMENT) parsingErrors += file.getName()
                + ": wrong state at end of document.\n";
        if(!layoutRead)
        	parsingErrors += file.getName() + ": layout data missing.";
        state = STATE_IDLE;
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName, Attributes atts){
        switch(state){
        case (STATE_IGNORE):
        case (STATE_META):
        	break;
        case (STATE_IDLE):
            parsingErrors += file.getName() + ": in state idle at start of element.\n";
            break;
        case (STATE_DOCUMENT):
            if(!qName.equals(ELEMENT_MODEL)){
                parsingErrors += file.getName()
                        + ": encountered wrong start of element in state document.\n";
                break;
            }
    		if(!"2.1".equals(atts.getValue(ATTRIBUTE_VERSION)))
    		{
    			parsingErrors+=file.getName()+": wrong file format version.";
    		}
    		if(!"FSA".equals(atts.getValue(ATTRIBUTE_TYPE)))
    		{
    			parsingErrors+=file.getName()+": this type of model is not supported.";
    			state = STATE_IGNORE;
    		}
    		else
    		{
                a = new Automaton(ParsingToolbox.removeFileType(file.getName()));
                a.setId(atts.getValue(ATTRIBUTE_ID));
    			state = STATE_MODEL;
    		}
            break;
        case (STATE_MODEL):
            if(qName.equals(ELEMENT_DATA)){
                state = STATE_DATA;
            }
            else if(qName.equals(ELEMENT_META))
            {
            	if(!"2.1".equals(atts.getValue(ATTRIBUTE_VERSION)))
            	{
            		parsingErrors+=file.getName()+": wrong file format version.";
            	}
            	if(!"layout".equals(atts.getValue(ATTRIBUTE_TAG)))
            	{
            		state = STATE_META;
            	}
            	if(a==null)
            	{
            		parsingErrors+=file.getName()+": metadata requires model which" +
					"may have not been parsed yet.";
            		state = STATE_META;
            	}
            	if(state!=STATE_META)
            	{
           			LayoutDataParser ldp=new LayoutDataParser(a);
           			ldp.parse(this.xmlReader,parsingErrors);
           			layoutRead=true;
            	}
            }
            else
            {
            	parsingErrors += file.getName()
            	+ ": encountered wrong start of element in state model.\n";
            }
            break;
        case (STATE_DATA):
            if(qName.equals(ELEMENT_STATE)){
                if(atts.getValue(ATTRIBUTE_ID) == null){
                    parsingErrors += file.getName() + " Unable to parse state with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                sec = new State(id);
                a.add((FSAState) sec);
                state = STATE_STATE;
            }
            else if(qName.equals(ELEMENT_EVENT)){
                if(atts.getValue(ATTRIBUTE_ID) == null){
                    parsingErrors += file.getName() + " Unable to parse event with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                sec = new Event(id);
                a.add((FSAEvent) sec);
                state = STATE_EVENT;
            }
            else if(qName.equals(ELEMENT_TRANSITION)){
                FSAState s = a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_SOURCE_ID)));
                FSAState t = a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_TARGET_ID)));
                FSAEvent e = atts.getValue(ATTRIBUTE_EVENT) != null ? a.getEvent(Integer.parseInt(atts
                        .getValue(ATTRIBUTE_EVENT))) : null;

                if(atts.getValue(ATTRIBUTE_ID) == null){
                    parsingErrors += file.getName() + " Unable to parse transition with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));

                if(s == null || t == null){
                    parsingErrors += file.getName() + " Unable to parse transition " + id + ".\n";
                    parsingErrors += "\tstate "
                            + Integer.parseInt(atts.getValue(ATTRIBUTE_SOURCE_ID)) + " or "
                            + a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_TARGET_ID)))
                            + "does not exist, or have not been parsed yet.";
                }
                else if(e == null){
                    sec = new Transition(id, s, t);
                    a.add((FSATransition) sec);
                }
                else{
                    sec = new Transition(id, s, t, e);
                    a.add((FSATransition) sec);
                }
                state = STATE_TRANSITION;
            }
            break;
        case (STATE_STATE):
        case (STATE_TRANSITION):
        case (STATE_EVENT):
            SubElement nse = new SubElement(qName);
            for(int i = 0; i < atts.getLength(); i++){
                nse.setAttribute(atts.getQName(i), atts.getValue(i));
            }
            sec.addSubElement(nse);
            SubElementParser sep = new SubElementParser();
            sep.fill(nse, xmlReader, parsingErrors);
            break;
        default:
            parsingErrors += file.getName() + ": encountered wrong beginning of element.\n";
            break;
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName){
        switch(state){
        case (STATE_IGNORE):
        	break;
        case (STATE_META):
            if(qName.equals(ELEMENT_META)){
                state = STATE_MODEL;
            }
        	break;        	
        case (STATE_MODEL):
            if(qName.equals(ELEMENT_MODEL)){
                state = STATE_DOCUMENT;
            }
            else{
                parsingErrors += file.getName()
                        + ": Wrong element endend while in state model.\n";
            }
        	break;
        case (STATE_DATA):
            if(qName.equals(ELEMENT_DATA)){
                state = STATE_MODEL;
            }
            else{
                parsingErrors += file.getName()
                        + ": Wrong element endend while in state data.\n";
            }
            break;
        case (STATE_STATE):
            if(qName.equals(ELEMENT_STATE)) state = STATE_DATA;
            else parsingErrors += file.getName() + ": Wrong element endend while in state state.\n";
            break;
        case (STATE_TRANSITION):
            if(qName.equals(ELEMENT_TRANSITION)) state = STATE_DATA;
            else parsingErrors += file.getName()
                    + ": Wrong element endend while in state transition.\n";
            break;
        case (STATE_EVENT):
            if(qName.equals(ELEMENT_EVENT)) state = STATE_DATA;
            else parsingErrors += file.getName() + ": Wrong element endend while in state event.\n";
            break;
        default:
            parsingErrors += file.getName() + ": encountered wrong state at end of element.\n";
            break;
        }
    }
}
