package projectPresentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import projectModel.Automaton;
import projectModel.Project;

/**
 * This is a xml parser for the project files
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 *
 */
public class ProjectParser extends AbstractFileParser {
    private Project p;

    private AutomatonParser ap;

    private int state = STATE_IDLE;

    protected static final int STATE_IDLE = 0, STATE_DOCUMENT = 1,
            STATE_PROJECT = 2, STATE_AUTOMATON = 3;

    private File file;

    /**
     * Constructer for the projectParser
     */
    public ProjectParser() {
        super();
        ap = new AutomatonParser();
    }

    
    /**
     * @see projectPresentation.AbstractFileParser#parse(java.io.File)
     */
    public Project parse(File f) {
        file = f;
        state = STATE_IDLE;
        p = null;
        parsingErrors = "";
        try {
            xmlr.parse(new InputSource(new FileInputStream(f)));
        } catch (FileNotFoundException fnfe) {
            parsingErrors += fnfe.getMessage() + "\n";
        } catch (IOException ioe) {
            parsingErrors += ioe.getMessage() + "\n";
        } catch (SAXException saxe) {
            parsingErrors += saxe.getMessage() + "\n";
        }
        return p;
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() {
        if (state != STATE_IDLE) {
            parsingErrors += file.getName()
                    + ": in wrong state at beginning of document.";
            return;
        }
        state = STATE_DOCUMENT;
    }

    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() {
        if (state != STATE_DOCUMENT) {
            parsingErrors += file.getName()
                    + ": wrong state at end of document.";
            return;
        }
        state = STATE_IDLE;
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) {
        switch (state) {
        case STATE_IDLE:
            parsingErrors += file.getName()
                    + ": wrong state at beginning of element.";
            break;
        case STATE_PROJECT:
            if (!qName.equals(ELEMENT_AUTOMATON)) {
                parsingErrors += file.getName()
                        + ": encountered wrong element in state project.";

            }
            if (atts.getValue(ATTRIBUTE_FILE) != null) {
                state = STATE_AUTOMATON;
                Automaton a = ap.parse(new File(file.getParent()
                        + File.separator + atts.getValue(ATTRIBUTE_FILE)));
                if (a != null)
                    p.addAutomaton(a);
                parsingErrors += ap.getParsingErrors();
            }
            break;
        case STATE_DOCUMENT:
            if (qName.equals(ELEMENT_PROJECT)) {
                state = STATE_PROJECT;
                p = new Project(ParsingToolbox.removeFileType(file.getName()));
            } else
                parsingErrors += file.getName()
                        + ": encountered wrong element while in state document.\n";
            break;
        case STATE_AUTOMATON:
            parsingErrors += file.getName()
                    + ": encountered wrong element while in state automaton.\n";
            break;
        default:
            parsingErrors += file.getName()
                    + ": beginElement(): panic! parser in unknown state.\n";
            break;
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName) {
        switch (state) {
        case STATE_IDLE:
            parsingErrors += file.getName()
                    + ": encountered wrong end of element while in state idle.\n";
            break;
        case STATE_PROJECT:
            if (qName.equals(ELEMENT_PROJECT))
                state = STATE_DOCUMENT;
            else
                parsingErrors += file.getName()
                        + ": encountered wrong end of element while in state project\n";
            break;
        case STATE_DOCUMENT:
            parsingErrors += file.getName()
                    + ": encountered end of element while in state document.\n";
            break;
        case STATE_AUTOMATON:
            if (qName.equals(ELEMENT_AUTOMATON))
                state = STATE_PROJECT;
            else
                parsingErrors += file.getName()
                        + ": encountered wrong end of element while in state automaton\n";
            break;
        default:
            parsingErrors += file.getName()
                    + ": endElement(): panic! parser in unknown state.\n";
            break;

        }
    }

    /**
     * Method used for testing the class
     * @param args not used
     */
    public static void main(String args[]) {
        ProjectManager p = new ProjectManager();
        p.openProject(new File("/home/agmi02/des/test.xml"));
        p.setProjectName("hmm");
        p.saveProject("/home/agmi02/des/");
    }
}