package ides.api.plugin.presentation;

import ides.api.copypaste.CopyPasteManager;

import javax.swing.Action;

/**
 * <p>
 * An interface for presentations that will implement cut, copy and paste.
 * CopyPastePresentations should notify the {@link CopyPasteManager} of any
 * changes in cut, copy or paste status using
 * Hub.getCopyPasteManager().refresh()
 * </p>
 * <p>
 * Only one CopyPastePresentation is active at a time, which is managed by the
 * {@link CopyPasteManager}. It is this active presentation that the
 * {@link CopyPasteManager} queries at any given time for enabled status and
 * appropriate actions.
 * </p>
 * 
 * @author Valerie Sugarman
 */
public interface CopyPastePresentation extends Presentation
{
	/**
	 * Determines whether cut and copy should currently be available to the
	 * user. This method is queried when the {@link CopyPasteManager} is
	 * refreshed.
	 * 
	 * @return <code> true </code> if cut and copy should be available to the
	 *         user.
	 */
	public boolean isCutCopyEnabled();

	/**
	 * Determines whether paste should currently be available to the user,
	 * depending on the state of the presentation and on the type of data on the
	 * clipboard. If the type of data on the clipboard cannot be accepted then
	 * return false. This method is queried when the {@link CopyPasteManager} is
	 * refreshed.
	 * 
	 * @return <code> true </code> if paste should be available to the user.
	 */
	public boolean isPasteEnabled();

	/**
	 * Gives the appropriate action for cut.
	 * 
	 * @return The appropriate action for cut.
	 */
	public Action getCutAction();

	/**
	 * Gives the appropriate action for copy.
	 * 
	 * @return The appropriate action for copy.
	 */
	public Action getCopyAction();

	/**
	 * Gives the appropriate action for paste.
	 * 
	 * @return The appropriate action for paste.
	 */
	public Action getPasteAction();

	/**
	 * Updates any aspects of the CopyPastePresentation that require knowledge
	 * of when a new item is on the clipboard. This method is ONLY received when
	 * this CopyPastePresentation is the active presentation in the
	 * {@link CopyPasteManager}.
	 */
	public void newItemOnClipboard();

}
