package projectPresentation;

import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import projectModel.*;

import java.util.*;

public class ProjectParser extends AbstractParser{
    private XMLReader xr;
    private Project p;
    private AutomatonParser ap;
    
    private int state = STATE_IDLE;
    
    private static final int STATE_IDLE = 0,
                             STATE_DOCUMENT = 1,
                             STATE_PROJECT = 2,
                             STATE_AUTOMATON = 3;
    
    private static final String ELEMENT_AUTOMATON = "automaton",
                                ELEMENT_PROJECT = "project";

    private static final String ATTRIBUTE_FILE = "file";
        
    private File file;
    
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
        file = f;
        state = STATE_IDLE;
        p = null;
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
        if(state != STATE_IDLE){
            System.err.println("XmlParser: in wrong state at beginning of document.");
            return;
        }
        state = STATE_DOCUMENT;
    }
    public void endDocument(){
        if(state != STATE_DOCUMENT){
            System.err.println("XmlParser: wrong state at end of document.");
            return;            
        }
        state = STATE_IDLE;
    }

    public void startElement(String uri, String localName, String qName, Attributes atts){
        switch(state){
        case STATE_IDLE:
            System.err.println("XmlParser: wrong state at beginning of element.");
            break;
        case STATE_PROJECT:
            if(!qName.equals(ELEMENT_AUTOMATON)){
                System.err.println("XmlParser: encountered wrong element in state project.");    
                
            }
            if(atts.getValue(ATTRIBUTE_FILE)!=null){
                state = STATE_AUTOMATON;
                try{
                    p.addAutomaton(ap.parse(new File(file.getParent()+File.separator+atts.getValue(ATTRIBUTE_FILE))));    
                }
                catch(Exception e){
                    System.err.println("XmlParser: Exception occured while parsing automaton. message: "+e.getMessage());
                }
            }
            break;
        case STATE_DOCUMENT:
            if(qName.equals(ELEMENT_PROJECT)){
                state = STATE_PROJECT;
                p = new Project(ParsingToolbox.removeFileType(file.getName()));
            }
            else System.err.println("XmlParser: encountered wrong element while in state document.");
            break;
        case STATE_AUTOMATON:
            System.err.println("XmlParser: encountered wrong element while in state automaton.");
        default:
            System.err.println("XmlParser: beginElement(): panic! parser in unknown state.");
            break;
        }
    }

    
    public void endElement(String uri, String localName, String qName){
        switch(state){
        case STATE_IDLE:
            System.err.println("XmlParser: encountered wrong end of element while in state idle.");
            break;
        case STATE_PROJECT:
            if(qName.equals(ELEMENT_PROJECT)) state = STATE_DOCUMENT;
            else System.err.println("XmlParser: encountered wrong end of element while in state project");
            break;
        case STATE_DOCUMENT:
            System.err.println("XmlParser: encountered end of element while in state document.");
            break;
        case STATE_AUTOMATON:
            if(qName.equals(ELEMENT_AUTOMATON)) state = STATE_PROJECT;
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
        System.out.println("Automata in project "+project.getName()+":");
        Iterator<Automaton> i = project.getAutomata().iterator();
        while(i.hasNext()){
            Automaton a = i.next();
            System.out.println(a);
            if(a != null){
                Iterator<State> si = a.getStateIterator();
                while(si.hasNext()){
                    System.out.println("\tstate     : "+si.next().getId());
                }
                System.out.println();
                Iterator<Event> ei = a.getEventIterator();
                while(ei.hasNext()){
                    System.out.println("\tevent     : "+ei.next().getId());
                }
                System.out.println();
                Iterator<Transition> ti = a.getTransitionIterator();
                while(ti.hasNext()){
                    Transition t = ti.next();
                    System.out.print("\ttransition: "+t.getId());
                    System.out.println("\t"
                                       +t.getSource().getId()
                                       +" -> "
                                       +t.getTarget().getId()
                                       +" : "
                                       +t.getEvent().getId());
                }
            }
            
        }    
        System.out.println("test done!");
    }
}