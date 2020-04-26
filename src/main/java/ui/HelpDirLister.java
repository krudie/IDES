package ui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.TreeMap;

import main.Main;

public class HelpDirLister extends TreeMap<String, String> {
    private static final long serialVersionUID = -7744108891039086287L;

    private HelpDirLister() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Instance for the non-static methods.
     */
    private static HelpDirLister me = null;

    public static HelpDirLister instance() {
        if (me == null) {
            me = new HelpDirLister();
        }
        return me;
    }

    public static void init() {
        File helpDir = null;
        try {
            helpDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (helpDir.isFile()) {
                helpDir = helpDir.getParentFile();
            }
            helpDir = new File(helpDir.getAbsolutePath() + File.separator + "help");
            if (!helpDir.exists()) {
                helpDir = null;
            }
        } catch (URISyntaxException e) {
            helpDir = null;
        }
        File localHelpDir = new File((String) System.getProperties().get("user.dir") + File.separator + "help");
        if (!localHelpDir.exists()) {
            localHelpDir = null;
        }
        if (helpDir != null && helpDir.equals(localHelpDir)) {
            helpDir = null;
        }
        if (helpDir != null) {
            try {
                for (File f : helpDir.listFiles()) {
                    if (f.isDirectory()) {
                        File index = new File(f.getAbsolutePath() + File.separator + "index.html");
                        if (index.exists()) {
                            instance().put(f.getName(), index.toURI().toURL().toString());
                        }
                    }
                }
            } catch (IOException e) {
            }
        }
        // local help dirs supersede remote dirs in case of name conflict
        if (localHelpDir != null) {
            try {
                for (File f : localHelpDir.listFiles()) {
                    if (f.isDirectory()) {
                        File index = new File(f.getAbsolutePath() + File.separator + "index.html");
                        if (index.exists()) {
                            instance().put(f.getName(), index.toURI().toURL().toString());
                        }
                    }
                }
            } catch (IOException e) {
            }
        }
    }
}
