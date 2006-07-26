package observer;

/**
 * 
 * @author helen
 * @deprecated 26 July 2006
 */
public interface Subscriber {

	/**
	 * Updates this subscriber with the most recent version of
	 * the FSAPublisher (state of data) it is observing.
	 */
	public void update();
	
}
