package ides.api.utilities;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 * A class which extends {@link javax.swing.JDialog} with the capability to
 * handle the event of the user pressing the <code>Escape</code> key. Instead of
 * subclassing {@link javax.swing.JDialog}, subclass {@link EscapeDialog} and
 * override {@link #onEscapeEvent()}.
 * 
 * @see #onEscapeEvent()
 * @see javax.swing.JDialog
 * @author Lenko Grigorov
 */
public class EscapeDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -6032942863756073899L;

    /**
     * The listener for the user pressing the <code>Escape</code> key. Calls
     * {@link #onEscapeEvent()}.
     * 
     * @see #onEscapeEvent()
     * @see #bindEscapeListener()
     */
    protected Action escapeListener = new AbstractAction() {
        /**
         * 
         */
        private static final long serialVersionUID = -8022129075413598931L;

        public void actionPerformed(ActionEvent actionEvent) {
            onEscapeEvent();
        }
    };

    /**
     * Creates a non-modal dialog without a title and without a specified
     * {@link java.awt.Frame} owner. A shared, hidden frame will be set as the owner
     * of the dialog.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog() throws HeadlessException {
        super();
        bindEscapeListener();
    }

    /**
     * Creates a non-modal dialog without a title with the specified
     * {@link java.awt.Frame} as its owner. If owner is <code>null</code>, a shared,
     * hidden frame will be set as the owner of the dialog.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the {@link java.awt.Frame} from which the dialog is displayed
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Frame owner) throws HeadlessException {
        super(owner);
        bindEscapeListener();
    }

    /**
     * Creates a modal or non-modal dialog without a title and with the specified
     * owner {@link java.awt.Frame}. If owner is <code>null</code>, a shared, hidden
     * frame will be set as the owner of the dialog.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the {@link java.awt.Frame} from which the dialog is displayed
     * @param modal true for a modal dialog, false for one that allows others
     *              windows to be active at the same time
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Frame owner, boolean modal) throws HeadlessException {
        super(owner, modal);
        bindEscapeListener();
    }

    /**
     * Creates a non-modal dialog with the specified title and with the specified
     * owner {@link java.awt.Frame}. If owner is <code>null</code>, a shared, hidden
     * frame will be set as the owner of the dialog.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the {@link java.awt.Frame} from which the dialog is displayed
     * @param title the string to display in the dialog's title bar
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Frame owner, String title) throws HeadlessException {
        super(owner, title);
        bindEscapeListener();
    }

    /**
     * Creates a modal or non-modal dialog with the specified title and the
     * specified owner {@link java.awt.Frame}. If owner is <code>null</code>, a
     * shared, hidden frame will be set as the owner of this dialog. All
     * constructors defer to this one.
     * <p>
     * NOTE: Any popup components ({@link javax.swing.JComboBox},
     * {@link javax.swing.JPopupMenu}, {@link javax.swing.JMenuBar}) created within
     * a modal dialog will be forced to be lightweight.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the {@link java.awt.Frame} from which the dialog is displayed
     * @param title the string to display in the dialog's title bar
     * @param modal true for a modal dialog, false for one that allows others
     *              windows to be active at the same time
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Frame owner, String title, boolean modal) throws HeadlessException {
        super(owner, title, modal);
        bindEscapeListener();
    }

    /**
     * Creates a modal or non-modal dialog with the specified title, owner
     * {@link java.awt.Frame}, and {@link java.awt.GraphicsConfiguration}.
     * <p>
     * NOTE: Any popup components ({@link javax.swing.JComboBox},
     * {@link javax.swing.JPopupMenu}, {@link javax.swing.JMenuBar}) created within
     * a modal dialog will be forced to be lightweight.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the {@link java.awt.Frame} from which the dialog is displayed
     * @param title the string to display in the dialog's title bar
     * @param modal true for a modal dialog, false for one that allows others
     *              windows to be active at the same time
     * @param gc    the {@link java.awt.GraphicsConfiguration} of the target screen
     *              device. If <code>gc</code> is <code>null</code>, the same
     *              {@link java.awt.GraphicsConfiguration} as the owning
     *              {@link java.awt.Frame} is used.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        bindEscapeListener();
    }

    /**
     * Creates a non-modal dialog without a title with the specified
     * {@link java.awt.Dialog} as its owner.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the non-null {@link java.awt.Dialog} from which the dialog is
     *              displayed
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Dialog owner) throws HeadlessException {
        super(owner);
        bindEscapeListener();
    }

    /**
     * Creates a modal or non-modal dialog without a title and with the specified
     * owner {@link java.awt.Dialog}.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the non-null {@link java.awt.Dialog} from which the dialog is
     *              displayed
     * @param modal true for a modal dialog, false for one that allows others
     *              windows to be active at the same time
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Dialog owner, boolean modal) throws HeadlessException {
        super(owner, modal);
        bindEscapeListener();
    }

    /**
     * Creates a non-modal dialog with the specified title and with the specified
     * owner {@link java.awt.Dialog}.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the non-null {@link java.awt.Dialog} from which the dialog is
     *              displayed
     * @param title the string to display in the dialog's title bar
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Dialog owner, String title) throws HeadlessException {
        super(owner, title);
        bindEscapeListener();
    }

    /**
     * Creates a modal or non-modal dialog with the specified title and the
     * specified owner {@link java.awt.Dialog}.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the non-null {@link java.awt.Dialog} from which the dialog is
     *              displayed
     * @param title the string to display in the dialog's title bar
     * @param modal true for a modal dialog, false for one that allows others
     *              windows to be active at the same time
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Dialog owner, String title, boolean modal) throws HeadlessException {
        super(owner, title, modal);
        bindEscapeListener();
    }

    /**
     * Creates a modal or non-modal dialog with the specified title, owner
     * {@link java.awt.Dialog}, and {@link java.awt.GraphicsConfiguration}.
     * <p>
     * NOTE: Any popup components ({@link javax.swing.JComboBox},
     * {@link javax.swing.JPopupMenu}, {@link javax.swing.JMenuBar}) created within
     * a modal dialog will be forced to be lightweight.
     * <p>
     * This constructor sets the component's locale property to the value returned
     * by {@link javax.swing.JComponent#getDefaultLocale()}.
     * 
     * @param owner the {@link java.awt.Dialog} from which the dialog is displayed
     * @param title the string to display in the dialog's title bar
     * @param modal true for a modal dialog, false for one that allows others
     *              windows to be active at the same time
     * @param gc    the {@link java.awt.GraphicsConfiguration} of the target screen
     *              device. If <code>gc</code> is <code>null</code>, the same
     *              {@link java.awt.GraphicsConfiguration} as the owning
     *              {@link java.awt.Dialog} is used.
     * @throws HeadlessException if
     *                           {@link java.awt.GraphicsEnvironment#isHeadless()}
     *                           returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless()
     * @see javax.swing.JComponent#getDefaultLocale()
     */
    public EscapeDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException {
        super(owner, title, modal, gc);
        bindEscapeListener();
    }

    /**
     * Called by the constructor to bind {@link #escapeListener} with the
     * {@link #rootPane} so that the <code>Escape</code> key can be intercepted.
     * 
     * @see #escapeListener
     */
    protected void bindEscapeListener() {
        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), this);
        rootPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), this);
        rootPane.getActionMap().put(this, escapeListener);
    }

    /**
     * Called when the user presses the <code>Escape</code> key. Override to
     * custom-handle this event.
     */
    protected void onEscapeEvent() {
    }
}
