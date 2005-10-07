package projectPresentation;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public abstract class AbstractFileParser extends AbstractParser {
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

    public abstract Object parse(File file);
}
