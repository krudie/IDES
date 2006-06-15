package model.fsa;

import model.DESElement;

public interface FSAEvent extends DESElement {
	
	public abstract String getSymbol();
	public abstract boolean isControllable();
	public abstract boolean isObservable();

}