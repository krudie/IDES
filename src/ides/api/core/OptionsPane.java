package ides.api.core;

import javax.swing.JPanel;

/**
 * The interface for modules that will appear in the Options dialog box. The
 * {@link ui.OptionsWindow} lets the user select from a list of sections, where
 * each section displays a different set of controls that let the user modify
 * the appplication settings. Each application module or plugin can register its
 * own section by calling
 * {@link ides.api.core.Hub#registerOptionsPane(OptionsPane)} and providing an
 * {@link OptionsPane} as an argument. The set of controls for the module or
 * plugin have to appear in the {@link javax.swing.JPanel} that is returned by
 * the {@link #getPane()} method.
 * <p>
 * The protocol for calls to the methods in the interface is as follows:
 * <ul>
 * <li>A call to {@link #getTitle()} can be made at any time.
 * <li>After a call to {@link #getPane()} and before the first call to
 * {@link #disposePane()} that follows it: all calls to {@link #resetOptions()}
 * and {@link #commitOptions()} should be acknowledged and all calls to
 * {@link #getPane()} should return a pointer to the same
 * {@link javax.swing.JPanel} that was returned in the first place.
 * <li>After a call to {@link #disposePane()} and before the first call to
 * {@link #getPane()} that follows it: all calls to {@link #disposePane()},
 * {@link #resetOptions()} and {@link #commitOptions()} should be ignored.
 * </ul>
 * In other words, the {@link javax.swing.JPanel} with the options controls
 * should only exist in between a call to {@link #getPane()} and the first
 * {@link #disposePane()} that follows it; and only during this time should
 * calls to {@link #resetOptions()} and {@link #commitOptions()} be
 * acknowledged.
 * 
 * @see ui.OptionsWindow
 * @see ides.api.core.Hub#registerOptionsPane(OptionsPane)
 * @see #getTitle()
 * @see #getPane()
 * @see #disposePane()
 * @see #resetOptions()
 * @see #commitOptions()
 * @author Lenko Grigorov
 */
public interface OptionsPane {

    /**
     * Returns the string which will appear in the list of sections in the Optins
     * dialog box. The user will identify the options controls in this
     * {@link OptionsPane} with this string.
     * 
     * @return the title of the section containing this options pane
     * @see ui.OptionsWindow
     */
    public String getTitle();

    /**
     * Returns the {@link javax.swing.JPanel} which contains the options controls
     * for this {@link OptionsPane}. This {@link javax.swing.JPanel} will be
     * displayed in the Options dialog box when the user selects the section for
     * this options pane.
     * <p>
     * This method may be called numerous times during one session. Do not construct
     * a new {@link javax.swing.JPanel} for each call unless {@link #disposePane()}
     * has been called. If {@link #disposePane()} has not been called, simply pass a
     * reference to the same object.
     * 
     * @return the {@link javax.swing.JPanel} with the options controls.
     * @see ui.OptionsWindow
     * @see #disposePane()
     */
    public JPanel getPane();

    /**
     * Reset all settings of the controls in the {@link javax.swing.JPanel}. This
     * method will usually be invoked when the user chooses to cancel any choices
     * they have made. The module or plugin must not change behavor.
     * <p>
     * This method may be called a number between calls to {@link #getPane()}. You
     * must not dispose of the {@link OptionsPane} after a call to this method.
     * <p>
     * Ignore if called before a call to {@link #getPane()}.
     * 
     * @see #getPane()
     * @see #disposePane()
     * @see ui.OptionsWindow
     */
    public void resetOptions();

    /**
     * Commit all settings from the controls in the {@link javax.swing.JPanel}. This
     * method will usually be invoked when the user chooses to accept any choices
     * thay have made. The module or plugin must change behavior according to the
     * new settings.
     * <p>
     * This method may be called a number between calls to {@link #getPane()}. You
     * must not dispose of the {@link OptionsPane} after a call to this method.
     * <p>
     * Ignore if called before a call to {@link #getPane()}.
     * 
     * @see #getPane()
     * @see #disposePane()
     * @see ui.OptionsWindow
     */
    public void commitOptions();

    /**
     * Called when it is safe to dispose of the {@link javax.swing.JPanel} with the
     * settings controls.
     * <p>
     * Ignore if called before a call to {@link #getPane()}.
     * 
     * @see #getPane()
     * @see ui.OptionsWindow
     */
    public void disposePane();

}
