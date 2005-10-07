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
public class AutomatonParser extends AbstractFileParser {
    private int state = STATE_IDLE;

    private static final int STATE_IDLE = 0, STATE_DOCUMENT = 1,
            STATE_AUTOMATON = 2, STATE_STATE = 3, STATE_TRANSITION = 4,
            STATE_EVENT = 5;

    private File file;

    private Automaton a;

    private SubElementContainer sec;

    public AutomatonParser() {
        super();
    }

    public Automaton parse(File f) {
        state = STATE_IDLE;
        a = null;
        file = f;
        parsingErrors = "";
        try {
            xmlr.parse(new InputSource(new FileInputStream(f)));
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

    public void startDocument() {
        if (state != STATE_IDLE)
            parsingErrors += file.getName()
                    + ": wrong state at start of document.";
        state = STATE_DOCUMENT;
    }

    public void endDocument() {
        if (state != STATE_DOCUMENT)
            parsingErrors += file.getName()
                    + ": wrong state at end of document.\n";
        state = STATE_IDLE;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes atts) {
        switch (state) {
        case (STATE_IDLE):
            parsingErrors += file.getName()
                    + ": in state idle at start of element.\n";
            break;
        case (STATE_DOCUMENT):
            if (!qName.equals(ELEMENT_AUTOMATON)) {
                parsingErrors += file.getName()
                        + ": encountered wrong start of element in state document.\n";
                break;
            }
            a = new Automaton(ParsingToolbox.removeFileType(file.getName()));
            state = STATE_AUTOMATON;
            break;
        case (STATE_AUTOMATON):
            if (qName.equals(ELEMENT_STATE)) {
                if (atts.getValue(ATTRIBUTE_ID) == null) {
                    parsingErrors += file.getName()
                            + " Unable to parse state with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                sec = new State(id);
                a.addState((State) sec);
                state = STATE_STATE;
            } else if (qName.equals(ELEMENT_EVENT)) {
                if (atts.getValue(ATTRIBUTE_ID) == null) {
                    parsingErrors += file.getName()
                            + " Unable to parse event with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                sec = new Event(id);
                a.addEvent((Event) sec);
                state = STATE_EVENT;
            } else if (qName.equals(ELEMENT_TRANSITION)) {
                State s = a.getState(Integer.parseInt(atts
                        .getValue(ATTRIBUTE_SOURCE_ID)));
                State t = a.getState(Integer.parseInt(atts
                        .getValue(ATTRIBUTE_TARGET_ID)));
                Event e = a.getEvent(Integer.parseInt(atts
                        .getValue(ATTRIBUTE_EVENT)));
                if (atts.getValue(ATTRIBUTE_ID) == null) {
                    parsingErrors += file.getName()
                            + " Unable to parse transition with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));

                if (s == null || t == null) {
                    parsingErrors += file.getName()
                            + " Unable to parse transition " + id + ".\n";
                    parsingErrors += "\tstate "
                            + Integer.parseInt(atts
                                    .getValue(ATTRIBUTE_SOURCE_ID))
                            + " or "
                            + a.getState(Integer.parseInt(atts
                                    .getValue(ATTRIBUTE_TARGET_ID)))
                            + "does not exist, or have not been parsed yet.";
                } else if (e == null) {
                    sec = new Transition(id, s, t);
                    a.addTransition((Transition) sec);
                } else {
                    sec = new Transition(id, s, t, e);
                    a.addTransition((Transition) sec);
                }
                state = STATE_TRANSITION;
            }
            break;
        case (STATE_STATE):
        case (STATE_TRANSITION):
        case (STATE_EVENT):
            SubElement nse = new SubElement(qName);
            for (int i = 0; i < atts.getLength(); i++) {
                nse.setAttribute(atts.getQName(i), atts.getValue(i));
            }
            sec.addSubElement(qName, nse);
            SubElementParser sep = new SubElementParser();
            sep.fill(nse, xmlr, parsingErrors);
            break;
        default:
            parsingErrors += file.getName()
                    + ": encountered wrong beginning of element.\n";
            break;
        }
    }

    public void endElement(String uri, String localName, String qName) {
        switch (state) {
        case (STATE_AUTOMATON):
            if (qName.equals(ELEMENT_AUTOMATON)) {
                state = STATE_DOCUMENT;
            } else {
                parsingErrors += file.getName()
                        + ": Wrong element endend while in state automaton.\n";
            }
            break;
        case (STATE_STATE):
            if (qName.equals(ELEMENT_STATE))
                state = STATE_AUTOMATON;
            else
                parsingErrors += file.getName()
                        + ": Wrong element endend while in state state.\n";
            break;
        case (STATE_TRANSITION):
            if (qName.equals(ELEMENT_TRANSITION))
                state = STATE_AUTOMATON;
            else
                parsingErrors += file.getName()
                        + ": Wrong element endend while in state transition.\n";
            break;
        case (STATE_EVENT):
            if (qName.equals(ELEMENT_EVENT))
                state = STATE_AUTOMATON;
            else
                parsingErrors += file.getName()
                        + ": Wrong element endend while in state event.\n";
            break;
        default:
            parsingErrors += file.getName()
                    + ": encountered wrong state at end of element.\n";
            break;
        }
    }
}
