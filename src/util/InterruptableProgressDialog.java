package util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import ides.api.core.Hub;
import ides.api.utilities.EscapeDialog;

/**
 * A dialog box with a label, a progress bar and a cancel button. It implements
 * the {@link java.lang.Runnable} interface. The code which performs the
 * operations whose progress has to be displayed can reside in the
 * {@link #run()} method. Thus the whole dialog box can be run as a thread. The
 * progress bar has to be updated manually. If the user chooses to cancel the
 * process, the {@link #interrupt()} method will be called and your code has to
 * react appropriately.
 * 
 * @see #run()
 * @see #progressBar
 * @see #interrupt()
 * @author Lenko Grigorov
 */
public abstract class InterruptableProgressDialog extends EscapeDialog implements Runnable {

    private static final long serialVersionUID = -37342748827455631L;

    /**
     * The default width of the options dialog
     */
    protected static final int WIDTH = 400;

    /**
     * The default height of the options dialog
     */
    protected static final int HEIGHT = 110;

    /**
     * The progress bar which displays the progress of the operation.
     * <p>
     * Its position has to be update manually.
     * 
     * @see #run()
     */
    protected JProgressBar progressBar;

    /**
     * The label in the dialog box.
     */
    protected JLabel label;

    /**
     * Constructs a new dialog box with a progress bar and sets it up so that it can
     * handle interruption by the user.
     * 
     * @param owner   the {@link java.awt.Frame} from which the dialog is displayed
     * @param title   the title of the dialog box
     * @param message the message displayed above the progress bar
     */
    protected InterruptableProgressDialog(Frame owner, String title, String message) {
        super(owner, title, true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onEscapeEvent();
            }
        });
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        progressBar = new JProgressBar();
        JButton cancelButton = new JButton(Hub.string("cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEscapeEvent();
            }
        });
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Box labelBox = Box.createHorizontalBox();
        label = new JLabel(message);
        labelBox.add(label);
        labelBox.add(Box.createHorizontalGlue());
        pane.add(labelBox);
        pane.add(Box.createRigidArea(new Dimension(0, 2)));
        pane.add(progressBar);
        pane.add(Box.createRigidArea(new Dimension(0, 10)));
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(cancelButton);
        pane.add(buttonBox);
        getContentPane().add(pane);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Point location = Hub.getCenteredLocationForDialog(new Dimension(WIDTH, HEIGHT));
        setLocation(location.x, location.y);

        pack();
    }

    /**
     * Called when the user presses the <code>Escape</code> key. Calls
     * {@link #interrupt()}.
     * 
     * @see #interrupt()
     */
    @Override
    protected void onEscapeEvent() {
        interrupt();
    }

    /**
     * Called when the user chooses to interrupt the process.
     * <p>
     * Place here the code that will halt the progress of your process.
     * 
     * @see #run()
     */
    public abstract void interrupt();

    /**
     * Called to trigger the activities whose progress will be displayed.
     * <p>
     * Place here the code which performs the activities whose progress is
     * displayed.
     * <p>
     * You have to manually update the progress bar.
     * 
     * @see #progressBar
     * @see #interrupt()
     */
    public abstract void run();

}
