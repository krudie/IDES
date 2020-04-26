package main;

import java.io.IOException;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.UIManager;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.layout.FSALayoutManager;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.presentation.ToolsetManager;
import io.fsa.ver2_1.GraphExporter;
import model.fsa.ver2_1.Automaton;
import pluggable.layout.tree.TreeLayouter;
import presentation.fsa.FSAToolset;
import services.cache.CacheBackend;
import services.ccp.CopyPasteBackend;
import services.latex.LatexBackend;
import services.notice.NoticeBackend;
import services.notice.NoticePopup;
import services.undo.UndoBackend;
import ui.HelpDirLister;
import ui.MainWindow;

/**
 * @author Lenko Grigorov
 */
public class Main {

    private Main() {
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // setup random stuff
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // set up global exception handler
        // TODO uncomment these lines before shipping. Default exception handler
        // disabled for debugging. -- CLM
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        // AWT/Swing Exception handling (changes expected in future Java
        // releases)
        System.setProperty("sun.awt.exception.handler", GlobalExceptionHandler.class.getName());

        // load resource with strings used in the program
        try {
            Hub.addResouceBundle(ResourceBundle.getBundle("strings"));
        } catch (MissingResourceException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Cannot load the file with the text messages used in the program.\n"
                            + "The file \"strings.properties\" has to be available at startup.");
            System.exit(2);
        }

        // load settings
        try {
            HubBackend.loadPersistentData();
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, Hub.string("cantLoadSettings"));
            System.exit(3);
        }

        try {
            if (UIManager.getSystemLookAndFeelClassName() == "com.sun.java.swing.plaf.gtk.GTKLookAndFeel") {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.metal.MetalLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
        }
        // DEBUG: remove eventually
        // for(Object o:UIManager.getLookAndFeelDefaults().keySet())
        // System.out.println(o.toString());

        // Register FSA
        ModelManager.instance().registerModel(Automaton.myDescriptor);
        ToolsetManager.instance().registerToolset(FSAModel.class, new FSAToolset());
        // FSA layout
        FSALayoutManager.instance().registerLayouter(new TreeLayouter());

        // setup stuff that doesn't need the main window
        CacheBackend.init();
        UndoBackend.init();
        NoticeBackend.init();
        HelpDirLister.init();

        // setup main window
        HubBackend.setUserInterface(new MainWindow());

        // setup stuff that needs the main window
        LatexBackend.init();

        FSAModel fsa = ModelManager.instance().createModel(FSAModel.class, Hub.string("newModelName"));
        Hub.getWorkspace().addModel(fsa);
        Hub.getWorkspace().setActiveModel(fsa.getName());
        Hub.registerOptionsPane(new GraphExporter.ExportOptionsPane());

        // go live!
        Hub.getMainWindow().setVisible(true);

        // setup stuff that needs the main window visible
        NoticePopup.init();
        CopyPasteBackend.init();

        // last initialize plugins
        PluginManager.init();
    }

    /**
     * Handles stuff that has to be done before the application terminates.
     */
    public static void onExit() {
        // This cannot be handled by the io.fsa.ver2_1.handleUnsavedWorkspace
        if (Hub.getWorkspace().isDirty()) {
            if (!io.CommonFileActions.handleUnsavedWorkspace()) {
                return;
            }
        }
        Vector<DESModel> models = new Vector<DESModel>();
        for (Iterator<DESModel> i = Hub.getWorkspace().getModels(); i.hasNext();) {

            DESModel m = i.next();
            if (m.needsSave() && m.getParentModel() == null) {
                models.add(m);
            }
        }

        if (!models.isEmpty()) {
            if (!io.CommonFileActions.handleUnsavedModels(models)) {
                return;
            }
        }

        Hub.getWorkspace().setActiveModel(null);

        // cleanup
        PluginManager.cleanup();
        NoticePopup.cleanup();
        NoticeBackend.cleanup();
        CacheBackend.close();
        Hub.getMainWindow().dispose();
        Hub.storePersistentData();
    }

}
