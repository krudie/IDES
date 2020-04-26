/**
 * 
 */
package io.fsa.ver2_1;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.io.FileIOPlugin;
import ides.api.plugin.io.FileLoadException;
import ides.api.plugin.io.FileSaveException;
import ides.api.plugin.io.IOPluginManager;
import ides.api.plugin.io.UnsupportedVersionException;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.ModelManager;
import ides.api.utilities.HeadTailInputStream;
import io.AbstractParser;
import io.IOUtilities;
import presentation.CubicParamCurve2D;
import presentation.fsa.BezierLayout;
import presentation.fsa.CircleNodeLayout;
import presentation.fsa.GraphLayout;
import util.AnnotationKeys;

/**
 * @author christiansilvano
 */
public class FSAFileIOPlugin implements FileIOPlugin {

    protected final static String MODEL_TYPE = "FSA";

    protected final static String META_TAG = "layout";

    public Set<String> getMetaTags() {
        Set<String> tags = new HashSet<String>();
        tags.add(META_TAG);
        return tags;
    }

    public String getIOTypeDescriptor() {
        return MODEL_TYPE;
    }

    /**
     * Subscribes itself to the IOIE_PluginManager informing whether this object is
     * a "metaSaver", "dataSaver", "metaLoader" or "dataLoader".
     */
    public void initialize() {
        IOPluginManager.instance().registerDataLoader(this, MODEL_TYPE);
        IOPluginManager.instance().registerDataSaver(this, FSAModel.class);
        IOPluginManager.instance().registerMetaSaver(this, FSAModel.class);
        IOPluginManager.instance().registerMetaLoader(this, MODEL_TYPE, META_TAG);
    }

    /**
     * Saves its data in <code>file</code> according to a <code>model</code>.
     * 
     * @param stream   the stream to save the data in.
     * @param model    the model to be saved in the file.
     * @param fileName path to the file, so auxiliar files can be created.
     */
    public void saveData(PrintStream stream, DESModel model, String fileName) throws FileSaveException {
        try {
            ListIterator<FSAState> si = ((FSAModel) model).getStateIterator();
            while (si.hasNext()) {
                XMLExporter.stateToXML(si.next(), stream, XMLExporter.INDENT);
            }

            ListIterator<SupervisoryEvent> ei = ((FSAModel) model).getEventIterator();
            while (ei.hasNext()) {
                XMLExporter.eventToXML(ei.next(), stream, XMLExporter.INDENT);
            }

            ListIterator<FSATransition> ti = ((FSAModel) model).getTransitionIterator();
            while (ti.hasNext()) {
                XMLExporter.transitionToXML(ti.next(), stream, XMLExporter.INDENT);
            }
        } catch (Exception e) {
            throw new FileSaveException(e);
        }
    }

    /**
     * Save metaData to the file, according to model.
     * 
     * @param stream the stream where to save the metadata
     * @param model  the model with the meta data
     * @param tag    the tag for the metadata
     */
    public void saveMeta(PrintStream stream, DESModel model, String tag) throws FileSaveException {
        // stream will be an OutputStream.
        // it will need to be converted to PrintStream(UTF-8).
        if (!tag.equals(META_TAG)) {
            throw new FileSaveException(Hub.string("ioUnsupportedMetaTag") + " [" + tag + "]");
        }
        ListIterator<FSAState> si = ((FSAModel) model).getStateIterator();
        ListIterator<FSATransition> ti = ((FSAModel) model).getTransitionIterator();

        GraphLayout layout = (GraphLayout) model.getAnnotation(AnnotationKeys.LAYOUT);
        if (layout != null) {
            stream.println("\n\t<font size=\"" + layout.getFontSize() + "\"/>");
            stream.println("\t<layout uniformnodes=\"" + layout.getUseUniformRadius() + "\"/>");
        }

        si = ((FSAModel) model).getStateIterator();
        while (si.hasNext()) {
            XMLExporter.stateLayoutToXML(si.next(), stream, XMLExporter.INDENT);
        }

        // Save the transitions
        ti = ((FSAModel) model).getTransitionIterator();
        // //Create a hashmap to store the transition layouts, this
        // information is used for the "groups" creation.
        // HashMap<Integer,BezierLayout> bezierCurves = new
        // HashMap<Integer,BezierLayout>();
        while (ti.hasNext()) {
            XMLExporter.transitionLayoutToXML(ti.next(), stream, XMLExporter.INDENT);
        }
    }

    /**
     * Loads data from the file.
     * 
     * @param version the version of the file format
     * @param f       the input
     * @param fileDir the directory with the input file
     * @return the loaded model
     */
    public DESModel loadData(String version, InputStream f, String fileDir) throws FileLoadException {
        if (!"2.1".equals(version)) {
            throw new UnsupportedVersionException(Hub.string("errorUnsupportedVersion"));
        }
        byte[] FILE_HEADER = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.getProperty("line.separator")
                + "<data>" + System.getProperty("line.separator")).getBytes();
        HeadTailInputStream dataField = new HeadTailInputStream(f, FILE_HEADER, "</data>".getBytes());
        FSAModel model = null;
        // Parse body:
        AutomatonParser parser = new AutomatonParser();
        // parser.printStream(dataField);
        model = ModelManager.instance().createModel(FSAModel.class);
        model = parser.parseData(dataField);

        if (model == null || !"".equals(parser.getParsingErrors())) {
            throw new FileLoadException(parser.getParsingErrors(), model);
        }
        return model;
    }

    /**
     * Loads metadata from the file
     * 
     * @param version the version of the file format
     * @param stream  the input
     * @param model   the model where the data should be stored
     * @param tag     the tag of the meta data
     */
    public void loadMeta(String version, InputStream stream, DESModel model, String tag) throws FileLoadException {
        if (!tag.equals(META_TAG)) {
            throw new FileLoadException(Hub.string("ioUnsupportedMetaTag") + " [" + tag + "]");
        }
        if (!"2.1".equals(version)) {
            throw new UnsupportedVersionException(Hub.string("errorUnsupportedVersion"));
        }
        byte[] FILE_HEADER = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.getProperty("line.separator")
                + "<meta>" + System.getProperty("line.separator")).getBytes();
        HeadTailInputStream metaField = new HeadTailInputStream(stream, FILE_HEADER, "</meta>".getBytes());

        AutomatonParser parser = new AutomatonParser();
        parser.parseMeta(metaField, model);

        if (!"".equals(parser.getParsingErrors())) {
            throw new FileLoadException(parser.getParsingErrors());
        }
    }

    /**
     * Unsubscribe itself from the IOIE_PluginManager
     */
    public void unload() {

    }

    private static class XMLExporter {
        private static final String INDENT = "\t";

        /**
         * prints a state in xml
         * 
         * @param s      the state to convert
         * @param ps     the printstream to print to
         * @param indent the indentation to be used in the file
         */
        private static void stateToXML(FSAState s, PrintStream ps, String indent) {
            ps.println(indent + "<state" + " id=\"" + s.getId() + "\">");

            if (!(s.isInitial() | s.isMarked())) {
                ps.println(indent + indent + "<properties/>");
            } else {
                ps.println(indent + indent + "<properties>");
                if (s.isInitial()) {
                    ps.println(indent + indent + indent + "<initial />");
                }
                if (s.isMarked()) {
                    ps.println(indent + indent + indent + "<marked />");
                }
                ps.println(indent + indent + "</properties>");
            }

            if (s.getName() != null) {
                ps.println(indent + indent + "<name>" + IOUtilities.encodeForXML(s.getName()) + "</name>");
            } else {
                ps.println(indent + indent + "<name />");
            }

            ps.println(indent + "</state>");
        }

        /**
         * prints an event in xml
         * 
         * @param e      the event to convert
         * @param ps     the printstream to print to
         * @param indent the indentation to be used in the file
         */
        private static void eventToXML(SupervisoryEvent e, PrintStream ps, String indent) {
            if (e.getSymbol() == "" & !(e.isObservable() | e.isControllable())) {
                ps.println(indent + "<event" + " id=\"" + e.getId() + "\" />");
            } else {
                ps.println(indent + "<event" + " id=\"" + e.getId() + "\">");
                if (!(e.isControllable() | e.isObservable())) {
                    ps.println(indent + indent + "<properties />");
                } else {
                    ps.println(indent + indent + "<properties>");
                    if (e.isControllable()) {
                        ps.println(indent + indent + indent + "<controllable />");
                    }
                    if (e.isObservable()) {
                        ps.println(indent + indent + indent + "<observable />");
                    }
                    ps.println(indent + indent + "</properties>");
                }
                if (e.getSymbol() != null) {
                    ps.println(indent + indent + "<name>" + IOUtilities.encodeForXML(e.getSymbol()) + "</name>");
                } else {
                    ps.println(indent + indent + "<name />");
                }
                ps.println(indent + "</event>");
            }
        }

        /**
         * prints a transition in xml
         * 
         * @param t      the transition to convert
         * @param ps     the printstream to print to
         * @param indent the indentation to be used in the file
         */
        private static void transitionToXML(FSATransition t, PrintStream ps, String indent) {
            ps.println(indent + "<transition" + " id=\"" + t.getId() + "\"" + " source=\"" + t.getSource().getId()
                    + "\"" + " target=\"" + t.getTarget().getId() + "\""

                    + ((t.getEvent() != null) ? " event=\"" + t.getEvent().getId() + "\"" : "") + ">");
            ps.println(indent + "</transition>");
        }

        /**
         * prints a state in xml
         * 
         * @param s      the state to convert
         * @param ps     the printstream to print to
         * @param indent the indentation to be used in the file
         */
        private static void stateLayoutToXML(FSAState s, PrintStream ps, String indent) {
            CircleNodeLayout c = (CircleNodeLayout) s.getAnnotation(AnnotationKeys.LAYOUT);
            if (c != null) {
                ps.println(indent + "<state" + " id=\"" + s.getId() + "\">");
                ps.println(indent + indent + "<circle r=\"" + String.valueOf(c.getRadius()) + "\" x=\""
                        + String.valueOf(c.getLocation().x) + "\" y=\"" + String.valueOf(c.getLocation().y) + "\" />");
                ps.println(indent + indent + "<arrow x=\"" + String.valueOf(c.getArrow().x) + "\" y=\""
                        + String.valueOf(c.getArrow().y) + "\" />");
                ps.println(indent + "</state>");
            }
        }

        /**
         * prints a transition in xml
         * 
         * @param t      the transition to convert
         * @param ps     the printstream to print to
         * @param indent the indentation to be used in the file
         */
        private static void transitionLayoutToXML(FSATransition t, PrintStream ps, String indent) {
            BezierLayout l = (BezierLayout) t.getAnnotation(AnnotationKeys.LAYOUT);
            if (l != null) {
                CubicParamCurve2D curve = l.getCurve();
                ps.println(indent + "<transition" + " id=\"" + t.getId() + "\""
                        + (l.getGroup() != BezierLayout.UNGROUPPED ? " group=\"" + l.getGroup() + "\"" : "") + ">");
                ps.println(indent + indent + "<bezier x1=\"" + curve.getX1() + "\" y1=\"" + curve.getY1() + "\" x2=\""
                        + curve.getX2() + "\" y2=\"" + curve.getY2() + "\" ctrlx1=\"" + curve.getCtrlX1()
                        + "\" ctrly1=\"" + curve.getCtrlY1() + "\" ctrlx2=\"" + curve.getCtrlX2() + "\" ctrly2=\""
                        + curve.getCtrlY2() + "\" />");
                ps.println(indent + indent + "<label x=\"" + l.getLabelOffset().x + "\" y=\"" + l.getLabelOffset().y
                        + "\" />");
                ps.println(indent + "</transition>");
            }
        }

    }

    public class AutomatonParser extends AbstractParser {
        boolean settingName = false;

        String tmpName = "";

        // Sometimes a model element needs to be stored so it can receive
        // different informations at different
        // times during the parse.
        private FSAModel model;

        private FSAState tmpState;

        private FSATransition tmpTransition;

        private SupervisoryEvent tmpEvent;

        private GraphLayout gl = new GraphLayout();

        HashMap<Long, BezierLayout> bezierCurves = new HashMap<Long, BezierLayout>();

        // Constants representing names of xml tags and subtags
        protected final String INITIAL = "initial", MARKED = "marked", OBSERVABLE = "observable",
                CONTROLLABLE = "controllable", NAME = "name", ID = "id", CIRCLE = "circle", RADIUS = "r", COORD_X = "x",
                COORD_Y = "y", BEZIER = "bezier", X1 = "x1", Y1 = "y1", X2 = "x2", Y2 = "y2", CTRLX1 = "ctrlx1",
                CTRLY1 = "ctrly1", CTRLX2 = "ctrlx2", CTRLY2 = "ctrly2", SOURCE = "source", TARGET = "target",
                STATE = "state", EVENT = "event", TRANSITION = "transition", FONT = "font", LABEL = "label",
                ARROW = "arrow", GROUP_ID = "group", LAYOUT = "layout", UNIFORM_NODES = "uniformnodes";

        // Auxiliar attributes: "Actions" to be developed by the parser
        // Tells some parseDataElements and parseMetaElements whether they are
        // parsing main tags or subtags
        protected final int MAINTAG = 2, SUBTAG = 3, SUBSUBTAG = 4, SUBSUBSUBTAG = 5; // ,,,

        private String CURRENT_PARSING_ELEMENT = "";

        boolean parsingData = false;

        boolean parsingMeta = false;

        // Chain to store the strings meaning all the xml tags being processed.
        // The order is from the most important xml tag, to the less importants
        // (MAINTAG->SUBTAG->SUBSUBTAG...)
        public Vector<String> tags = new Vector<String>();

        /**
         * creates an automatonParser.
         */
        public AutomatonParser() {
            super();
        }

        public FSAModel parseData(InputStream stream) {
            parsingData = true;
            parsingErrors = "";
            model = ModelManager.instance().createModel(FSAModel.class);
            try {
                xmlReader.parse(new InputSource(stream));
            } catch (FileNotFoundException fnfe) {
                parsingErrors += fnfe.getMessage() + "\n";
            } catch (IOException ioe) {
                parsingErrors += ioe.getMessage() + "\n";
            } catch (SAXException saxe) {
                parsingErrors += saxe.getMessage() + "\n";
            } catch (NullPointerException npe) {
                parsingErrors += npe.getMessage() + "\n";
            }
            return model;
        }

        /**
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
         *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) {

            try {
                tags.add(qName);
                if (tags.size() > 1) {
                    CURRENT_PARSING_ELEMENT = tags.get(1);
                }
            } catch (Exception e) {
                parsingErrors += e.getMessage() + "\n";
            }

            if (parsingData) {
                parseDataElements(qName, atts);
            }

            if (parsingMeta) {
                parseMetaElements(qName, atts);
            }
        }

        /**
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
         *      java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals(NAME) && settingName) {
                if (CURRENT_PARSING_ELEMENT == STATE) {
                    tmpState.setName(tmpName);
                }
                if (CURRENT_PARSING_ELEMENT == EVENT) {
                    tmpEvent.setSymbol(tmpName);
                }
                // reset name parsing
                settingName = false;
                tmpName = "";
            }

            if (!qName.equals(tags.get(tags.size() - 1))) {
                parsingErrors += Hub.string("xmlParsingBadFormat") + "\n";
            } else {
                tags.remove(tags.remove(tags.size() - 1));
                CURRENT_PARSING_ELEMENT = "";
            }
        }

        /**
         * @see org.xml.sax.ContentHandler#startDocument()
         */
        @Override
        public void startDocument() {
        }

        /**
         * @see org.xml.sax.ContentHandler#endDocument()
         */
        @Override
        public void endDocument() {

            if (tags.size() > 0) {
                parsingErrors += Hub.string("xmlParsingBadFormat") + "\n";
            }

            parsingData = false;
            parsingMeta = false;
        }

        /**
         * Debug function. Prints the content of the stream.
         * 
         * @param stream the stream
         */
        public void printInputStream(InputStream stream) {
            String body = "";
            try {
                BufferedReader head = new BufferedReader(new InputStreamReader(stream));
                String line = head.readLine();
                // Process the file (Add States, Events and Transitions to the
                // model):
                while (line != null) {
                    body += line;
                    body += System.getProperty("line.separator");
                    line = head.readLine();
                }
                head.close();
            } catch (IOException e) {
                Hub.displayAlert("Error: " + e.getMessage());
            }
        }

        /**
         * Parses XML information and builds a FSAModels based on the content given by
         * the IOCoordinator
         * 
         * @param qName name of the current XML tag being processed.
         * @param atts  the attributes of the XML tag, ex:
         *              <tag at1="value1" at2="value2" />, where at1 and at2 are the
         *              attributes
         */
        public void parseDataElements(String qName, Attributes atts) {
            switch (tags.size()) {
            case MAINTAG:
                if (CURRENT_PARSING_ELEMENT == STATE) {
                    tmpState = (FSAState) getModelElement(atts, CURRENT_PARSING_ELEMENT);
                    model.add(tmpState);
                }

                else if (CURRENT_PARSING_ELEMENT == TRANSITION) {
                    tmpTransition = (FSATransition) getModelElement(atts, CURRENT_PARSING_ELEMENT);
                    model.add(tmpTransition);
                }

                else if (CURRENT_PARSING_ELEMENT == EVENT) {
                    tmpEvent = (SupervisoryEvent) getModelElement(atts, CURRENT_PARSING_ELEMENT);
                    model.add(tmpEvent);
                }

                else {
                    parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";
                }
                break;
            // ////////////////////////////////////////////
            case SUBTAG:
                if (qName.equals(NAME)) {
                    settingName = true;
                    tmpName = "";
                } else {
                    settingName = false;
                }

                break;
            // ////////////////////////////////////////////
            case SUBSUBTAG:
                if (CURRENT_PARSING_ELEMENT == STATE) {
                    if (qName.equals(INITIAL)) {
                        tmpState.setInitial(true);
                    }

                    else if (qName.equals(MARKED)) {
                        tmpState.setMarked(true);
                    } else {
                        parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";
                    }
                } else if (CURRENT_PARSING_ELEMENT == EVENT) {
                    if (qName.equals(OBSERVABLE)) {
                        tmpEvent.setObservable(true);
                    } else if (qName.equals(CONTROLLABLE)) {
                        tmpEvent.setControllable(true);
                    } else {
                        parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";
                    }
                } else {
                    parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";
                }
                break;
            }

        }

        public void parseMetaElements(String qName, Attributes atts) {
            // THIS STRUCTURE CAN BE USED TO PARSE A XML TAG IN ANY SUB-LEVEL
            // The sublevel is identified by the class constants: MAINTAG,
            // SUBTAG, SUBSUBTAG, etc,
            // which are integers with value meaning the sublevel of the tag.

            switch (tags.size()) {
            case MAINTAG:// MAINTAG
                if (CURRENT_PARSING_ELEMENT == STATE) {
                    long id = Long.parseLong(atts.getValue(ID));
                    FSAState s = null;
                    // Select the state referred by <code>id</code>
                    Iterator<FSAState> sIt = model.getStateIterator();
                    while (sIt.hasNext()) {
                        FSAState tmpS = sIt.next();
                        if (tmpS.getId() == id) {
                            s = tmpS;
                            break;
                        }
                    }
                    CircleNodeLayout tmpCircleNodeLayout = new CircleNodeLayout();
                    s.setAnnotation(AnnotationKeys.LAYOUT, tmpCircleNodeLayout);
                    tmpState = s;
                }

                else if (CURRENT_PARSING_ELEMENT == TRANSITION) {
                    long id = Long.parseLong(atts.getValue(ID));
                    long groupId = -1;
                    try {
                        groupId = Long.parseLong(atts.getValue(GROUP_ID));
                    } catch (Exception e) {
                        // The transition is ungrouped.
                        // It will have its own layout.
                    }

                    FSATransition transition = null;
                    // Select the transition referred by id
                    Iterator<FSATransition> tIt = model.getTransitionIterator();
                    while (tIt.hasNext()) {
                        FSATransition t = tIt.next();
                        if (t.getId() == id) {
                            transition = t;
                            break;
                        }
                    }

                    BezierLayout l = null;
                    if (groupId == -1)// The transition has no group
                    {
                        // If the transition has no group, create a layout to
                        // represent it:
                        l = new BezierLayout();
                    } else {
                        // The transition have a group.
                        // Use the groupLayout stored in "bezierCurves"
                        // Create a layout it if it still does not exist.
                        l = bezierCurves.get(groupId);
                        if (l == null) {
                            // Create the layout:
                            l = new BezierLayout();
                            bezierCurves.put(groupId, l);
                        }
                    }
                    l.setGroup(groupId);
                    transition.setAnnotation(AnnotationKeys.LAYOUT, l);
                    tmpTransition = transition;
                }

                else if (CURRENT_PARSING_ELEMENT == FONT) {
                    gl.setFontSize(Float.parseFloat(atts.getValue("size")));
                    model.setAnnotation(AnnotationKeys.LAYOUT, gl);
                }

                else if (CURRENT_PARSING_ELEMENT == LAYOUT) {
                    String uniformNodes = atts.getValue(UNIFORM_NODES);
                    if (uniformNodes == null) {
                        uniformNodes = "false";
                    }
                    gl.setUseUniformRadius(Boolean.parseBoolean(uniformNodes));
                    model.setAnnotation(AnnotationKeys.LAYOUT, gl);
                }

                else {
                    parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";
                }

                break;
            case SUBTAG: // FIRST LEVEL AFTER THE MAIN TAG
                if (CURRENT_PARSING_ELEMENT == STATE) {
                    if (qName.equals(CIRCLE)) {
                        CircleNodeLayout layout = (CircleNodeLayout) tmpState.getAnnotation(AnnotationKeys.LAYOUT);
                        layout.setRadius(Float.parseFloat(atts.getValue(RADIUS)));
                        layout.setLocation(Float.parseFloat(atts.getValue(COORD_X)),
                                Float.parseFloat(atts.getValue(COORD_Y)));
                        layout.setText(tmpState.getName());
                    }

                    else if (qName.equals(ARROW)) {
                        CircleNodeLayout layout = (CircleNodeLayout) tmpState.getAnnotation(AnnotationKeys.LAYOUT);
                        layout.setArrow(new Point2D.Float(Float.parseFloat(atts.getValue(COORD_X)),
                                Float.parseFloat(atts.getValue(COORD_Y))));
                    }

                    else {
                        parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";
                    }
                }

                else if (CURRENT_PARSING_ELEMENT == TRANSITION) {
                    // Setting the layout for the edge:
                    if (qName.equals(BEZIER)) {
                        float x1 = Float.parseFloat(atts.getValue(X1));
                        float x2 = Float.parseFloat(atts.getValue(X2));
                        float y1 = Float.parseFloat(atts.getValue(Y1));
                        float y2 = Float.parseFloat(atts.getValue(Y2));
                        float ctrlx1 = Float.parseFloat(atts.getValue(CTRLX1));
                        float ctrly1 = Float.parseFloat(atts.getValue(CTRLY1));
                        float ctrlx2;
                        float ctrly2;
                        try {
                            ctrlx2 = Float.parseFloat(atts.getValue(CTRLX2));
                            ctrly2 = Float.parseFloat(atts.getValue(CTRLY2));
                        } catch (Exception e) {
                            ctrlx2 = ctrlx1;
                            ctrly2 = ctrly1;
                        }
                        // Get the annotation BezierLayout, and set the curve
                        // paramethers
                        BezierLayout l = (BezierLayout) tmpTransition.getAnnotation(AnnotationKeys.LAYOUT);
                        l.setCurve(new CubicCurve2D.Float(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2));
                    }

                    // Setting the label for the edge:
                    else if (qName.equals(LABEL)) {
                        SupervisoryEvent e = tmpTransition.getEvent();
                        if (e != null) {
                            BezierLayout layout = (BezierLayout) tmpTransition.getAnnotation(AnnotationKeys.LAYOUT);
                            if (e.getSymbol() != "") {
                                // System.out.println("Adding " + e.getSymbol()
                                // + " to " + layout);
                                // Add eventName to the edgeLayout:
                                Point2D.Float offset = new Point2D.Float();
                                offset.setLocation(Float.parseFloat(atts.getValue(COORD_X)),
                                        Float.parseFloat(atts.getValue(COORD_Y)));
                                layout.setLabelOffset(offset);
                                layout.addEventName(e.getSymbol());
                                // System.out.println(layout.getEventNames());
                            }
                        }
                    }

                    else {
                        parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";
                    }
                }

                else {
                    parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";
                }

                break;
            }
        }

        /**
         * Sets annotations for all the model elements in <code>model</code> based on
         * the informations contained in <code>stream</code>
         * 
         * @param stream
         * @param model
         */
        public void parseMeta(InputStream stream, DESModel model) {
            parsingMeta = true;
            this.model = (FSAModel) model;
            try {
                xmlReader.parse(new InputSource(stream));
            } catch (FileNotFoundException fnfe) {
                parsingErrors += fnfe.getMessage() + "\n";
            } catch (IOException ioe) {
                parsingErrors += ioe.getMessage() + "\n";
            } catch (SAXException saxe) {
                parsingErrors += saxe.getMessage() + "\n";
            } catch (NullPointerException npe) {
                parsingErrors += npe.getMessage() + "\n";
            }
        }

        /**
         * Create either a State, Event or Transition based on the given atts. Since it
         * returns an Object, the function is expandable to return any model element
         * which may be created in the future.
         * 
         * @param atts
         * @param parsingElement
         * @return the FSA element
         */
        public Object getModelElement(Attributes atts, String parsingElement) {
            if (parsingElement == STATE) {
                long id = Long.parseLong(atts.getValue(ID));
                FSAState s = model.assembleState();// new State(id);
                s.setId(id);
                return s;
            }

            if (parsingElement == EVENT) {
                long id = Long.parseLong(atts.getValue(ID));
                SupervisoryEvent e = model.assembleEvent("");// new Event(id);
                e.setId(id);
                e.setObservable(false);
                return e;
            }

            if (parsingElement == TRANSITION) {
                long id = Long.parseLong(atts.getValue(ID));
                long sourceN = Long.parseLong(atts.getValue(SOURCE));
                long targetN = Long.parseLong(atts.getValue(TARGET));
                long eventN;
                try {
                    eventN = Long.parseLong(atts.getValue(EVENT));
                } catch (NumberFormatException e) {
                    eventN = -1;
                }

                // Iterator<FSAState> sIt = model.getStateIterator();
                FSAState src = null, dst = null;
                src = model.getState(sourceN);
                dst = model.getState(targetN);
                // while(sIt.hasNext())
                // {
                // FSAState s = sIt.next();
                // if(s.getId() == sourceN)
                // {
                // src = s;
                // }
                //
                // if(s.getId() == targetN)
                // {
                // dst = s;
                // }
                // }
                // Iterator<SupervisoryEvent> eIt = model.getEventIterator();
                SupervisoryEvent event = null;
                event = model.getEvent(eventN);
                // while(eIt.hasNext())
                // {
                // SupervisoryEvent e = eIt.next();
                // if(e.getId() == eventN)
                // {
                // event = e;
                // }
                // }
                if (event != null) {
                    FSATransition t = model.assembleTransition(src.getId(), dst.getId(), event.getId());
                    t.setId(id);
                    return t;// new Transition(id, src, dst, event);
                } else {
                    FSATransition t = model.assembleEpsilonTransition(src.getId(), dst.getId());
                    t.setId(id);
                    return t;// new Transition(id, src, dst);
                }
            }

            parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";

            return null;
        }

        @Override
        public void characters(char buf[], int offset, int len) throws SAXException {
            if (parsingData) {
                // Get the value between the tags <name> and </name>
                // The same code can be used to get values between different XML
                // tags
                if (settingName == true) {
                    StringBuffer tst = new StringBuffer();
                    tst.append(buf, offset, len);
                    tmpName += tst.toString();
                }
            }
        }
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
        return "FSA IO";
    }

    public String getVersion() {
        return Hub.string("IDES_VER");
    }

    public String getSaveDataVersion() {
        return "2.1";
    }

    public String getSaveMetaVersion(String tag) {
        if (META_TAG.equals(tag)) {
            return "2.1";
        }
        return "";
    }

}
