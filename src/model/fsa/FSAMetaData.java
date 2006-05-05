package model.fsa;

import presentation.fsa.StateLayout;
import presentation.fsa.TransitionLayout;
import model.DESMetaData;

public interface FSAMetaData extends DESMetaData {
	abstract StateLayout getLayoutData(FSAState s);
	abstract TransitionLayout getLayoutData(FSATransition t);	
}
