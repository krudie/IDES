package presentation.fsa;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

import presentation.GraphicalLayout;
import presentation.fsa.actions.UIActions;
import presentation.fsa.tools.CreationTool;
import presentation.fsa.tools.DrawingTool;
import presentation.fsa.tools.ModifyEdgeTool;
import presentation.fsa.tools.MovementTool;
import presentation.fsa.tools.SelectionTool;
import presentation.fsa.tools.TextTool;
import util.BooleanUIBinder;

/**
 * The component in which users view, create and modify a graph representation
 * of an automaton.
 * 
 * @author Helen Bretzke
 * @author Lenko Grigorov
 */
@SuppressWarnings("serial")
public class GraphDrawingView extends GraphView implements MouseMotionListener,
		MouseListener, KeyListener
{

	protected static class CanvasSettings
	{
		public boolean gridOn = false;

		public Rectangle viewport = new Rectangle(0, 0, 0, 0);

		public float zoom = 1;
	}

	protected final static String CANVAS_SETTINGS = "canvasSettings";

	protected CanvasSettings canvasSettings = null;

	/** If true, the next refresh will not change the view */
	private boolean avoidNextDraw;

	/** Set a flag to avoid a chage for the next refresh operation */
	public void setAvoidNextDraw(boolean b)
	{
		avoidNextDraw = b;
	}

	public boolean getAvoidNextDraw()
	{
		return avoidNextDraw;
	}

	private boolean moving = false;

	public void setMoving(boolean b)
	{
		moving = b;
	}

	public boolean getMoving()
	{
		return moving;
	}

	// The tool to be automatically returned to after the MODIFY tool is used
	private int PreferredTool;

	public int getPreferredTool()
	{
		return PreferredTool;
	}

	public void setPreferredTool(int pt)
	{
		avoidNextDraw = false;
		PreferredTool = pt;
	}

	protected BooleanUIBinder gridToggle = null;

	private TexturePaint gridBG = null;

	public void setShowGrid(boolean b)
	{
		if (b != gridToggle.get())
		{
			gridToggle.set(b);
			if (b)
			{
				Hub.getUserInterface().getZoomControl().setZoom(1);
			}
			invalidate();
			Hub.getWorkspace().fireRepaintRequired();
		}
	}

	public boolean getShowGrid()
	{
		return gridToggle.get();
	}

	private int currentTool = DEFAULT;

	/**
	 * The set of drawing tools which handle delegated i.e. events forwarded
	 * from this view. Each tool corresponds to a drawing mode.
	 */
	private DrawingTool[] drawingTools;

	public DrawingTool[] getTools()
	{
		return drawingTools;
	}

	/**
	 * Retangle to render as the area selected by mouse.
	 */
	private Rectangle selectionArea;

	/**
	 * Temporary edge for use while creating new edge.
	 */
	private Edge tempEdge;

	/**
	 * Currently selected group of 0 or more elements
	 */
	private SelectionGroup selectedGroup;

	/**
	 * The listener for the user pressing the <code>Delete</code> key.
	 */
	protected UIActions.DeleteAction deleteCommand = new UIActions.DeleteAction(
			this);

	public Action getDeleteAction()
	{
		return deleteCommand;
	}

	protected UIActions.AlignAction alignCommand = new UIActions.AlignAction(
			this);

	public Action getAlignAction()
	{
		return alignCommand;
	}

	protected Action escapeCommand = new AbstractAction("escape")
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			if (((CreationTool)drawingTools[CREATE]).isDrawingEdge())
			{
				((CreationTool)drawingTools[CREATE]).abortEdge();
			}
			else
			{
				setTool(GraphDrawingView.DEFAULT);
			}
			setAvoidNextDraw(false);
		}
	};

	Box largeGraphNotice;

	public GraphDrawingView(FSAModel model, BooleanUIBinder gridBinder)
	{
		super();

		gridToggle = gridBinder;
		// Hub.getWorkspace().addSubscriber(this);

		scaleFactor = 1f;
		scaleToFit = false;

		selectedGroup = new SelectionGroup();
		selectionArea = new Rectangle();

		drawingTools = new DrawingTool[NUMBER_OF_TOOLS];
		drawingTools[DEFAULT] = new SelectionTool();
		drawingTools[SELECT] = drawingTools[DEFAULT];
		drawingTools[CREATE] = new CreationTool();
		drawingTools[TEXT] = new TextTool();
		drawingTools[MOVE] = new MovementTool();
		drawingTools[MODIFY] = new ModifyEdgeTool();
		currentTool = DEFAULT;

		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
		this.setFocusable(true);
		this.requestFocus();

		// Custom actions that can be performed, sometimes undone.
		String escAction = "esc";
		// Undoable actions
		String deleteAction = "deleteSelection";

		// Associating key strokes with action names:
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
				.getKeyStroke(KeyEvent.VK_DELETE, 0),
				deleteAction);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke
				.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				escAction);
		// Associating the action names with operations:
		getActionMap().put(deleteAction, deleteCommand);
		getActionMap().put(escAction, escapeCommand);

		largeGraphNotice = Box.createVerticalBox();
		largeGraphNotice.add(new JLabel(Hub.string("largeGraphNotice1")));
		largeGraphNotice.add(new JLabel(Hub.string("largeGraphNotice2")));
		largeGraphNotice.add(new JLabel(Hub.string("largeGraphNotice3")));
		largeGraphNotice.add(Box.createRigidArea(new Dimension(0, 5)));
		JButton button = new JButton(Hub.string("largeGraphButton"));
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				largeGraphNotice.setVisible(false);
				graphModel.forceLayoutDisplay();
				forceRepaint();
				Hub.getWorkspace().fireRepaintRequired();
			}
		});
		largeGraphNotice.add(button);
		largeGraphNotice.add(Box.createRigidArea(new Dimension(0, 5)));
		largeGraphNotice.add(new JLabel(Hub.string("largeGraphNotice4")));
		largeGraphNotice.setBorder(BorderFactory.createEmptyBorder(10,
				10,
				10,
				10));
		largeGraphNotice.setSize(largeGraphNotice.getPreferredSize());
		add(largeGraphNotice);
		largeGraphNotice.setVisible(false);

		setGraphModel(retrieveGraph(model));
		setVisible(true);
	}

	@Override
	public void setEnabled(boolean b)
	{
		if (b)
		{
			MouseMotionListener[] mml = getMouseMotionListeners();
			boolean isInML = false;
			for (int i = 0; i < mml.length; ++i)
			{
				if (mml[i] == this)
				{
					isInML = true;
					break;
				}
			}
			if (!isInML)
			{
				addMouseMotionListener(this);
			}
			MouseListener[] ml = getMouseListeners();
			isInML = false;
			for (int i = 0; i < ml.length; ++i)
			{
				if (ml[i] == this)
				{
					isInML = true;
					break;
				}
			}
			if (!isInML)
			{
				addMouseListener(this);
			}
			KeyListener[] kl = getKeyListeners();
			isInML = false;
			for (int i = 0; i < kl.length; ++i)
			{
				if (kl[i] == this)
				{
					isInML = true;
					break;
				}
			}
			if (!isInML)
			{
				addKeyListener(this);
			}
		}
		else
		{
			removeMouseMotionListener(this);
			removeMouseListener(this);
			removeKeyListener(this);
		}
	}

	/**
	 * TODO If no model open, set BG colour to some inactive looking grey.
	 */
	@Override
	public void paint(Graphics g)
	{
		if (graphModel.isAvoidLayoutDrawing())
		{
			largeGraphNotice.setVisible(true);
			originalPaint(g);
			return;
		}
		if (canvasSettings != null)
		{
			scrollRectToVisible(canvasSettings.viewport);
			canvasSettings = null;
		}
		scaleFactor = Hub.getUserInterface().getZoomControl().getZoom();
		if (scaleFactor != 1)
		{
			setShowGrid(false);
		}

		Graphics2D g2D = (Graphics2D)g;
		if (gridBG == null)
		{
			BufferedImage image = (BufferedImage)createImage(GraphicalLayout.GRID_SIZE,
					GraphicalLayout.GRID_SIZE);
			Graphics2D im_g2d = (Graphics2D)image.getGraphics();
			im_g2d.setColor(Color.WHITE);
			im_g2d.fillRect(0,
					0,
					GraphicalLayout.GRID_SIZE,
					GraphicalLayout.GRID_SIZE);
			im_g2d.setColor(Color.BLACK);
			im_g2d.drawLine(0, 0, 0, 0);
			gridBG = new TexturePaint(image, new Rectangle(
					0,
					0,
					GraphicalLayout.GRID_SIZE,
					GraphicalLayout.GRID_SIZE));
		}
		if (gridBG != null && scaleFactor == 1 && gridToggle.get())
		{
			Rectangle r = new Rectangle();
			r.width = Math.max(getBounds().width, graphBounds.width);
			r.height = Math.max(getBounds().height, graphBounds.height);
			g2D.setPaint(gridBG);
			g2D.fill(r);
		}
		else
		{
			g2D.setColor(Color.WHITE);
			g2D.fillRect(0, 0, getBounds().width, getBounds().height);
		}
		super.paint(g, false);

		if (tempEdge != null)
		{
			tempEdge.draw(g2D);
		}

		if (selectionArea.height > 0 && selectionArea.width > 0)
		{
			g2D.setStroke(GraphicalLayout.DASHED_STROKE);
			g2D.setColor(Color.DARK_GRAY);

			// DEBUG
			try
			{
				g2D.draw(selectionArea);
			}
			catch (Exception e)
			{
				throw new RuntimeException(selectionArea.toString());
			}
		}
		// TODO: CHRISTIAN
		// moving = false;
		// if(!moving)
		// {
		// super.graphModel.updateSelection(null);
		// }
	}

	// Mouse events
	public void mouseClicked(MouseEvent arg0)
	{

		if (uiInteraction)
		{
			uiInteraction = false;
			return;
		}

		arg0 = transformMouseCoords(arg0);
		FSAGraph g = super.getGraphModel();
		if (g.isAvoidLayoutDrawing())
		{
			return;
		}
		// Switch to labelling tool if we are not in creation mode
		// NOTE there is a conflict with double click paradigm in creation mode
		// since don't know we've got a double click until after self-loop has
		// been created.
		// SOLUTION delay creation of self loops until we know if double
		// clicked.
		// Don't finish edge on mouse released if target == source.
		if (arg0.getClickCount() == 2 && currentTool != CREATE)
		{
			currentTool = TEXT;
		}
		drawingTools[currentTool].handleMouseClicked(arg0);
	}

	public void mousePressed(MouseEvent arg0)
	{

		if (uiInteraction)
		{
			return;
		}

		arg0 = transformMouseCoords(arg0);
		FSAGraph g = super.getGraphModel();
		if (g.isAvoidLayoutDrawing())
		{
			return;
		}
		if (arg0.isPopupTrigger())
		{
			// from both mousePressed and mouseReleased to be truly platform
			// independant.
			drawingTools[currentTool].handleRightClick(arg0);
		}
		else
		{
			drawingTools[currentTool].handleMousePressed(arg0);
		}
	}

	public void mouseReleased(MouseEvent arg0)
	{

		if (uiInteraction)
		{
			uiInteraction = false;
			return;
		}

		arg0 = transformMouseCoords(arg0);

		if (arg0.isPopupTrigger())
		{
			// from both mousePressed and mouseReleased to be truly platform
			// independant.
			drawingTools[currentTool].handleRightClick(arg0);
		}
		else
		{
			drawingTools[currentTool].handleMouseReleased(arg0);
		}

		// If the user was moving graph elements before releasing the button,
		// inform FSAGraph that translations were done. So FSAGraph will inform
		// its subscribers.
		if (this.moving)
		{
			this.setMoving(false);
			GraphElement selection = this.getSelectedGroup();
			if (selection != null)
			{
				this.graphModel.commitLayoutModified(this.getSelectedElement());
			}
		}
	}

	public void mouseDragged(MouseEvent arg0)
	{

		if (uiInteraction)
		{
			return;
		}

		FSAGraph g = super.getGraphModel();
		if (g.isAvoidLayoutDrawing())
		{
			return;
		}
		arg0 = transformMouseCoords(arg0);
		drawingTools[currentTool].handleMouseDragged(arg0);
		this.setMoving(true);
	}

	public void mouseMoved(MouseEvent arg0)
	{

		if (uiInteraction)
		{
			return;
		}

		arg0 = transformMouseCoords(arg0);
		drawingTools[currentTool].handleMouseMoved(arg0);
	}

	public void mouseEntered(MouseEvent arg0)
	{
	}

	public void mouseExited(MouseEvent arg0)
	{
		FSAGraph g = super.getGraphModel();
		if (g.isAvoidLayoutDrawing())
		{
			return;
		}
		if (((CreationTool)drawingTools[CREATE]).isDrawingEdge())
		{
			// USABILITY Cancels edge when leave panel bounds.
			// Seems rude to cancel the edge while it is being drawn.
			// Why not resize the canvas when the user chooses the target point?
			((CreationTool)drawingTools[CREATE]).abortEdge();
			repaint();
		}
	}

	// Key listener events
	// FIXME move all key listening to the main window since it seems events
	// don't make it this far...
	public void keyTyped(KeyEvent arg0)
	{

		if (uiInteraction)
		{
			uiInteraction = false;
			return;
		}

		drawingTools[currentTool].handleKeyTyped(arg0);
	}

	public void keyPressed(KeyEvent arg0)
	{

		if (uiInteraction)
		{
			return;
		}

		drawingTools[currentTool].handleKeyPressed(arg0);
	}

	public void keyReleased(KeyEvent arg0)
	{

		if (uiInteraction)
		{
			uiInteraction = false;
			return;
		}

		drawingTools[currentTool].handleKeyReleased(arg0);
	}

	/**
	 * Deselects and un-highlights the set of selected elements.
	 */
	public void clearCurrentSelection()
	{

		boolean anythingCleared = selectedGroup.size() > 0;

		selectedGroup.setSelected(false);
		selectedGroup.setHighlighted(false);
		selectedGroup.clear();
		selectionArea.setSize(0, 0);

		if (anythingCleared)
		{
			// Notify the subscribers that the layout was changed (selection
			// colors)
			// so they can repaint the graphs.
			this.graphModel.commitLayoutModified();
		}
	}

	/**
	 * Updates the selection group to the element hit by <code>point</code> and
	 * returns true. If nothing intersected, returns false.
	 * 
	 * @param point
	 * @return true iff something hit.
	 */
	public boolean updateCurrentSelection(Point point)
	{
		if (graphModel != null)
		{
			// selectedElement = graphModel.getElementIntersectedBy(point);
			// if(selectedElement != null)
			// {
			// selectedElement.setSelected(true);
			// return true; // selectedElement != null;
			// }
			GraphElement el = graphModel.getElementIntersectedBy(point);
			if (el != null)
			{
				selectedGroup.insert(el);
				selectedGroup.setSelected(true);
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the current selection to all elements contained by the given
	 * rectangle.
	 * 
	 * @param rectangle
	 */
	public void updateCurrentSelection(Rectangle rectangle)
	{
		if (graphModel != null)
		{
			// CLM: unhighlight nodes whenever they leave the selection
			// rectangle
			selectedGroup.setHighlighted(false);
			selectedGroup = graphModel.getElementsContainedBy(rectangle);
		}
	}

	/**
	 * Highlights the graph elements currently selected iff <code>b</code>.
	 * 
	 * @param b
	 *            boolean flag to toggle highlighting
	 */
	public void highlightCurrentSelection(boolean b)
	{
		selectedGroup.setHighlighted(b);
		selectedGroup.setSelected(!b);
	}

	/**
	 * @return true iff there is a current selection of elements
	 */
	public boolean hasSelection()
	{
		return selectedGroup.hasChildren(); // selectedElement != null ||
	}

	/**
	 * Precondition: <code>currentSelection</code> != null
	 * 
	 * @param currentSelection
	 */
	protected void setSelectedGroup(SelectionGroup currentSelection)
	{
		this.selectedGroup = currentSelection;
	}

	/**
	 * Returns the set of currently selected elements in this view.
	 * 
	 * @return the set of currently selected elements in this view
	 */
	public SelectionGroup getSelectedGroup()
	{
		return selectedGroup;
	}

	/**
	 * @return the single element currently selected (if exists), otherwise
	 *         returns null
	 */
	public GraphElement getSelectedElement()
	{
		if (selectedGroup.size() == 1)
		{
			return selectedGroup.children().next(); // selectedElement;
		}
		return null;
	}

	public Rectangle getSelectionArea()
	{
		return selectionArea;
	}

	/**
	 * Tools types (corresponding to user interaction modes) to determine mouse
	 * and keyboard responses.
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
	public void setTool(int toolId)
	{
		currentTool = toolId;
		this.setCursor(drawingTools[currentTool].getCursor());
	}

	/**
	 * @return true iff there are elements in the currentSelection
	 */
	public boolean hasCurrentSelection()
	{
		return selectedGroup.hasChildren();
	}

	protected MouseEvent transformMouseCoords(MouseEvent e)
	{
		Point2D.Float p = new Point2D.Float(e.getX(), e.getY());
		p = screenToLocal(p);
		return new MouseEvent(
				(Component)e.getSource(),
				e.getID(),
				e.getWhen(),
				e.getModifiersEx(),
				(int)p.x,
				(int)p.y,
				e.getClickCount(),
				e.isPopupTrigger(),
				e.getButton());
	}

	public Point2D.Float screenToLocal(Point2D.Float p)
	{
		Point2D.Float r = (Point2D.Float)p.clone();
		r.x = r.x / scaleFactor;
		r.y = r.y / scaleFactor;
		return r;
	}

	public Point2D.Float localToScreen(Point2D.Float p)
	{
		Point2D.Float r = (Point2D.Float)p.clone();
		r.x = r.x * scaleFactor;
		r.y = r.y * scaleFactor;
		return r;
	}

	/**
	 * @return the current drawing tool
	 */
	public DrawingTool getCurrentTool()
	{
		return drawingTools[currentTool];
	}

	public Edge getTempEdge()
	{
		return tempEdge;
	}

	public void setTempEdge(Edge tempEdge)
	{
		this.tempEdge = tempEdge;
	}

	@Override
	public JComponent getGUI()
	{
		JScrollPane sp = new JScrollPane(
				this,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		return sp;
	}

	// public void update(){
	// scaleFactor=((MainWindow)Hub.getMainWindow()).getZoomControl().getZoom();
	// if(scaleFactor!=1)
	// setShowGrid(false);

	// // get the active graph model and update the graph view part of me
	// graphModel = Workspace.instance().getActiveGraphModel();
	// super.update();
	// Hub.getMainWindow().validate();
	// }

	/**
	 * Override
	 * 
	 * @see observer.FSAGraphSubscriber#fsmGraphSelectionChanged(observer.FSAGraphMessage)
	 */
	// public void fsmGraphSelectionChanged(FSMGraphMessage message)
	// {
	// // ??? Do I need to do anything here ?
	// // Or is this event always going to be fired from within this class ?
	// }
	// /* (non-Javadoc)
	// * @see
	// observer.WorkspaceSubscriber#modelCollectionChanged(observer.
	// WorkspaceMessage)
	// */
	// public void modelCollectionChanged(WorkspaceMessage message) {
	// // get the active graph model and update the graph view part of me
	// if(Hub.getWorkspace().getActiveLayoutShell() instanceof FSAGraph)
	// setGraphModel((FSAGraph)Hub.getWorkspace().getActiveLayoutShell());
	// else
	// setGraphModel(null);
	// Hub.getMainWindow().validate();
	// }
	// /* (non-Javadoc)
	// * @see
	// observer.WorkspaceSubscriber#repaintRequired(observer.WorkspaceMessage)
	// */
	// public void repaintRequired(WorkspaceMessage message) {
	// Hub.getMainWindow().validate();
	// repaint();
	// }
	// /* (non-Javadoc)
	// * @see
	// observer.WorkspaceSubscriber#modelSwitched(observer.WorkspaceMessage)
	// */
	// public void modelSwitched(WorkspaceMessage message) {
	// // get the active graph model and update the graph view part of me
	// if(Hub.getWorkspace().getActiveLayoutShell() instanceof FSAGraph)
	// setGraphModel((FSAGraph)Hub.getWorkspace().getActiveLayoutShell());
	// else
	// setGraphModel(null);
	// Hub.getMainWindow().validate();
	// //repaint();
	// }
	@Override
	protected void setGraphModel(FSAGraph graphModel)
	{
		super.setGraphModel(graphModel);
		if (graphModel == null)
		{
			return;
		}
		canvasSettings = (CanvasSettings)graphModel
				.getAnnotation(CANVAS_SETTINGS);
		if (canvasSettings != null)
		{
			setShowGrid(canvasSettings.gridOn);
			Hub
					.getUserInterface().getZoomControl()
					.setZoom(canvasSettings.zoom);
		}
		else
		{
			setShowGrid(false);
			Hub.getUserInterface().getZoomControl().setZoom(1);
		}
		//if the scale factor isn't set properly before adding the GraphView to the
		//scroll pane, auto-scroll to the position stored in canvas settings won't work
		scaleFactor = Hub.getUserInterface().getZoomControl().getZoom();
		uiInteraction = false;
	}

	@Override
	public void release()
	{
		clearCurrentSelection();
		if (graphModel != null)
		{
			canvasSettings = new CanvasSettings();
			canvasSettings.gridOn = getShowGrid();
			canvasSettings.viewport = getVisibleRect();
			canvasSettings.zoom = scaleFactor;
			graphModel.setAnnotation(CANVAS_SETTINGS, canvasSettings);
		}
		super.release();
	}

	protected boolean uiInteraction = false;

	public void startUIInteraction()
	{
		uiInteraction = true;
	}
}
