package pluggable.ui;

import javax.swing.JPanel;

/**
 * The interface for modules that will appear in the Options dialog box.
 * The {@link ui.OptionsWindow} lets the user select from a list of
 * sections, where each section displays a different set of controls
 * that let the user modify the appplication settings. Each application
 * module or plugin can register its own section by calling
 * {@link main.Hub#registerOptionsPane(OptionsPane)} and providing
 * an {@link OptionsPane} as an argument. The set of controls
 * for the module or plugin have to appear in the {@link javax.swing.JPanel}
 * that is returned by the {@link #getPane()} method. 
 * @see ui.OptionsWindow
 * @see main.Hub#registerOptionsPane(OptionsPane)
 * 
 * @author Lenko Grigorov
 *
 */
public interface OptionsPane {
	
	/**
	 * Returns the string which will appear in the list of sections
	 * in the Optins dialog box. The user will identify the options
	 * controls in this {@link OptionsPane} with this string. 
	 * @return the title of the section containing this options pane
	 * @see ui.OptionsWindow
	 */
	public String getTitle();
	
	/**
	 * Returns the {@link javax.swing.JPanel} which contains the options
	 * controls for this {@link OptionsPane}. This {@link javax.swing.JPanel}
	 * will be displayed in the Options dialog box when the user selects
	 * the section for this options pane.
	 * <p>This method may be called numerous times during one session.
	 * Do not construct a new {@link javax.swing.JPanel} for each call;
	 * simply pass the same reference.
	 * @return the {@link javax.swing.JPanel} with the options controls.
	 * @see ui.OptionsWindow
	 */
	public JPanel getPane();
	
	/**
	 * Reset all settings of the controls in the {@link javax.swing.JPanel}.
	 * This method will usually be invoked when the user chooses to cancel
	 * any choices they have made. The module or plugin must not change
	 * behavor.
	 * <p>This method may be called a number between calls to
	 * {@link #getPane()}. You must not dispose of the {@link OptionsPane}
	 * after a call to this method.
	 * @see #getPane()
	 * @see ui.OptionsWindow
	 */
	public void resetOptions();
	
	/**
	 * Commit all settings from the controls in the {@link javax.swing.JPanel}.
	 * This method will usually be invoked when the user chooses to accept
	 * any choices thay have made. The module or plugin must change
	 * behavior according to the new settings. 
	 * <p>This method may be called a number between calls to
	 * {@link #getPane()}. You must not dispose of the {@link OptionsPane}
	 * after a call to this method.
	 * @see #getPane()
	 * @see ui.OptionsWindow
	 */
	public void commitOptions();
	
}
