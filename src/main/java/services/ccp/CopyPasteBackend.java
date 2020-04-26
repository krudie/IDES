package services.ccp;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Action;

import ides.api.copypaste.CopyPasteManager;
import ides.api.core.Hub;
import ides.api.core.WorkspaceMessage;
import ides.api.core.WorkspaceSubscriber;
import ides.api.plugin.presentation.CopyPastePresentation;

/**
 * Manages cut/copy/paste in select presentations.
 * 
 * @author Valerie Sugarman
 */
public class CopyPasteBackend implements CopyPasteManager {
    protected Vector<AbstractButton> uiElementsCutCopy = new Vector<AbstractButton>();

    protected Vector<AbstractButton> uiElementsPaste = new Vector<AbstractButton>();

    protected Vector<CopyPastePresentation> presentations = new Vector<CopyPastePresentation>();

    protected static Clipboard clipboard = new Clipboard("IDES");

    protected static CopyPastePresentation activeCopyPastePresentation = null;

    protected Object prevClipboardContents = null;

    // Make the class non-instantiable.
    private CopyPasteBackend() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Instance for the non-static methods.
     */
    private static CopyPasteBackend me = null;

    public static CopyPasteBackend instance() {
        if (me == null) {
            me = new CopyPasteBackend();
        }
        return me;
    }

    /**
     * Initializes the CopyPasteBackend. The CopyPasteBackend subscribes itself to
     * the workspace, so it can proccess events for model switching and similar
     * tasks.
     */
    public static void init() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("permanentFocusOwner",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent arg0) {
                        Component focus = KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                .getPermanentFocusOwner();
                        CopyPastePresentation newActive = null;
                        for (CopyPastePresentation p : Hub.getWorkspace()
                                .getPresentationsOfType(CopyPastePresentation.class)) {
                            if (p.getGUI().isAncestorOf(focus) || p.getGUI().equals(focus)) {
                                newActive = p;
                                break;
                            }
                        }
                        boolean changed = false;
                        if (newActive != null && newActive != activeCopyPastePresentation) {
                            activeCopyPastePresentation = newActive;
                            changed = true;
                        }
                        // only refresh if the
                        // activeCopyPastePresentation has
                        // changed
                        if (changed) {
                            instance().refresh();
                        }
                    }
                });
        // refresh the enabled status whenever the flavors on the clipboard
        // change.
        clipboard.addFlavorListener(new FlavorListener() {

            public void flavorsChanged(FlavorEvent arg0) {
                try {
                    instance().refresh();
                } catch (IllegalStateException e) {
                    // happens (sometimes? not always) when the clipboard is
                    // being used by another application, because the refresh
                    // method accesses the clipboard.
                }
            }

        });

        // Subscribing as a WorkspaceSubscriber
        Hub.getWorkspace().addSubscriber(new WorkspaceSubscriber() {
            /**
             * Notifies this subscriber that a model collection change (a DES model is
             * created or opened (added), closed (removed) or renamed) has occurred in a
             * <code>WorkspacePublisher</code> to which I have subscribed.
             * 
             * @param message details of the change notification
             */
            public void modelCollectionChanged(WorkspaceMessage message) {/* NOTE not used here */
            }

            /**
             * Notifies this subscriber that a the model type has been switched (the type of
             * active model has changed e.g. from FSA to petri net) in a
             * <code>WorkspacePublisher</code> to which I have subscribed.
             * 
             * @param message details of the change notification
             */
            public void modelSwitched(WorkspaceMessage message) {
                instance().refresh();
            }

            /**
             * Notifies this subscriber that a change requiring a repaint has occurred in a
             * <code>WorkspacePublisher</code> to which I have subscribed.
             */
            public void repaintRequired() { /* NOTE not used here */
            }

            public void aboutToRearrangeWorkspace() {
                activeCopyPastePresentation = null;
            }
        });
    }

    /**
     * Determines whether the cut and copy options should be available to the user.
     * First checks whether the activeCopyPastePresentation is not null, then calls
     * isCutCopyEnabled() on the presentation.
     * 
     * @return <code> true </code> if the activeCCPPresenation is not null, and
     *         isCutCopyEnabled() is <code> true </code> on that presentation.
     */
    protected static boolean isCopyOrCutable() {
        if (activeCopyPastePresentation != null) {
            return activeCopyPastePresentation.isCutCopyEnabled();
        }
        return false;
    }

    /**
     * Determines whether the paste option should be available to the user. First
     * checks whether the activeCCPPresentatino is not null, then calls
     * isPasteEnabled() on the presentation.
     * 
     * @return <code> true </code> if the activeCCPPresenation is not null, and
     *         isPasteEnabled() is <code> true </code> on that presentation.
     */
    protected static boolean isPasteable() {
        if (activeCopyPastePresentation != null) {
            return activeCopyPastePresentation.isPasteEnabled();
        }
        return false;
    }

    /**
     * Updates the enabled status of the cut/copy/paste items on the user menu.
     * Refreshes whenever the activeCopyPastePresentation changes or any of the
     * other presentations call this method. If the clipboard contents have changed,
     * calls newItemOnClipboard on the active CopyPastePresentation.
     */
    public void refresh() {
        if (clipboard.getContents(instance()) != prevClipboardContents) {
            prevClipboardContents = clipboard.getContents(instance());
            if (activeCopyPastePresentation != null) {
                activeCopyPastePresentation.newItemOnClipboard();
            }
        }

        if (!isCopyOrCutable()) {
            for (AbstractButton button : uiElementsCutCopy) {
                button.setEnabled(false);
            }
        } else {
            for (AbstractButton button : uiElementsCutCopy) {
                button.setEnabled(true);
            }
        }

        if (!isPasteable()) {
            for (AbstractButton button : uiElementsPaste) {
                button.setEnabled(false);
            }
        } else {
            for (AbstractButton button : uiElementsPaste) {
                button.setEnabled(true);
            }
        }
    }

    /**
     * Registers an {@link AbstractButton} to be updated when the c/c/p state
     * changes.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindCutCopy(AbstractButton element) {
        if (!uiElementsCutCopy.contains(element)) {
            uiElementsCutCopy.add(element);
        }
    }

    /**
     * Registers an {@link AbstractButton} to be updated when the c/c/p state
     * changes
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bindPaste(AbstractButton element) {
        if (!uiElementsPaste.contains(element)) {
            uiElementsPaste.add(element);
        }
    }

    /**
     * Unregisters an {@link AbstractButton} so that it is no longer updated when
     * the c/c/p state changes.
     * 
     * @param element the UI element to be unregistered from automatic updates
     */
    public void unbind(AbstractButton element) {
        uiElementsCutCopy.remove(element);
        uiElementsPaste.remove(element);
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

    public DataFlavor[] getAvailableDataFlavors() {
        return clipboard.getAvailableDataFlavors();
    }

    public CopyPastePresentation getActiveCopyPastePresentation() {
        return activeCopyPastePresentation;
    }

    public Action getCopyOverwriteAction() {
        Action copyAction = new CopyPasteAction();
        copyAction.putValue(Action.ACTION_COMMAND_KEY, Hub.string("copy"));
        return copyAction;
    }

    public Action getCutOverwriteAction() {
        Action cutAction = new CopyPasteAction();
        cutAction.putValue(Action.ACTION_COMMAND_KEY, Hub.string("cut"));
        return cutAction;
    }

    public Action getPasteOverwriteAction() {
        Action pasteAction = new CopyPasteAction();
        pasteAction.putValue(Action.ACTION_COMMAND_KEY, Hub.string("paste"));
        return pasteAction;
    }

}
