/**
 * 
 */
package presentation.fsa;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import ides.api.core.Hub;
import ides.api.utilities.EscapeDialog;
import presentation.fsa.actions.GraphActions;

/**
 * @author Lenko Grigorov
 */
public class SingleLineNodeLabellingDialog extends EscapeDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -2203401206948087957L;

    protected static final int WIDTH = 15;

    protected static final int HEIGHT = 4;

    private static SingleLineNodeLabellingDialog me = null;

    private static FSAGraph gm = null;

    private static Node n;

    protected Action enterListener = new AbstractAction() {
        /**
         * 
         */
        private static final long serialVersionUID = 764189878462890163L;

        public void actionPerformed(ActionEvent actionEvent) {
            commitAndClose();
        }
    };

    protected static WindowListener commitOnFocusLost = new WindowListener() {
        public void windowActivated(WindowEvent arg0) {
            if (arg0.getOppositeWindow() != null && !Hub.getUserInterface().isWindowActivationAfterNoticePopup(arg0)) {
                instance().commitAndClose();
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        area.requestFocus();
                    }
                });
            }
        }

        public void windowClosed(WindowEvent arg0) {
        }

        public void windowClosing(WindowEvent arg0) {
        }

        public void windowDeactivated(WindowEvent arg0) {
        }

        public void windowDeiconified(WindowEvent arg0) {
        }

        public void windowIconified(WindowEvent arg0) {
        }

        public void windowOpened(WindowEvent arg0) {
        }
    };

    private SingleLineNodeLabellingDialog() {
        super(Hub.getMainWindow(), Hub.string("nodeLabellingTitle"));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                commitAndClose();
            }
        });
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Box mainBox = Box.createVerticalBox();
        mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        area = new JTextField(WIDTH);
        // Object
        // actionKey=area.getInputMap(JComponent.WHEN_FOCUSED).get(KeyStroke.
        // getKeyStroke(KeyEvent.VK_ENTER,0));
        area.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), this);
        // area.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(
        // KeyEvent.VK_ENTER,KeyEvent.CTRL_DOWN_MASK),actionKey);
        area.getActionMap().put(this, enterListener);
        // JScrollPane sPane=new JScrollPane(area);
        mainBox.add(area);
        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));
        // Box labelBox=Box.createHorizontalBox();
        // labelBox.add(new JLabel(Hub.string("ctrlEnter4NewLine")));
        // labelBox.add(Box.createHorizontalGlue());
        // mainBox.add(labelBox);

        getContentPane().add(mainBox);
        pack();
    }

    public static SingleLineNodeLabellingDialog instance() {
        if (me == null) {
            me = new SingleLineNodeLabellingDialog();
        }
        return me;
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    protected static JTextField area;

    public static void showAndLabel(GraphDrawingView gdv, FSAGraph gm, Node node) {
        gdv.startUIInteraction();
        SingleLineNodeLabellingDialog.gm = gm;
        n = node;
        Point p = new Point((int) node.getLayout().getLocation().x, (int) node.getLayout().getLocation().y);
        instance();
        me.pack();
        String label = node.getLabel().getText();
        boolean hasOurListener = false;
        for (int i = 0; i < Hub.getMainWindow().getWindowListeners().length; ++i) {
            if (Hub.getMainWindow().getWindowListeners()[i] == commitOnFocusLost) {
                hasOurListener = true;
            }
        }
        if (!hasOurListener) {
            Hub.getMainWindow().addWindowListener(commitOnFocusLost);
        }
        area.setText(label);
        area.selectAll();
        if (gdv == null) {
            return;
        }
        Point2D.Float r = gdv.localToScreen(new Point2D.Float(p.x, p.y));
        p.x = (int) r.x + gdv.getLocationOnScreen().x;
        p.y = (int) r.y + gdv.getLocationOnScreen().y;
        if (p.x + me.getWidth() > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
            p.x = p.x - me.getWidth();
        }
        if (p.y + me.getHeight() > Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
            p.y = p.y - me.getHeight();
        }
        me.setLocation(p);
        me.setVisible(true);
        area.requestFocus();
    }

    @Override
    public void onEscapeEvent() {
        Hub.getMainWindow().removeWindowListener(commitOnFocusLost);
        setVisible(false);
    }

    protected void commitAndClose() {
        if (gm != null && !area.getText().equals(n.getLabel().getText())) {
            new GraphActions.LabelAction(gm, n, area.getText()).execute();
        }
        onEscapeEvent();
    }
}
