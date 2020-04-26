package services.notice;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalIconFactory;

import ides.api.core.Hub;

/**
 * The UI element displaying the pop-up balloon when new notices are posted with
 * {@link NoticeBackend}. The balloon is actually always visible but has 0
 * dimensions when there are no messages to display. "Always on" is necessary to
 * prevent stealing the focus of the main window when the balloon is made
 * visible.
 * 
 * @author Lenko Grigorov
 */
public class NoticePopup extends JDialog implements WindowListener, ComponentListener, MouseListener {
    private static final long serialVersionUID = 665616176564861213L;

    private static NoticePopup me = null;

    /**
     * Object used for synchronization purposes. Needed for a nasty hack not to
     * steal focus from the main window.
     */
    private Object sync = new Object();

    /**
     * Set to <code>true</code> when the pop-up balloon is becoming visible. Needed
     * for a nasty hack not to steal focus from the main window.
     */
    private boolean becomingVisible = false;

    public static NoticePopup instance() {
        if (me == null) {
            me = new NoticePopup();
        }
        return me;
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Defines the number of milliseconds after which the popup of a notice should
     * expire.
     */
    public static final long POPUP_EXPIRY_MSEC = 10000;

    /**
     * The list with the pop-up messages.
     */
    protected JList list;

    /**
     * The data model for the {@link #list}.
     */
    protected DefaultListModel model = new DefaultListModel();

    /**
     * The panel which contains all UI elements for the pop-up balloon.
     */
    protected JPanel panel = new JPanel();

    /**
     * Keeps track of the latest time the user closed the pop-up balloon. Notices
     * posted earlier than this time will not be displayed in the balloon.
     */
    protected long closedAt = 0;

    private NoticePopup() {
        list = new JList(model);
        list.setEnabled(false);
        list.setCellRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                return (JLabel) value;
            }
        });
        Box mainBox = Box.createVerticalBox();
        JToolBar tb = new JToolBar();
        JButton disposeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(16));
        tb.setFloatable(false);
        tb.add(disposeButton);
        panel.setBackground(list.getBackground());
        tb.setBackground(list.getBackground());
        disposeButton.setBackground(list.getBackground());
        Box topBox = Box.createHorizontalBox();
        JLabel newNotices = new JLabel(Hub.string("newNotices"));
        newNotices.setFont(newNotices.getFont().deriveFont(Font.BOLD));
        topBox.add(newNotices);
        topBox.add(Box.createHorizontalGlue());
        topBox.add(tb);
        mainBox.add(topBox);
        Box bottomBox = Box.createHorizontalBox();
        bottomBox.add(list);
        bottomBox.add(Box.createHorizontalGlue());
        mainBox.add(bottomBox);
        panel.add(mainBox);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setUndecorated(true);
        setAlwaysOnTop(true);
        pack();
        addMouseListener(this);
        list.addMouseListener(this);
        tb.addMouseListener(this);
        disposeButton.addMouseListener(this);
        Hub.getMainWindow().addComponentListener(this);
        Hub.getMainWindow().addWindowListener(this);
        alignWindow();
        setVisible(true);
    }

    /**
     * Thread to remove expired notices.
     */
    private static Thread expiryWatch;

    /**
     * Set to <code>true</code> to notify expired notices thread to terminate.
     */
    private static boolean terminating = false;

    /**
     * Initializes the NoticePopup. Should be called only once during the execution
     * of the program.
     */
    public static void init() {
        instance();
        expiryWatch = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    instance().update();
                    synchronized (expiryWatch) {
                        if (terminating) {
                            break;
                        }
                        try {
                            expiryWatch.wait(2000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        });
        expiryWatch.start();
    }

    /**
     * Cleans up resources, notifies the thread for expired notices to terminate.
     * Should be called only once when closing the program.
     */
    public static void cleanup() {
        synchronized (expiryWatch) {
            terminating = true;
            expiryWatch.notifyAll();
        }
    }

    /**
     * Aligns the pop-up balloon with the position and the size of the main window.
     */
    protected void alignWindow() {
        Frame win = Hub.getMainWindow();
        setLocation(win.getX() + win.getWidth() - win.getInsets().right - getWidth(), win.getY() + win.getHeight()
                - win.getInsets().bottom - Hub.getUserInterface().getStatusBar().getHeight() - getHeight());
    }

    /**
     * When the main window is activated (e.g., user brings it to the foreground),
     * also make the pop-up balloon visible.
     */
    public void windowActivated(WindowEvent arg0) {
        setVisible(true);
    }

    /**
     * When the main window is closed get rid of the pop-up balloon visible.
     */
    public void windowClosed(WindowEvent arg0) {
        this.dispose();
    }

    public void windowClosing(WindowEvent arg0) {
    }

    /**
     * When the main window is deactivated (e.g., user sends it to the background),
     * hide the pop-up balloon.
     */
    public void windowDeactivated(WindowEvent arg0) {
        synchronized (sync) {
            // nasty hack. When the pop-up balloon is made visible, it steals
            // the
            // focus from the main window and the main window is deactivated.
            // This will prevent the pop-up window from disappearing as a result
            // (and triggering an infinite cycle of focus switching).
            if (becomingVisible) {
                becomingVisible = false;
            } else {
                setVisible(false);
            }
        }
    }

    /**
     * When the main window is deiconified, also make the pop-up balloon visible.
     */
    public void windowDeiconified(WindowEvent arg0) {
        setVisible(true);
    }

    /**
     * When the main window is iconified, hide the pop-up balloon.
     */
    public void windowIconified(WindowEvent arg0) {
        setVisible(false);
    }

    public void windowOpened(WindowEvent arg0) {
    }

    /**
     * When the main window is hidden, hide the pop-up balloon.
     */
    public void componentHidden(ComponentEvent arg0) {
        setVisible(false);
    }

    /**
     * When the main window is moved, re-align the pop-up balloon.
     */
    public void componentMoved(ComponentEvent arg0) {
        alignWindow();
    }

    /**
     * When the main window is resized, re-align the pop-up balloon.
     */
    public void componentResized(ComponentEvent arg0) {
        alignWindow();
    }

    public void componentShown(ComponentEvent arg0) {
    }

    /**
     * Set the visibility of the UI element. When the pop-up balloon becomes
     * visible, this method returns the focus back to the main window.
     */
    public void setVisible(boolean b) {
        if (!b) {
            super.setVisible(false);
        } else if (!this.isVisible()) {
            synchronized (sync) {
                becomingVisible = true;
                super.setVisible(b);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Hub.getMainWindow().requestFocus();
                    }
                });
            }
        }
    }

    /**
     * Updates the content of the pop-up balloon. New notices are added, expired
     * notices are removed.
     */
    public void update() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Vector<Notice> notices = NoticeBackend.instance().getNotices();
                remove(panel);
                model.removeAllElements();
                for (int i = notices.size() - 1; i >= 0; --i) {
                    if (System.currentTimeMillis() - notices.elementAt(i).timeStamp < POPUP_EXPIRY_MSEC
                            && notices.elementAt(i).timeStamp > closedAt) {
                        JLabel label = new JLabel(notices.elementAt(i).digest);
                        if (notices.elementAt(i).type == Notice.ERROR) {
                            label.setIcon(NoticeUI.ICON_ERROR);
                        } else if (notices.elementAt(i).type == Notice.WARNING) {
                            label.setIcon(NoticeUI.ICON_WARN);
                        } else {
                            label.setIcon(NoticeUI.ICON_INFO);
                        }
                        model.addElement(label);
                    } else {
                        break;
                    }
                }
                if (model.size() > 0) {
                    add(panel);
                }
                pack();
                alignWindow();
            }
        });
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    /**
     * If the user clicks anywhere in the pop-up balloon, the balloon gets hidden.
     * If the user does not click on the close button of the balloon, the
     * {@link NoticeBoard} with all the notices is activated in the main window.
     */
    public void mousePressed(MouseEvent arg0) {
        closedAt = System.currentTimeMillis();
        instance().update();
        if (!(arg0.getSource() instanceof JButton)) {
            Hub.getUserInterface().activateNotices();
        }
    }

    public void mouseReleased(MouseEvent arg0) {
    }

}
