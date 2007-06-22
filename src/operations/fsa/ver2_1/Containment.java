/**
 * 
 */
package operations.fsa.ver2_1;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import pluggable.operation.Operation;
import pluggable.operation.OperationManager;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class Containment implements Operation {

	public final static String NAME="containment";
	public final static String DESCRIPTION="temp";
	
	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getName()
	 */
	public String getName() {
		return NAME;
	}
	
	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getNumberOfInputs()
	 */
	public int getNumberOfInputs() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getTypeOfInputs()
	 */
	public Class[] getTypeOfInputs() {
		return new Class[]{FSAModel.class,FSAModel.class};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfInputs()
	 */
	public String[] getDescriptionOfInputs() {
		return new String[]{"Sublanguage","Superlanguage"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getNumberOfOutputs()
	 */
	public int getNumberOfOutputs() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getTypeOfOutputs()
	 */
	public Class[] getTypeOfOutputs() {
		return new Class[]{Boolean.class};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#getDescriptionOfOutputs()
	 */
	public String[] getDescriptionOfOutputs() {
		return new String[]{"containment of sublanguage in superlanguage"};
	}

	/* (non-Javadoc)
	 * @see pluggable.operation.Operation#perform(java.lang.Object[])
	 */
	public Object[] perform(Object[] inputs) {
		FSAModel a=(FSAModel)inputs[0];
		FSAModel b=(FSAModel)inputs[1];
		
		a=(FSAModel)OperationManager.getOperation("trim").perform(new Object[]{a})[0];
		b=(FSAModel)OperationManager.getOperation("trim").perform(new Object[]{b})[0];
		
        LinkedList<FSAState[]> searchList = new LinkedList<FSAState[]>();
    	Set<String> pairs=new HashSet<String>(); 
    	FSAState[] cState = new FSAState[2];

        Iterator<FSAState> sia = a.getStateIterator();
        while(sia.hasNext()){
            cState[0] = sia.next();
            if(cState[0].isInitial()){
                Iterator<FSAState> sib = b.getStateIterator();
                while(sib.hasNext()){
                    cState[1] = sib.next();
                    if(cState[1].isInitial()){
                        searchList.add(cState.clone());
                    }
                }
            }
        }
        
        boolean contained=!searchList.isEmpty()||a.getStateCount()==0;
        
		while(!searchList.isEmpty())
		{
			cState=searchList.removeFirst();
			contained&=!(cState[0].isMarked()&&!cState[1].isMarked());
			if(!contained)
				break;
			pairs.add(""+cState[0].getId()+","+cState[1].getId());
			for(Iterator<FSATransition> i=cState[0].getSourceTransitionsListIterator();i.hasNext();)
			{
				FSATransition ta=i.next();
				boolean matchingFound=false;
				for(Iterator<FSATransition> j=cState[1].getSourceTransitionsListIterator();j.hasNext();)
				{
					FSATransition tb=j.next();
					if((ta.getEvent()==null&&tb.getEvent()==null)||
							(ta.getEvent()!=null&&ta.getEvent().equals(tb.getEvent())))
					{
						if(!pairs.contains(""+ta.getTarget().getId()+","+tb.getTarget().getId()))
							searchList.add(new FSAState[]{ta.getTarget(),tb.getTarget()});
						matchingFound=true;
						break;
					}
				}
				contained&=matchingFound;
				if(!contained)
					break;
			}
		}
		return new Object[]{new Boolean(contained)};
	}

}
