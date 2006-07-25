package presentation.fsa;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.pietschy.command.CommandManager;
import org.pietschy.command.ToggleCommand;

import main.Hub;
import main.IDESWorkspace;
import model.Subscriber;
import model.fsa.FSAEvent;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.Event;
import presentation.GraphicalLayout;
import presentation.PresentationElement;
import ui.command.UniformNodesCommand;
import ui.tools.CreationTool;
import ui.tools.DrawingTool;
import ui.tools.ModifyEdgeTool;
import ui.tools.MovementTool;
import ui.tools.SelectionTool;
import ui.tools.TextTool;
import ui.MainWindow;
/**
 * The component in which users view, create and modify a graph representation
 * of an automaton.
 * 
 * 
 * * current interaction mode,
 * * currently selected object in the drawing area,
 * * copy and cut buffers.
 * 
 * TODO override setName to set name of parent component (i.e. scrollpane) so the name
 * will appear in the tabbed pane or title area of frame?
 * OR define a custom (decorated) scrollpane for this component that sets its name to the name of this component.
 * 
 * @author helen bretzke
 *
 */
@SuppressWarnings("serial")
public class GraphDrawingView extends GraphView implements Subscriber, MouseMotionListener, MouseListener, KeyListener {

	protected static ToggleCommand nodesControl;
	public static boolean isUniformNodes()
	{
		return nodesControl.isSelected();
	}
	
	private TexturePaint gridBG=null;
	private boolean showGrid=false;
	public void setShowGrid(boolean b)
	{
		if(b!=showGrid)
		{
			showGrid=b;
			repaint();
			if(!showGrid)
			{
				((ToggleCommand)CommandManager.defaultInstance().getCommand("showgrid.command")).setSelected(false);
			}
		}
	}
	
	private int currentTool = DEFAULT;
	private DrawingTool[] drawingTools;
	
	/**
	 * Retangle to render as the area selected by mouse. 
	 */
	private Rectangle selectionArea;
	
	// ??? Do I really need these buffers?  
	// Won't associated elements be stored with most recently executed commands in the history?
	
	/**
	 * Copy buffer
	 */
	private PresentationElement copyBuffer;
	
	/**
	 * Cut buffer 
	 */
	private PresentationElement cutBuffer;
	
	/**
	 * Delete and restore buffer
	 */
	private PresentationElement deleteBuffer;
	
	
	/**
	 * Currently selected group or item.
	 */
	private SelectionGroup currentSelection;
	private GraphElement hoverElement;
	
	/**
	 * The selected print area.
	 */
	private PresentationElement printArea;

	/**
	 * The listener for the user pressing the <code>Delete</code> key.
	 */
	protected Action deleteListener = new AbstractAction()
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			if(currentSelection!=null)
			{
				if(((CreationTool)drawingTools[CREATE]).isDrawingEdge())
					((CreationTool)drawingTools[CREATE]).abortEdge();
				for(Iterator i=currentSelection.children();i.hasNext();)
				{
					GraphElement ge=(GraphElement)i.next();
					graphModel.delete(ge);
				}
				update();
			}
		}
	};
	
	public GraphDrawingView() {
		super();
		IDESWorkspace.instance().attach(this);
		
		graph = new GraphElement();
		scaleFactor = 1f;
		scaleToFit=false;
	
		nodesControl=new UniformNodesCommand();
		nodesControl.export();
		
		currentSelection = new SelectionGroup();
		selectionArea = new Rectangle();
		hoverElement = null;
		
		drawingTools = new DrawingTool[NUMBER_OF_TOOLS];
		drawingTools[DEFAULT] = new SelectionTool(this);
		drawingTools[SELECT] = drawingTools[DEFAULT];
		drawingTools[CREATE] = new CreationTool(this);
		drawingTools[TEXT] = new TextTool(this);
		drawingTools[MOVE] = new MovementTool(this);
		drawingTools[MODIFY] = new ModifyEdgeTool(this);
		
		// TODO construct all other drawing tools
		currentTool = DEFAULT;		
	    
		//addMouseListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		this.setFocusable(true);
		this.requestFocus();

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0),this);
		getActionMap().put(this,deleteListener);
		
	    setVisible(true);
	}	
	
	public void setEnabled(boolean b)
	{
		if(b)
		{
			MouseMotionListener[] mml=getMouseMotionListeners();
			boolean isInML=false;
			for(int i=0;i<mml.length;++i)
				if(mml[i]==this)
				{
					isInML=true;
					break;
				}
			if(!isInML)
				addMouseMotionListener(this);
			MouseListener[] ml=getMouseListeners();
			isInML=false;
			for(int i=0;i<ml.length;++i)
				if(ml[i]==this)
				{
					isInML=true;
					break;
				}
			if(!isInML)
				addMouseListener(this);
			KeyListener[] kl=getKeyListeners();
			isInML=false;
			for(int i=0;i<kl.length;++i)
				if(kl[i]==this)
				{
					isInML=true;
					break;
				}
			if(!isInML)
				addKeyListener(this);
		}
		else
		{
			removeMouseMotionListener(this);
			removeMouseListener(this);
			removeKeyListener(this);
		}
	}
	
	public void update(){
		scaleFactor=((MainWindow)Hub.getMainWindow()).getZoomControl().getZoom();
		if(scaleFactor!=1)
			setShowGrid(false);
		// get the active graph model and update the graph view part of me		
		graphModel = IDESWorkspace.instance().getActiveGraphModel();		
		super.update();
		Hub.getMainWindow().validate();
	}
	
	public void paint(Graphics g){
		Graphics2D g2D = (Graphics2D)g;
		if(gridBG == null)
		{
				BufferedImage image= (BufferedImage) createImage(GraphicalLayout.GRID_SIZE,GraphicalLayout.GRID_SIZE);
				Graphics2D im_g2d = (Graphics2D) image.getGraphics();
		    	im_g2d.setColor(Color.WHITE);
		    	im_g2d.fillRect(0,0,GraphicalLayout.GRID_SIZE,GraphicalLayout.GRID_SIZE);
				im_g2d.setColor(Color.BLACK);
				im_g2d.drawLine(0,0,0,0);
				gridBG = new TexturePaint(image,new Rectangle(0,0,GraphicalLayout.GRID_SIZE,GraphicalLayout.GRID_SIZE));
		}
		if(gridBG != null&&scaleFactor==1&&showGrid)
		{
			Rectangle r=new Rectangle();
			r.width=Math.max(getBounds().width,graphBounds.width);
			r.height=Math.max(getBounds().height,graphBounds.height);			
			g2D.setPaint(gridBG);
			g2D.fill(r);
		}
		else
		{
	    	g2D.setColor(Color.WHITE);
	    	g2D.fillRect(0,0,getBounds().width,getBounds().height);
		}
		super.paint(g,false);
		g2D.setStroke(GraphicalLayout.DASHED_STROKE);
		g2D.setColor(Color.DARK_GRAY);
		// DEBUG
		//System.err.println(selectionArea.getSize() + " " + selectionArea.getLocation());		
		//g2D.drawRect(selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height);
		g2D.draw(selectionArea);		
	}
	
	/**
	 * Returns the set of currently selected elements in this view.
	 * 
	 * @return
	 */
	public SelectionGroup getCurrentSelection() {
		return currentSelection;
	}

	/**
	 * Precondition: <code>currentSelection</code> != null
	 * 
	 * @param currentSelection
	 */
	protected void setCurrentSelection(SelectionGroup currentSelection) {
		this.currentSelection = currentSelection;
	}	

	// Mouse events
	public void mouseClicked(MouseEvent arg0) {
		arg0=tranformMouseCoords(arg0);
		// Switch to labelling tool if we are not in creation mode
		// NOTE conflict with double click paradigm in creation mode
		// since don't know we've got a double click until after self-loop has been created.
		// IDEA delay creation of self loops until we know if double clicked.
		// Don't finish edge on mouse released if target == source.
		if(arg0.getClickCount() == 2 && currentTool != CREATE){
			currentTool = TEXT;
		}
		drawingTools[currentTool].handleMouseClicked(arg0);	
	}


	public void mousePressed(MouseEvent arg0) {
		arg0=tranformMouseCoords(arg0);
		if(arg0.isPopupTrigger()){			
			// from both mousePressed and mouseReleased to be truly platform independant.
			drawingTools[currentTool].handleRightClick(arg0);
		}else{
			drawingTools[currentTool].handleMousePressed(arg0);
		}
	}


	public void mouseReleased(MouseEvent arg0) {
		arg0=tranformMouseCoords(arg0);
		if(arg0.isPopupTrigger()){			
			// from both mousePressed and mouseReleased to be truly platform independant.
			drawingTools[currentTool].handleRightClick(arg0);
		}else{
			drawingTools[currentTool].handleMouseReleased(arg0);			
		}
	}

	public void mouseDragged(MouseEvent arg0) {		
		arg0=tranformMouseCoords(arg0);
		drawingTools[currentTool].handleMouseDragged(arg0);		
	}

	public void mouseMoved(MouseEvent arg0) {
		arg0=tranformMouseCoords(arg0);
		drawingTools[currentTool].handleMouseMoved(arg0);
	}	

	public void mouseEntered(MouseEvent arg0) {}
	
	public void mouseExited(MouseEvent arg0) {
		if(((CreationTool)drawingTools[CREATE]).isDrawingEdge())
		{
			// USABILITY Cancels edge when leave panel bounds.
			// Seems rude to cancel the edge while it is being drawn.
			// Why not resize the canvas when the user chooses the target point?
			((CreationTool)drawingTools[CREATE]).abortEdge();
			repaint();
		}
	}
	

	// Key listener events
	// FIXME move all key listening to the main window since it seems events don't make it this far...
	public void keyTyped(KeyEvent arg0) {
		drawingTools[currentTool].handleKeyTyped(arg0);		
	}


	public void keyPressed(KeyEvent arg0) {		
		drawingTools[currentTool].handleKeyPressed(arg0);	
	}


	public void keyReleased(KeyEvent arg0) {		
		drawingTools[currentTool].handleKeyReleased(arg0);
	}

	/**
	 * 
	 * @return true iff there is a current selection of elements
	 */
	public boolean hasSelection(){
		return currentSelection.children().hasNext();
	}
	
	/**
	 * Deselects and un-highlights the set of selected elements. 
	 */
	public void clearCurrentSelection(){
		//if(currentSelection != null){
			currentSelection.setSelected(false);
			currentSelection.setHighlighted(false);
			//currentSelection = null;
			currentSelection.clear();
			selectionArea.setSize(0,0);			
		//}
	}
	
	/**
	 * Updates the selection group to the element hit by <code>point</code> and returns true.
	 * If nothing intersected, returns false.
	 * 
	 * @param point
	 * @return true iff something hit.
	 */
	public boolean updateCurrentSelection(Point point) {
		if(graphModel != null){
			GraphElement el = graphModel.getElementIntersectedBy(point);
			if(el != null){				
				currentSelection.insert(el);
				currentSelection.setSelected(true);
				return true;
			}
		}
		return false;				
	}	
	
	/**
	 * Set the current selection to all elements contained by the given rectangle.
	 * 
	 * @param rectangle
	 */
	public void updateCurrentSelection(Rectangle rectangle){
		// IDEA make a GraphElement called Group (see EdgeGroup in Ver1 & 2)
		// that sets highlight(boolean) on all of its elements.
		if(graphModel != null){
			currentSelection = graphModel.getElementsContainedBy(rectangle);
			currentSelection.setSelected(true);			
		}
	}

	/**
	 * Highlights the graph elements currently selected iff <code>b</code>. 
	 * 
	 * @param b boolean flag to toggle highlighting
	 */
	public void highlightCurrentSelection(boolean b){
		//if(currentSelection != null){
			currentSelection.setHighlighted(b);
			currentSelection.setSelected(!b);
		//}
	}	
	
	public Rectangle getSelectionArea() {
		return selectionArea;
	}	
	
	/**
	 * Tools types (corresponding to user interaction modes) to 
	 * determine mouse and keyboard responses.
	 */
	public final static int DEFAULT = 0;
	public final static int SELECT = 1;
	public final static int ZOOM_IN = 2;
	public final static int ZOOM_OUT = 7;
	public final static int SCALE = 8;
	public final static int CREATE = 3;
	public final static int MODIFY = 4;
	public final static int MOVE = 5;
	public final static int TEXT = 6;
	public final static int NUMBER_OF_TOOLS = 9;
	
	/**
	 * Set the current drawing tool to the one with the given tool id.
	 * 
	 * @param toolId 
	 */
	public void setTool(int toolId){
		currentTool = toolId;
		this.setCursor(drawingTools[currentTool].getCursor());
	}

	/**
	 * @return true iff there are elements in the currentSelection
	 */
	public boolean hasCurrentSelection() {		
		return currentSelection.hasChildren();
	}
	
	protected MouseEvent tranformMouseCoords(MouseEvent e)
	{
		Point2D.Float p=new Point2D.Float(e.getX(),e.getY());
		p=screenToLocal(p);
		return new MouseEvent(
				(Component)e.getSource(),
				e.getID(),
				e.getWhen(),
				e.getModifiersEx(),
				(int)p.x,
				(int)p.y,
				e.getClickCount(),
				e.isPopupTrigger(),
				e.getButton()
				);
	}
	
	public Point2D.Float screenToLocal(Point2D.Float p)
	{
		Point2D.Float r=(Point2D.Float)p.clone();
		r.x=r.x/scaleFactor;
		r.y=r.y/scaleFactor;
		return r;
	}
	
	public Point2D.Float localToScreen(Point2D.Float p)
	{
		Point2D.Float r=(Point2D.Float)p.clone();
		r.x=r.x*scaleFactor;
		r.y=r.y*scaleFactor;
		return r;
	}

	/**
	 * @return the current drawing tool
	 */
	public DrawingTool getCurrentTool() {		
		return drawingTools[currentTool];
	}
}
