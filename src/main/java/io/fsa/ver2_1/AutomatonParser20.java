package io.fsa.ver2_1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.ModelManager;
import io.AbstractParser;
import io.ParsingToolbox;

/**
 * Objects of this class parse a file into an automaton.
 * 
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class AutomatonParser20 extends AbstractParser {
    private int state = STATE_IDLE;

    protected static final String ELEMENT_AUTOMATON = "automaton", ELEMENT_STATE = "state",
            ELEMENT_TRANSITION = "transition", ELEMENT_EVENT = "event", ELEMENT_NAME = "name",
            ELEMENT_PROPERTIES = "properties", ELEMENT_INITIAL = "initial", ELEMENT_MARKED = "marked",
            ELEMENT_OBSERVABLE = "observable", ELEMENT_CONTROLLABLE = "controllable";

    protected static final String ATTRIBUTE_ID = "id", ATTRIBUTE_SOURCE_ID = "source", ATTRIBUTE_TARGET_ID = "target",
            ATTRIBUTE_EVENT = "event", ATTRIBUTE_FILE = "file";

    private static final int STATE_IDLE = 0, STATE_DOCUMENT = 1, STATE_AUTOMATON = 2, STATE_STATE = 3,
            STATE_TRANSITION = 4, STATE_EVENT = 5, STATE_STATE_PROPERTIES = 6, STATE_STATE_NAME = 7,
            STATE_EVENT_PROPERTIES = 8, STATE_EVENT_NAME = 9;

    private File file;

    private FSAModel a;

    // private SubElementContainer sec;

    private FSAState lastState;

    private SupervisoryEvent lastEvent;

    private FSATransition lastTransition;

    private String lastLabel = "";

    /**
     * creates an automatonParser.
     */
    public AutomatonParser20() {
        super();
    }

    /**
     * @see AbstractParser
     */
    public FSAModel parse(File f) {
        state = STATE_IDLE;
        a = null;
        file = f;
        parsingErrors = "";
        try {
            xmlReader.parse(new InputSource(new FileInputStream(f)));
        } catch (FileNotFoundException fnfe) {
            parsingErrors += file.getName() + ": " + fnfe.getMessage() + "\n";
        } catch (IOException ioe) {
            parsingErrors += file.getName() + ": " + ioe.getMessage() + "\n";
        } catch (SAXException saxe) {
            parsingErrors += file.getName() + ": " + saxe.getMessage() + "\n";
        }
        state = STATE_IDLE;
        return a;
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    @Override
    public void startDocument() {
        if (state != STATE_IDLE) {
            parsingErrors += file.getName() + ": wrong state at start of document.";
        }
        state = STATE_DOCUMENT;
    }

    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @Override
    public void endDocument() {
        if (state != STATE_DOCUMENT) {
            parsingErrors += file.getName() + ": wrong state at end of document.\n";
        }
        state = STATE_IDLE;
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        switch (state) {
        case (STATE_IDLE):
            parsingErrors += file.getName() + ": in state idle at start of element.\n";
            break;
        case (STATE_DOCUMENT):
            if (!qName.equals(ELEMENT_AUTOMATON)) {
                parsingErrors += file.getName() + ": encountered wrong start of element in state document.\n";
                break;
            }
            a = ModelManager.instance().createModel(FSAModel.class, ParsingToolbox.removeFileType(file.getName()));
            state = STATE_AUTOMATON;
            break;
        case (STATE_AUTOMATON):
            if (qName.equals(ELEMENT_STATE)) {
                if (atts.getValue(ATTRIBUTE_ID) == null) {
                    parsingErrors += file.getName() + " Unable to parse state with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                lastState = a.assembleState();
                lastState.setId(id);// new State(id);
                a.add(lastState);
                state = STATE_STATE;
            } else if (qName.equals(ELEMENT_EVENT)) {
                if (atts.getValue(ATTRIBUTE_ID) == null) {
                    parsingErrors += file.getName() + " Unable to parse event with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                lastEvent = a.assembleEvent("");// new Event(id);
                lastEvent.setId(id);
                a.add(lastEvent);
                state = STATE_EVENT;
            } else if (qName.equals(ELEMENT_TRANSITION)) {
                FSAState s = a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_SOURCE_ID)));
                FSAState t = a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_TARGET_ID)));
                SupervisoryEvent e = atts.getValue(ATTRIBUTE_EVENT) != null
                        ? a.getEvent(Integer.parseInt(atts.getValue(ATTRIBUTE_EVENT)))
                        : null;

                if (atts.getValue(ATTRIBUTE_ID) == null) {
                    parsingErrors += file.getName() + " Unable to parse transition with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));

                if (s == null || t == null) {
                    parsingErrors += file.getName() + " Unable to parse transition " + id + ".\n";
                    parsingErrors += "\tstate " + Integer.parseInt(atts.getValue(ATTRIBUTE_SOURCE_ID)) + " or "
                            + a.getState(Integer.parseInt(atts.getValue(ATTRIBUTE_TARGET_ID)))
                            + "does not exist, or have not been parsed yet.";
                } else if (e == null) {
                    lastTransition = a.assembleEpsilonTransition(s.getId(), t.getId());// new Transition(id, s, t);
                    lastTransition.setId(id);
                    a.add(lastTransition);
                } else {
                    lastTransition = a.assembleTransition(s.getId(), t.getId(), e.getId());// new Transition(id, s, t,
                                                                                           // e);
                    lastTransition.setId(id);
                    a.add(lastTransition);
                }
                state = STATE_TRANSITION;
            }
            break;
        case (STATE_STATE):
            if (qName.equals(ELEMENT_NAME)) {
                lastLabel = "";
                state = STATE_STATE_NAME;
            } else if (qName.equals(ELEMENT_PROPERTIES)) {
                state = STATE_STATE_PROPERTIES;
            }
            break;
        case (STATE_STATE_PROPERTIES):
            if (qName.equals(ELEMENT_INITIAL)) {
                lastState.setInitial(true);
            } else if (qName.equals(ELEMENT_MARKED)) {
                lastState.setMarked(true);
            }
            break;
        case (STATE_TRANSITION):
            break;
        case (STATE_EVENT):
            if (qName.equals(ELEMENT_NAME)) {
                lastLabel = "";
                state = STATE_EVENT_NAME;
            } else if (qName.equals(ELEMENT_PROPERTIES)) {
                state = STATE_EVENT_PROPERTIES;
            }
            break;
        case (STATE_EVENT_PROPERTIES):
            if (qName.equals(ELEMENT_OBSERVABLE)) {
                lastEvent.setObservable(true);
            } else if (qName.equals(ELEMENT_CONTROLLABLE)) {
                lastEvent.setControllable(true);
            }
            break;
        default:
            parsingErrors += file.getName() + ": encountered wrong beginning of element.\n";
            break;
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (state) {
        case (STATE_AUTOMATON):
            if (qName.equals(ELEMENT_AUTOMATON)) {
                state = STATE_DOCUMENT;
            } else {
                parsingErrors += file.getName() + ": Wrong element endend while in state automaton.\n";
            }
            break;
        case (STATE_STATE):
            if (qName.equals(ELEMENT_STATE)) {
                state = STATE_AUTOMATON;
            } else if (qName.equals("graphic") || qName.equals("circle") || qName.equals("arrow")) {
            } else {
                parsingErrors += file.getName() + ": Wrong element endend while in state state.\n";
            }
            break;
        case (STATE_STATE_NAME):
            if (qName.equals(ELEMENT_NAME)) {
                lastState.setName(lastLabel);
                state = STATE_STATE;
            } else {
                parsingErrors += file.getName() + ": Wrong element endend while in state state.\n";
            }
            break;
        case (STATE_STATE_PROPERTIES):
            if (qName.equals(ELEMENT_PROPERTIES)) {
                state = STATE_STATE;
            } else if (qName.equals(ELEMENT_INITIAL) || qName.equals(ELEMENT_MARKED)) {
            } else {
                parsingErrors += file.getName() + ": Wrong element endend while in state state.\n";
            }
            break;
        case (STATE_TRANSITION):
            if (qName.equals(ELEMENT_TRANSITION)) {
                state = STATE_AUTOMATON;
            } else if (qName.equals("graphic") || qName.equals("bezier") || qName.equals("label")) {
            } else {
                parsingErrors += file.getName() + ": Wrong element endend while in state transition.\n";
            }
            break;
        case (STATE_EVENT):
            if (qName.equals(ELEMENT_EVENT)) {
                state = STATE_AUTOMATON;
            } else if (qName.equals("description")) {
            } else {
                parsingErrors += file.getName() + ": Wrong element endend while in state event.\n";
            }
            break;
        case (STATE_EVENT_NAME):
            if (qName.equals(ELEMENT_NAME)) {
                lastEvent.setSymbol(lastLabel);
                state = STATE_EVENT;
            } else {
                parsingErrors += file.getName() + ": Wrong element endend while in state state.\n";
            }
            break;
        case (STATE_EVENT_PROPERTIES):
            if (qName.equals(ELEMENT_PROPERTIES)) {
                state = STATE_EVENT;
            } else if (qName.equals(ELEMENT_CONTROLLABLE) || qName.equals(ELEMENT_OBSERVABLE)) {
            } else {
                parsingErrors += file.getName() + ": Wrong element endend while in state state.\n";
            }
            break;
        default:
            parsingErrors += file.getName() + ": encountered wrong state at end of element.\n";
            break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        lastLabel += new StringBuffer().append(ch, start, length).toString();
    }

}
