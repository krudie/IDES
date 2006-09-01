package model.fsa;

import presentation.fsa.CircleNodeLayout;
import presentation.fsa.BezierLayout;
import model.DESMetaData;

public interface FSAMetaData extends DESMetaData {
	abstract CircleNodeLayout getLayoutData(FSAState s);
	abstract BezierLayout getLayoutData(FSATransition t);	
}
