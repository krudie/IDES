package services.notice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalIconFactory;

import ides.api.core.Hub;
import ides.api.utilities.ContractableTextArea;

/**
 * The UI element (representation) of a notice. It can have one of two states,
 * expanded and collapsed, and it can be highlighted.
 * 
 * @author Lenko Grigorov
 */
public class NoticeUI extends JPanel {
    private static final long serialVersionUID = 7473968869949252281L;

    /**
     * Icon for "information" notices.
     */
    public final static Icon ICON_INFO = new ImageIcon(
            Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/notice_info.gif")));

    /**
     * Icon for "warning" notices.
     */
    public final static Icon ICON_WARN = new ImageIcon(
            Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/notice_warn.gif")));

    /**
     * Icon for "error" notices.
     */
    public final static Icon ICON_ERROR = new ImageIcon(
            Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/notice_error.gif")));

    /**
     * Highlight color for "info" notices.
     */
    public final static Color COLOR_INFO = new Color(124, 200, 255);

    /**
     * Highlight color for "warning" notices.
     */
    public final static Color COLOR_WARN = new Color(255, 250, 198);

    /**
     * Highlight color for "error" notices.
     */
    public final static Color COLOR_ERROR = new Color(255, 169, 142);

    /**
     * The notice represented by the UI element.
     */
    protected Notice notice;

    /**
     * The icon for the notice.
     */
    protected Icon icon;

    /**
     * Tooolbar containing the close button.
     */
    protected JToolBar tb = new JToolBar();

    /**
     * The close button.
     */
    protected JButton disposeButton;

    /**
     * Label with the short summary of the notice.
     */
    protected JLabel label;

    /**
     * The color to be used for highlighting the notice.
     */
    protected Color highlightColor;

    /**
     * The text area with the complete text of the notice.
     */
    protected JTextArea area;

    /**
     * Keeps track of the expanded/collapsed state of the notice.
     */
    protected boolean isExpanded = false;

    /**
     * Constructs the UI element for the given notice.
     * 
     * @param n the notice to be represented by the UI element
     */
    public NoticeUI(Notice n) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Box box = Box.createHorizontalBox();
        add(box);
        notice = n;
        if (notice.type == Notice.ERROR) {
            icon = ICON_ERROR;
            highlightColor = COLOR_ERROR;
        } else if (notice.type == Notice.WARNING) {
            icon = ICON_WARN;
            highlightColor = COLOR_WARN;
        } else {
            icon = ICON_INFO;
            highlightColor = COLOR_INFO;
        }
        disposeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(16));
        disposeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NoticeBackend.instance().revoke(notice.id);
            }
        });
        tb.setFloatable(false);
        tb.add(disposeButton);
        label = new JLabel(notice.digest);
        label.setIcon(icon);
        area = new ContractableTextArea(notice.fullBody);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(label.getFont());
        box.add(tb);
        box.add(Box.createRigidArea(new Dimension(5, 0)));
        box.add(label);
        box.add(Box.createHorizontalGlue());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setExpanded(false);
    }

    /**
     * Sets the expanded/collapsed state of the notice.
     * 
     * @param b if <code>true</code> expand the notice, else collapse it
     */
    public void setExpanded(boolean b) {
        isExpanded = b;
        if (isExpanded) {
            if (getComponentCount() < 2) {
                add(area);
            }
        } else {
            remove(area);
        }
    }

    /**
     * Checks the expanded/collapsed state of the notice.
     * 
     * @return <code>true</code> if the notice is expanded, else <code>false</code>
     */
    public boolean isExpanded() {
        return isExpanded;
    }

    /**
     * Sets the highlighting of the notice.
     * 
     * @param b if <code>true</code> the notice gets highlighted, else it gets
     *          de-highlighted
     */
    public void setHighlight(boolean b) {
        if (b) {
            setBackground(highlightColor);
            tb.setBackground(highlightColor);
            disposeButton.setBackground(highlightColor);
        } else {
            setBackground((ColorUIResource) UIManager.getDefaults().get("Panel.background"));
            tb.setBackground((ColorUIResource) UIManager.getDefaults().get("Panel.background"));
            disposeButton.setBackground((ColorUIResource) UIManager.getDefaults().get("Panel.background"));
        }
    }
}
