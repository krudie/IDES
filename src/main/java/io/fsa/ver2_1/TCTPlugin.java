/**
 * 
 */
package io.fsa.ver2_1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
import io.IOUtilities;

/**
 * @author christiansilvano, Lenko Grigorov
 */
public class TCTPlugin implements ImportExportPlugin {
    private static final String DEFAULT_EVENTMAP_FILE = "tct-event-map.txt";
    private static final String TCT_FILE_MARKER = "Z8^0L;1";
    // The TCT format uses 10 bits to encode events
    private static final int MAX_EVENT_ID = 0x3ff;
    // The TCT format uses 22 bits to encode destination states of transitions
    private static final int MAX_STATE_ID = 0x3fffff;
    private static final int STATE_ID_BITS = 22;

    private static enum ImportEventMapOptions {
        EXISTING_MAP(Hub.string("tctImportUseDefaultMap") + " " + DEFAULT_EVENTMAP_FILE),
        SELECTED_MAP(Hub.string("tctImportUseCustomMap"));
        private final String label;

        private ImportEventMapOptions(String label) {
            this.label = label;
        }

        public String toString() {
            return label;
        }
    }

    private static enum ExportEventMapOptions {
        DEFAULT_MAP(Hub.string("tctExportUseDefaultMap") + " " + DEFAULT_EVENTMAP_FILE),
        SELECTED_MAP(Hub.string("tctExportUseCustomMap"));
        private final String label;

        private ExportEventMapOptions(String label) {
            this.label = label;
        }

        public String toString() {
            return label;
        }
    }

    public String getFileExtension() {
        return "des";
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
     * Export to a TCT file
     * 
     * @param src - the source file
     * @param dst - the destination
     */
    public void exportFile(File src, File dst) throws FormatTranslationException {
        File eventMapFile = new File(dst.getParentFile(), DEFAULT_EVENTMAP_FILE);
        List<ExportEventMapOptions> eventMappingOptions = new ArrayList<>();
        eventMappingOptions.add(ExportEventMapOptions.DEFAULT_MAP);
        eventMappingOptions.add(ExportEventMapOptions.SELECTED_MAP);
        Object selectedEventMapping = JOptionPane.showInputDialog(Hub.getMainWindow(), Hub.string("tctExportMapAsk"),
                Hub.string("tctImpexMappingTitle"), JOptionPane.QUESTION_MESSAGE, null, eventMappingOptions.toArray(),
                eventMappingOptions.get(0));
        if (selectedEventMapping == null) {
            eventMapFile = null;
        } else if (selectedEventMapping == ExportEventMapOptions.SELECTED_MAP) {
            JFileChooser fc = new JFileChooser(dst.getParentFile());
            int option = fc.showOpenDialog(Hub.getMainWindow());
            if (option != JFileChooser.APPROVE_OPTION) {
                eventMapFile = null;
            } else {
                eventMapFile = fc.getSelectedFile();
            }
        }

        Properties reverseEventMap = new Properties();
        if (eventMapFile != null && eventMapFile.isFile()) {
            try (FileInputStream in = new FileInputStream(eventMapFile)) {
                reverseEventMap.load(in);
            } catch (IOException e) {
                throw new FormatTranslationException(e);
            }
        }
        Set<Integer> unavailableControllableIds = new HashSet<>();
        Set<Integer> unavailableUncontrollableIds = new HashSet<>();
        Map<String, Integer> eventMap = new HashMap<>();
        for (Entry<Object, Object> entry : reverseEventMap.entrySet()) {
            try {
                Integer id = Integer.valueOf((String) entry.getKey());
                if (id < 0 || id > MAX_EVENT_ID) {
                    throw new NumberFormatException();
                }
                eventMap.put((String) entry.getValue(), id);
                if (id % 2 == 0) {
                    unavailableUncontrollableIds.add(id);
                } else {
                    unavailableControllableIds.add(id);
                }
            } catch (NumberFormatException | NullPointerException e) {
                // skip invalid entry
            }
        }
        Set<String> eventsNotInMap = new TreeSet<>();

        FSAModel a = null;
        try {
            a = (FSAModel) IOCoordinator.getInstance().load(src);
        } catch (IOException e) {
            throw new FormatTranslationException(e);
        }

        // obtain statistics needed to compute block size, validate model,
        // and prepare data structures for output
        boolean hasInitialState = false;
        Map<Long, Integer> stateMap = new HashMap<>();
        Set<Long> markedStates = new HashSet<>();
        int countStatesWithOutgoingTransitions = 0;
        int maxUsedControllableEventId = 1;
        int maxUsedUncontrollableEventId = 0;
        int latestNewControllableEventId = -1;
        int latestNewUncontrollableEventId = -2;
        if (a.getStateCount() > MAX_STATE_ID) {
            throw new FormatTranslationException(Hub.string("tctErrorExportTooManyStates"));
        }
        for (Iterator<FSAState> si = a.getStateIterator(); si.hasNext();) {
            FSAState s = si.next();
            if (s.isInitial()) {
                if (hasInitialState) {
                    throw new FormatTranslationException(Hub.string("tctErrorExportMultipleInitial"));
                }
                hasInitialState = true;
                stateMap.put(s.getId(), 0);
            } else {
                // keep state id 0 for the initial state
                stateMap.put(s.getId(), stateMap.size() + (hasInitialState ? 0 : 1));
            }
            if (s.isMarked()) {
                markedStates.add(s.getId());
            }
            int outgoingTransitionsCount = s.getOutgoingTransitionsCount();
            if (outgoingTransitionsCount > 0) {
                if (outgoingTransitionsCount > Short.MAX_VALUE) {
                    throw new FormatTranslationException(Hub.string("tctErrorExportTooManyTransitions"));
                }
                countStatesWithOutgoingTransitions++;
            }
            for (Iterator<FSATransition> ti = s.getOutgoingTransitionsListIterator(); ti.hasNext();) {
                FSATransition t = ti.next();
                if (t.getEvent() == null) {
                    throw new FormatTranslationException(Hub.string("tctErrorExportNoTransitionEvent"));
                }
                Integer eventId = eventMap.get(t.getEvent().getSymbol());
                if (eventId == null) {
                    eventsNotInMap.add(t.getEvent().getSymbol());
                    if (t.getEvent().isControllable()) {
                        latestNewControllableEventId = getNextFreeEventId(unavailableControllableIds,
                                latestNewControllableEventId);
                        eventMap.put(t.getEvent().getSymbol(), latestNewControllableEventId);
                        reverseEventMap.put(String.valueOf(latestNewControllableEventId), t.getEvent().getSymbol());
                        unavailableControllableIds.add(latestNewControllableEventId);
                        maxUsedControllableEventId = Math.max(maxUsedControllableEventId, latestNewControllableEventId);
                    } else {
                        latestNewUncontrollableEventId = getNextFreeEventId(unavailableUncontrollableIds,
                                latestNewUncontrollableEventId);
                        eventMap.put(t.getEvent().getSymbol(), latestNewUncontrollableEventId);
                        reverseEventMap.put(String.valueOf(latestNewUncontrollableEventId), t.getEvent().getSymbol());
                        unavailableUncontrollableIds.add(latestNewUncontrollableEventId);
                        maxUsedUncontrollableEventId = Math.max(maxUsedUncontrollableEventId,
                                latestNewUncontrollableEventId);
                    }
                } else {
                    if (t.getEvent().isControllable()) {
                        if (eventId % 2 == 0) {
                            throw new FormatTranslationException(Hub.string("tctErrorExportExpectedControllableId")
                                    + " " + t.getEvent().getSymbol());
                        }
                        maxUsedControllableEventId = Math.max(maxUsedControllableEventId, eventId);
                    } else {
                        if (eventId % 2 != 0) {
                            throw new FormatTranslationException(Hub.string("tctErrorExportExpectedUncontrollableId")
                                    + " " + t.getEvent().getSymbol());
                        }
                        maxUsedUncontrollableEventId = Math.max(maxUsedUncontrollableEventId, eventId);
                    }
                }
            }
        }
        if (!hasInitialState) {
            throw new FormatTranslationException(Hub.string("tctErrorExportNoInitial"));
        }
        if (maxUsedControllableEventId > MAX_EVENT_ID || maxUsedUncontrollableEventId > MAX_EVENT_ID) {
            throw new FormatTranslationException(Hub.string("tctErrorExportTooManyEvents"));
        }

        long blockSize = 4 // 4 bytes state count
                + 4 // 4 bytes initial state indicator
                + 4 * markedStates.size() // 4 bytes * marked states
                + 4 // 4 bytes marked states terminator
                + 4 * countStatesWithOutgoingTransitions // 4 bytes state id * states with outgoing transitions
                + 2 * countStatesWithOutgoingTransitions // 2 bytes transition count * states with outgoing transitions
                + 4 * a.getTransitionCount() // 4 bytes * transitions
                + 4 // 4 bytes states with outgoing transitions terminator
                + 4; // 4 bytes vocal states terminator
        // Total block size cannot exceed 0x7fffffff bytes
        if (blockSize < 0 || blockSize > Integer.MAX_VALUE) {
            throw new FormatTranslationException(Hub.string("tctErrorExportTooLarge"));
        }

        try (FileOutputStream fout = new FileOutputStream(dst);
                BufferedOutputStream out = new BufferedOutputStream(fout)) {
            // write header
            out.write(("CTCT DES file written by IDES v" + Hub.string("IDES_VER")).getBytes(StandardCharsets.US_ASCII));
            out.write(26);
            out.write(TCT_FILE_MARKER.getBytes(StandardCharsets.US_ASCII));
            IOUtilities.writeIntLE(out, 0xff00aa55);

            // block type and size
            IOUtilities.writeIntLE(out, 0);
            IOUtilities.writeIntLE(out, (int) blockSize);
            // state count
            IOUtilities.writeIntLE(out, (int) a.getStateCount());
            // initial state indicator (0 for DES)
            IOUtilities.writeIntLE(out, 0);
            // marked states
            for (long id : markedStates) {
                IOUtilities.writeIntLE(out, stateMap.get(id));
            }
            IOUtilities.writeIntLE(out, -1);
            // transitions
            for (Iterator<FSAState> si = a.getStateIterator(); si.hasNext();) {
                FSAState s = si.next();
                int countOutgoingTransitions = s.getOutgoingTransitionsCount();
                if (countOutgoingTransitions <= 0) {
                    continue;
                }
                IOUtilities.writeIntLE(out, stateMap.get(s.getId()));
                IOUtilities.writeShortLE(out, (short) countOutgoingTransitions);
                // event ids have to be written out in ascending order
                Map<Integer, Integer> transitions = new TreeMap<>();
                for (Iterator<FSATransition> ti = s.getOutgoingTransitionsListIterator(); ti.hasNext();) {
                    FSATransition t = ti.next();
                    transitions.put(eventMap.get(t.getEvent().getSymbol()), stateMap.get(t.getTarget().getId()));
                }
                if (transitions.size() < countOutgoingTransitions) {
                    throw new FormatTranslationException(Hub.string("tctErrorExportMultipleTransitionsSameEvent"));
                }
                for (Entry<Integer, Integer> entry : transitions.entrySet()) {
                    int transitionData = entry.getKey() << STATE_ID_BITS;
                    transitionData |= entry.getValue() & MAX_STATE_ID;
                    IOUtilities.writeIntLE(out, transitionData);
                }
            }
            IOUtilities.writeIntLE(out, -1);
            // vocal states
            IOUtilities.writeIntLE(out, -1);

            // write termination block
            IOUtilities.writeIntLE(out, -1);
            IOUtilities.writeIntLE(out, 8);
            IOUtilities.writeIntLE(out, 0);
            IOUtilities.writeIntLE(out, 0);
        } catch (IOException e) {
            throw new FormatTranslationException(e);
        }

        if (eventMapFile != null) {
            try (FileOutputStream out = new FileOutputStream(eventMapFile)) {
                reverseEventMap.store(out, null);
            } catch (IOException e) {
                throw new FormatTranslationException(e);
            }
            if (eventsNotInMap.size() > 0) {
                Hub.getNoticeManager().postInfoTemporary(
                        String.valueOf(eventsNotInMap.size()) + " " + Hub.string("tctInfoExportMapShort"),
                        Hub.string("tctInfoExportMapLong") + " " + String.join(", ", eventsNotInMap));
            }
        }
    }

    /**
     * Import from a TCT file
     * 
     * @param src the source file
     * @param dst the destination file
     */
    public void importFile(File src, File dst) throws FormatTranslationException {
        File eventMapFile = new File(src.getParentFile(), DEFAULT_EVENTMAP_FILE);
        List<ImportEventMapOptions> eventMappingOptions = new ArrayList<>();
        if (eventMapFile.isFile()) {
            eventMappingOptions.add(ImportEventMapOptions.EXISTING_MAP);
        }
        eventMappingOptions.add(ImportEventMapOptions.SELECTED_MAP);
        Object selectedEventMapping = JOptionPane.showInputDialog(Hub.getMainWindow(), Hub.string("tctImportMapAsk"),
                Hub.string("tctImpexMappingTitle"), JOptionPane.QUESTION_MESSAGE, null, eventMappingOptions.toArray(),
                eventMappingOptions.get(0));
        if (selectedEventMapping == null) {
            eventMapFile = null;
        } else if (selectedEventMapping == ImportEventMapOptions.SELECTED_MAP) {
            JFileChooser fc = new JFileChooser(src.getParentFile());
            int option = fc.showOpenDialog(Hub.getMainWindow());
            if (option != JFileChooser.APPROVE_OPTION) {
                eventMapFile = null;
            } else {
                eventMapFile = fc.getSelectedFile();
            }
        }

        Properties eventMap = new Properties();
        if (eventMapFile != null) {
            try (FileInputStream in = new FileInputStream(eventMapFile)) {
                eventMap.load(in);
            } catch (IOException e) {
                throw new FormatTranslationException(e);
            }
        }
        Set<String> eventsNotInMap = new TreeSet<>();

        FSAModel a = ModelManager.instance().createModel(FSAModel.class, src.getName());

        try (FileInputStream fin = new FileInputStream(src); BufferedInputStream in = new BufferedInputStream(fin)) {
            // read header
            IOUtilities.readSkipUntil(in, b -> b == 26);
            byte[] authString = new byte[7];
            IOUtilities.readInto(in, authString);
            if (!TCT_FILE_MARKER.equals(new String(authString))) {
                throw new FormatTranslationException(Hub.string("tctErrorImportNotTCT"));
            }
            if (IOUtilities.readIntLE(in) != 0xff00aa55) {
                throw new FormatTranslationException(Hub.string("tctErrorImportEndian"));
            }

            // find block of type 0 which contains FSA data
            int blockType = IOUtilities.readIntLE(in);
            while (blockType != 0) {
                IOUtilities.readSkip(in, IOUtilities.readIntLE(in));
                blockType = IOUtilities.readIntLE(in);
            }
            // ignore block size due to some buggy TCT versions that write incorrect value
            IOUtilities.readIntLE(in);

            // create states
            int countStates = IOUtilities.readIntLE(in);
            for (int i = 0; i < countStates; ++i) {
                FSAState s = a.assembleState();
                s.setId(i);
                s.setName(String.valueOf(i));
                a.add(s);
            }
            if (countStates > 0) {
                a.getState(0).setInitial(true);
            }

            // ignore DAT indicator, will attempt to load file anyways
            IOUtilities.readIntLE(in);

            // set marked states
            int nextMarkedStateId = IOUtilities.readIntLE(in);
            while (nextMarkedStateId != -1) {
                a.getState(nextMarkedStateId).setMarked(true);
                nextMarkedStateId = IOUtilities.readIntLE(in);
            }

            int nextTransitionSrc = IOUtilities.readIntLE(in);
            while (nextTransitionSrc != -1) {
                if (nextTransitionSrc < 0 || nextTransitionSrc >= countStates) {
                    throw new FormatTranslationException(Hub.string("tctErrorImportInvalidState"));
                }
                // count is stored in 2 bytes (contrary to documentation which says 4 bytes)
                int countTransitions = IOUtilities.readShortLE(in);
                while (countTransitions > 0) {
                    int transitionData = IOUtilities.readIntLE(in);
                    int nextTransitionDst = transitionData & MAX_STATE_ID;
                    int eventId = transitionData >>> STATE_ID_BITS;
                    if (nextTransitionDst < 0 || nextTransitionDst >= countStates) {
                        throw new FormatTranslationException(Hub.string("tctErrorImportInvalidState"));
                    }
                    if (a.getEvent(eventId) == null) {
                        String event = eventMap.getProperty(String.valueOf(eventId));
                        if (event == null) {
                            event = String.valueOf(eventId);
                            eventsNotInMap.add(event);
                        }
                        SupervisoryEvent e = a.assembleEvent(event);
                        e.setId(eventId);
                        e.setControllable(eventId % 2 != 0);
                        e.setObservable(true);
                        a.add(e);
                    }
                    FSATransition t = a.assembleTransition(nextTransitionSrc, nextTransitionDst, eventId);
                    a.add(t);
                    countTransitions--;
                }
                nextTransitionSrc = IOUtilities.readIntLE(in);
            }

            // ignore vocal states because there is no corresponding construct in IDES
            // ignore other blocks in the file because we don't support them
        } catch (IOException e) {
            if (e instanceof FormatTranslationException) {
                throw (FormatTranslationException) e;
            } else {
                throw new FormatTranslationException(e);
            }
        }

        try {
            IOCoordinator.getInstance().save(a, dst);
        } catch (IOException e) {
            throw new FormatTranslationException(e);
        }

        if (eventMapFile != null && eventsNotInMap.size() > 0) {
            Hub.getNoticeManager().postWarningTemporary(
                    String.valueOf(eventsNotInMap.size()) + " " + Hub.string("tctWarnImportMapShort"),
                    Hub.string("tctWarnImportMapLong") + " " + String.join(", ", eventsNotInMap));
        }
    }

    /**
     * Return a human readable description of the plugin
     */
    public String getFileDescription() {
        return "TCT";
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
        return "TCT import and export";
    }

    public String getVersion() {
        return Hub.string("IDES_VER");
    }

    private static int getNextFreeEventId(Set<Integer> ids, int startingAt) {
        // need to preserve odd/even so increase by 2
        int nextId = startingAt + 2;
        while (nextId > startingAt && ids.contains(nextId)) {
            nextId += 2;
        }
        if (nextId <= startingAt) {
            throw new RuntimeException(Hub.string("tctErrorExportNoMoreEventIds"));
        }
        return nextId;
    }
}
