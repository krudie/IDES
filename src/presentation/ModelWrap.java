package presentation;

import model.DESModel;

/**
 * A shell which wraps a raw {@link DESModel} and maintains
 * the layout information needed to display the model. This
 * shell is passed onto a {@link Presentation} to render it on
 * the screen.
 * 
 * <p>The may be a different {@link ModelWrap} implementation for each
 * type of {@link DESModel}. As well, there may be many
 * {@link ModelWrap} implementations for the same {@link DESModel}.
 * 
 * TODO extend as needed.
 * 
 * @author Lenko Grigorov
 */
public interface ModelWrap {
	
	/**
	 * Returns the {@link DESModel} wrapped by this shell.
	 * @return the model wrapped
	 */
	public DESModel getModel();
	
	/**
	 * Returns the interface class used by the shell to
	 * interact with the {@link DESModel}. A {@link DESModel}
	 * may support a number of interfaces (e.g., both
	 * {@link FSAModel} and {@link model.DESEventSet}). Thus,
	 * the same model may be wrapped in different types
	 * of shells to visualize different aspects of it.
	 * This method returns the specific interface used by
	 * this shell to interact with the wrapped {@link DESModel}.
	 * @return the interface used by the shell to interact
	 * with the wrapped {@link DESModel}.
	 */
	public Class getModelInterface();
	
	/**
	 * Returns <code>true</code> if the information about
	 * the presentation of the model needs to be saved;
	 * <code>false</code> otherwise. E.g., if the user moves
	 * a node of a {@link FSAGraph}, this method will return
	 * <code>true</code> since the updated position of the
	 * node has to be saved.
	 * @return <code>true</code> if the information maintained
	 * by the shell about the presentation of the model
	 * needs to be saved; <code>false</code> otherwise
	 */
	public boolean needsSave();
}