package io.template.ver2_1;

import java.util.Iterator;
import java.util.Vector;

import model.fsa.FSAState;
import model.fsa.FSATransition;

import des.interfaces.State;
import des.interfaces.Transition;

public class StateWrapper implements State {
	
	protected FSAState state;
	protected Vector<Transition> st=new Vector<Transition>();
	protected Vector<Transition> tt=new Vector<Transition>();
	
	public StateWrapper(FSAState state)
	{
		this.state=state;
	}

	public void addSourceTransition(Transition arg0) {
		st.add(arg0);
	}

	public void addTargetTransition(Transition arg0) {
		tt.add(arg0);
	}

	public int getNumber() {
		return (int)state.getId();
	}

	public Vector<Transition> getSourceTransitions() {
		return st;
	}

	public Vector<Transition> getTargetTransitions() {
		return tt;
	}

	public boolean isInitial() {
		return state.isInitial();
	}

	public boolean isMarked() {
		return state.isMarked();
	}

	public void removeSourceTransition(Transition arg0) {
		throw new UnsupportedOperationException();
	}

	public void removeTargetTransition(Transition arg0) {
		throw new UnsupportedOperationException();
	}

	public void setInitial(boolean arg0) {
		state.setInitial(arg0);
	}

	public void setMarked(boolean arg0) {
		state.setMarked(arg0);
	}

	public void setNumber(int arg0) {
		state.setId(arg0);
	}

}
