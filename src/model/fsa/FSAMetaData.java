package model.fsa;

import presentation.fsa.NodeLayout;
import presentation.fsa.BezierLayout;
import model.DESMetaData;

public interface FSAMetaData extends DESMetaData {
	abstract NodeLayout getLayoutData(FSAState s);
	abstract BezierLayout getLayoutData(FSATransition t);	
}
