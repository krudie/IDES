package ides.api.utilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * A collection of methods and classes of general utility.
 * 
 * @author Lenko Grigorov
 */
public class GeneralUtils {
    /**
     * Truncates to 10 lines and every line to 250 chars. Ellipses (...) are
     * appended to denote truncation.
     * 
     * @param msg original message
     * @return truncated message
     */
    public static String truncateMessage(String msg) {
        if (msg == null) {
            return null;
        }
        String[] lines = msg.split("\n");
        String ret = "";
        for (int i = 0; i < Math.min(10, lines.length); ++i) {
            lines[i] = lines[i].substring(0, Math.min(250, lines[i].length())) + (lines[i].length() > 250 ? "..." : "");
            ret += lines[i] + "\n";
        }
        if (lines.length > 10) {
            ret += "...";
        }
        return ret;
    }

    /**
     * Escapes the ampersand (&amp;) and opening angle bracket (&lt;) as defined in
     * XML. Ampersand becomes &amp;amp; and angle bracket becomes &amp;lt;
     * 
     * @param s original text
     * @return text with XML-escaped ampersands and opening angle brackets
     */
    public static String XMLTextEscape(String s) {
        return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
    }

    /**
     * Unescapes the ampersand (&amp;) and opening angle bracket (&lt;) in
     * XML-escaped text. &amp;amp; becomes ampersand and &amp;lt; becomes angle
     * bracket.
     * 
     * @param s original text with XML-escaped ampersands and opening angle brackets
     * @return text with unescaped ampersands and opening angle brackets
     */
    public static String XMLTextUnescape(String s) {
        return s.replaceAll("&lt;", "<").replaceAll("&amp;", "&");
    }

    /**
     * This class allows the use of simple keyboard shortcuts for the Yes/No/etc.
     * buttons in a {@link JOptionPane}.
     * <p>
     * Normally, a {@link JOptionPane} has mnemonic key bindings with its buttons.
     * For example, in order to select the Yes button via the keyboard, one needs to
     * press <code>Alt+Y</code>. However, this may be counter-intuitive as many
     * users would expect to use simply the key <code>Y</code>.
     * <p>
     * This class provides a hack which lets the use of simple key shortcuts instead
     * of the mnemonic shortcuts. It does the following:
     * <ol>
     * <li>Creates a {@link JComponent} with the given text message.
     * <li>Checks what are the default mnemonic shortcuts. In the default English
     * Java installation these should be <code>Alt+Y</code> for the Yes button,
     * <code>Alt+N</code> for the No button and no shortcuts for the OK and Cancel
     * buttons.
     * <li>Creates new simplified shortcuts (e.g., <code>Y</code> and
     * <code>N</code>) and associates these shortcuts with the text component.
     * <li>For each one of the shortcuts, an {@link Action} is created which looks
     * up the corresponding button in the {@link JOptionPane} and calls
     * {@link JButton#doClick()} on it.
     * </ol>
     * All of this is necessary since the buttons of a {@link JOptionPane} cannot be
     * accessed and customized directly. More information can be found on
     * <a href="http://forums.sun.com/thread.jspa?threadID=5335863">this page</a>.
     * <p>
     * In order to use this hack, simply wrap your message. For example<br>
     * <code>JOptionPane.showConfirmDialog(window,message,title,JOptionPane.YES_NO_CANCEL_OPTION);</code>
     * <br>
     * becomes<br>
     * <code>JOptionPane.showConfirmDialog(window,GeneralUtils.JOptionPaneKeyBinder.messageLabel(message),title,JOptionPane.YES_NO_CANCEL_OPTION);</code>
     * 
     * @author Lenko Grigorov
     */
    public static class JOptionPaneKeyBinder {
        /**
         * ID for Yes button.
         */
        protected static final String YES = "yes";

        /**
         * ID for No button.
         */
        protected static final String NO = "no";

        /**
         * ID for OK button.
         */
        protected static final String OK = "ok";

        /**
         * ID for Cancel button.
         */
        protected static final String CANCEL = "cancel";

        /**
         * Wraps a message in a {@link JComponent} with simplified key bindings for the
         * {@link JOptionPane} buttons. Use the component instead of the string message
         * when calling option pane methods.
         * <p>
         * For example<br>
         * <code>JOptionPane.showConfirmDialog(window,message,title,JOptionPane.YES_NO_CANCEL_OPTION);</code>
         * <br>
         * becomes<br>
         * <code>JOptionPane.showConfirmDialog(window,GeneralUtils.JOptionPaneKeyBinder.messageLabel(message),title,JOptionPane.YES_NO_CANCEL_OPTION);</code>
         * 
         * @param message text message
         * @return a {@link JComponent} with simplified key bindings for the
         *         {@link JOptionPane} buttons
         */
        public static JComponent messageLabel(String message) {
            JTextArea label = new JTextArea(message);
            label.setFont(UIManager.getFont("OptionPane.font"));
            label.setEditable(false);
            label.setLineWrap(false);
            label.setOpaque(false);
            InputMap inputMap = label.getInputMap(JLabel.WHEN_IN_FOCUSED_WINDOW);
            KeyStroke stroke = KeyStroke.getKeyStroke(getMnemonicKey(YES));
            if (stroke != null) {
                inputMap.put(stroke, YES);
            }
            stroke = KeyStroke.getKeyStroke(getMnemonicKey(NO));
            if (stroke != null) {
                inputMap.put(stroke, NO);
            }
            stroke = KeyStroke.getKeyStroke(getMnemonicKey(OK));
            if (stroke != null) {
                inputMap.put(stroke, OK);
            }
            stroke = KeyStroke.getKeyStroke(getMnemonicKey(CANCEL));
            if (stroke != null) {
                inputMap.put(stroke, CANCEL);
            }
            ActionMap actionMap = label.getActionMap();
            actionMap.put(YES,
                    new ShortcutHandler(UIManager.getDefaults().getString("OptionPane." + YES + "ButtonText")));
            actionMap.put(NO,
                    new ShortcutHandler(UIManager.getDefaults().getString("OptionPane." + NO + "ButtonText")));
            actionMap.put(OK,
                    new ShortcutHandler(UIManager.getDefaults().getString("OptionPane." + OK + "ButtonText")));
            actionMap.put(CANCEL,
                    new ShortcutHandler(UIManager.getDefaults().getString("OptionPane." + CANCEL + "ButtonText")));
            return label;
        }

        /**
         * Retrieve the simple key from the menmonic shortcut for the given
         * {@link JOptionPane} button. Only one of {@link JOptionPaneKeyBinder#YES},
         * {@link JOptionPaneKeyBinder#NO}, {@link JOptionPaneKeyBinder#OK} and
         * {@link JOptionPaneKeyBinder#CANCEL} is a valid input. If the simple key
         * cannot be retrieved (e.g., there is no mnemonic shortcut associated with the
         * button), returns <code>null</code>.
         * 
         * @param button one of {@link JOptionPaneKeyBinder#YES},
         *               {@link JOptionPaneKeyBinder#NO},
         *               {@link JOptionPaneKeyBinder#OK} and
         *               {@link JOptionPaneKeyBinder#CANCEL}
         * @return the simple key from the menmonic shortcut for the given
         *         {@link JOptionPane} button, or <code>null</code> if there is no such
         *         key
         */
        protected static String getMnemonicKey(String button) {
            int code;
            try {
                code = Integer.parseInt(UIManager.getDefaults().getString("OptionPane." + button + "ButtonMnemonic"));
                if (code == 0) {
                    return null;
                }
            } catch (NumberFormatException e) {
                return null;
            }
            return KeyEvent.getKeyText(code);
        }

        /**
         * Handler for the activation of the simplified custom shortcuts.
         * 
         * @author Lenko Grigorov
         */
        protected static class ShortcutHandler extends AbstractAction {
            private static final long serialVersionUID = -1159358315366596356L;

            /**
             * The text of the button which has to be activated.
             */
            protected String buttonText;

            /**
             * Create a handler which will activate the button with the given text.
             * 
             * @param buttonText the text of the button which needs to be activated
             */
            public ShortcutHandler(String buttonText) {
                this.buttonText = buttonText;
            }

            /**
             * Looks up the button with the specified text and calls its the
             * {@link JButton#doClick()} method. The button is looked up in the top-level
             * ancestor of the source (component) of this action.
             */
            public void actionPerformed(ActionEvent arg0) {
                clickButton(((JComponent) arg0.getSource()).getTopLevelAncestor(), buttonText);
            }
        }

        /**
         * Calls the {@link JButton#doClick()} method on the button with the given text
         * in the given container. If the button cannot be found in the container, does
         * nothing.
         * 
         * @param container  container with the button
         * @param buttonText text of the button
         */
        protected static void clickButton(Container container, String buttonText) {
            JButton button = findButton(container, buttonText);
            if (button != null) {
                button.doClick();
            }
        }

        /**
         * Looks up recursively the button with the given text.
         * 
         * @param container  the container which contains the button
         * @param buttonText the text of the button
         * @return the button with the text or <code>null</code> if the button cannot be
         *         found
         */
        private static JButton findButton(Container container, String buttonText) {
            for (Component component : container.getComponents()) {
                if (component instanceof JButton) {
                    if (buttonText.equals(((JButton) component).getText())) {
                        return (JButton) component;
                    }
                } else if (component instanceof Container) {
                    JButton button = findButton((Container) component, buttonText);
                    if (button != null) {
                        return button;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Attempts to open a browser in the host OS and load the provided URL.
     * <p>
     * contains code written by Dem Pilafian.
     * 
     * @param url the URL to be loaded in the browser
     * @return <code>true</code> if no exceptions were encountered in launching the
     *         browser and loading the provided URL; <code>false</code> otherwise
     */
    public static boolean launchBrowser(String url) {
        String[] browsers = { "google-chrome", "firefox", "opera", "konqueror", "epiphany", "seamonkey", "galeon",
                "kazehakase", "mozilla" };
        try { // attempt to use Desktop library from JDK 1.6+ (even if on 1.5)
            Class<?> d = Class.forName("java.awt.Desktop");
            d.getDeclaredMethod("browse", new Class[] { java.net.URI.class })
                    .invoke(d.getDeclaredMethod("getDesktop").invoke(null), new Object[] { java.net.URI.create(url) });
            // above code mimics:
            // java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (Exception ignore) { // library not available or failed
            String osName = System.getProperty("os.name");
            try {
                if (osName.startsWith("Mac OS")) {
                    Class.forName("com.apple.eio.FileManager")
                            .getDeclaredMethod("openURL", new Class[] { String.class })
                            .invoke(null, new Object[] { url });
                } else if (osName.startsWith("Windows"))
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                else { // assume Unix or Linux
                    boolean found = false;
                    for (String browser : browsers)
                        if (!found) {
                            found = Runtime.getRuntime().exec(new String[] { "which", browser }).waitFor() == 0;
                            if (found)
                                Runtime.getRuntime().exec(new String[] { browser, url });
                        }
                    if (!found)
                        throw new Exception(Arrays.toString(browsers));
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
