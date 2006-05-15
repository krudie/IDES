package model.fsa;

import presentation.fsa.NodeLayout;
import presentation.fsa.EdgeLayout;
import model.DESMetaData;

public interface FSAMetaData extends DESMetaData {
	abstract NodeLayout getLayoutData(FSAState s);
	abstract EdgeLayout getLayoutData(FSATransition t);	
}
