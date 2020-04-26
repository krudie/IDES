package services.notice;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ides.api.core.Hub;

/**
 * The UI element which displays the notices posted at {@link NoticeBackend}.
 * 
 * @author Lenko Grigorov
 */
public class NoticeBoard extends JScrollPane implements MouseListener, MouseMotionListener, ChangeListener {
    private static final long serialVersionUID = -4516469678131280143L;

    private static NoticeBoard me = null;

    public static NoticeBoard instance() {
        if (me == null) {
            me = new NoticeBoard();
        }
        return me;
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    private NoticeBoard() {
        mainBox = Box.createVerticalBox();
        setViewportView(mainBox);
        setName(Hub.string("noticeTab"));
        addMouseListener(this);
        addMouseMotionListener(this);
        getViewport().addChangeListener(this);
        noNoticesBox = Box.createHorizontalBox();
        noNoticesBox.add(Box.createHorizontalGlue());
        noNoticesBox.add(new JLabel(Hub.string("noNotices")));
        noNoticesBox.add(Box.createHorizontalGlue());
        noNoticesBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * The box containing the UI elements of the posted notices.
     */
    protected Box mainBox = Box.createVerticalBox();

    /**
     * The UI element displayed when there are no notices.
     */
    protected Box noNoticesBox;

    /**
     * The UI element of the notice which was last clicked. This info is needed for
     * a nasty hack to achieve auto-scrolling.
     */
    protected NoticeUI focused = null;

    /**
     * The height of the UI element of the notice which was last clicked, at the
     * time of clicking it. This info is needed for a nasty hack to achieve
     * auto-scrolling.
     */
    protected int focusedLastHeight = 0;

    /**
     * Responds to changes in the scroll pane containing the notices. This callback
     * method is needed for a nasty hack to achieve auto-scrolling.
     */
    public void stateChanged(ChangeEvent e) {
        // this auto-scroll is necessary because Swing does additional resizing
        // after we "expand" a notice and having the auto-scroll
        // immediately after the "expand" won't work properly
        // ...bad, bad hack. Swing sucks (sometimes)
        if (focused != null && focusedLastHeight != focused.getHeight()) {
            mainBox.scrollRectToVisible(focused.getBounds());
            focusedLastHeight = focused.getHeight();
        }
    }

    /**
     * Responds to mouse clicks to expand/collapse notices.
     */
    public void mouseClicked(MouseEvent me) {
        Point p = me.getPoint();
        SwingUtilities.convertPointToScreen(p, this);
        SwingUtilities.invokeLater(new ToggleNotice(p));
    }

    /**
     * Initiates de-highlighting of all notices if the mouse cursor exits the notice
     * board.
     */
    public void mouseExited(MouseEvent me) {
        if (contains(me.getPoint())) {
            return;
        }
        unhighlightAll();
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }

    /**
     * Initiates the highlighting of the notice under the mouse cursor.
     */
    public void mouseMoved(MouseEvent me) {
        Point p = me.getPoint();
        SwingUtilities.convertPointToScreen(p, this);
        SwingUtilities.invokeLater(new HighlightNotice(p));
    }

    public void mouseDragged(MouseEvent me) {
    }

    /**
     * Updates the UI with the current collection of notices.
     */
    public synchronized void update() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Vector<Notice> notices = NoticeBackend.instance().getNotices();
                mainBox.removeAll();
                for (Notice n : notices) {
                    mainBox.add(n.uiElement);
                    Box border = Box.createHorizontalBox();
                    border.add(Box.createHorizontalGlue());
                    border.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    mainBox.add(border);
                }
                if (notices.isEmpty()) {
                    mainBox.add(noNoticesBox);
                }
                validate();
                repaint();
            }
        });
    }

    /**
     * Returns the UI element of the notice displayed at the given location on the
     * screen. If there is no notice at this location, returns <code>null</code>.
     * 
     * @param p a point on the screen
     * @return the notice displayed at the given location, or <code>null</code> if
     *         there is no notice at the location
     */
    protected synchronized NoticeUI getNoticeAt(Point p) {
        for (Component c : mainBox.getComponents()) {
            if (c instanceof NoticeUI) {
                SwingUtilities.convertPointFromScreen(p, c);
                if (c.contains(p)) {
                    return (NoticeUI) c;
                }
                SwingUtilities.convertPointToScreen(p, c);
            }
        }
        return null;
    }

    /**
     * When run, highlights the notice displayed at the given location on the screen
     * and un-highlights all other notices. If there is no notice at the given
     * location, all notices get un-highlighted.
     * <p>
     * A method cannot be used since the operation has to be performed within the
     * Swing thread.
     */
    protected class HighlightNotice implements Runnable {
        /**
         * Point on the screen.
         */
        private Point p;

        /**
         * Initializes with the point on the screen where a notice should be found.
         * 
         * @param p a point on the screen
         */
        public HighlightNotice(Point p) {
            this.p = p;
        }

        /**
         * Performs the actual highlighting.
         */
        public void run() {
            focused = null;
            NoticeUI notice = getNoticeAt(p);
            for (Component c : mainBox.getComponents()) {
                if (c instanceof NoticeUI) {
                    if (c == notice) {
                        ((NoticeUI) c).setHighlight(true);
                        focused = (NoticeUI) c;
                        focusedLastHeight = c.getHeight();
                    } else {
                        ((NoticeUI) c).setHighlight(false);
                    }
                }
            }
        }
    }

    /**
     * Un-highlights all notices.
     */
    protected synchronized void unhighlightAll() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                focused = null;
                for (Component c : mainBox.getComponents()) {
                    if (c instanceof NoticeUI) {
                        ((NoticeUI) c).setHighlight(false);
                    }
                }
            }
        });
    }

    /**
     * When run, toggles the "expanded" state of the notice at the given location on
     * the screen. If there is no notice at the given location, it does not do
     * anything.
     * <p>
     * A method cannot be used since the operation has to be performed within the
     * Swing thread.
     */
    protected class ToggleNotice implements Runnable {
        /**
         * Point on the screen.
         */
        private Point p;

        /**
         * Initializes with the point on the screen where a notice should be found.
         * 
         * @param p a point on the screen
         */
        public ToggleNotice(Point p) {
            this.p = p;
        }

        /**
         * Performs the actual toggling.
         */
        public void run() {
            NoticeUI notice = getNoticeAt(p);
            if (notice == null) {
                return;
            }
            for (Component c : mainBox.getComponents()) {
                if (c instanceof NoticeUI) {
                    if (c == notice) {
                        ((NoticeUI) c).setExpanded(!((NoticeUI) c).isExpanded());
                        validate();
                        repaint();
                        return;
                    }
                }
            }
        }
    }
}
