package ides.api.core;

import java.awt.Frame;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;

import ides.api.ui.FontSizeSelector;
import ides.api.ui.ZoomControl;
import services.notice.NoticeBoard;

/**
 * Interface for the services provided by the main window interface.
 * 
 * @author Lenko Grigorov
 */
public interface UserInterface {
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
     * Access to the font selector element.
     * 
     * @return the font selector
     */
    public FontSizeSelector getFontSelector();

    /**
     * Bring forward the list of notices ({@link NoticeBoard}).
     */
    public void activateNotices();

    /**
     * Returns <code>true</code> if the argument is a {@link WindowEvent} that
     * describes the activation (gaining of focus) of the main window after the
     * notices popup has just become deactivated (lost focus).
     * <p>
     * This information is important in case some custom elements of the interface
     * track the activation state of the main window. An activation of the main
     * window can be followed by activation of the notices popup (main window
     * becomes deactivated), which then switches focus back to the main window (main
     * window becomes activated again). Thus, it may be necessary to differentiate
     * between these two activation conditions.
     * 
     * @param we event describing the change of window activation
     * @return <code>true</code> if the argument is a {@link WindowEvent} that
     *         describes the activation (gaining of focus) of the main window after
     *         the notices popup has just become deactivated (lost focus);
     *         <code>false</code> otherwise
     * @see #isWindowDeactivationDuetoNoticePopup(WindowEvent)
     */
    public boolean isWindowActivationAfterNoticePopup(WindowEvent we);

    /**
     * Returns <code>true</code> if the argument is a {@link WindowEvent} that
     * describes the deactivation (loss of focus) of the main window due to the
     * notices popup becoming activated (gaining focus).
     * <p>
     * This information is important in case some custom elements of the interface
     * track the activation state of the main window. A deactivation of the main
     * window may be due to the activation of the notices popup. Add-ons may wish to
     * ignore this deactivation.
     * 
     * @param we event describing the change of window activation
     * @return <code>true</code> if the argument is a {@link WindowEvent} that
     *         describes the activation (gaining of focus) of the main window after
     *         the notices popup has just become deactivated (lost focus);
     *         <code>false</code> otherwise
     * @see #isWindowActivationAfterNoticePopup(WindowEvent)
     */
    public boolean isWindowDeactivationDuetoNoticePopup(WindowEvent we);
}
