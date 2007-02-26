package operations.fsa.ver1;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import model.ModelFactory;
import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.Event;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;
import pluggable.operation.FilterOperation;

public class ControlMap implements FilterOperation {

	public final static String NAME="control map";

	/**
	 * To be used to store the ids of pairs of states
	 */
	protected static Map<String,Long> pairIds=new TreeMap<String,Long>(); 

	public Object[] filter(Object[] inputs) {
		FSAModel supervisor=(FSAModel)inputs[0];
//		Unary.buildStateCompositionOfClone((Automaton)supervisor);
		FSAModel plant=(FSAModel)inputs[1];
		
		FSAModel product=ModelFactory.getFSA("temp");

        // find initial states, mark them as reached and add them to the que
        FSAState[] initial = new FSAState[2];
        long stateNumber = 0;
        LinkedList<FSAState[]> searchList = new LinkedList<FSAState[]>();

        Iterator<FSAState> sia = plant.getStateIterator();
        while(sia.hasNext()){
            initial[0] = sia.next();
            if(initial[0].isInitial()){
                Iterator<FSAState> sib = supervisor.getStateIterator();
                while(sib.hasNext()){
                    initial[1] = sib.next();
                    if(initial[1].isInitial()){
                        searchList.add(initial.clone());
                        product.add(makeState(initial,stateNumber));
                        setStateId(initial, stateNumber++);
                    }
                }
            }
        }

        // accessibility. All accessible states are added to product.
        // Transitions are only traversible if they can be traversed from both
        // states in sa
        // firing the same event, i.e., the intersection of the transitions
        // originating from the two
        // states are the transitions of state in product.
        FSAState[] s = new FSAState[2];
        while(!searchList.isEmpty()){
            FSAState[] sa = searchList.removeFirst();

            ListIterator<FSATransition> sti0 = sa[0].getSourceTransitionsListIterator();
            while(sti0.hasNext()){
                FSATransition t0 = sti0.next();
                ListIterator<FSATransition> sti1 = sa[1].getSourceTransitionsListIterator();
                FSAEvent notMatched=t0.getEvent();
                while(sti1.hasNext()){
                    FSATransition t1 = sti1.next();
                    if((t0.getEvent() == null && t1.getEvent() == null) || (t0.getEvent() != null
                            && t1.getEvent() != null && t0.getEvent().equals(t1.getEvent()))){

                        s[0] = (State)t0.getTarget();
                        s[1] = (State)t1.getTarget();

                        long id = getStateId(s);
                        if(id == -1){
                            State target = makeState(s, stateNumber);
                            product.add(target);
                            setStateId(s, stateNumber++);
                            searchList.add(s.clone());
                        }
                        notMatched=null;
                    }
                }
                if(notMatched!=null)
                {
                    Set<FSAEvent> de=((State)sa[1]).getDisabledEvents();
                	de.add(notMatched);
                	((State)sa[1]).setDisabledEvents(de);
                }
            }
        }

        pairIds.clear();
		
		for(Iterator<FSAState> i=supervisor.getStateIterator();i.hasNext();)
		{
			System.out.println(((State)i.next()).getDisabledEvents().toString());
		}
		return new Object[]{supervisor};
	}
	
    /**
     * Private function for making a new state from a stateset
     * @param s the stateset to make a new state from
     * @param stateNumber the id of the new state
     * @return the newly created state
     */
    private static State makeState(FSAState[] s, long stateNumber){
        State state = new State(stateNumber);
//        SubElement name = new SubElement("name");
//        name.setChars("(" + s[0].getSubElement("name").getChars() + ", "
//                + s[1].getSubElement("name").getChars() + ")");
//        state.addSubElement(name);

//        SubElement properties = new SubElement("properties");

        state.setStateCompositionList(
        		new long[]{s[0].getId(),s[1].getId()});
        
        if(s[0].isInitial() && s[1].isInitial())
        	state.setInitial(true);

        if(s[0].isMarked() && s[1].isMarked())
        	state.setMarked(true);
        return state;
    }

    /**
     * set the stateid for a set of states 
     * @param s the stateset
     * @param stateId the id to set
     */
    private static void setStateId(FSAState[] s, long stateId){
    	pairIds.put(""+s[0].getId()+","+s[1].getId(),new Long(stateId));
    }

    /**
     * Gets the id from a set of states
     * 
     * @param s the stateset
     * @return the id of the stateset
     */
    private static long getStateId(FSAState[] s){
    	String key=""+s[0].getId()+","+s[1].getId();
        if(pairIds.containsKey(key))
            return pairIds.get(key).longValue();
        return -1;
    }

	public int[] getInputOutputIndexes() {
		return new int[]{0};
	}

	public String[] getDescriptionOfInputs() {
		return new String[]{"Supervisor","Plant"};
	}

	public String[] getDescriptionOfOutputs() {
		return new String[]{"Supervisor with control map"};
	}

	public String getName() {
		return NAME;
	}

	public int getNumberOfInputs() {
		return 2;
	}

	public int getNumberOfOutputs() {
		return 1;
	}

	public Class[] getTypeOfInputs() {
		return new Class[]{FSAModel.class,FSAModel.class};
	}

	public Class[] getTypeOfOutputs() {
		return new Class[]{FSAModel.class};
	}

	public Object[] perform(Object[] inputs) {
		FSAModel supervisor=((FSAModel)inputs[0]).clone();
//		Unary.buildStateCompositionOfClone((Automaton)supervisor);
		FSAModel plant=(FSAModel)inputs[1];
		filter(new Object[]{supervisor,plant});
		return new Object[]{supervisor};
	}

}
