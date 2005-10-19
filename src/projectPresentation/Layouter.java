package projectPresentation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import projectModel.Automaton;
import projectModel.State;
import projectModel.Transition;

import att.grappa.*;

public class Layouter{

    public static final String LINUX_COMMAND = "/usr/bin/dot -Tdot";
    
    
    private Process engine = null;
    Graph graph = null;
    
    /**
     * Constructer for the class
     *
     */
    public Layouter(){}
    
    /**
     * This function is called to initilise the engine
     * Only works with Linux atm. 
     */
    private void initEngine(){
        
        try{
            engine = Runtime.getRuntime().exec(LINUX_COMMAND);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void doLayout(InputStream file){       
        try {
            Parser program = new Parser(file,System.err);
            program.parse();
            graph = program.getGraph();
        } catch(Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
            ex.printStackTrace(System.err);
            return;
        }
        
        if(engine == null) initEngine();
       
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
        graph.printGraph(System.out);
        
        
    }
    
    private InputStream toDot(Automaton automaton){
        
        PipedInputStream result = new PipedInputStream();
        PipedOutputStream pipedOutputStream = null;
        PrintWriter input = null;
        
        
        try{
            pipedOutputStream = new PipedOutputStream(result);
            input = new PrintWriter(pipedOutputStream, true);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        
        
        //putting out basic info that needs to be put into the buffer
        
        input.println("digraph Test_Graph {");
        
        //Putting in the nodes
        //Nodes are going to look like
        // ID[];
        Iterator<State> states = automaton.getStateIterator();
        State state = null;
        while(states.hasNext()){
            state = states.next();
            input.println(state.getId() + "[");
            input.print("Shape=ellipse, width=15, height=15");            
            input.println("];");
        }
        
       //putting in the transitions
       //sourceID->targetID[];
        
        Iterator<Transition> transitions = automaton.getTransitionIterator();
        Transition trans = null;
        while(transitions.hasNext()){
            trans = transitions.next();
            input.println(trans.getSource().getId() + "->" + trans.getTarget().getId() + "[");
            input.println("];");
        }
        
        //input footer
        input.println("}");
        input.flush();
        input.close();

        return (InputStream) result;
    }
    
    
    
    public void layoutAutomaton(Automaton automaton){
        InputStream dotFormat = toDot(automaton);
        
        doLayout(dotFormat);
    }
    
    
    
    public static void main(String[] args){
        Layouter layout = new Layouter();
        
        
        Automaton automaton = new Automaton("Test");
        State[] state = new State[3];
        
        for(int i = 0; i < state.length; i++){
            state[i] = new State(i);
            automaton.add(state[i]);
        }
        
        automaton.add(new Transition(0, state[0], state[1]));
        automaton.add(new Transition(0, state[1], state[1]));
        automaton.add(new Transition(0, state[1], state[2]));
        
        layout.layoutAutomaton(automaton);
        
    }
}
