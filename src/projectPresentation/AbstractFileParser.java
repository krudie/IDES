package projectPresentation;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * This is an abstract class for xml parsers that take a file as input.
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public abstract class AbstractFileParser extends AbstractParser {

    /**
     * constructs an abstract file parser.
     */
    public AbstractFileParser() {
        try {
            xmlr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            xmlr.setContentHandler(this);
        } catch (ParserConfigurationException pce) {
            System.err
                    .println("AbstractParser: could not configure parser, message: "
                            + pce.getMessage());
        } catch (SAXException se) {
            System.err
                    .println("AbstractParser: could not do something, message: "
                            + se.getMessage());
        }
    }

    /**
     * Parses a file. Returns the coresponding object.
     * @param file the file that needs parsing
     * @return the corresponding object.
     */
    public abstract Object parse(File file);
}
