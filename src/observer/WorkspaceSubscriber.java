/**
 * 
 */
package observer;

/**
 * @author helen
 *
 */
public interface WorkspaceSubscriber {

	public void modelCollectionChanged(WorkspaceMessage message);
	
	// NOTE ignore param except for possibly for source field
	public void repaintRequired(WorkspaceMessage message); 
	
	public void modelSwitched(WorkspaceMessage message);
	
}
