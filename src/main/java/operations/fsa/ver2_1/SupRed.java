package operations.fsa.ver2_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.ModelManager;

public class SupRed extends AbstractOperation {

    public SupRed() {
        NAME = "supred-grail";
        DESCRIPTION = "Computes a reduced supervisor (experimental). "
                + " Requires that \"fsasupred.exe\" from Grail be" + " present in the folder where IDES is installed.";

        // WARNING - Ensure that input type and description always match!
        inputType = new Class[] { FSAModel.class, FSAModel.class };
        inputDesc = new String[] { "Plant", "Specification" };

        // WARNING - Ensure that output type and description always match!
        outputType = new Class[] { FSAModel.class };
        outputDesc = new String[] { "Reduced supervisor" };
    }

    /*
     * (non-Javadoc)
     * 
     * @see pluggable.operation.Operation#perform(java.lang.Object[])
     */
    @Override
    public Object[] perform(Object[] inputs) {
        exportGrail((FSAModel) inputs[0], new File("PLT"));
        exportGrail((FSAModel) inputs[1], new File("SUP"));
        Set<SupervisoryEvent> unctrl = new TreeSet<SupervisoryEvent>();
        for (Iterator<SupervisoryEvent> i = ((FSAModel) inputs[1]).getEventIterator(); i.hasNext();) {
            SupervisoryEvent e = i.next();
            if (!e.isControllable()) {
                unctrl.add(e);
            }
        }
        try {
            Process p = Runtime.getRuntime().exec("fmsupred PLT SUP");
            InputStream stdin = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            BufferedWriter out = new BufferedWriter(new FileWriter("RED"));
            while ((line = br.readLine()) != null) {
                out.write(line + "\n");
            }
            out.close();
            p.waitFor();
        } catch (Exception e) {
            Hub.displayAlert(Hub.string("problemSupRed"));
        }
        FSAModel a = importGrail(new File("RED"));
        for (Iterator<SupervisoryEvent> i = a.getEventIterator(); i.hasNext();) {
            SupervisoryEvent e = i.next();
            if (unctrl.contains(e)) {
                e.setControllable(false);
            } else {
                e.setControllable(true);
            }
        }
        return new Object[] { a };
    }

    public static void exportGrail(FSAModel a, File file) {
        String fileContents = "";
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

        FileWriter latexWriter = null;

        if (fileContents == null) {
            return;
        }

        try {
            latexWriter = new FileWriter(file);
            latexWriter.write(fileContents);
            latexWriter.close();
        } catch (IOException fileException) {
            Hub.displayAlert(Hub.string("problemSupRed"));
        }
    }

    public static FSAModel importGrail(File file) {
        FSAModel a = null;
        java.io.BufferedReader in = null;
        try {
            in = new java.io.BufferedReader(new java.io.FileReader(file));
            a = ModelManager.instance().createModel(FSAModel.class, file.getName());
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
                        a.add(s);
                    }
                    s.setInitial(true);
                } else if (parts[2].startsWith("(")) {
                    long sId = Long.parseLong(parts[0]);
                    FSAState s = a.getState(sId);
                    if (s == null) {
                        s = a.assembleState();
                        s.setId(sId);// new model.fsa.ver2_1.State(sId);
                        a.add(s);
                    }
                    s.setMarked(true);
                } else {
                    long sId1 = Long.parseLong(parts[0]);
                    FSAState s1 = a.getState(sId1);
                    if (s1 == null) {
                        s1 = a.assembleState();
                        s1.setId(sId1);// new model.fsa.ver2_1.State(sId1);
                        a.add(s1);
                    }
                    long sId2 = Long.parseLong(parts[2]);
                    FSAState s2 = a.getState(sId2);
                    if (s2 == null) {
                        s2 = a.assembleState();
                        s2.setId(sId2);// new model.fsa.ver2_1.State(sId2);
                        a.add(s2);
                    }
                    SupervisoryEvent e = null;
                    Long eId = events.get(parts[1]);
                    if (eId == null) {
                        e = a.assembleEvent(parts[1]);// new
                                                      // model.supeventset.ver3.Event(eCount);
                        // e.setSymbol(parts[1]);
                        e.setObservable(true);
                        e.setControllable(true);
                        // eCount++;
                        a.add(e);
                        events.put(parts[1], new Long(e.getId()));
                    } else {
                        e = a.getEvent(eId.longValue());
                    }
                    FSATransition t = a.assembleTransition(s1.getId(), s2.getId(), e.getId());// new
                                                                                              // model.fsa.ver2_1.Transition(
                    // tCount,
                    // s1,
                    // s2,
                    // e);
                    a.add(t);
                    // tCount++;
                }
            }
        } catch (java.io.IOException e) {
            Hub.displayAlert(Hub.string("cantParseImport") + file);
        } catch (RuntimeException e) {
            Hub.displayAlert(Hub.string("cantParseImport") + file);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (java.io.IOException e) {
            }
        }
        return a;
    }
}
