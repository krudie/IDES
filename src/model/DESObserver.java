package model;

public interface DESObserver {

	/**
	 * Updates this observer with the most recent version of
	 * the DESModel (state of data) it is observing.
	 */
	public void update();
	
}
