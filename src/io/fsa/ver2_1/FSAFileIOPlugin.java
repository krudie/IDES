/**
 * 
 */
package io.fsa.ver2_1;

import io.AbstractFileParser;
import io.IOUtilities;
import io.ParsingToolbox;
import io.HeadTailInputStream;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

import presentation.CubicParamCurve2D;
import presentation.fsa.CircleNodeLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PushbackInputStream;
import java.io.PushbackReader;
import org.xml.sax.ContentHandler;
import main.Annotable;
import main.Hub;
import model.DESModel;
import model.ModelManager;
import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver2_1.Event;
import model.fsa.ver2_1.State;
import model.fsa.ver2_1.Transition;
import pluggable.io.FileIOPlugin;
import pluggable.io.IOPluginManager;
import presentation.fsa.BezierEdge;
import presentation.fsa.BezierLayout;
import presentation.fsa.CircleNode;
import presentation.fsa.CircleNodeLayout;
import presentation.fsa.FSAGraph;
import presentation.fsa.InitialArrow;
import presentation.fsa.Node;
import presentation.fsa.ReflexiveEdge;

import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.visualization.contrib.CircleLayout;

/**
 * @author christiansilvano
 *
 */
public class FSAFileIOPlugin implements FileIOPlugin{    
	public Set<String> getMetaTags(String type)
	{
		if(type.equals("FSA"))
		{
			Set<String> returnSet = new HashSet<String>();
			returnSet.add("layout");
			return returnSet;	
		}
		return null;
	}

	public String getIOTypeDescriptor()
	{
		return "FSA";
	}

	//Singleton instance:
	private static FSAFileIOPlugin instance = null;
	private FSAFileIOPlugin()
	{
		this.initializeFileIO();
	}


	public static FSAFileIOPlugin getInstance()
	{
		if (instance == null)
		{
			instance = new FSAFileIOPlugin();
		}
		return instance;
	}

	/**
	 * Subscribes itself to the IOIE_PluginManager informing whether this object
	 * is a "metaSaver", "dataSaver", "metaLoader" or "dataLoader".
	 */
	public void initializeFileIO()
	{
		IOPluginManager.getInstance().registerDataLoader(this ,"FSA");
		IOPluginManager.getInstance().registerDataSaver(this, "FSA");
		IOPluginManager.getInstance().registerMetaSaver(this, "FSA", "layout");
		IOPluginManager.getInstance().registerMetaLoader(this, "FSA", "layout");
	}


	/**
	 * Saves its data in <code>file</code> according to a <code>model</code>.
	 * @param file the file to save the data in.
	 * @param model the model to be saved in the file.
	 * @param fileDirectory path to the file, so auxiliar files can be created.
	 */
	public boolean saveData(PrintStream stream, DESModel model, File fileDirectory)
	{
		ListIterator<FSAState> si = ((FSAModel)model).getStateIterator();
		while(si.hasNext()){
			XMLExporter.stateToXML((State)si.next(), stream, XMLExporter.INDENT);            
		}

		ListIterator<FSAEvent> ei = ((FSAModel)model).getEventIterator();
		while(ei.hasNext()){
			XMLExporter.eventToXML((Event)ei.next(),stream, XMLExporter.INDENT);
		}

		ListIterator<FSATransition> ti = ((FSAModel)model).getTransitionIterator();
		while(ti.hasNext()){
			XMLExporter.transitionToXML((Transition)ti.next(),stream, XMLExporter.INDENT);
		}
		//TODO make IOCoordinator call a "fireModelSaved", it should be a generic call for any
		//kind of DES Model.
		((FSAModel)model).fireFSASaved();
		return true;
	}

	/**
	 * Save metaData to the file, according to model.
	 * @param file
	 * @param model
	 */
	public boolean saveMeta(PrintStream stream, DESModel model, String type, String tag)
	{
		//stream will be an OutputStream.
		//it will need to be converted to PrintStream(UTF-8).
		if(type.equals("FSA") & tag.equals("layout"))
		{
			ListIterator<FSAState> si = ((FSAModel)model).getStateIterator();
			ListIterator<FSATransition> ti = ((FSAModel)model).getTransitionIterator();
			stream.println("\t<font size=\""+(((FSAModel)model).getMeta()==null?12:((FSAModel)model).getMeta().getAttribute("size"))+"\"/>");
			si = ((FSAModel)model).getStateIterator();
	
			while(si.hasNext())
			{
				XMLExporter.stateLayoutToXML((State)si.next(), stream, XMLExporter.INDENT);            
			}
	
			ti = ((FSAModel)model).getTransitionIterator();
			while(ti.hasNext())
			{
				XMLExporter.transitionLayoutToXML((Transition)ti.next(),stream, XMLExporter.INDENT);
			}
			return true;
		}
		return false;
	}

	/**
	 * Loads data from the file.
	 * @param file
	 * @param fileDir
	 * @return
	 */
	public DESModel loadData(InputStream f, File fileDir)
	{
		byte[] FILE_HEADER = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.getProperty("line.separator") + "<data>" + System.getProperty("line.separator")).getBytes();
		HeadTailInputStream dataField = new HeadTailInputStream(f,FILE_HEADER,"</data>".getBytes());
		FSAModel model = null;
		String errors="";
		//Parse body:	
		AutomatonParser parser = new AutomatonParser();
//		parser.printStream(dataField);
		model = ModelManager.createModel(FSAModel.class);
		model = parser.parseData(dataField);

		if(model == null)
		{
			//TODO THROW AN ERROR
			return null;
		}

		if(!"".equals(errors))
		{
			Hub.displayAlert(Hub.string("errorsParsingXMLFileL1")+
					"\n"+Hub.string("errorsParsingXMLFileL2"));
		}
		return model;//(DESModel)a;
	}

	/**
	 * Loads metadata from the file
	 * @param file
	 */
	public void loadMeta(InputStream stream, DESModel model)
	{
		byte[] FILE_HEADER = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.getProperty("line.separator") + "<meta>" + System.getProperty("line.separator")).getBytes();
		HeadTailInputStream metaField = new HeadTailInputStream(stream,FILE_HEADER,"</meta>".getBytes());
		
		AutomatonParser parser = new AutomatonParser();
		parser.parseMeta(metaField, model);
	}

	/**
	 * Unsubscribe itself from the IOIE_PluginManager
	 *
	 */
	public void unload()
	{

	}


	private static class XMLExporter{
		private static final String INDENT =  "\t";

		/**
		 * prints a state in xml
		 * @param s the state to convert 
		 * @param ps the printstream to print to 
		 * @param indent the indentation to be used in the file
		 */ 
		private static void stateToXML(State s, PrintStream ps,String indent){
			if(s.isEmpty()) ps.println(indent + "<state" + " id=\"" + s.getId() + "\" />");
			else{
				ps.println(indent + "<state" + " id=\"" + s.getId() + "\">");
				
				if(!(s.isInitial() | s.isMarked()))
				{
					ps.println(indent + indent + "<properties/>");
				}else{
					ps.println(indent + indent + "<properties>");
					if(s.isInitial())
					{
						ps.println(indent + indent + indent + "<initial />");
					}
					if(s.isMarked())
					{
						ps.println(indent + indent + indent + "<marked />");
					}
					ps.println(indent + indent + "</properties>");
				}
				
				if(s.getName() != null)
				{
					ps.println(indent + indent + "<name>" + s.getName() + "</name>");
				}else{
					ps.println(indent + indent + "<name />");
				}
				
				ps.println(indent + "</state>");
			}
		}
		/**
		 * prints an event in xml
		 * @param e the event to convert 
		 * @param ps the printstream to print to 
		 * @param indent the indentation to be used in the file
		 */ 
		private static void eventToXML(Event e, PrintStream ps, String indent){
			if(e.getSymbol() == "" & !(e.isObservable() | e.isControllable())){
				ps.println(indent + "<event" + " id=\"" + e.getId() + "\" />");
			}
			else{
				ps.println(indent + "<event" + " id=\"" + e.getId() + "\">");
				if(!(e.isControllable() | e.isObservable()))
				{
					ps.println(indent + indent + "<properties />");
				}else{
					ps.println(indent + indent + "<properties>");
					if(e.isControllable())
					{
						ps.println(indent + indent + indent + "<controllable />");
					}
					if(e.isObservable())
					{
						ps.println(indent + indent + indent + "<observable />");
					}
					ps.println(indent + indent + "</properties>");
				}
				if(e.getSymbol() != null)
				{
					ps.println(indent + indent + "<name>" + e.getSymbol() + "</name>");
				}else{
					ps.println(indent + indent + "<name />");
				}
				ps.println(indent + "</event>");
			}
		}
		/**
		 * prints a transition in xml
		 * @param t the transition to convert 
		 * @param ps the printstream to print to 
		 * @param indent the indentation to be used in the file
		 */ 
		private static void transitionToXML(Transition t, PrintStream ps, String indent){
				ps.println(indent + "<transition" + " id=\"" + t.getId() + "\"" + " source=\""
						+ t.getSource().getId() + "\"" + " target=\"" + t.getTarget().getId() + "\""

						+ ((t.getEvent() != null) ? " event=\"" + t.getEvent().getId() + "\"" : "") + ">");
				ps.println(indent + "</transition>");			
		}
	
		/**
		 * prints a state in xml
		 * @param s the state to convert 
		 * @param ps the printstream to print to 
		 * @param indent the indentation to be used in the file
		 */ 
		private static void stateLayoutToXML(State s, PrintStream ps,String indent){
			CircleNodeLayout c = (CircleNodeLayout)s.getAnnotation(Annotable.LAYOUT);
			if(c != null)
			{
				ps.println(indent + "<state" + " id=\"" + s.getId() + "\">");
				ps.println(indent + indent + "<circle r=\"" + String.valueOf(c.getRadius()) + "\" x=\"" + String.valueOf(c.getLocation().x) + "\" y=\"" + String.valueOf(c.getLocation().y) + "\" />");
				ps.println(indent + indent + "<arrow x=\"" + String.valueOf(c.getArrow().x) + "\" y=\"" + String.valueOf(c.getArrow().y) + "\" />");
				ps.println(indent + "</state>");
			}
		}  

		/**
		 * prints a transition in xml
		 * @param t the transition to convert 
		 * @param ps the printstream to print to 
		 * @param indent the indentation to be used in the file
		 */ 
		private static void transitionLayoutToXML(Transition t, PrintStream ps, String indent){
			BezierLayout l = (BezierLayout)t.getAnnotation(Annotable.LAYOUT);
			if(l!= null)
			{
				CubicParamCurve2D curve = l.getCurve();
				ps.println(indent + "<transition" + " id=\"" + t.getId() + "\">");
				ps.println(indent + indent + "<bezier x1=\"" + curve.getX1() +"\" y1=\"" + curve.getY1() + "\" x2=\"" + 
						curve.getX2() + "\" y2=\"" + curve.getY2() + "\" ctrlx1=\"" + curve.getCtrlX1() + "\" ctrly1=\"" + curve.getCtrlY1() + "\" ctrlx2=\"" + 
						curve.getCtrlX2() + "\" ctrly2=\"" + curve.getCtrlY2() + "\" />");
				ps.println(indent + indent + "<label x=\"" + l.getLabelOffset().x + "\" y=\"" + l.getLabelOffset().y +"\" />");
				ps.println(indent + "</transition>");
			}
		}

	}

	public class AutomatonParser extends AbstractFileParser{  
		Set<String> mainXmlDataTags = new HashSet<String>();
		Set<String> mainXmlMetaTags = new HashSet<String>();
		boolean settingName = false;
		String tmpName = "";
		private FSAModel model;
		private State tmpState;
		private Transition tmpTransition;
		private Event tmpEvent;
		protected final String STATE = "state", EVENT = "event", TRANSITION="transition", NONE="none";
		protected final String FONT = "font", BEZIER="bezier", LABEL="label";
		
		//Auxiliar attributes: "Actions" to be developed by the parser
		//Tells some parseDataElements and parseMetaElements whether they are
		//parsing main tags or subtags
		protected final String PARSE_MAIN_TAGS="maintag", PARSE_SUB_TAGS="subtag";
		private String CURRENT_PARSING_ELEMENT=NONE;
		boolean parsingState = false;
		boolean parsingData = false;
		boolean parsingMeta = false;
		
		/**
		 * creates an automatonParser.
		 */
		public AutomatonParser(){
			super();
			mainXmlDataTags.add(STATE);
			mainXmlDataTags.add(EVENT);
			mainXmlDataTags.add(TRANSITION);
			
			mainXmlMetaTags.add(STATE);
			mainXmlMetaTags.add(FONT);
			mainXmlMetaTags.add(TRANSITION);
			
		}
		//TODO Eliminate this function from the Interface
		public FSAModel parse(File file)
		{
			return null;
		}
		public FSAModel parseData(InputStream stream){
			parsingData = true;
			parsingErrors = "";
			model = ModelManager.createModel(FSAModel.class);
			try{
				xmlReader.parse(new InputSource(stream));
			}
			catch(FileNotFoundException fnfe){

			}
			catch(IOException ioe){

			}
			catch(SAXException saxe){

			}
			catch(NullPointerException npe){

			}
			return model;
		}
		/**
		 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String qName, Attributes atts){
			if(parsingData)
			{
				if(mainXmlDataTags.contains(qName))
				{
					parseDataElements(qName,atts, PARSE_MAIN_TAGS);
				}else{
					parseDataElements(qName, null, PARSE_SUB_TAGS);
				}
			}
			
			if(parsingMeta)
			{
				if(mainXmlMetaTags.contains(qName))
				{
					parseMetaElements(qName,atts,PARSE_MAIN_TAGS);
				}else{
					parseMetaElements(qName,atts,PARSE_SUB_TAGS);
				}
			}
		}
		/**
		 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		public void endElement(String uri, String localName, String qName){	

		} 
		/**
		 * @see org.xml.sax.ContentHandler#startDocument()
		 */
		public void startDocument(){}
		/**
		 * @see org.xml.sax.ContentHandler#endDocument()
		 */
		public void endDocument(){
			parsingData = false;
			parsingMeta = false;
		}
		/**
		 * Debug function. Prints the content of the stream.
		 * @param dataSection
		 */
		public void printStream(InputStream stream)
		{
			String body = "";
			try
			{
				BufferedReader head = new BufferedReader(new InputStreamReader(stream));        	
				String line = head.readLine();	        	
				//Process the file (Add States, Events and Transitions to the model):
				while(line != null)
				{
					body += line;
					body += System.getProperty("line.separator"); 
					line = head.readLine();
				}
				head.close();
			}catch(IOException e)
			{
				System.out.println("Erro: " + e.getMessage());
			}
			System.out.println("Stream content:\n=======\n" + body);
		}
		/**
		 * Parses XML information and builds a FSAModels based on the content given by the IOCoordinator
		 * @param qName name of the current XML tag being processed.
		 * @param atts the attributes of the XML tag, ex: <tag at1="value1" at2="value2" />, where at1 and at2 are the attributes 
		 * @param action, tells witch parsing action needs to be performed. Currently supports: "start" and "end"
		 */
		public void parseDataElements(String qName, Attributes atts, String action)
		{
			if(action.equals(PARSE_MAIN_TAGS))//Parsing one of the main data tags
			{
				if(qName.equals(STATE))
				{
					CURRENT_PARSING_ELEMENT = STATE;
					tmpState = (State)getModelElement(atts, CURRENT_PARSING_ELEMENT);
					model.add(tmpState);
				}

				if(qName.equals(TRANSITION))
				{
					CURRENT_PARSING_ELEMENT = TRANSITION;
					tmpTransition = (Transition)getModelElement(atts, CURRENT_PARSING_ELEMENT);
					model.add(tmpTransition);
				}

				if(qName.equals(EVENT))
				{
					CURRENT_PARSING_ELEMENT = EVENT;
					tmpEvent = (Event)getModelElement(atts, CURRENT_PARSING_ELEMENT);
					//Obs:
						//Note that the event is not added here.
						//That happens because an event needs to have a name to identify it.
					//The name is generated by the function characters(), which adds the name to the event and
					//the event to the model.
				}
			}
			if(action.equals(PARSE_SUB_TAGS))//Parsing a sub tag of one of the main data tags.
			{	
				if(CURRENT_PARSING_ELEMENT == STATE)
				{
					if(qName.equals("initial"))
					{
						tmpState.setInitial(true);
					}

					if(qName.equals("marked"))
					{
						tmpState.setMarked(true);
					}
				}

				if(CURRENT_PARSING_ELEMENT == EVENT)
				{
					if(qName.equals("observable"))
					{
						tmpEvent.setObservable(true);
					}
					if(qName.equals("controllable"))
					{
						tmpEvent.setControllable(true);
					}
				}
				
				if(qName.equals("name"))
				{    	
					settingName = true;
				}else{
					settingName = false;
				}
			}

		}
		public void parseMetaMainTags(String qName, Attributes atts)
		{
			if(qName.equals(STATE))
			{
				CURRENT_PARSING_ELEMENT = STATE;
//				System.out.println("NODE");
				long id = Long.parseLong(atts.getValue("id"));
				FSAState s = null;
				//Select the state referred by <code>id</code>
				Iterator<FSAState> sIt = model.getStateIterator();
				while(sIt.hasNext())
				{
					FSAState tmpS = sIt.next();
					if(tmpS.getId() == id)
					{
						s = tmpS;
						break;
					}
				}
				CircleNodeLayout tmpCircleNodeLayout = new CircleNodeLayout();
				s.setAnnotation(Annotable.LAYOUT, tmpCircleNodeLayout);
				tmpState = (State)s;
			}
			
			if(qName.equals(TRANSITION))
			{
//				System.out.println("TRANSITION");
				CURRENT_PARSING_ELEMENT = TRANSITION;
				long id = Long.parseLong(atts.getValue("id"));
				FSATransition transition = null;
				//Select the transition referred by <code>id</code>
				Iterator<FSATransition> tIt = model.getTransitionIterator();
				while(tIt.hasNext())
				{
					FSATransition t = tIt.next();
					if(t.getId() == id)
					{
						transition = t;
						break;
					}
				}	
				//transition.setAnnotation(Annotable.LAYOUT, new BezierLayout());
				tmpTransition = (Transition)transition;
			}
			
			if(qName.equals(FONT))
			{
				CURRENT_PARSING_ELEMENT = FONT;
			}
		}
		public void parseMetaSubTags(String qName, Attributes atts)
		{
			if(CURRENT_PARSING_ELEMENT == STATE)
			{
				if(qName.equals("circle"))
				{
					CircleNodeLayout layout = (CircleNodeLayout)tmpState.getAnnotation(Annotable.LAYOUT);
					layout.setRadius(Float.parseFloat(atts.getValue("r")));
					layout.setLocation(Float.parseFloat(atts.getValue("x")),Float.parseFloat(atts.getValue("y")));
						
				}
				
				if(qName.equals("arrow"))
				{
					CircleNodeLayout layout = (CircleNodeLayout)tmpState.getAnnotation(Annotable.LAYOUT);
					layout.setArrow(new Point2D.Float(Float.parseFloat(atts.getValue("x"))  , Float.parseFloat(atts.getValue("y"))));
				}
			}
			
			if(CURRENT_PARSING_ELEMENT == TRANSITION)
			{
				//Setting the layout for the edge:
				if(qName.equals("bezier"))
				{   
					float x1 = Float.parseFloat(atts.getValue("x1"));
					float x2 = Float.parseFloat(atts.getValue("x2"));
					float y1 = Float.parseFloat(atts.getValue("y1"));
					float y2 = Float.parseFloat(atts.getValue("y2"));
					float ctrlx1 = Float.parseFloat(atts.getValue("ctrlx1"));
					float ctrly1 = Float.parseFloat(atts.getValue("ctrly1"));
					float ctrlx2;
					float ctrly2;
					try{
						ctrlx2 = Float.parseFloat(atts.getValue("ctrlx2"));
						ctrly2 = Float.parseFloat(atts.getValue("ctrly2"));
					}catch(Exception e){
						ctrlx2 = ctrlx1;
						ctrly2 = ctrly1;
					}

					
					Point2D.Float[] controls = new Point2D.Float[4];
					controls[BezierLayout.P1] = new Point2D.Float(x1,y1);
					controls[BezierLayout.P2] = new Point2D.Float(x2, y2);
					controls[BezierLayout.CTRL1] = new Point2D.Float(ctrlx1,ctrly1);
					controls[BezierLayout.CTRL2] = new Point2D.Float(ctrlx2,ctrly2);
					tmpTransition.setAnnotation(Annotable.LAYOUT, new BezierLayout(controls));
				}

				//Setting the label for the edge:
				if(qName.equals("label"))
				{	
					FSAEvent e = tmpTransition.getEvent();
					if(e!=null)
					{
						BezierLayout layout = (BezierLayout)tmpTransition.getAnnotation(Annotable.LAYOUT);
						layout.addEventName(e.getSymbol());		
						Point2D.Float offset = new Point2D.Float();
						offset.setLocation(Float.parseFloat(atts.getValue("x")), Float.parseFloat(atts.getValue("y")));
						layout.setLabelOffset(offset);		
					}
					
				}
			}
		}
		public void parseMetaElements(String qName, Attributes atts, String action)
		{
			if(action.equals(PARSE_MAIN_TAGS))//Parsing one of the main data tags
			{	
				parseMetaMainTags(qName, atts);
			}
			
			if(action.equals(PARSE_SUB_TAGS))
			{
				parseMetaSubTags(qName,atts);
			}
		}
		/**
		 * Sets annotations for all the model elements in <code>model</code> based on the informations
		 * countained in <code>stream</code>
		 * @param stream
		 * @param model
		 */
		public void parseMeta(InputStream stream, DESModel model)
		{
			parsingMeta = true;
			this.model = (FSAModel)model;
			try{
				xmlReader.parse(new InputSource(stream));
			}
			catch(FileNotFoundException fnfe){

			}
			catch(IOException ioe){

			}
			catch(SAXException saxe){

			}
			catch(NullPointerException npe){

			}
		}
		/**
		 * Create either a State, Event or Transition based on the given atts. Since it returns an Object, the function 
		 * is expandable to return any model element which may be created in the future.
		 * @param atts
		 * @param parsingElement
		 * @return
		 */
		public Object getModelElement(Attributes atts, String parsingElement)
		{
			if(parsingElement == STATE)
			{
				long id = Long.parseLong(atts.getValue("id"));
				State s =  new State(id);
				return s;
			}

			if(parsingElement == EVENT)
			{
				long id = Long.parseLong(atts.getValue("id")); 	
				return new Event(id);
			}

			if(parsingElement == TRANSITION)
			{
				long id = Long.parseLong(atts.getValue("id")); 
				long sourceN = Long.parseLong(atts.getValue("source"));
				long targetN = Long.parseLong(atts.getValue("target"));
				long eventN;
				try{
					eventN = Long.parseLong(atts.getValue("event"));
				}catch(NumberFormatException e)
				{
					eventN = -1;
				}

				Iterator<FSAState> sIt = model.getStateIterator();
				FSAState src = null, dst = null;
				while(sIt.hasNext())
				{
					FSAState s = sIt.next();
					if(s.getId() == sourceN)
					{
						src = s;
					}

					if(s.getId() == targetN)
					{
						dst = s;
					}
				} 		
				Iterator<FSAEvent> eIt = model.getEventIterator();
				FSAEvent event = null;
				while(eIt.hasNext())
				{
					FSAEvent e = eIt.next();
					if(e.getId() == eventN)
					{
						event = e;
					}
				}
				if(event != null)
				{
					return new Transition(id, src, dst, event);
				}
				else
				{
					return new Transition(id,src,dst);
				}
			}
			return null;
		} 
		public void characters(char buf[], int offset, int len)
		throws SAXException
		{
			if(parsingData)
			{
				//Get the value between the tags <name> and </name>
				//The same code can be used to get values between different XML tags
				if(settingName == true)
				{
					StringBuffer tst = new StringBuffer();
					tst.append(buf, offset, len);
					tmpName = tst.toString();
					if(CURRENT_PARSING_ELEMENT == STATE)
					{
						tmpState.setName(tmpName);
					}
					if(CURRENT_PARSING_ELEMENT == EVENT)
					{
						tmpEvent.setSymbol(tmpName);
						model.add(tmpEvent);
					}
				}
				settingName = false;
				//
			}
		}
	}

}
