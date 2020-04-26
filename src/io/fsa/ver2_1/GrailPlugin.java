/**
 * 
 */
package io.fsa.ver2_1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.io.FormatTranslationException;
import ides.api.plugin.io.IOPluginManager;
import ides.api.plugin.io.ImportExportPlugin;
import ides.api.plugin.model.ModelManager;
import io.IOCoordinator;

/**
 * @author christiansilvano
 */
public class GrailPlugin implements ImportExportPlugin {

    private String description = "Grail+";

    private String ext = "fm";

    public String getFileExtension() {
        return ext;
    }

    /**
     * Registers itself to the IOPluginManager
     */
    public void initialize() {
        IOPluginManager.instance().registerExport(this, FSAModel.class);
        IOPluginManager.instance().registerImport(this);
    }

    /**
     * Unregisters itself from the IOPluginManager
     */
    public void unload() {
    }

    /**
     * Exports a file to a different format
     * 
     * @param src - the source file
     * @param dst - the destination
     */
    public void exportFile(File src, File dst) throws FormatTranslationException {
        // Loading the model from the file:
        FSAModel a = null;
        try {
            a = (FSAModel) IOCoordinator.getInstance().load(src);
        } catch (IOException e) {
            throw new FormatTranslationException(e);
        }

        // Container for the grail model:
        String fileContents = "";

        // Translating the model to the grail format:
        for (Iterator<ides.api.model.fsa.FSAState> i = a.getStateIterator(); i.hasNext();) {
            ides.api.model.fsa.FSAState s = i.next();
            if (s.isInitial()) {
                fileContents += "(START) |- " + s.getId() + "\n";
            }
            if (s.isMarked()) {
                fileContents += "" + s.getId() + " -| (FINAL)\n";
            }
            for (Iterator<ides.api.model.fsa.FSATransition> j = s.getOutgoingTransitionsListIterator(); j.hasNext();) {
                ides.api.model.fsa.FSATransition t = j.next();
                fileContents += "" + s.getId() + " " + (t.getEvent() == null ? "NULL" : t.getEvent().getSymbol()) + " "
                        + t.getTarget().getId() + "\n";
            }
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(dst);
            writer.write(fileContents);
            writer.close();
        } catch (IOException e) {
            throw new FormatTranslationException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (java.io.IOException e) {
            }
        }

    }

    /**
     * Import a file from a different format to the IDES file system
     * 
     * @param src the source file
     * @param dst the destination file
     */
    public void importFile(File src, File dst) throws FormatTranslationException {
        java.io.BufferedReader in = null;
        try {
            in = new java.io.BufferedReader(new java.io.FileReader(src));
            FSAModel a = ModelManager.instance().createModel(FSAModel.class, src.getName());
            // long tCount = 0;
            // long eCount = 0;
            java.util.Hashtable<String, Long> events = new java.util.Hashtable<String, Long>();
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].startsWith("(")) {
                    long sId = Long.parseLong(parts[2]);
                    FSAState s = a.getState(sId);
                    if (s == null) {
                        s = a.assembleState();
                        s.setId(sId);// new model.fsa.ver2_1.State(sId);
                        s.setName("" + sId);
                        a.add(s);
                    }
                    s.setInitial(true);
                } else if (parts[2].startsWith("(")) {
                    long sId = Long.parseLong(parts[0]);
                    FSAState s = a.getState(sId);
                    if (s == null) {
                        s = a.assembleState();
                        s.setId(sId);// new model.fsa.ver2_1.State(sId);
                        s.setName("" + sId);
                        a.add(s);
                    }
                    s.setMarked(true);
                } else {
                    long sId1 = Long.parseLong(parts[0]);
                    FSAState s1 = a.getState(sId1);
                    if (s1 == null) {
                        s1 = a.assembleState();
                        s1.setId(sId1);// new model.fsa.ver2_1.State(sId1);
                        s1.setName("" + sId1);
                        a.add(s1);
                    }
                    long sId2 = Long.parseLong(parts[2]);
                    FSAState s2 = a.getState(sId2);
                    if (s2 == null) {
                        s2 = a.assembleState();
                        s2.setId(sId2);// new model.fsa.ver2_1.State(sId2);
                        s2.setName("" + sId2);
                        a.add(s2);
                    }
                    SupervisoryEvent e = null;
                    Long eId = events.get(parts[1]);
                    if (eId == null) {
                        e = a.assembleEvent("");// new
                                                // model.supeventset.ver3.Event
                                                // (eCount);
                        e.setSymbol(parts[1]);
                        e.setObservable(true);
                        e.setControllable(true);
                        // eCount++;
                        a.add(e);
                        events.put(parts[1], new Long(e.getId()));
                    } else {
                        e = a.getEvent(eId.longValue());
                    }
                    FSATransition t = a.assembleTransition(s1.getId(), s2.getId(), e.getId());// new
                                                                                              // model.fsa.ver2_1.Transition
                                                                                              // (
                    // tCount,
                    // s1,
                    // s2,
                    // e);
                    a.add(t);
                    // tCount++;
                }
            }
            // // Create an automatic layout given the imported method
            // new GraphView(a);
            // Save the model to the selected destination
            IOCoordinator.getInstance().save(a, dst);
        } catch (IOException e) {
            throw new FormatTranslationException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (java.io.IOException e) {
            }
        }
    }

    /**
     * Return a human readable description of the plugin
     */
    public String getFileDescription() {
        return description;
    }

    public String getCredits() {
        return Hub.string("DEVELOPERS");
    }

    public String getDescription() {
        return "part of IDES";
    }

    public String getLicense() {
        return "same as IDES";
    }

    public String getName() {
        return "Grail+ import and export";
    }

    public String getVersion() {
        return Hub.string("IDES_VER");
    }
}
