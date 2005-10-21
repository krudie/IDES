package projectPresentation;

import ides2.SystemVariables;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;

import projectModel.Automaton;
import projectModel.State;
import projectModel.SubElement;
import projectModel.Transition;
import userinterface.geometric.UnitVector;

import att.grappa.*;

public class Layouter{

    private String graphvizPath;       
    private Process engine = null;
           

    /**
     * The constructor of the layouter
     *
     */
    public Layouter(){
        graphvizPath = SystemVariables.getGraphvizPath() + " -Tdot";        
    }   
    
    /**
     * Calls graphviz through grappa to do the laytout
     * @param file the dotformatted file
     */
    
    public Graph doLayout(InputStream file) throws Exception{       
        
        Graph graph;
        
        try {
            Parser program = new Parser(file,System.err);
            program.parse();
            
            
            
            
            graph = program.getGraph();
        } catch(Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
            ex.printStackTrace(System.err);
            return null;
        }
        
        engine = Runtime.getRuntime().exec(graphvizPath);        
        if(!GrappaSupport.filterGraph(graph,engine)) {
            System.err.println("ERROR: somewhere in filterGraph");
        }
        try {
            int code = engine.waitFor();
            if(code != 0) {
                System.err.println("WARNING: proc exit code is: " + code);
            }            
        } catch(InterruptedException ex) {
            System.err.println("Exception while closing down proc: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
        engine = null;
        return graph;       
    }
    
    
    /**
     *  Transform the automaton into dot format
     * @param automaton the automaton to be formatted
     * @return The input stream containing the dot formatted version of the automaton
     */
    private InputStream toDot(Automaton automaton){             
        
              
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PrintWriter input = new PrintWriter(pipedOutputStream, true);
          
        //putting out basic info that needs to be put into the buffer
        
        input.println("digraph Test_Graph {");
        input.println("graph[");
        input.println("];");
        
        //Putting in the nodes
        //Nodes are going to look like
        // ID[];
        Iterator<State> states = automaton.getStateIterator();
        State state = null;
        int i = 0;
        while(states.hasNext()){            
            state = states.next();
            input.println(state.getId() + "[");
            input.println("Shape=ellipse, width=\".4\", height=\".4\"");
            input.println("];");
        }
        
       //putting in the transitions
       //sourceID->targetID[];        
        Iterator<Transition> transitions = automaton.getTransitionIterator();
        Transition trans = null;
        while(transitions.hasNext()){
            trans = transitions.next();
            input.println(trans.getSource().getId() + "->" + trans.getTarget().getId() + "[");            
            input.println("label=" + trans.getId());            
            input.println("];");
        }
        
        
        //input footer
        input.println("}");
        input.flush();
        input.close();

        
        PipedInputStream result = null;        
        try{
            result = new PipedInputStream(pipedOutputStream);
            System.out.println("kommer jeg hertil?");
            pipedOutputStream.flush();
            input.close();
            pipedOutputStream.close();            
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return (InputStream) result;
    }
    
    
    public void fromDot(Graph graph, Automaton automaton){
        
        int ydisplacement = (int) Float.parseFloat(graph.getThisAttributeValue(Graph.BBOX_ATTR).toString().split(",")[3]) + 25;
        int xdisplacement = 25;
        
        Enumeration<Node> nodes = graph.nodeElements();
        
        while(nodes.hasMoreElements()){
            Node node = nodes.nextElement();
            
            State state = automaton.getState(Integer.parseInt(node.getName()));
            SubElement graphic = new SubElement("graphic");
            graphic.addSubElement(new SubElement("circle"));
            
            graphic.getSubElement("circle").setAttribute("r", "15");
            
            String[] posSplit = node.getThisAttributeValue(Node.POS_ATTR).toString().split(",");
            
            graphic.getSubElement("circle").setAttribute("x", Integer.toString((int) Float.parseFloat(posSplit[0]) + xdisplacement));
            graphic.getSubElement("circle").setAttribute("y", Integer.toString((int) Float.parseFloat(posSplit[1]) + ydisplacement));
                      
            graphic.addSubElement(new SubElement("arrow"));
            graphic.getSubElement("arrow").setAttribute("x", "1.0");
            graphic.getSubElement("arrow").setAttribute("y", "0.0");
            
            state.addSubElement(graphic);
        }

        Enumeration<Edge> edges = graph.edgeElements();
        
        while(edges.hasMoreElements()){
            Edge edge = edges.nextElement();
                                                
            Transition trans = automaton.getTransition(Integer.parseInt(edge.getAttribute(Edge.LABEL_ATTR).getStringValue()));
        
            SubElement graphic = new SubElement("graphic");
            graphic.addSubElement(new SubElement("bezier"));
                  
            String[] posSplit = edge.getThisAttributeValue(Edge.POS_ATTR).toString().split(" |,");

            
            graphic.getSubElement("bezier").setAttribute("x1", Float.toString(Float.parseFloat(posSplit[3]) + xdisplacement));                                     
            graphic.getSubElement("bezier").setAttribute("y1", Float.toString(Float.parseFloat(posSplit[4]) + ydisplacement));
                
            graphic.getSubElement("bezier").setAttribute("ctrlx1", Float.toString(Float.parseFloat(posSplit[5]) + xdisplacement));
            graphic.getSubElement("bezier").setAttribute("ctrly1", Float.toString(Float.parseFloat(posSplit[6]) + ydisplacement));                                    
                
            graphic.getSubElement("bezier").setAttribute("x2", Float.toString(Float.parseFloat(posSplit[posSplit.length -2]) + xdisplacement));            
            graphic.getSubElement("bezier").setAttribute("y2", Float.toString(Float.parseFloat(posSplit[posSplit.length -1]) + ydisplacement));            
                                                        
            graphic.getSubElement("bezier").setAttribute("ctrlx2", Float.toString(Float.parseFloat(posSplit[posSplit.length -4]) + xdisplacement));            
            graphic.getSubElement("bezier").setAttribute("ctrly2", Float.toString(Float.parseFloat(posSplit[posSplit.length -3]) + ydisplacement));            

            
            UnitVector uv = new UnitVector();
            graphic.getSubElement("bezier").setAttribute("dx", Float.toString(uv.x));            
            graphic.getSubElement("bezier").setAttribute("dy", Float.toString(uv.y));            
            
            graphic.addSubElement(new SubElement("label"));
            graphic.getSubElement("label").setAttribute("x", "0");
            graphic.getSubElement("label").setAttribute("y", "0");
            
            trans.addSubElement(graphic);
        }
        
    }
    
    
    /**
     * Takes an automaton and lays it out
     * @param automaton the automaton that needs graphic info
     */
    public void layoutAutomaton(Automaton automaton) throws Exception{
        System.out.println("Så laver vi toDot");
        InputStream dotFormat = toDot(automaton);
        System.out.println("Så laver vi doLayout");
        Graph graph = doLayout(dotFormat);
        System.out.println("Så laver vi mike format");
        fromDot(graph, automaton);           
        System.out.println("Jeg er færdig!");
    }
    
    /**
     * Test main
     * @param args not used
     */
    public static void main(String[] args){
        Layouter layout = new Layouter();
        
        
        Automaton automaton = new Automaton("Test");
        State[] state = new State[14];
        
        for(int i = 0; i < state.length; i++){
            state[i] = new State(i);
            state[i].addSubElement(new SubElement("name"));
            state[i].addSubElement(new SubElement("properties"));
            state[i].getSubElement("properties").addSubElement(new SubElement("marked"));
            state[i].getSubElement("properties").getSubElement("marked").setChars("false");
            state[i].getSubElement("properties").addSubElement(new SubElement("initial"));
            state[i].getSubElement("properties").getSubElement("initial").setChars("false");
            
            automaton.add(state[i]);
        }
        
        automaton.add(new Transition(0, state[0], state[1]));
        automaton.add(new Transition(1, state[1], state[1]));
        automaton.add(new Transition(2, state[1], state[2]));
        automaton.add(new Transition(3, state[1], state[1]));
        automaton.add(new Transition(4, state[4], state[2]));
        automaton.add(new Transition(5, state[5], state[8]));
        automaton.add(new Transition(6, state[7], state[6]));
        automaton.add(new Transition(7, state[2], state[4]));
        automaton.add(new Transition(8, state[9], state[12]));
        automaton.add(new Transition(9, state[9], state[12]));
        automaton.add(new Transition(10, state[9], state[12]));
        automaton.add(new Transition(11, state[12], state[9]));
        
        
        try{        
            layout.layoutAutomaton(automaton);
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
