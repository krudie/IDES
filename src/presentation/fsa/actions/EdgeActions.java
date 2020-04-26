package presentation.fsa.actions;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import ides.api.model.supeventset.SupervisoryEvent;
import presentation.GraphicalLayout;
import presentation.fsa.BezierLayout;
import presentation.fsa.Edge;
import presentation.fsa.FSAGraph;
import presentation.fsa.FSAGraphMessage;

/**
 * This class holds static commands that can perform actions over edges. All the
 * actions that can be undone, follows the "Swing way" and the "Command" Design
 * Pattern. When the user wants to execute an action (e.g.: by clicking a
 * button), an <code>AbstractAction</code> encapsulates an UndoableAction that
 * knows how to undo/redo the action. The abstract action also reports a manager
 * about an undoable action everytime such an action is taken. So everytime an
 * action that can be undone is executed, two steps follows the user's request:
 * 1- One <code>AbstractAction</code> executes an
 * <action>UndoableAction</action> that can redo\/undo the the desired action.
 * 2- A code inside the AbstractAction, notifies the UndoManager in the
 * CommandManager about a performed undoable action. One of the reasons for
 * making an UndoableAction be called by an AbstractAction (instead of making
 * the action be an AbstractAction AND an UndoableAction) is the fact that by
 * doing this, one AbstractAction could encapsulate several UndoableActions
 * generating a "composite" UndoableAction. So according to the chosen design,
 * every UndoableAction should, in fact, be atomic, so that big undoable actions
 * can be made by composing smaller UndobleActions. It is simpler (in my opinion
 * (Christian)), to have always the job done by two simple classes (one to
 * instanciate an UndoableAction and update to the CommandManager about the
 * action, and other being UndoableAction itself rather than having one classe
 * inheriting AbstractAction and extending UndoableCommand. Having everything in
 * just one class would make this class be too big, more difficult to write and
 * also, less usable.
 * 
 * @author Christian Silvano
 */
public class EdgeActions {

    public static class LabelAction extends AbstractGraphAction {
        /**
         * 
         */
        private static final long serialVersionUID = -6195557069948251536L;

        protected Vector<SupervisoryEvent> assignedEvents = null;

        protected Edge edge;

        protected FSAGraph graph;

        public LabelAction(FSAGraph graph, Edge edge, Vector<SupervisoryEvent> assignedEvents) {
            this(null, graph, edge, assignedEvents);
        }

        public LabelAction(CompoundEdit parentEdit, FSAGraph graph, Edge edge,
                Vector<SupervisoryEvent> assignedEvents) {
            this.graph = graph;
            this.edge = edge;
            this.assignedEvents = assignedEvents;
            if (this.assignedEvents == null) {
                this.assignedEvents = new Vector<SupervisoryEvent>();
            }
            this.parentEdit = parentEdit;
        }

        public void actionPerformed(ActionEvent event) {
            if (edge != null) {
                GraphUndoableEdits.UndoableEdgeLabel action = new GraphUndoableEdits.UndoableEdgeLabel(graph, edge,
                        assignedEvents);
                action.redo();
                postEditAdjustCanvas(graph, action);
            }
        }
    }

    /**
     * This method is executed every-time one of the Edge's control points is
     * edited.
     * 
     * @author christiansilvano
     */
    public static class ModifyAction extends AbstractGraphAction {

        /**
         * 
         */
        private static final long serialVersionUID = -5898215625745787054L;

        protected Edge edge;

        protected FSAGraph graph;

        protected GraphicalLayout originalLayout;

        public ModifyAction(FSAGraph graph, Edge edge, GraphicalLayout originalLayout) {
            this(null, graph, edge, originalLayout);
        }

        public ModifyAction(CompoundEdit parentEdit, FSAGraph graph, Edge edge, GraphicalLayout originalLayout) {
            this.graph = graph;
            this.edge = edge;
            this.parentEdit = parentEdit;
            this.originalLayout = originalLayout;
        }

        /**
         * Instantiate the UndoableAction of interest, sending references to the Edge,
         * the GraphDrawingView * and the backup for the layout of the edge. * Reports
         * the UndoableAction to the CommandManager.
         */
        public void actionPerformed(ActionEvent evt) {
            if (graph != null) {
                UndoableEdit edit = new GraphUndoableEdits.UndoableModifyEdge(graph, edge, originalLayout);
                // no need to "redo" the edit since the edge has already been
                // modified
                postEditAdjustCanvas(graph, edit);
                graph.fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.EDGE,
                        edge.getId(), edge.bounds(), graph, "reshape edge"));
            }
        }
    }

    public static class SymmetrizeAction extends AbstractGraphAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1086822671377848158L;

        protected Edge edge;

        protected FSAGraph graph;

        protected BezierLayout originalLayout;

        public SymmetrizeAction(FSAGraph graph, Edge edge) {
            this(null, graph, edge);
        }

        public SymmetrizeAction(CompoundEdit parentEdit, FSAGraph graph, Edge edge) {
            this.graph = graph;
            this.edge = edge;
            this.parentEdit = parentEdit;
        }

        /**
         * Instantiate the UndoableAction of interest, sending references to the Edge,
         * the GraphDrawingView * and the backup for the layout of the edge. * Reports
         * the UndoableAction to the CommandManager.
         */
        public void actionPerformed(ActionEvent evt) {
            if (graph != null && edge != null) {
                originalLayout = ((BezierLayout) edge.getLayout()).clone();
                graph.symmetrize(edge);
                UndoableEdit edit = new GraphUndoableEdits.UndoableModifyEdge(graph, edge, originalLayout);
                // no need to "redo" the edit since the edge has already been
                // modified
                postEditAdjustCanvas(graph, edit);
            }
        }
    }

    public static class StraightenAction extends AbstractGraphAction {

        /**
         * 
         */
        private static final long serialVersionUID = -8600716233449696165L;

        protected Edge edge;

        protected FSAGraph graph;

        protected BezierLayout originalLayout;

        public StraightenAction(FSAGraph graph, Edge edge) {
            this(null, graph, edge);
        }

        public StraightenAction(CompoundEdit parentEdit, FSAGraph graph, Edge edge) {
            this.graph = graph;
            this.edge = edge;
            this.parentEdit = parentEdit;
        }

        /**
         * Instantiate the UndoableAction of interest, sending references to the Edge,
         * the GraphDrawingView * and the backup for the layout of the edge. * Reports
         * the UndoableAction to the CommandManager.
         */
        public void actionPerformed(ActionEvent evt) {
            if (graph != null && edge != null) {
                originalLayout = ((BezierLayout) edge.getLayout()).clone();
                graph.straighten(edge);
                UndoableEdit edit = new GraphUndoableEdits.UndoableModifyEdge(graph, edge, originalLayout);
                // no need to "redo" the edit since the edge has already been
                // modified
                postEditAdjustCanvas(graph, edit);
            }
        }
    }

    public static class ArcMoreAction extends AbstractGraphAction {

        /**
         * 
         */
        private static final long serialVersionUID = -4912990740327535014L;

        protected Edge edge;

        protected FSAGraph graph;

        protected BezierLayout originalLayout;

        public ArcMoreAction(FSAGraph graph, Edge edge) {
            this(null, graph, edge);
        }

        public ArcMoreAction(CompoundEdit parentEdit, FSAGraph graph, Edge edge) {
            this.graph = graph;
            this.edge = edge;
            this.parentEdit = parentEdit;
        }

        /**
         * Instantiate the UndoableAction of interest, sending references to the Edge,
         * the GraphDrawingView * and the backup for the layout of the edge. * Reports
         * the UndoableAction to the CommandManager.
         */
        public void actionPerformed(ActionEvent evt) {
            if (graph != null && edge != null) {
                originalLayout = ((BezierLayout) edge.getLayout()).clone();
                graph.arcMore(edge);
                UndoableEdit edit = new GraphUndoableEdits.UndoableModifyEdge(graph, edge, originalLayout);
                // no need to "redo" the edit since the edge has already been
                // modified
                postEditAdjustCanvas(graph, edit);
            }
        }
    }

    public static class ArcLessAction extends AbstractGraphAction {

        /**
         * 
         */
        private static final long serialVersionUID = 4607148428978393473L;

        protected Edge edge;

        protected FSAGraph graph;

        protected BezierLayout originalLayout;

        public ArcLessAction(FSAGraph graph, Edge edge) {
            this(null, graph, edge);
        }

        public ArcLessAction(CompoundEdit parentEdit, FSAGraph graph, Edge edge) {
            this.graph = graph;
            this.edge = edge;
            this.parentEdit = parentEdit;
        }

        /**
         * Instantiate the UndoableAction of interest, sending references to the Edge,
         * the GraphDrawingView * and the backup for the layout of the edge. * Reports
         * the UndoableAction to the CommandManager.
         */
        public void actionPerformed(ActionEvent evt) {
            if (graph != null && edge != null) {
                originalLayout = ((BezierLayout) edge.getLayout()).clone();
                graph.arcLess(edge);
                UndoableEdit edit = new GraphUndoableEdits.UndoableModifyEdge(graph, edge, originalLayout);
                // no need to "redo" the edit since the edge has already been
                // modified
                postEditAdjustCanvas(graph, edit);
            }
        }
    }
}
