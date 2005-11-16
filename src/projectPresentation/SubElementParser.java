package projectPresentation;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import projectModel.SubElement;

/**
 * This class parses subelements of an xml document.
 * @author Axel Gottlieb Michelsen
 * @author Kristian Edlund
 */
public class SubElementParser extends AbstractParser {
    private SubElement se;

    private String chars;

    private ContentHandler ch;

    /**
     * constructs a subelementparser.
     */
    public SubElementParser() {

    }

    /**
     * Fills the subelement se with data read from the XMLreader xmlr and writes all errors
     * encountered while parsing in parsingerrors
     * @param se the subelement that needs filling.
     * @param xmlr the xml reader that should be read for information to fill into the subelement.
     * 
     * @param parsingErrors the string to write parsingerrors into.
     */
    public void fill(SubElement se, XMLReader xmlr, String parsingErrors) {
        this.parsingErrors = parsingErrors;
        this.xmlr = xmlr;
        ch = xmlr.getContentHandler();
        xmlr.setContentHandler(this);
        this.se = se;
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        SubElement nse = new SubElement(qName);
        for (int i = 0; i < atts.getLength(); i++) {
            nse.setAttribute(atts.getQName(i), atts.getValue(i));
        }
        se.addSubElement(nse);
        SubElementParser sep = new SubElementParser();
        sep.fill(nse, xmlr, parsingErrors);
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (!qName.equals(se.getName())) {
            parsingErrors += "received end of element " + qName
                    + " while parsing " + se.getName();
        }
        if (chars != null) {
            se.setChars(chars.trim());
        }
        xmlr.setContentHandler(ch);
    }

    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (chars == null)
            chars = "";
        for (int i = start; i < start + length; i++) {
            chars += ch[i];
        }
    }
}
