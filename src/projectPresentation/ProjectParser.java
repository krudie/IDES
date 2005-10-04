package projectPresentation;

import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import projectModel.*;

import java.util.*;

public class ProjectParser implements ContentHandler{
    private XMLReader xr;
    private Project p;
    private AutomatonParser ap;
    
    private int projectState = PROJECT_IDLE;
    
    private static final int PROJECT_IDLE = 0,
                             PROJECT_DOCUMENT = 1,
                             PROJECT_PROJECT = 2,
                             PROJECT_AUTOMATON = 3;
    
    private static final String ELEMENT_AUTOMATON = "automaton",
                                ELEMENT_PROJECT = "project";

    private static final String ATTRIBUTE_FILE = "file";
        
    
    public ProjectParser(){
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
        ap = new AutomatonParser();
    }

    public Project parse(File f) throws FileNotFoundException, IOException, SAXException{
        projectState = PROJECT_IDLE;
        p = new Project();
        xr.parse(new InputSource(new FileInputStream(f)));
        return p;
    }
    
    public void	characters(char[] ch, int start, int length){
    }
    public void	endPrefixMapping(String prefix){
    }
    public void ignorableWhitespace(char[] ch, int start, int length){
    }
    public void processingInstruction(String target, String data){
    }
    public void setDocumentLocator(Locator locator){
    }
    public void skippedEntity(String name){
    }
    public void startDocument(){
        if(projectState != PROJECT_IDLE){
            System.err.println("XmlParser: in wrong state at beginning of document.");
            return;
        }
        projectState = PROJECT_DOCUMENT;
        p = new Project();
    }
    public void endDocument(){
        if(projectState != PROJECT_DOCUMENT){
            System.err.println("XmlParser: wrong state at end of document.");
            return;            
        }
        projectState = PROJECT_IDLE;
    }

    public void startElement(String uri, String localName, String qName, Attributes atts){
        switch(projectState){
        case PROJECT_IDLE:
            System.err.println("XmlParser: wrong state at beginning of element.");
            break;
        case PROJECT_PROJECT:
            if(!qName.equals(ELEMENT_AUTOMATON)){
                System.err.println("XmlParser: encountered wrong element in state project.");    
                
            }
            for(int i = 0; i < atts.getLength(); i++){
                if(atts.getQName(i).equals(ATTRIBUTE_FILE)){
                    projectState = PROJECT_AUTOMATON;
                    try{
                        p.addAutomaton(ap.parse(new File(atts.getValue(i))));
                        
                    }
                    catch(Exception e){
                        System.err.println("XmlParser: Exception occured while parsing automaton. message: "+e.getMessage());
                    }
                }
            }
            break;
        case PROJECT_DOCUMENT:
            if(qName.equals(ELEMENT_PROJECT)) projectState = PROJECT_PROJECT;
            else System.err.println("XmlParser: encountered wrong element while in state document.");
            break;
        case PROJECT_AUTOMATON:
            System.err.println("XmlParser: encountered wrong element while in state automaton.");
        default:
            System.err.println("XmlParser: beginElement(): panic! parser in unknown state.");
            break;
        }
    }

    
    public void endElement(String uri, String localName, String qName){
        switch(projectState){
        case PROJECT_IDLE:
            System.err.println("XmlParser: encountered wrong end of element while in state idle.");
            break;
        case PROJECT_PROJECT:
            if(qName.equals(ELEMENT_PROJECT)) projectState = PROJECT_DOCUMENT;
            else System.err.println("XmlParser: encountered wrong end of element while in state project");
            break;
        case PROJECT_DOCUMENT:
            System.err.println("XmlParser: encountered end of element while in state document.");
            break;
        case PROJECT_AUTOMATON:
            if(qName.equals(ELEMENT_AUTOMATON)) projectState = PROJECT_PROJECT;
            else System.err.println("XmlParser: encountered wrong end of element while in state automaton");
            break;
        default:
            System.err.println("XmlParser: endElement(): panic! parser in unknown state.");
            break;
        
        }
    }

    public void startPrefixMapping(String prefix, String uri){
    }
    
    public static void main(String args[]){
        ProjectParser p = new ProjectParser();
        Project project = null;
        try{
            project = p.parse(new File("/home/agmi02/code/test.xml"));
        }
        catch(SAXException saxe){
        }
        catch(FileNotFoundException fnfe){            
        }
        catch(IOException ioe){   
        }
        System.out.println("Automata in project:");
        Iterator<Automaton> i = project.getAutomata();
        while(i.hasNext()) System.out.println(i.next());
            
        System.out.println("test done!");
    }
}
