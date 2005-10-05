package projectPresentation;

import java.io.*;

import projectModel.*;

import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ProjectParser extends AbstractFileParser{
    private Project p;
    private AutomatonParser ap;
    
    private int state = STATE_IDLE;
    
    protected static final int STATE_IDLE = 0,
                               STATE_DOCUMENT = 1,
                               STATE_PROJECT = 2,
                               STATE_AUTOMATON = 3;
        
    private File file;
    
    public ProjectParser(){
        super();
        ap = new AutomatonParser();
    }

    public Project parse(File f){
        file = f;
        state = STATE_IDLE;
        p = null;
        parsingErrors = "";
        try{
            xmlr.parse(new InputSource(new FileInputStream(f)));
        }
        catch(FileNotFoundException fnfe){
            parsingErrors += fnfe.getMessage()+"\n";
        }
        catch(IOException ioe){
            parsingErrors += ioe.getMessage()+"\n";
        }
        catch(SAXException saxe){
            parsingErrors += saxe.getMessage()+"\n";
        }
        return p;
    }

    public void startDocument(){
        if(state != STATE_IDLE){
            parsingErrors += file.getName()+": in wrong state at beginning of document.";
            return;
        }
        state = STATE_DOCUMENT;
    }
    public void endDocument(){
        if(state != STATE_DOCUMENT){
            parsingErrors += file.getName()+": wrong state at end of document.";
            return;            
        }
        state = STATE_IDLE;
    }

    public void startElement(String uri, String localName, String qName, Attributes atts){
        switch(state){
        case STATE_IDLE:
            parsingErrors += file.getName()+": wrong state at beginning of element.";
            break;
        case STATE_PROJECT:
            if(!qName.equals(ELEMENT_AUTOMATON)){
                parsingErrors += file.getName()+": encountered wrong element in state project.";    
                
            }
            if(atts.getValue(ATTRIBUTE_FILE)!=null){
                state = STATE_AUTOMATON;
                Automaton a = ap.parse(new File(file.getParent()+File.separator+atts.getValue(ATTRIBUTE_FILE)));
                if(a!= null) p.addAutomaton(a);    
                parsingErrors += ap.getParsingErrors();
            }
            break;
        case STATE_DOCUMENT:
            if(qName.equals(ELEMENT_PROJECT)){
                state = STATE_PROJECT;
                p = new Project(ParsingToolbox.removeFileType(file.getName()));
            }
            else parsingErrors += file.getName()+": encountered wrong element while in state document.\n";
            break;
        case STATE_AUTOMATON:
            parsingErrors += file.getName()+": encountered wrong element while in state automaton.\n";
            break;
        default:
            parsingErrors += file.getName()+": beginElement(): panic! parser in unknown state.\n";
            break;
        }
    }

    
    public void endElement(String uri, String localName, String qName){
        switch(state){
        case STATE_IDLE:
            parsingErrors += file.getName()+": encountered wrong end of element while in state idle.\n";
            break;
        case STATE_PROJECT:
            if(qName.equals(ELEMENT_PROJECT)) state = STATE_DOCUMENT;
            else parsingErrors += file.getName()+": encountered wrong end of element while in state project\n";
            break;
        case STATE_DOCUMENT:
            parsingErrors += file.getName()+": encountered end of element while in state document.\n";
            break;
        case STATE_AUTOMATON:
            if(qName.equals(ELEMENT_AUTOMATON)) state = STATE_PROJECT;
            else parsingErrors += file.getName()+": encountered wrong end of element while in state automaton\n";
            break;
        default:
            parsingErrors += file.getName()+": endElement(): panic! parser in unknown state.\n";
            break;
        
        }
    }

    public void startPrefixMapping(String prefix, String uri){
    }
    
    public static void main(String args[]){
        ProjectParser p = new ProjectParser();
        Project project = null;

        project = p.parse(new File("/home/agmi02/code/test.xml"));

        System.out.println("Automata in project: "+project.getName()+":");
        Iterator<Automaton> i = project.getAutomata().iterator();
        while(i.hasNext()){
            Automaton a = i.next();
            System.out.println("Automaton: "+a.getName());
            if(a != null){
                Iterator<State> si = a.getStateIterator();
                while(si.hasNext()){
                    State s = si.next();
                    System.out.println("\tstate     : "+s.getId());
                    printSubElements(s);
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
        System.out.println("Errors while parsing\n"+p.getParsingErrors());
        File outputFile = new File("/home/agmi02/code/output.xml");
        try{
            System.out.println(outputFile.createNewFile());
        }
        catch(Exception e){
            System.err.println("test: message: "+e.getMessage());
        }
        project.getAutomata().getFirst().toXML(outputFile);
    }
    public static void printSubElements(SubElementContainer sec){
        Enumeration<SubElement> see = sec.getSubElements();
        while(see.hasMoreElements()){
            SubElement se = see.nextElement();
            System.out.print(se.getName());
            Enumeration<String> ane = se.getAttributeNames();
            Enumeration<String> ave = se.getAttributeValues();
            while(ane.hasMoreElements()){
                System.out.print(" "+ane.nextElement() +"="+ ave.nextElement());
            }
            System.out.println();
            printSubElements(se);
            
        }
    }
}