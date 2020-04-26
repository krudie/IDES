package io.supeventset.ver3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ides.api.core.Hub;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.model.supeventset.SupervisoryEventSet;
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

/**
 * @author Valerie Sugarman
 * @author christiansilvano
 */
public class SupEventSetFileIOPlugin implements FileIOPlugin {

    protected final static String MODEL_TYPE = "SupEventSet";

    public String getIOTypeDescriptor() {
        return MODEL_TYPE;
    }

    public Set<String> getMetaTags() {
        Set<String> set = new HashSet<String>();
        return set;
    }

    public String getSaveDataVersion() {
        return "3";
    }

    public String getSaveMetaVersion(String tag) {
        return "";
    }

    public void initialize() {
        IOPluginManager.instance().registerDataLoader(this, MODEL_TYPE);
        IOPluginManager.instance().registerDataSaver(this, SupervisoryEventSet.class);
        // this does nothing but IOManager complains if missing.
        IOPluginManager.instance().registerMetaSaver(this, SupervisoryEventSet.class);
    }

    public void saveData(PrintStream stream, DESModel model, String fileName) throws FileSaveException {
        try {
            Iterator<SupervisoryEvent> ei = ((SupervisoryEventSet) model).iteratorSupervisory();
            while (ei.hasNext()) {
                XMLExporter.eventToXML(ei.next(), stream, XMLExporter.INDENT);
            }

        } catch (Exception e) {
            throw new FileSaveException(e);
        }

    }

    public DESModel loadData(String version, InputStream stream, String fileName) throws FileLoadException {
        if (!"3".equals(version)) {
            throw new UnsupportedVersionException(Hub.string("errorUnsupportedVersion"));
        }
        byte[] FILE_HEADER = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.getProperty("line.separator")
                + "<data>" + System.getProperty("line.separator")).getBytes();
        HeadTailInputStream dataField = new HeadTailInputStream(stream, FILE_HEADER, "</data>".getBytes());

        SupEventSetParser parser = new SupEventSetParser();
        SupervisoryEventSet model = parser.parseData(dataField);

        if (model == null || !"".equals(parser.getParsingErrors())) {
            throw new FileLoadException(parser.getParsingErrors(), model);
        }
        return model;

    }

    public void loadMeta(String version, InputStream stream, DESModel model, String tag) throws FileLoadException {

    }

    public void saveMeta(PrintStream stream, DESModel model, String tag) throws FileSaveException {

    }

    private static class XMLExporter {
        private static final String INDENT = "\t";

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
    }

    public class SupEventSetParser extends AbstractParser

    {
        boolean settingName = false;

        String tmpName = "";

        // Sometimes a model element needs to be stored so it can receive
        // different informations at different
        // times during the parse.
        private SupervisoryEventSet model;

        private SupervisoryEvent tmpEvent;

        // Constants representing names of xml tags and subtags
        protected final String OBSERVABLE = "observable", CONTROLLABLE = "controllable", NAME = "name", ID = "id";

        // Auxiliary attributes: "Actions" to be developed by the parser
        // Tells some parseDataElements and parseMetaElements whether they are
        // parsing main tags or subtags
        protected final int MAINTAG = 2, SUBTAG = 3, SUBSUBTAG = 4, SUBSUBSUBTAG = 5; // ,,,

        boolean parsingData = false;

        boolean parsingMeta = false;

        // Chain to store the strings meaning all the xml tags being processed.
        // The order is from the most important xml tag, to the less importants
        // (MAINTAG->SUBTAG->SUBSUBTAG...)
        public Vector<String> tags = new Vector<String>();

        /**
         * creates a SupEventSetParser.
         */
        public SupEventSetParser() {
            super();
        }

        public SupervisoryEventSet parseData(InputStream stream) {
            parsingData = true;
            parsingErrors = "";
            model = ModelManager.instance().createModel(SupervisoryEventSet.class);
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
            } catch (Exception e) {
                parsingErrors += e.getMessage() + "\n";
            }

            if (parsingData) {
                parseDataElements(qName, atts);
            }
        }

        /**
         * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
         *      java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals(NAME) && settingName) {
                // need to remove then add again since HashSet implementation
                // based on name
                model.remove(tmpEvent);
                tmpEvent.setSymbol(tmpName);
                model.add(tmpEvent);

                // reset name parsing
                settingName = false;
                tmpName = "";
            }

            if (!qName.equals(tags.get(tags.size() - 1))) {
                parsingErrors += Hub.string("xmlParsingBadFormat") + "\n";
            } else {
                tags.remove(tags.remove(tags.size() - 1));
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
         * Parses XML information and builds a SupervisoryEventSet based on the content
         * given by the IOCoordinator
         * 
         * @param qName name of the current XML tag being processed.
         * @param atts  the attributes of the XML tag, ex:
         *              <tag at1="value1" at2="value2" />, where at1 and at2 are the
         *              attributes
         */
        public void parseDataElements(String qName, Attributes atts) {
            switch (tags.size()) {
            case MAINTAG:

                tmpEvent = (SupervisoryEvent) getModelElement(atts);
                model.add(tmpEvent);

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

                if (qName.equals(OBSERVABLE)) {
                    tmpEvent.setObservable(true);
                } else if (qName.equals(CONTROLLABLE)) {
                    tmpEvent.setControllable(true);
                } else {
                    parsingErrors += Hub.string("xmlParsingUnrecogized") + "\n";
                }

                break;
            }

        }

        /**
         * Create an Event based on the given atts. Since it returns an Object, the
         * function is expandable to return any model element which may be created in
         * the future.
         * 
         * @param atts the attributes for the event
         * @return the newly created event
         */
        public Object getModelElement(Attributes atts) {
            long id = Long.parseLong(atts.getValue(ID));
            SupervisoryEvent e = model.assembleEvent("");
            e.setId(id);
            e.setObservable(false);
            return e;

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

}
