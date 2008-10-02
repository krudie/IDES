package ides.api.core;

import ides.api.ui.ZoomControl;

import java.awt.Frame;

import javax.swing.JComponent;

import services.notice.NoticeBoard;

/**
 * Interface for the services provided by the main window interface.
 * 
 * @author Lenko Grigorov
 */
public interface UserInterface
{
	/**
	 * Access to the main window.
	 * 
	 * @return the main window of the application
	 */
	public Frame getWindow();

	/**
	 * Access to the status bar.
	 * 
	 * @return the status bar of the application
	 */
	public JComponent getStatusBar();

	/**
	 * Access to the zoom control element.
	 * 
	 * @return the zoom control
	 */
	public ZoomControl getZoomControl();

	/**
	 * Bring forward the list of notices ({@link NoticeBoard}).
	 */
	public void activateNotices();
}
