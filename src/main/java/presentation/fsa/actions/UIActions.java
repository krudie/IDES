package presentation.fsa.actions;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.layout.FSALayoutManager;
import ides.api.plugin.layout.FSALayouter;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.Edge;
import presentation.fsa.EdgeLabellingDialog;
import presentation.fsa.FSAGraph;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.GraphElement;
import presentation.fsa.GraphLabel;
import presentation.fsa.Node;
import presentation.fsa.SelectionGroup;
import presentation.fsa.tools.CreationTool;

public class UIActions {

    /**
     * The class for that toggles grid display.
     * 
     * @author Lenko Grigorov
     */
    public static class ShowGridAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 2322557924097560254L;

        private static ImageIcon icon = new ImageIcon();

        public boolean state = false;

        protected GraphDrawingView gdv = null;

        public ShowGridAction(GraphDrawingView gdv) {
            super(Hub.string("comGrid"), icon);
            icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/view_grid.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintGrid"));
            this.gdv = gdv;
        }

        /**
         * Changes the property state.
         */
        public void actionPerformed(ActionEvent e) {
            gdv.setShowGrid(!gdv.getShowGrid());
        }
    }

    /**
     * A command to set the current drawing mode to creation mode. While in creating
     * mode, user may create new objects in the GraphDrawingView.
     * 
     * @author Helen Bretzke
     */
    public static class CreateTool extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -555982355953868129L;

        // An icon that can be used to describe this action
        private static ImageIcon icon = new ImageIcon();

        // Default constructor.
        public CreateTool() {
            super(Hub.string("comCreateTool"), icon);
            icon.setImage(
                    Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/graphic_create.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintCreateTool"));
        }

        // Switches the tool to Creating tool
        public void actionPerformed(ActionEvent event) {
            ContextAdaptorHack.context.setTool(GraphDrawingView.CREATE);
            ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.CREATE);
        }
    }

    /**
     * A command to set the current drawing mode to creating mode. While in creating
     * mode, user may create new objects in the GraphDrawingView.
     * 
     * @author Helen Bretzke
     */
    public static class MoveTool extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -5257037794586342249L;

        private static ImageIcon icon = new ImageIcon();

        public MoveTool() {
            super(Hub.string("comMoveTool"), icon);
            icon.setImage(
                    Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/graphic_move.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintMoveTool"));
        }

        // Switches the tool to Moving Tool
        public void actionPerformed(ActionEvent event) {
            ContextAdaptorHack.context.setTool(GraphDrawingView.MOVE);
            ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.MOVE);
        }
    }

    /**
     * A command to set the current drawing mode to editing mode. While in editing
     * mode, user may select graph objects in the GraphDrawingView for deleting,
     * copying, pasting and moving.
     * 
     * @author Helen Bretzke
     */
    public static class SelectTool extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 7782203298654874650L;

        // An icon that can be used to describe this action
        private static ImageIcon icon = new ImageIcon();

        // Default constructor
        public SelectTool() {
            super(Hub.string("comSelectTool"), icon);
            icon.setImage(
                    Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/graphic_modify.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintSelectTool"));
        }

        // Switches the tool to Selecting Tool.
        public void actionPerformed(ActionEvent event) {
            // TODO set the tool in the *currently active* drawing view
            ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
            ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.SELECT);
        }
    }

    /**
     * A command to set the current drawing mode to labelling mode. While in
     * labelling mode, user may label nodes and edges or create free labels.
     * 
     * @author Lenko Grigorov
     */
    public static class TextTool extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 8345246415041270237L;

        // An icon that can be used to describe this action
        private static ImageIcon icon = new ImageIcon();

        // Default constructor
        public TextTool() {
            super(Hub.string("comTextTool"), icon);
            icon.setImage(
                    Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/machine_alpha.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintTextTool"));
        }

        // Switches the tool to Selecting Tool.
        public void actionPerformed(ActionEvent event) {
            // TODO set the tool in the *currently active* drawing view
            ContextAdaptorHack.context.setTool(GraphDrawingView.SELECT);
            ContextAdaptorHack.context.setPreferredTool(GraphDrawingView.SELECT);
        }
    }

    /**
     * Represent a user issued command to delete an element of the graph. What about
     * deleting elements of a text label?
     * 
     * @author helen bretzke
     */
    public static class DeleteAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 7444510438682409608L;

        private static ImageIcon icon = new ImageIcon();

        protected GraphDrawingView context;

        private SelectionGroup selection;

        public DeleteAction(GraphDrawingView context) {
            super(Hub.string("comDelete"), icon);
            icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/edit_delete.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintDelete"));
            this.context = context;
        }

        public void actionPerformed(ActionEvent evt) {
            if (((CreationTool) context.getTools()[GraphDrawingView.CREATE]).isDrawingEdge()) {
                ((CreationTool) context.getTools()[GraphDrawingView.CREATE]).abortEdge();
            }
            selection = context.getSelectedGroup();
            new GraphActions.RemoveAction(context.getGraphModel(), selection).execute();
            context.setTool(context.getPreferredTool());
        }

    }

    public static class TextAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -1083762321834986131L;

        GraphElement element = null;

        Point2D.Float location = null;

        // An icon that can be used to describe this action
        private static ImageIcon icon = new ImageIcon();

        // Default constructor
        protected TextAction() {
            super(Hub.string("comLabel"), icon);
            icon.setImage(
                    Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/machine_alpha.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintLabel"));
        }

        public TextAction(Node n) {
            super(Hub.string("comLabelNode"), icon);
            icon.setImage(
                    Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/machine_alpha.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintLabelNode"));
            element = n;
        }

        public TextAction(Edge e) {
            super(Hub.string("comLabelEdge"), icon);
            icon.setImage(
                    Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/machine_alpha.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintLabelEdge"));
            element = e;
        }

        public TextAction(GraphElement currentSelection) {
            this();
            this.element = currentSelection;
        }

        /**
         * @param location
         */
        public TextAction(Point location) {
            this();
            this.location = new Point2D.Float(location.x, location.y);
        }

        public void setElement(GraphElement element) {
            this.element = element;
        }

        public void actionPerformed(ActionEvent event) {
            if (element == null) {
                // create a new free label
                // TODO uncomment the following statement when finished
                // implementing
                // saving and loading free labels to file.
                /*
                 * presentation.fsa.SingleLineFreeLabellingDialog.showAndLabel(
                 * context.getGraphModel(), location);
                 */
            } else {
                // KLUGE: instanceof is rotten style, fix this
                if (element instanceof Node) {
                    Node node = (Node) element;
                    // if selection is a node
                    presentation.fsa.SingleLineNodeLabellingDialog.showAndLabel(ContextAdaptorHack.context,
                            ContextAdaptorHack.context.getGraphModel(), node);
                } else if (element instanceof Edge) {
                    Edge edge = (Edge) element;
                    EdgeLabellingDialog.showDialog(ContextAdaptorHack.context, edge);
                    // new EdgeCommands.CreateEventCommand(,edge).execute();
                    // EdgeLabellingDialog.showDialog(ContextAdaptorHack.context,
                    // edge);
                    // TODO accumulate set of edits that were performed in the
                    // edge
                    // labelling dialog
                } else if (element instanceof GraphLabel && element.getParent() instanceof Edge) {
                    Edge edge = (Edge) element.getParent();
                    EdgeLabellingDialog.showDialog(ContextAdaptorHack.context, edge);
                    // new
                    // EdgeCommands.CreateEventCommand(ContextAdaptorHack.context
                    // ,edge).execute();
                    // EdgeLabellingDialog.showDialog(ContextAdaptorHack.context,
                    // edge);
                } else {
                    // TODO uncomment the following statement when finished
                    // implementing
                    // saving and loading free labels to file.
                    /*
                     * presentation.fsa.SingleLineFreeLabellingDialog.showAndLabel (
                     * context.getGraphModel(), (GraphLabel)element);
                     */
                }
                ContextAdaptorHack.context.repaint();
            }
            element = null;
        }

        public void execute() {
            actionPerformed(null);
        }
    }

    /**
     * A command that creates a reflexive edge on a node.
     * 
     * @author helen bretzke
     */

    public static class SelfLoopAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = -512437464224438187L;

        protected Node node;

        protected FSAGraph graph;

        public SelfLoopAction(FSAGraph graph, Node node) {
            super(Hub.string("comAddSelfloop"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintAddSelfloop"));
            this.node = node;
            this.graph = graph;
        }

        public void actionPerformed(ActionEvent e) {
            new GraphActions.CreateEdgeAction(graph, node, node).execute();
        }
    }

    /**
     * A command that creates an UndoableAction that sets the value of a boolean
     * attribute for a DES element.
     * 
     * @author Christian Silvano
     */
    public static class ModifyInitialAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -6713385734543186940L;

        protected Node node;

        protected FSAGraph graph;

        public ModifyInitialAction(FSAGraph graph, Node node) {
            super(Hub.string("comInitialNode"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintInitialNode"));
            this.node = node;
            this.graph = graph;
        }

        public void actionPerformed(ActionEvent e) {
            new NodeActions.SetInitialAction(graph, node, !node.getState().isInitial()).execute();
        }

    }

    /**
     * A command that creates an UndoableAction to set the value of a boolean
     * attribute for a DES element.
     * 
     * @author Christian Silvano
     */
    public static class ModifyMarkingAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = -7614527071380920315L;

        protected Node node;

        protected FSAGraph graph;

        public ModifyMarkingAction(FSAGraph graph, Node node) {
            super(Hub.string("comMarkedNode"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintMarkedNode"));
            this.node = node;
            this.graph = graph;
        }

        public void actionPerformed(ActionEvent e) {
            new NodeActions.SetMarkingAction(graph, node, !node.getState().isMarked()).execute();
        }

    }

    /**
     * If this edge is not straight, make it have a symmetrical appearance. Make the
     * two vectors - from P1 to CTRL1 and from P2 to CTRL2, be of the same length
     * and have the same angle. So the edge will look it has a symmetrical curve.
     * There are two cases: The 2 control points are on the same side of the curve
     * (a curve with the form of a bow); and the 2 control points are on different
     * sides of the edge (a curve like a wave). In one of the cases, theangles of
     * the vectors should be A=B, in the other A=-B.
     */
    public static class SymmetrizeEdgeAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 5249370980521438496L;

        protected FSAGraph graph;

        protected Edge edge;

        public SymmetrizeEdgeAction(FSAGraph graph, Edge edge) {
            super(Hub.string("comSymmetrizeEdge"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintSymmetrizeEdge"));
            this.edge = edge;
            this.graph = graph;
        }

        public void actionPerformed(ActionEvent evt) {
            new EdgeActions.SymmetrizeAction(graph, edge).execute();
        }
    }

    public static class StraightenEdgeAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 4118559208669733440L;

        protected FSAGraph graph;

        protected Edge edge;

        public StraightenEdgeAction(FSAGraph graph, Edge edge) {
            super(Hub.string("comStraightenEdge"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintStraightenEdge"));
            this.edge = edge;
            this.graph = graph;
        }

        public void actionPerformed(ActionEvent evt) {
            new EdgeActions.StraightenAction(graph, edge).execute();
        }
    }

    public static class ArcMoreEdgeAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = -1162794610613928972L;

        protected FSAGraph graph;

        protected Edge edge;

        public ArcMoreEdgeAction(FSAGraph graph, Edge edge) {
            super(Hub.string("comArcMoreEdge"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintArcMoreEdge"));
            this.edge = edge;
            this.graph = graph;
        }

        public void actionPerformed(ActionEvent evt) {
            new EdgeActions.ArcMoreAction(graph, edge).execute();
        }
    }

    public static class ArcLessEdgeAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 4665577899493155872L;

        protected FSAGraph graph;

        protected Edge edge;

        public ArcLessEdgeAction(FSAGraph graph, Edge edge) {
            super(Hub.string("comArcLessEdge"));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintArcLessEdge"));
            this.edge = edge;
            this.graph = graph;
        }

        public void actionPerformed(ActionEvent evt) {
            new EdgeActions.ArcLessAction(graph, edge).execute();
        }
    }

    /**
     * Emulates "snap to grid".
     * 
     * @author Lenko Grigorov, Christian Silvano
     */
    public static class AlignAction extends AbstractAction {
        private static final long serialVersionUID = -9179684732457960463L;

        private static ImageIcon icon = new ImageIcon();

        protected GraphDrawingView gdv;

        public AlignAction(GraphDrawingView gdv) {
            super(Hub.string("comAlign"), icon);
            icon.setImage(
                    Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/graphic_align.gif")));
            putValue(SHORT_DESCRIPTION, Hub.string("comHintAlign"));
            this.gdv = gdv;
        }

        public void actionPerformed(ActionEvent event) {
            if (gdv == null) {
                return;
            }

            SelectionGroup group = new SelectionGroup();
            for (Iterator<GraphElement> i = gdv.getGraphModel().children(); i.hasNext();) {
                group.insert(i.next());
            }
            new GraphActions.AlignNodesAction(gdv.getGraphModel(), group).execute();

        }
    }

    /**
     * Sets the default {@link FSALayouter} and lays out the FSA graph using it.
     * 
     * @author Lenko Grigorov
     */
    public static class SelectLayoutAction extends AbstractAction {
        private static final long serialVersionUID = 4113413289499126023L;

        protected FSAGraph graph;

        protected FSALayouter layouter;

        public SelectLayoutAction(FSAGraph graph, FSALayouter layouter) {
            super(layouter == null ? "" : layouter.getName());
            this.graph = graph;
            this.layouter = layouter;
        }

        public void actionPerformed(ActionEvent event) {
            if (graph == null || layouter == null) {
                return;
            }
            FSALayoutManager.instance().setDefaultLayouter(layouter);
            new GraphActions.LayoutAction(graph, layouter).execute();
        }
    }

    public static class DuplicateModelAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -4284838639085719455L;

        public DuplicateModelAction() {
            super(Hub.string("comDuplicateModel"), new ImageIcon());
            putValue(SHORT_DESCRIPTION, Hub.string("comHintDuplicateModel"));
        }

        public void actionPerformed(ActionEvent arg0) {
            FSAModel model = (FSAModel) Hub.getWorkspace().getActiveModel();
            FSAModel clone = model.clone();
            clone.setName(model.getName());
            Hub.getWorkspace().addModel(clone);

            // if someone wanted to duplicate the default Untitled model before
            // having made any changed (just in case...)
            // without this it closes the default Untitled model when adding a
            // new model so you get the screen of no model open with the clone
            // in the filmstrip
            if (Hub.getWorkspace().size() <= 1) {
                Hub.getWorkspace().setActiveModel(clone.getName());
            }

        }

    }

}
