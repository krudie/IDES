package model.fsa;

import model.DESElement;

public interface FSAEvent extends DESElement {
	
	public abstract String getSymbol();
	public abstract void setSymbol(String symbol);	
	public abstract boolean isControllable();
	public abstract void setControllable(boolean b);
	public abstract boolean isObservable();
	public abstract void setObservable(boolean b);

}