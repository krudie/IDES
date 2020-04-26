package ides.api.copypaste;

import java.awt.datatransfer.Clipboard;

import javax.swing.AbstractButton;
import javax.swing.Action;

import ides.api.plugin.presentation.CopyPastePresentation;

/**
 * Interface to the IDES CopyPasteManager. The manager is responsible for
 * keeping track of whether cut, copy and paste options should be available to
 * the user. Registered UI elements are automatically updated with the changing
 * status of the cut, copy and paste system. The CopyPasteManager keeps track of
 * the active CopyPastePresentation, which is the presentation that is queried
 * for cut, copy and paste enabled status and for appropriate actions. Also
 * provides actions to overwrite those in default swing components if required.
 * 
 * @author Valerie Sugarman
 */
public interface CopyPasteManager {

    /**
     * Registers an {@link AbstractButton} to be updated when the cut/copy state
     * changes.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindCutCopy(AbstractButton element);

    /**
     * Registers an {@link AbstractButton} to be updated when the paste state
     * changes.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindPaste(AbstractButton element);

    /**
     * Unregisters an {@link AbstractButton} so that it is no longer updated when
     * the cut/copy/paste state changes.
     * 
     * @param element the UI element to be unregistered from automatic updates
     */
    public void unbind(AbstractButton element);

    /**
     * <p>
     * Updates the enabled status of the cut/copy/paste items on the menu and
     * toolbar. If the clipboard contents have changed, calls newItemOnClipboard on
     * the active CopyPastePresentation. Refreshes whenever there is a new active
     * CopyPastePresentation or any of the other presentations call this method.
     * </p>
     * <p>
     * CopyPastePresentations should call this method when there is a change in some
     * aspect such as a selection that could change whether cut, copy and paste
     * should be enabled.
     * </p>
     */
    public void refresh();

    /**
     * Returns the clipboard used by IDES.
     * 
     * @return the clipboard used by IDES
     */
    public Clipboard getClipboard();

    /**
     * Returns the active CopyPastePresentation, or <code>null</code> if there is no
     * currently active CopyPastePresentation.
     * 
     * @return the active CopyPastePresentation, or <code>null</code> if there is no
     *         currently active CopyPastePresentation.
     */
    public CopyPastePresentation getActiveCopyPastePresentation();

    /**
     * <p>
     * In order for copy and paste to work for swing components that already have a
     * copy and paste implementation, the default functionality must be overwritten.
     * This action is meant to overwrite the default cut action in the ActionMap of
     * a swing component.
     * </p>
     * <p>
     * An example of this is copy and pasting events represented in a JTable. In
     * order to integrate with the IDES copy and paste system, and call the proper
     * action defined in CopyPastePresentation's getCutAction(), the ActionMap must
     * be overwritten as follows:
     * </p>
     * <p>
     * <code>
     * JTable table = new JTable(); <br>
     * Action cutAction = Hub.getCopyPasteManager().getCutOverwriteAction(); <br>
     * Action copyAction = Hub.getCopyPasteManager().getCopyOverwriteAction();<br> 
     * Action pasteAction = Hub.getCopyPasteManager().getPasteOverwriteAction();<br>
     * table.getActionMap().put("cut", cutAction);<br>
     * table.getActionMap().put("copy", copyAction);<br>
     * table.getActionMap().put("paste", pasteAction);<br>
     * </code>
     * </p>
     * 
     * @return the cut action to be used when creating copy and paste for IDES
     *         elements using a swing component that already has a default copy and
     *         paste implementation.
     */
    public Action getCutOverwriteAction();

    /**
     * <p>
     * In order for copy and paste to work for swing components that already have a
     * copy and paste implementation, the default functionality must be overwritten.
     * This action is meant to overwrite the default cut action in the ActionMap of
     * the swing component.
     * </p>
     * <p>
     * An example of this is copy and pasting events represented in a JTable. In
     * order to integrate with the IDES copy and paste system, and call the proper
     * action defined in CopyPastePresentation's getCopyAction(), the ActionMap must
     * be overwritten as follows:
     * </p>
     * <p>
     * <code>
     * JTable table = new JTable(); <br>
     * Action cutAction = Hub.getCopyPasteManager().getCutOverwriteAction(); <br>
     * Action copyAction = Hub.getCopyPasteManager().getCopyOverwriteAction();<br> 
     * Action pasteAction = Hub.getCopyPasteManager().getPasteOverwriteAction();<br>
     * table.getActionMap().put("cut", cutAction);<br>
     * table.getActionMap().put("copy", copyAction);<br>
     * table.getActionMap().put("paste", pasteAction);<br>
     * </code>
     * </p>
     * 
     * @return the copy action to be used when creating copy and paste for IDES
     *         elements using a swing component that already has a default copy and
     *         paste implementation.
     */
    public Action getCopyOverwriteAction();

    /**
     * <p>
     * In order for copy and paste to work for swing components that already have a
     * copy and paste implementation, the default functionality must be overwritten.
     * This action is meant to overwrite the default cut action in the ActionMap of
     * the swing component.
     * </p>
     * <p>
     * An example of this is copy and pasting events represented in a JTable. In
     * order to integrate with the IDES copy and paste system, and call the proper
     * action defined in CopyPastePresentation's getPasteAction(), the ActionMap
     * must be overwritten as follows:
     * </p>
     * <p>
     * <code>
     * JTable table = new JTable(); <br>
     * Action cutAction = Hub.getCopyPasteManager().getCutOverwriteAction(); <br>
     * Action copyAction = Hub.getCopyPasteManager().getCopyOverwriteAction();<br> 
     * Action pasteAction = Hub.getCopyPasteManager().getPasteOverwriteAction();<br>
     * table.getActionMap().put("cut", cutAction);<br>
     * table.getActionMap().put("copy", copyAction);<br>
     * table.getActionMap().put("paste", pasteAction);<br>
     * </code>
     * </p>
     * 
     * @return the paste action to be used when creating copy and paste for IDES
     *         elements using a swing component that already has a default copy and
     *         paste implementation.
     */
    public Action getPasteOverwriteAction();
}
