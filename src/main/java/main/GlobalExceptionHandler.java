/**
 * 
 */
package main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * @author Lenko Grigorov
 */
public class GlobalExceptionHandler implements UncaughtExceptionHandler {
    private static final long serialVersionUID = 3509293345082828829L;

    private JTextArea messageArea;

    private JTextArea labelArea;

    private JLabel iconLabel;

    private JDialog win;

    private void createWindow() {
        win = new JDialog((java.awt.Frame) null, "Error");
        win.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });
        win.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        Box mainBox = Box.createVerticalBox();

        Box messageBox = Box.createHorizontalBox();
        iconLabel = new JLabel();
        messageBox.add(iconLabel);
        messageBox.add(Box.createRigidArea(new Dimension(10, 0)));
        labelArea = new JTextArea(
                "A serious error occurred in the IDES software.\nYou can choose to continue and check if IDES is still responsive.\nIf you press on the quit button, IDES will terminate.");
        labelArea.setEditable(false);
        labelArea.setBackground(mainBox.getBackground());
        messageBox.add(labelArea);
        messageBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        mainBox.add(messageBox);

        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane sPane = new JScrollPane(messageArea);
        sPane.setPreferredSize(new Dimension(200, 300));
        sPane.setBorder(BorderFactory.createTitledBorder("Error details"));
        mainBox.add(sPane);

        mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                goOn();
            }
        });
        buttonBox.add(continueButton);
        buttonBox.add(Box.createRigidArea(new Dimension(5, 0)));
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                quit();
            }
        });
        buttonBox.add(quitButton);
        mainBox.add(buttonBox);
        mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        win.getContentPane().add(mainBox);
        // pack();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang
     * .Thread, java.lang.Throwable)
     */
    public void uncaughtException(Thread arg0, Throwable arg1) {
        createWindow();
        messageArea.setText("Message: " + arg1.getMessage() + "\n");
        messageArea.append("Exception in thread \"" + arg0.getName() + "\" " + arg1.getClass().getName() + "\n");
        StackTraceElement[] elements = arg1.getStackTrace();
        for (int i = 0; i < elements.length; ++i) {
            messageArea.append("    at " + elements[i].toString() + "\n");
        }

        // Logging the message into a text file that follows the format:
        // log_|YEAR|MONTH|DAY|HOUR|MIN|SEC|.log
        // The file will be saved on the IDES main folder.
        String logMessage = messageArea.getText();
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String logFilename = "IDES_" + dataFormat.format(new Date()) + ".log";
        try {
            PrintStream ps = new PrintStream(new FileOutputStream(logFilename));
            ps.print(logMessage);
            ps.close();
        } catch (Exception e) {

        }

        javax.swing.SwingUtilities.updateComponentTreeUI(win);
        iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        labelArea.setBackground(win.getBackground());
        labelArea.setFont(new JLabel().getFont());
        win.pack();
        win.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - win.getWidth()) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - win.getHeight()) / 2);
        win.setModal(true);
        win.setVisible(true);
        win.dispose();
    }

    /**
     * Handling of AWT/Swing exceptions.
     * 
     * @param t exception
     */
    public void handle(Throwable t) {
        uncaughtException(Thread.currentThread(), t);
    }

    protected void goOn() {
        win.setVisible(false);
    }

    protected void quit() {
        System.exit(2);
    }
}
