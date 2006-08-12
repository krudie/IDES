/**
 * 
 */
package io.fsa.ver1;

import java.awt.geom.Point2D;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import presentation.fsa.BezierEdge;
import presentation.fsa.BezierLayout;
import presentation.fsa.Edge;
import presentation.fsa.FSMGraph;
import presentation.fsa.GraphElement;
import presentation.fsa.CircleNode;
import presentation.fsa.NodeLayout;
import presentation.fsa.ReflexiveEdge;

import model.fsa.FSAState;
import model.fsa.FSATransition;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.Event;
import model.fsa.ver1.State;
import model.fsa.ver1.Transition;

import io.AbstractParser;

/**
 * 
 * @author Lenko Grigorov
 */
public class LayoutDataParser extends AbstractParser {

    private int state = STATE_IDLE;

    protected static final String ELEMENT_STATE = "state",
    ELEMENT_TRANSITION = "transition", ELEMENT_GRAPHIC = "graphic", ELEMENT_META = "meta",
    ELEMENT_FONT = "font";

    protected static final String ATTRIBUTE_ID = "id", ATTRIBUTE_SIZE = "size";

    private static final int STATE_IDLE = 0, STATE_META = 1,
    STATE_STATE = 2, STATE_TRANSITION = 3, STATE_FONT = 4;
    
	Automaton a=null;
	ContentHandler ch=null;
	
	/**
	 * Top element in recursive graph structure used to store
	 * Nodes, Edges and Labels for display of the automaton.
	 */
	GraphElement graph=null;
	
	public LayoutDataParser(Automaton a) {
		super();
		this.a=a;
		this.graph = new GraphElement();
	}

	/**
	 * @see io.AbstractFileParser#parse(java.io.File)
	 */
	public Object parse(XMLReader xmlr, String errors) {
		this.xmlReader = xmlr;
		parsingErrors=errors;
		ch=xmlr.getContentHandler();
		xmlr.setContentHandler(this);
		//return null;
		return graph;
	}
	
    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
    	switch(state){
        case (STATE_IDLE):
            if(qName.equals(ELEMENT_STATE)){
                if(atts.getValue(ATTRIBUTE_ID) == null){
                    parsingErrors += "Unable to parse state with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                State s = (State)a.getState(id);
                if(s==null)
                {
                	parsingErrors += "Couldn't find state with given id.\n";
                    break;
                }
                
                ////////////////////////////////////////////////////
                SubElement graphic=new SubElement(ELEMENT_STATE);
                SubElementParser sep = new SubElementParser();
                sep.fill(graphic, xmlReader, parsingErrors);
                graphic.setName(ELEMENT_GRAPHIC);
                
                // TODO don't store layout data in the Automaton
                s.addSubElement(graphic); 
                
//                NodeLayout nL=getLayoutData(s);
//    			// TODO nL.setUniformRadius(graph.uniformR);    			
//                Node node = new Node(s, nL);
//                graph.insert(node);
                ////////////////////////////////////////////////////
                                
                state = STATE_IDLE;
            }
            else if(qName.equals(ELEMENT_TRANSITION)){
                if(atts.getValue(ATTRIBUTE_ID) == null){
                    parsingErrors += "Unable to parse transition with no id.\n";
                    break;
                }
                int id = Integer.parseInt(atts.getValue(ATTRIBUTE_ID));
                Transition t=(Transition)a.getTransition(id);
                if(t==null)
                {
                	parsingErrors += "Couldn't find transition with given id.\n";
                    break;
                }
                
                /////////////////////////////////////////////////////
                SubElement graphic=new SubElement(ELEMENT_TRANSITION);
                SubElementParser sep = new SubElementParser();
                sep.fill(graphic, xmlReader, parsingErrors);
                graphic.setName(ELEMENT_GRAPHIC);
                
                // TODO don't store layout data in the Automaton
                t.addSubElement(graphic);            

//                BezierLayout layout = getLayoutData(t);
//                
//                // TEST make sure there are no GraphLabels hashed as the same child.
//    			Node n1 = (Node)graph.child(t.getSource().getId());
//    			Node n2 = (Node)graph.child(t.getTarget().getId());
//    			
//    			// if the edge corresponding to t already exists,
//    			// add t to the edge's set of transitions
//    			Edge e = directedEdgeBetween(n1, n2); 
//    			if(e != null && e.getLayout().equals(layout)){    				    				
//    				e.addTransition(t);		
//    			}else{  // otherwise, create a new edge
//    				// get the graphic data for the transition and all associated events
//    				// construct the edge				
//    				if(n1.equals(n2))
//    				{
//    					e = new SelfLoop(layout, n1, t);
//    				}else{    				
//    					e = new BezierEdge(layout, n1, n2, t);
//    				}
//    				
//    				// add this edge to source and target nodes' children
//    				//Long ID = new Long(id);
//    				n1.insert(e);				
//    				n2.insert(e);    				
//    			}               
                
                ////////////////////////////////////////////////////
                state = STATE_IDLE;
            }
            else if(qName.equals(ELEMENT_FONT)){
                if(atts.getValue(ATTRIBUTE_SIZE) == null){
                    parsingErrors += "Font with undefined size.\n";
                    break;
                }
                SubElement font=new SubElement(ELEMENT_FONT);
                font.setAttribute(ATTRIBUTE_SIZE,atts.getValue(ATTRIBUTE_SIZE));
                SubElementParser sep = new SubElementParser();
                sep.fill(font, xmlReader, parsingErrors);
                
                // TODO figure out where to store the font for this model
                a.setMeta(font);
                /////////////////////////////////////////////////////////
                state = STATE_IDLE;
            }
            break;            
        default:
            parsingErrors += "Encountered wrong beginning of element.\n";
            break;
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        switch(state){
//        case (STATE_FONT):
//            if(!qName.equals(ELEMENT_FONT))
//            {
//            	parsingErrors += "Wrong element endend while parsing metadata.\n";
//            }
//        	state=STATE_IDLE;
//        	break;
        case (STATE_IDLE):
            if(!qName.equals(ELEMENT_META))
            {
            	parsingErrors += "Wrong element ended while parsing metadata.\n";
            }
            break;
        default:
            parsingErrors += "encountered wrong state at end of element.\n";
            break;
        }
    	xmlReader.setContentHandler(ch);
    }


    /** 
     * @return an object encapsulating all of the graphical layout 
	 * 	information required to display the given state.
	 */
	public NodeLayout getLayoutData(FSAState state) {
		State s = (State)state;
		
		// radius, centre point, label text and arrow vector (if initial)
		SubElement layout = s.getSubElement("graphic").getSubElement("circle");
		int radius = Integer.parseInt(layout.getAttribute("r"));
		Point2D.Float centre = new Point2D.Float(Integer.parseInt(layout.getAttribute("x")),
								 				Integer.parseInt(layout.getAttribute("y")));
		String name;
		SubElement n = s.getSubElement("name");
		if(n != null){
			name = (n.getChars() != null) ? n.getChars() : "";
		}else{
			name = "";
		}
		
		if(s.isInitial()) {
        	SubElement a = s.getSubElement("graphic").getSubElement("arrow");
        	Point2D.Float arrow = new Point2D.Float(Float.parseFloat(a.getAttribute("x")),
								 					Float.parseFloat(a.getAttribute("y")));
        	
        	
        	return new NodeLayout(centre, radius, name, arrow);
        } else {
		 	return new NodeLayout(centre, radius, name);
        }	
	}

	/**
	 * Extracts and returns the graphical layout for the Edge representing
	 * the given tranistion.
	 * 
	 * @return graphical layout for the Edge representing the given transition.
	 */
	public BezierLayout getLayoutData(FSATransition transition){
		Transition t = (Transition)transition;
		SubElement layout = t.getSubElement("graphic");
		
		SubElement bezier = layout.getSubElement("bezier");
		Point2D.Float[] controls = new Point2D.Float[4];
		controls[BezierLayout.P1] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("x1")),
				Float.parseFloat(bezier.getAttribute("y1")));
		controls[BezierLayout.P2] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("x2")),
				Float.parseFloat(bezier.getAttribute("y2")));
		controls[BezierLayout.CTRL1] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("ctrlx1")),
				Float.parseFloat(bezier.getAttribute("ctrly1")));
		controls[BezierLayout.CTRL2] = new Point2D.Float(Float.parseFloat(bezier.getAttribute("ctrlx2")),
				Float.parseFloat(bezier.getAttribute("ctrly2")));

		// TODO find out if self-loop
		BezierLayout edgeLayout = new BezierLayout(controls, t.getSource().equals(t.getTarget()));		
				
		// extract label offset
		Point2D.Float offset = new Point2D.Float();
		SubElement label = layout.getSubElement("label");
		offset.setLocation(Float.parseFloat(label.getAttribute("x")), Float.parseFloat(label.getAttribute("y")));
		edgeLayout.setLabelOffset(offset);		
		
		// extract transition event symbol (if exists)
		Event e = (Event) t.getEvent();		
		if(e != null){
			edgeLayout.addEventName(e.getSymbol());
		}		
		return edgeLayout;
	}	

	/**
	 * FIXME There may be a set of these.  
	 * Find the one that has the same layout as the candidate transition.
	 * 
	 * Returns the directed edge from <code>source</code> to <code>target</code> if exists.
	 * Otherwise returns null.
	 */
	private Edge directedEdgeBetween(CircleNode source, CircleNode target){		
		Iterator<Edge> edges = source.adjacentEdges();
		while(edges.hasNext()){
			Edge edge = edges.next();
			if(source.equals(edge.getSource()) && target.equals(edge.getTarget())){
				return edge;
			}
		}
		return null;
	}
	
	
}
