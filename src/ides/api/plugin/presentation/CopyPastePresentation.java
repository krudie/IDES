package ides.api.plugin.presentation;

import javax.swing.Action;

import ides.api.copypaste.CopyPasteManager;

/**
 * <p>
 * An interface for {@link Presentation}s that implement cut, copy and paste.
 * {@link CopyPastePresentation}s should notify the {@link CopyPasteManager} of
 * any changes in the cut, copy or paste status using
 * Hub.getCopyPasteManager().refresh()
 * </p>
 * <p>
 * Only one {@link CopyPastePresentation} can be active at a time. It is this
 * active presentation that the {@link CopyPasteManager} queries at any given
 * time for enabled status and appropriate actions. The {@link CopyPasteManager}
 * tracks the switching between presentations.
 * </p>
 * 
 * @author Valerie Sugarman
 */
public interface CopyPastePresentation extends Presentation {
    /**
     * Determine whether cut and copy should currently be available to the user.
     * This method is queried when the {@link CopyPasteManager} is refreshed.
     * 
     * @return <code>true</code> if cut and copy should be available to the user,
     *         <code>false</code> otherwise.
     */
    public boolean isCutCopyEnabled();

    /**
     * Determine whether paste should currently be available to the user, depending
     * on the state of the presentation and on the type of data on the clipboard. If
     * the type of data on the clipboard cannot be accepted then return
     * <code>false</code>. This method is queried when the {@link CopyPasteManager}
     * is refreshed.
     * 
     * @return <code>true</code> if paste should be available to the user,
     *         <code>false</code> otherwise.
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
     * Notification that a new item is on the clipboard. This method is ONLY called
     * when this {@link CopyPastePresentation} is the active presentation in the
     * {@link CopyPasteManager}.
     */
    public void newItemOnClipboard();

}
