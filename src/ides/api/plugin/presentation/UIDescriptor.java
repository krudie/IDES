package ides.api.plugin.presentation;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToolBar;

public interface UIDescriptor
{
	public Presentation[] getMainPanePresentations();

	public Presentation[] getLeftPanePresentations();

	public Presentation[] getRightPanePresentations();

	public JMenu[] getMenus();

	public JToolBar getToolbar();

	public JComponent getStatusBar();

	public JMenu getPopupMenu();

	public boolean supportsZoom();
}
