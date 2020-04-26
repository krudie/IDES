package presentation.fsa;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
import javax.swing.undo.CompoundEdit;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSAState;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import ides.api.plugin.model.ModelManager;
import ides.api.plugin.presentation.CopyPastePresentation;
import presentation.GraphicalLayout;
import presentation.fsa.actions.GraphActions;
import presentation.fsa.actions.GraphUndoableEdits;
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
public class GraphDrawingView extends GraphView
        implements MouseMotionListener, MouseListener, KeyListener, CopyPastePresentation, ClipboardOwner {

    protected static class CanvasSettings {
        public boolean gridOn = false;

        public Rectangle viewport = new Rectangle(0, 0, 0, 0);

        public float zoom = 1;
    }

    protected final static String CANVAS_SETTINGS = "canvasSettings";

    protected CanvasSettings canvasSettings = null;

    protected JComponent gui = null;

    /** If true, the next refresh will not change the view */
    private boolean avoidNextDraw;

    /** Set a flag to avoid a chage for the next refresh operation */
    public void setAvoidNextDraw(boolean b) {
        avoidNextDraw = b;
    }

    public boolean getAvoidNextDraw() {
        return avoidNextDraw;
    }

    private boolean moving = false;

    public void setMoving(boolean b) {
        moving = b;
    }

    public boolean getMoving() {
        return moving;
    }

    // The tool to be automatically returned to after the MODIFY tool is used
    private int PreferredTool;

    public int getPreferredTool() {
        return PreferredTool;
    }

    public void setPreferredTool(int pt) {
        avoidNextDraw = false;
        PreferredTool = pt;
    }

    protected BooleanUIBinder gridToggle = null;

    private TexturePaint gridBG = null;

    public void setShowGrid(boolean b) {
        if (b != gridToggle.get()) {
            gridToggle.set(b);
            if (b) {
                Hub.getUserInterface().getZoomControl().setZoom(1);
            }
            invalidate();
            Hub.getWorkspace().fireRepaintRequired();
        }
    }

    public boolean getShowGrid() {
        return gridToggle.get();
    }

    private int currentTool = DEFAULT;

    /**
     * The set of drawing tools which handle delegated i.e. events forwarded from
     * this view. Each tool corresponds to a drawing mode.
     */
    private DrawingTool[] drawingTools;

    public DrawingTool[] getTools() {
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
    protected UIActions.DeleteAction deleteCommand = new UIActions.DeleteAction(this);

    public Action getDeleteAction() {
        return deleteCommand;
    }

    protected UIActions.AlignAction alignCommand = new UIActions.AlignAction(this);

    public Action getAlignAction() {
        return alignCommand;
    }

    protected Action escapeCommand = new AbstractAction("escape") {
        public void actionPerformed(ActionEvent actionEvent) {
            if (((CreationTool) drawingTools[CREATE]).isDrawingEdge()) {
                ((CreationTool) drawingTools[CREATE]).abortEdge();
            } else {
                setTool(GraphDrawingView.DEFAULT);
            }
            setAvoidNextDraw(false);
        }
    };

    Box largeGraphNotice;

    public GraphDrawingView(FSAModel model, BooleanUIBinder gridBinder) {
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), deleteAction);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escAction);
        // Associating the action names with operations:
        getActionMap().put(deleteAction, deleteCommand);
        getActionMap().put(escAction, escapeCommand);

        largeGraphNotice = Box.createVerticalBox();
        largeGraphNotice.add(new JLabel(Hub.string("largeGraphNotice1")));
        largeGraphNotice.add(new JLabel(Hub.string("largeGraphNotice2")));
        largeGraphNotice.add(new JLabel(Hub.string("largeGraphNotice3")));
        largeGraphNotice.add(Box.createRigidArea(new Dimension(0, 5)));
        JButton button = new JButton(Hub.string("largeGraphButton"));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                largeGraphNotice.setVisible(false);
                graphModel.forceLayoutDisplay();
                forceRepaint();
                Hub.getWorkspace().fireRepaintRequired();
            }
        });
        largeGraphNotice.add(button);
        largeGraphNotice.add(Box.createRigidArea(new Dimension(0, 5)));
        largeGraphNotice.add(new JLabel(Hub.string("largeGraphNotice4")));
        largeGraphNotice.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        largeGraphNotice.setSize(largeGraphNotice.getPreferredSize());
        add(largeGraphNotice);
        largeGraphNotice.setVisible(false);

        setGraphModel(retrieveGraph(model));
        setVisible(true);
    }

    @Override
    public void setEnabled(boolean b) {
        if (b) {
            MouseMotionListener[] mml = getMouseMotionListeners();
            boolean isInML = false;
            for (int i = 0; i < mml.length; ++i) {
                if (mml[i] == this) {
                    isInML = true;
                    break;
                }
            }
            if (!isInML) {
                addMouseMotionListener(this);
            }
            MouseListener[] ml = getMouseListeners();
            isInML = false;
            for (int i = 0; i < ml.length; ++i) {
                if (ml[i] == this) {
                    isInML = true;
                    break;
                }
            }
            if (!isInML) {
                addMouseListener(this);
            }
            KeyListener[] kl = getKeyListeners();
            isInML = false;
            for (int i = 0; i < kl.length; ++i) {
                if (kl[i] == this) {
                    isInML = true;
                    break;
                }
            }
            if (!isInML) {
                addKeyListener(this);
            }
        } else {
            removeMouseMotionListener(this);
            removeMouseListener(this);
            removeKeyListener(this);
        }
    }

    /**
     * TODO If no model open, set BG colour to some inactive looking grey.
     */
    @Override
    public void paint(Graphics g) {
        if (graphModel.isAvoidLayoutDrawing()) {
            largeGraphNotice.setVisible(true);
            originalPaint(g);
            return;
        }
        if (canvasSettings != null) {
            scrollRectToVisible(canvasSettings.viewport);
            canvasSettings = null;
        }
        scaleFactor = Hub.getUserInterface().getZoomControl().getZoom();
        if (scaleFactor != 1) {
            setShowGrid(false);
        }

        Graphics2D g2D = (Graphics2D) g;
        if (gridBG == null) {
            BufferedImage image = (BufferedImage) createImage(GraphicalLayout.GRID_SIZE, GraphicalLayout.GRID_SIZE);
            Graphics2D im_g2d = (Graphics2D) image.getGraphics();
            im_g2d.setColor(Color.WHITE);
            im_g2d.fillRect(0, 0, GraphicalLayout.GRID_SIZE, GraphicalLayout.GRID_SIZE);
            im_g2d.setColor(Color.BLACK);
            im_g2d.drawLine(0, 0, 0, 0);
            gridBG = new TexturePaint(image, new Rectangle(0, 0, GraphicalLayout.GRID_SIZE, GraphicalLayout.GRID_SIZE));
        }
        if (gridBG != null && scaleFactor == 1 && gridToggle.get()) {
            Rectangle r = new Rectangle();
            r.width = Math.max(getBounds().width, graphBounds.width);
            r.height = Math.max(getBounds().height, graphBounds.height);
            g2D.setPaint(gridBG);
            g2D.fill(r);
        } else {
            g2D.setColor(Color.WHITE);
            g2D.fillRect(0, 0, getBounds().width, getBounds().height);
        }
        super.paint(g, false);

        if (tempEdge != null) {
            tempEdge.draw(g2D);
        }

        if (selectionArea.height > 0 && selectionArea.width > 0) {
            g2D.setStroke(GraphicalLayout.DASHED_STROKE);
            g2D.setColor(Color.DARK_GRAY);

            // DEBUG
            try {
                g2D.draw(selectionArea);
            } catch (Exception e) {
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
    public void mouseClicked(MouseEvent arg0) {

        if (uiInteraction) {
            uiInteraction = false;
            return;
        }

        arg0 = transformMouseCoords(arg0);
        FSAGraph g = super.getGraphModel();
        if (g.isAvoidLayoutDrawing()) {
            return;
        }
        // Switch to labelling tool if we are not in creation mode
        // NOTE there is a conflict with double click paradigm in creation mode
        // since don't know we've got a double click until after self-loop has
        // been created.
        // SOLUTION delay creation of self loops until we know if double
        // clicked.
        // Don't finish edge on mouse released if target == source.
        if (arg0.getClickCount() == 2 && currentTool != CREATE) {
            currentTool = TEXT;
        }
        drawingTools[currentTool].handleMouseClicked(arg0);
    }

    public void mousePressed(MouseEvent arg0) {

        if (uiInteraction) {
            return;
        }

        arg0 = transformMouseCoords(arg0);
        FSAGraph g = super.getGraphModel();
        if (g.isAvoidLayoutDrawing()) {
            return;
        }
        if (arg0.isPopupTrigger()) {
            // from both mousePressed and mouseReleased to be truly platform
            // independant.
            drawingTools[currentTool].handleRightClick(arg0);
        } else {
            drawingTools[currentTool].handleMousePressed(arg0);
        }
    }

    public void mouseReleased(MouseEvent arg0) {

        if (uiInteraction) {
            uiInteraction = false;
            return;
        }

        arg0 = transformMouseCoords(arg0);

        if (arg0.isPopupTrigger()) {
            // from both mousePressed and mouseReleased to be truly platform
            // independant.
            drawingTools[currentTool].handleRightClick(arg0);
        } else {
            drawingTools[currentTool].handleMouseReleased(arg0);
        }

        // If the user was moving graph elements before releasing the button,
        // inform FSAGraph that translations were done. So FSAGraph will inform
        // its subscribers.
        if (this.moving) {
            this.setMoving(false);
            GraphElement selection = this.getSelectedGroup();
            if (selection != null) {
                this.graphModel.fireFSAGraphSelectionChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY,
                        FSAGraphMessage.SELECTION, -1, selection.bounds(), graphModel, ""));
                // this method used to call
                // this.graphModel.commitLayoutModified(this.getSelectedElement()
                // );
                // I replaced with selectionChanged message but not 100% sure
                // there won't be side-effects
                // -- Lenko
            }
        }
    }

    public void mouseDragged(MouseEvent arg0) {

        if (uiInteraction) {
            return;
        }

        FSAGraph g = super.getGraphModel();
        if (g.isAvoidLayoutDrawing()) {
            return;
        }
        arg0 = transformMouseCoords(arg0);
        drawingTools[currentTool].handleMouseDragged(arg0);
        this.setMoving(true);
    }

    // boolean tooltipAlternator = true; // used to alternate shape of the
    //
    // // controllability tooltip

    public void mouseMoved(MouseEvent arg0) {

        if (uiInteraction) {
            return;
        }

        arg0 = transformMouseCoords(arg0);

        // Node n = graphModel.getNodeIntersectedBy(arg0.getPoint());
        // setToolTipText(null);
        // if (n != null && graphModel.getModel() instanceof FSASupervisor)
        // {
        // DESEventSet set = ((FSASupervisor)graphModel.getModel())
        // .getDisabledEvents(n.getState());
        // if (set != null)
        // {
        // if (!set.isEmpty())
        // {
        // String disabled = "Disabled events: ";
        // for (DESEvent event : set)
        // {
        // disabled += event.getSymbol() + "; ";
        // }
        // setToolTipText(disabled.substring(0, disabled.length() - 2)
        // + (tooltipAlternator ? " " : ""));
        // tooltipAlternator = !tooltipAlternator;
        // }
        // else
        // {
        // setToolTipText("No disabled events"
        // + (tooltipAlternator ? " " : ""));
        // tooltipAlternator = !tooltipAlternator;
        // }
        // }
        // }

        drawingTools[currentTool].handleMouseMoved(arg0);
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
        FSAGraph g = super.getGraphModel();
        if (g.isAvoidLayoutDrawing()) {
            return;
        }
        if (((CreationTool) drawingTools[CREATE]).isDrawingEdge()) {
            // USABILITY Cancels edge when leave panel bounds.
            // Seems rude to cancel the edge while it is being drawn.
            // Why not resize the canvas when the user chooses the target point?
            ((CreationTool) drawingTools[CREATE]).abortEdge();
            repaint();
        }
    }

    // Key listener events
    // FIXME move all key listening to the main window since it seems events
    // don't make it this far...
    public void keyTyped(KeyEvent arg0) {

        if (uiInteraction) {
            uiInteraction = false;
            return;
        }

        drawingTools[currentTool].handleKeyTyped(arg0);
    }

    public void keyPressed(KeyEvent arg0) {

        if (uiInteraction) {
            return;
        }

        drawingTools[currentTool].handleKeyPressed(arg0);
    }

    public void keyReleased(KeyEvent arg0) {

        if (uiInteraction) {
            uiInteraction = false;
            return;
        }

        drawingTools[currentTool].handleKeyReleased(arg0);
    }

    /**
     * Deselects and un-highlights the set of selected elements.
     */
    public void clearCurrentSelection() {

        boolean anythingCleared = selectedGroup.size() > 0;

        selectedGroup.setSelected(false);
        selectedGroup.setHighlighted(false);
        selectedGroup.clear();
        selectionArea.setSize(0, 0);

        if (anythingCleared) {
            // Notify the subscribers that the layout was changed (selection
            // colors)
            // so they can repaint the graphs.
            this.graphModel.fireFSAGraphSelectionChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY,
                    FSAGraphMessage.SELECTION, -1, graphModel.getBounds(false), graphModel, ""));
            // this method used to call
            // this.graphModel.commitLayoutModified();
            // I replaced with selectionChanged message but not 100% sure there
            // won't be side-effects
            // -- Lenko
            CCPStatusChanged();
        }
    }

    /**
     * Updates the selection group to the element hit by <code>point</code> and
     * returns true. If nothing intersected, returns false.
     * 
     * @param point
     * @return true iff something hit.
     */
    public boolean updateCurrentSelection(Point point) {
        if (graphModel != null) {
            // selectedElement = graphModel.getElementIntersectedBy(point);
            // if(selectedElement != null)
            // {
            // selectedElement.setSelected(true);
            // return true; // selectedElement != null;
            // }
            GraphElement el = graphModel.getElementIntersectedBy(point);
            if (el != null) {
                selectedGroup.insert(el);
                selectedGroup.setSelected(true);
                CCPStatusChanged();
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
    public void updateCurrentSelection(Rectangle rectangle) {
        if (graphModel != null) {
            // CLM: unhighlight nodes whenever they leave the selection
            // rectangle
            selectedGroup.setHighlighted(false);
            selectedGroup = graphModel.getElementsContainedBy(rectangle);
            CCPStatusChanged();
        }
    }

    /**
     * Highlights the graph elements currently selected iff <code>b</code>.
     * 
     * @param b boolean flag to toggle highlighting
     */
    public void highlightCurrentSelection(boolean b) {
        selectedGroup.setHighlighted(b);
        selectedGroup.setSelected(!b);
    }

    /**
     * @return true iff there is a current selection of elements
     */
    public boolean hasSelection() {
        return selectedGroup.hasChildren(); // selectedElement != null ||
    }

    /**
     * Precondition: <code>currentSelection</code> != null
     * 
     * @param currentSelection
     */
    protected void setSelectedGroup(SelectionGroup currentSelection) {
        this.selectedGroup = currentSelection;
        CCPStatusChanged();
    }

    /**
     * Returns the set of currently selected elements in this view.
     * 
     * @return the set of currently selected elements in this view
     */
    public SelectionGroup getSelectedGroup() {
        return selectedGroup;
    }

    /**
     * @return the single element currently selected (if exists), otherwise returns
     *         null
     */
    public GraphElement getSelectedElement() {
        if (selectedGroup.size() == 1) {
            return selectedGroup.children().next(); // selectedElement;
        }
        return null;
    }

    public Rectangle getSelectionArea() {
        return selectionArea;
    }

    /**
     * Tools types (corresponding to user interaction modes) to determine mouse and
     * keyboard responses.
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
    public void setTool(int toolId) {
        currentTool = toolId;
        this.setCursor(drawingTools[currentTool].getCursor());
    }

    /**
     * @return true iff there are elements in the currentSelection
     */
    public boolean hasCurrentSelection() {
        return selectedGroup.hasChildren();
    }

    protected MouseEvent transformMouseCoords(MouseEvent e) {
        Point2D.Float p = new Point2D.Float(e.getX(), e.getY());
        p = screenToLocal(p);
        return new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiersEx(), (int) p.x,
                (int) p.y, e.getClickCount(), e.isPopupTrigger(), e.getButton());
    }

    public Point2D.Float screenToLocal(Point2D.Float p) {
        Point2D.Float r = (Point2D.Float) p.clone();
        r.x = r.x / scaleFactor;
        r.y = r.y / scaleFactor;
        return r;
    }

    public Point2D.Float localToScreen(Point2D.Float p) {
        Point2D.Float r = (Point2D.Float) p.clone();
        r.x = r.x * scaleFactor;
        r.y = r.y * scaleFactor;
        return r;
    }

    /**
     * @return the current drawing tool
     */
    public DrawingTool getCurrentTool() {
        return drawingTools[currentTool];
    }

    public Edge getTempEdge() {
        return tempEdge;
    }

    public void setTempEdge(Edge tempEdge) {
        this.tempEdge = tempEdge;
    }

    @Override
    public JComponent getGUI() {
        if (gui == null) {
            gui = new JScrollPane(this, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        }

        return gui;
    }

    @Override
    protected void setGraphModel(FSAGraph graphModel) {
        super.setGraphModel(graphModel);
        if (graphModel == null) {
            return;
        }
        canvasSettings = (CanvasSettings) graphModel.getAnnotation(CANVAS_SETTINGS);
        if (canvasSettings != null) {
            setShowGrid(canvasSettings.gridOn);
            Hub.getUserInterface().getZoomControl().setZoom(canvasSettings.zoom);
        } else {
            setShowGrid(false);
            Hub.getUserInterface().getZoomControl().setZoom(1);
        }
        // if the scale factor isn't set properly before adding the GraphView to
        // the
        // scroll pane, auto-scroll to the position stored in canvas settings
        // won't work
        scaleFactor = Hub.getUserInterface().getZoomControl().getZoom();
        uiInteraction = false;
        Hub.getUserInterface().getFontSelector().setFontSize(graphModel.getFontSize());
    }

    @Override
    public void release() {
        clearCurrentSelection();
        if (graphModel != null) {
            canvasSettings = new CanvasSettings();
            canvasSettings.gridOn = getShowGrid();
            canvasSettings.viewport = getVisibleRect();
            canvasSettings.zoom = scaleFactor;
            graphModel.setAnnotation(CANVAS_SETTINGS, canvasSettings);
        }
        super.release();
    }

    protected boolean uiInteraction = false;

    public void startUIInteraction() {
        uiInteraction = true;
    }

    private class GraphSelectionCopyAction extends AbstractAction {
        public void actionPerformed(ActionEvent arg0) {
            SelectionGroup selection = getSelectedGroup().copy();
            addChildrenToSelection(selection);
            FSAGraph graph = new FSAGraph(ModelManager.instance().createModel(FSAModel.class));
            // paste the selection into intermediary FSAGraph to break link to
            // original, then put the new selection on the clipboard
            selection = paste(graph, selection, false);
            Clipboard clipboard = Hub.getCopyPasteManager().getClipboard();
            clipboard.setContents(selection, GraphDrawingView.this);
        }
    }

    private class GraphSelectionCutAction extends AbstractAction {
        public void actionPerformed(ActionEvent arg0) {
            SelectionGroup selection = getSelectedGroup().copy();
            addChildrenToSelection(selection);
            // clear the selection so that when undoing the cut, some parts
            // aren't still selected
            clearCurrentSelection();
            FSAGraph graph = new FSAGraph(ModelManager.instance().createModel(FSAModel.class));
            // paste the selection into intermediary FSAGraph to break link to
            // original, then put the new selection on the clipboard
            SelectionGroup newSelection = paste(graph, selection, false);
            Clipboard clipboard = Hub.getCopyPasteManager().getClipboard();
            clipboard.setContents(newSelection, GraphDrawingView.this);
            CompoundEdit allEdits = new CompoundEdit();
            new GraphActions.RemoveAction(allEdits, getGraphModel(), selection).execute();

            // show something useful in edit menu for undo/redo
            allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(Hub.string("cut")));

            allEdits.end();
            Hub.getUndoManager().addEdit(allEdits);

        }
    }

    protected final static int pasteOffsetIncrease = 20;

    /**
     * The amount the pasted selection is offset from the original selection.
     * Increased by pasetOffsetIncrease every time the selection is pasted until
     * there is a new item on the clipboard (@see newItemOnClipboard)
     */
    protected int pasteOffset = pasteOffsetIncrease;

    protected class GraphSelectionPasteAction extends AbstractAction {

        public void actionPerformed(ActionEvent arg0) {

            Transferable clipboardContent = Hub.getCopyPasteManager().getClipboard().getContents(GraphDrawingView.this);
            if (clipboardContent != null
                    && clipboardContent.isDataFlavorSupported(SelectionGroup.selectionGroupFlavor)) {
                SelectionGroup clipboardSelection = null;
                try {
                    clipboardSelection = (SelectionGroup) clipboardContent
                            .getTransferData(SelectionGroup.selectionGroupFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                if (clipboardSelection.hasChildren()) {
                    SelectionGroup newSelection = paste(getGraphModel(), clipboardSelection, true);
                    clearCurrentSelection();
                    newSelection.translate(pasteOffset, pasteOffset);
                    pasteOffset += pasteOffsetIncrease;
                    newSelection.setHighlighted(true);
                    setSelectedGroup(newSelection);
                    /*
                     * // to group the newly created elements so that they are all // selected and
                     * movable together when pasted SelectionGroup newSelection = new
                     * SelectionGroup(); // map from the original node to the new node for creating
                     * // the new edges HashMap<Node, Node> nodes = new HashMap<Node, Node>();
                     * Node[] nodeBuffer = new Node[1]; Edge[] edgeBuffer = new Edge[1];
                     * SupervisoryEvent[] eventBuffer = new SupervisoryEvent[1]; CompoundEdit
                     * allEdits = new CompoundEdit(); GraphElement currElement; Node oldNode,
                     * newNode; Edge oldEdge, newEdge; SupervisoryEvent newEvent; FSAState oldState,
                     * newState; // iterate through children once for the nodes, then again // for
                     * the edges (can't create new edges without new nodes) for
                     * (Iterator<GraphElement> i = clipboardSelection .children(); i.hasNext();) {
                     * currElement = i.next(); if (currElement instanceof Node) { oldNode =
                     * (Node)currElement; new GraphActions.CreateNodeAction( allEdits,
                     * GraphDrawingView.this.getGraphModel(), (Point2D.Float)oldNode
                     * .getLayout().getLocation(), nodeBuffer).execute(); newNode = nodeBuffer[0];
                     * // a little clunky since some with states and some // with nodes, but without
                     * making changes to Node // that's how it works newState = newNode.getState();
                     * oldState = oldNode.getState(); // needs to be done on the node to "active"
                     * initial // arrow newNode.setInitial(oldState.isInitial()); if
                     * (newNode.getState().isInitial()) {
                     * newNode.getInitialArrow().setDirection(oldNode
                     * .getInitialArrow().getDirection()); }
                     * newState.setMarked(oldState.isMarked()); if (oldState.getName() != null) {
                     * getGraphModel().labelNode(newNode, oldState.getName()); }
                     * newSelection.insert(newNode); nodes.put(oldNode, newNode); } } // iterate
                     * again for the edges for (Iterator<GraphElement> i = clipboardSelection
                     * .children(); i.hasNext();) { currElement = i.next(); if (currElement
                     * instanceof Edge) { // initial arrow was already created when the new // node
                     * was set to initial, and without this // continue, the create edge action
                     * throws a cannot // redo exception since the source of the edge is // null if
                     * (currElement instanceof InitialArrow) { continue; } oldEdge =
                     * (Edge)currElement; // figure out whether equivalent event(s) to those // on
                     * this edge exist in the model being pasted // into, if not, create them
                     * HashSet<SupervisoryEvent> eventsToAddToEdge = new
                     * HashSet<SupervisoryEvent>(); for (Iterator<FSATransition> it = oldEdge
                     * .getTransitions(); it.hasNext();) { FSATransition t = it.next();
                     * SupervisoryEvent e = null; if (t.getEvent() != null) { e = t.getEvent(); } //
                     * potential event with same name that already // exists in the model to be
                     * pasted into SupervisoryEvent equivEvent = null; for
                     * (Iterator<SupervisoryEvent> ie = getGraphModel()
                     * .getModel().getEventIterator(); ie .hasNext();) { SupervisoryEvent modelEvent
                     * = ie.next(); if (modelEvent.equals(e)) { equivEvent = modelEvent; } } if
                     * (equivEvent != null) { eventsToAddToEdge.add(equivEvent); } else if (e !=
                     * null) { eventBuffer = new SupervisoryEvent[1]; new
                     * GraphActions.CreateEventAction( allEdits, getGraphModel(), e.getSymbol(),
                     * e.isControllable(), e.isObservable(), eventBuffer).execute(); newEvent =
                     * eventBuffer[0]; eventsToAddToEdge.add(newEvent); } } // create the new edge
                     * here (as opposed to above // finding the transitions) so that undo works //
                     * properly. If creating a new event is last, it // would be undone first, and
                     * removing the event // deletes associated transitions and possibly edges //
                     * which throws exceptions when it comes time to // undo creating those. new
                     * GraphActions.CreateEdgeAction( allEdits, getGraphModel(),
                     * nodes.get(oldEdge.getSourceNode()), nodes.get(oldEdge.getTargetNode()),
                     * edgeBuffer).execute(); newEdge = edgeBuffer[0]; getGraphModel()
                     * .replaceEventsOnEdge(eventsToAddToEdge .toArray(new SupervisoryEvent[0]),
                     * newEdge); // preserve the layout of Bezier edges. // no need to make these
                     * explicitly undoable, // since the edge itself is modified, and will // be
                     * deleted/revived as needed. if (oldEdge instanceof ReflexiveEdge) {
                     * ReflexiveLayout newLayout = ((ReflexiveLayout)((ReflexiveEdge)oldEdge)
                     * .getLayout()).clone(); newLayout.setEdge((ReflexiveEdge)newEdge);
                     * newEdge.setLayout(newLayout); } else if (oldEdge instanceof BezierEdge) {
                     * BezierLayout newLayout = ((BezierLayout)((BezierEdge)oldEdge)
                     * .getLayout()).clone(); newLayout.setEdge((BezierEdge)newEdge);
                     * newEdge.setLayout(newLayout); } newSelection.insert(newEdge); } } // show
                     * something useful in edit menu for undo/redo allEdits.addEdit(new
                     * GraphUndoableEdits.UndoableDummyLabel( Hub.string("paste"))); allEdits.end();
                     * Hub.getUndoManager().addEdit(allEdits); // TODO have the pasted selection
                     * appear in the top left // corner? newSelection.translate(20, 20);
                     * newSelection.setHighlighted(true); clearCurrentSelection();
                     * setSelectedGroup(newSelection); }
                     */
                } else
                // for information/debug purposes, remove when complete
                {
                    System.out.println("something's up with the clipboard contents..." + "is it null?: "
                            + (clipboardContent == null) + ".");
                    if (clipboardContent != null) {
                        System.out.println("is the data flavor supported?: "
                                + (clipboardContent.isDataFlavorSupported(SelectionGroup.selectionGroupFlavor)) + ". ");
                        DataFlavor[] flavours = clipboardContent.getTransferDataFlavors();
                        System.out.println("the available dataflavors are: ");
                        for (int i = 0; i < flavours.length; i++) {
                            System.out.println(i + flavours[i].getHumanPresentableName());
                        }
                    }

                }
            }

        }

    }

    /**
     * Pastes a selection into the given graph.
     * 
     * @param graph     the FSAGraph to be pasted into
     * @param selection the SelectionGroup to be pasted
     * @param undoable  whether or not the edits should be added to the undo stack.
     * @return the new equivalent SelectionGroup in the given FSAGraph.
     */
    protected SelectionGroup paste(FSAGraph graph, SelectionGroup selection, boolean undoable) {

        // to group the newly created elements so that they are all
        // selected and movable together when pasted
        SelectionGroup newSelection = new SelectionGroup();
        // map from the original node to the new node for creating
        // the new edges
        HashMap<Node, Node> nodes = new HashMap<Node, Node>();
        Node[] nodeBuffer = new Node[1];
        Edge[] edgeBuffer = new Edge[1];
        SupervisoryEvent[] eventBuffer = new SupervisoryEvent[1];
        CompoundEdit allEdits = new CompoundEdit();
        GraphElement currElement;
        Node oldNode, newNode;
        Edge oldEdge, newEdge;
        SupervisoryEvent newEvent;
        FSAState oldState, newState;
        // iterate through children once for the nodes, then again
        // for the edges (can't create new edges without new nodes)
        for (Iterator<GraphElement> i = selection.children(); i.hasNext();) {
            currElement = i.next();
            if (currElement instanceof Node) {
                oldNode = (Node) currElement;
                new GraphActions.CreateNodeAction(allEdits, graph, (Point2D.Float) oldNode.getLayout().getLocation(),
                        nodeBuffer).execute();

                newNode = nodeBuffer[0];
                // a little clunky since some with states and some
                // with nodes, but without making changes to Node
                // that's how it works
                newState = newNode.getState();
                oldState = oldNode.getState();
                // needs to be done on the node to "active" initial
                // arrow
                newNode.setInitial(oldState.isInitial());
                if (newNode.getState().isInitial()) {
                    newNode.getInitialArrow().setDirection(oldNode.getInitialArrow().getDirection());
                }

                newState.setMarked(oldState.isMarked());
                if (oldState.getName() != null) {
                    graph.labelNode(newNode, oldState.getName());
                }

                newSelection.insert(newNode);
                nodes.put(oldNode, newNode);

            }

        }

        // iterate again for the edges
        for (Iterator<GraphElement> i = selection.children(); i.hasNext();) {
            currElement = i.next();
            if (currElement instanceof Edge) {
                // initial arrow was already created when the new
                // node was set to initial, and without this
                // continue, the create edge action throws a cannot
                // redo exception since the source of the edge is
                // null
                if (currElement instanceof InitialArrow) {
                    continue;
                }
                oldEdge = (Edge) currElement;

                // figure out whether equivalent event(s) to those
                // on this edge exist in the model being pasted
                // into, if not, create them
                HashSet<SupervisoryEvent> eventsToAddToEdge = new HashSet<SupervisoryEvent>();
                for (Iterator<FSATransition> it = oldEdge.getTransitions(); it.hasNext();) {
                    FSATransition t = it.next();
                    SupervisoryEvent e = null;

                    if (t.getEvent() != null) {
                        e = t.getEvent();
                    }
                    // potential event with same name that already
                    // exists in the model to be pasted into
                    SupervisoryEvent equivEvent = null;
                    for (Iterator<SupervisoryEvent> ie = graph.getModel().getEventIterator(); ie.hasNext();) {
                        SupervisoryEvent modelEvent = ie.next();
                        if (modelEvent.equals(e)) {
                            equivEvent = modelEvent;
                        }
                    }

                    if (equivEvent != null) {
                        eventsToAddToEdge.add(equivEvent);
                    } else if (e != null) {
                        eventBuffer = new SupervisoryEvent[1];

                        new GraphActions.CreateEventAction(allEdits, graph, e.getSymbol(), e.isControllable(),
                                e.isObservable(), eventBuffer).execute();

                        newEvent = eventBuffer[0];
                        eventsToAddToEdge.add(newEvent);
                    }
                }

                // create the new edge here (as opposed to above
                // finding the transitions) so that undo works
                // properly. If creating a new event is last, it
                // would be undone first, and removing the event
                // deletes associated transitions and possibly edges
                // which throws exceptions when it comes time to
                // undo creating those.

                new GraphActions.CreateEdgeAction(allEdits, graph, nodes.get(oldEdge.getSourceNode()),
                        nodes.get(oldEdge.getTargetNode()), edgeBuffer).execute();

                newEdge = edgeBuffer[0];

                graph.replaceEventsOnEdge(eventsToAddToEdge.toArray(new SupervisoryEvent[0]), newEdge);

                // preserve the layout of Bezier edges.
                // no need to make these explicitly undoable,
                // since the edge itself is modified, and will
                // be deleted/revived as needed.
                if (oldEdge instanceof ReflexiveEdge) {
                    ReflexiveLayout newLayout = ((ReflexiveLayout) ((ReflexiveEdge) oldEdge).getLayout()).clone();
                    newLayout.setEdge((ReflexiveEdge) newEdge);
                    newEdge.setLayout(newLayout);
                } else if (oldEdge instanceof BezierEdge) {
                    BezierLayout newLayout = ((BezierLayout) ((BezierEdge) oldEdge).getLayout()).clone();
                    newLayout.setEdge((BezierEdge) newEdge);
                    newEdge.setLayout(newLayout);
                }

                newSelection.insert(newEdge);
            }
        }

        // show something useful in edit menu for undo/redo
        allEdits.addEdit(new GraphUndoableEdits.UndoableDummyLabel(Hub.string("paste")));
        allEdits.end();
        if (undoable) {
            Hub.getUndoManager().addEdit(allEdits);
        }

        // TODO have the pasted selection appear in the top left
        // corner?
        return newSelection;

    }

    /**
     * <p>
     * Iterates through the elements in <code>selection</code> and for all nodes
     * contained, iterates over adjacent edges, and for any BezierEdges, if both the
     * source and target nodes of the edge are in the selection, the edge is added
     * to the selection.
     * </p>
     * <p>
     * This method was added to address the fact that when BeizerEdges are curved,
     * the size of the handles can get large and if they are not completely
     * contained in the selection rectangle, the edge otherwise wouldn't be
     * contained in the selection group. It keeps the selection more in line with
     * the highlighted selection, though not identical
     * </p>
     * 
     * @param selection the SelectionGroup under consideration
     */
    protected void addChildrenToSelection(SelectionGroup selection) {
        HashSet<GraphElement> elementsToAdd = new HashSet<GraphElement>();
        for (Iterator<GraphElement> i = selection.children(); i.hasNext();) {
            GraphElement currElement = i.next();
            if (currElement instanceof Node) {
                Node node = (Node) currElement;
                for (Iterator<Edge> i2 = node.adjacentEdges(); i2.hasNext();) {
                    Edge edge = (Edge) i2.next();
                    // make sure not to mess this up with initial arrows?
                    if (edge instanceof BezierEdge)// ignores initial
                    // arrows
                    {
                        BezierEdge nodeEdge = (BezierEdge) edge;
                        if (selection.contains(nodeEdge.getSourceNode())
                                && selection.contains(nodeEdge.getTargetNode())) {
                            elementsToAdd.add(nodeEdge);
                        }

                    }

                }
            }
        }
        for (GraphElement e : elementsToAdd) {
            selection.insert(e);
        }
    }

    protected void CCPStatusChanged() {
        Hub.getCopyPasteManager().refresh();
    }

    public Action getCopyAction() {
        return new GraphSelectionCopyAction();
    }

    public Action getCutAction() {
        return new GraphSelectionCutAction();
    }

    public boolean isCutCopyEnabled() {
        if (hasSelection()) {
            return true;
        }
        return false;
    }

    public boolean isPasteEnabled() {
        // if (CopyPasteBackend
        // .getClipboard()
        // .isDataFlavorAvailable(SelectionGroup.selectionGroupFlavor))
        // {
        // return true;
        // }
        // return false;
        return FSAPasteHandler.isPasteEnabled();
    }

    public Action getPasteAction() {
        // return new GraphSelectionPasteAction();
        return FSAPasteHandler.getPasteAction();
    }

    public void newItemOnClipboard() {
        pasteOffset = pasteOffsetIncrease;
    }

    public void lostOwnership(Clipboard arg0, Transferable arg1) {
        // do nothing
    }
}
