/**
 * 
 */
package presentation.fsa;

import java.util.ListIterator;

import model.fsa.FSAState;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.State;

/**
 *
 * @author Lenko Grigorov
 */
public class StateLabeler {

	public static void labelCompositeStates(Automaton a)
	{
        ListIterator<FSAState> si = a.getStateIterator();
        while(si.hasNext()){
        	State s=(State)si.next();
        	String label="(";
            for(int i=0;i<s.getStateCompositionList().length;++i)
            {
            	label+=String.valueOf(s.getStateCompositionList()[i])+",";
            }
            if(label.endsWith(","))
            	label=label.substring(0,label.length()-1);
            label+=")";
        }
	}
}
