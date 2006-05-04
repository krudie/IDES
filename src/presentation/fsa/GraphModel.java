package presentation.fsa;

import java.awt.Point;

import model.fsa.FSAMetaData;
import model.fsa.FSAModel;
import presentation.GlyphLabel;

public class GraphModel {

	/**
	 * TODO implement as a more usable form of list or map.
	 */
	private Node[] nodes;
	private Edge[] edges;
	private GlyphLabel[] labels;
	
	/**
	 * The data models to keep synchronized.
	 */	
	private FSAModel fsa;			// abstract system model
	private FSAMetaData layoutData; // presentation data for the system model
	
	public void addNode(Point p){
		// create a State corresponding to the point p
	}
	
	public void addEdge(Node n1, Node n2){
		
	}
	
	public void addEdgeAndNode(Node n1, Point p){
		
	}
	
	public void addLabelToNode(String text){
		// TODO Think about rendering the text as LaTeX.		
	}
		
	public void delete(Node n1){
		// delete all adjacent edges
		// remove n
	}
	
	public void delete(Edge e){
		// remove the edge from the list
		// don't remove the event
		// get n1 and n2, the source and target nodes for this edge
		// if e is the only edge adjacent to n1, delete n1
		// if e is the only edge adjacent to n2, delete n2
	}
}
