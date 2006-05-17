package model;

public interface Subscriber {

	/**
	 * Updates this subscriber with the most recent version of
	 * the FSAPublisher (state of data) it is observing.
	 */
	public void update();
	
}
