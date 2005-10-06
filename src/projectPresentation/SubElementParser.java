package projectPresentation;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import projectModel.SubElement;

public class SubElementParser extends AbstractParser{
    private SubElement se;
    private String chars;
    private ContentHandler ch;
    
    public SubElementParser(){
        
    }
    
    
    public void fill(SubElement se, XMLReader xmlr, String parsingErrors) {
        this.parsingErrors = parsingErrors;
        this.xmlr = xmlr;
        ch = xmlr.getContentHandler();
        xmlr.setContentHandler(this);
        this.se = se;
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        SubElement nse = new SubElement(qName);
        for(int i = 0; i < atts.getLength(); i++){
            nse.setAttribute(atts.getQName(i), atts.getValue(i));
        }
        se.addSubElement(qName, nse);
        SubElementParser sep = new SubElementParser();
        sep.fill(nse, xmlr, parsingErrors);
    }
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(!qName.equals(se.getName())){
            parsingErrors += "received end of element "+qName+" while parsing "+se.getName();
        }
        if(chars != null){
            se.setChars(chars.trim());
        }
        xmlr.setContentHandler(ch);
    }
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(chars == null) chars = "";
        for(int i = start; i < start+length; i++){
            chars += ch[i];
        }
    }
}
