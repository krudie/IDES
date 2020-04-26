package presentation.fsa.actions;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAMessage;
import ides.api.model.fsa.FSAModel;
import ides.api.model.fsa.FSATransition;
import ides.api.model.supeventset.SupervisoryEvent;
import presentation.GraphicalLayout;
import presentation.fsa.Edge;
import presentation.fsa.FSAGraph;
import presentation.fsa.FSAGraphMessage;
import presentation.fsa.GraphElement;
import presentation.fsa.GraphLabel;
import presentation.fsa.InitialArrow;
import presentation.fsa.Node;

public class GraphUndoableEdits {

    public static class UndoableDummyLabel extends AbstractUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 4900035740928121027L;

        String label = "";

        public UndoableDummyLabel(String label) {
            this.label = label;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            return label;
        }
    }

    public static class UndoableLabel extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 6648771210676870307L;

        protected String text = null;

        protected String originalText = null;

        protected GraphElement element;

        protected FSAGraph graph;

        public UndoableLabel(FSAGraph graph, GraphElement element, String text) {
            this.element = element;
            this.text = text;
            this.graph = graph;
        }

        @Override
        public void undo() throws CannotUndoException {
            if (originalText == null) {
                throw new CannotUndoException();
            }
            if (element instanceof Node) {
                text = ((Node) element).getLabel().getText();
                graph.labelNode((Node) element, originalText);
            } else if (element instanceof GraphLabel) {
                text = ((GraphLabel) element).getText();
                graph.setLabelText((GraphLabel) element, originalText);
            }
            // else
            // {
            // //TODO add modification for free labels
            // }
            originalText = null;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (text == null) {
                throw new CannotRedoException();
            }
            if (element instanceof Node) {
                originalText = ((Node) element).getLabel().getText();
                graph.labelNode((Node) element, text);
            } else if (element instanceof GraphLabel) {
                originalText = ((GraphLabel) element).getText();
                graph.setLabelText((GraphLabel) element, text);
            }
            // else
            // {
            // //TODO add modification for free labels
            // }
            text = null;
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public boolean canRedo() {
            return true;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (element instanceof Node) {
                return Hub.string("undoLabelNode");
            } else {
                return Hub.string("undoLabel");
            }
        }
    }

    public static class UndoableEdgeLabel extends AbstractUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = -5669021233876887701L;

        protected Vector<FSATransition> newTransitions = new Vector<FSATransition>();

        protected Vector<FSATransition> originalTransitions = new Vector<FSATransition>();

        protected Edge edge;

        protected FSAGraph graph;

        protected Vector<SupervisoryEvent> assignedEvents;

        public UndoableEdgeLabel(FSAGraph graph, Edge edge, Vector<SupervisoryEvent> assignedEvents) {
            this.graph = graph;
            this.edge = edge;
            this.assignedEvents = assignedEvents;
        }

        @Override
        public void undo() throws CannotUndoException {
            if (originalTransitions == null) {
                throw new CannotUndoException();
            }
            graph.replaceTransitionsOnEdge(originalTransitions, edge);
        }

        @Override
        public void redo() throws CannotRedoException {
            if (assignedEvents != null) {
                for (Iterator<FSATransition> i = edge.getTransitions(); i.hasNext();) {
                    originalTransitions.add(i.next());
                }
                graph.replaceEventsOnEdge(assignedEvents.toArray(new SupervisoryEvent[0]), edge);
                for (Iterator<FSATransition> i = edge.getTransitions(); i.hasNext();) {
                    newTransitions.add(i.next());
                }
                assignedEvents = null;
            } else {
                graph.replaceTransitionsOnEdge(newTransitions, edge);
            }
        }

        @Override
        public boolean canUndo() {
            return originalTransitions != null;
        }

        @Override
        public boolean canRedo() {
            return true;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            return Hub.string("undoLabelEdge");
        }
    }

    /**
     * Undo/Redo a movement of a SelectionGroup (collection of graph elements) over
     * a Graph. In order to do that, the SelectionGroup and a vector representing
     * the displacement are sent in the class constructor.
     * 
     * @author Christian Silvano
     */
    public static class UndoableMove extends AbstractGraphUndoableEdit {

        /**
         * 
         */
        private static final long serialVersionUID = -7068522115195894462L;

        // A collection of graph elements
        protected GraphElement element = null;

        // A vector meaning the displacement of the selection
        protected Point2D.Float displacement;

        protected FSAGraph graph;

        /**
         * Default constructor
         * 
         * @param graph   the graph with the element
         * @param element a graph elements
         * @param d       displacement of the elements
         */
        public UndoableMove(FSAGraph graph, GraphElement element, Point2D.Float d) {
            this.graph = graph;
            this.element = element;
            displacement = (Point2D.Float) d.clone();
            if (displacement == null) {
                displacement = new Point2D.Float();
            }
        }

        /**
         * Undoes a movement by applying a vector opposite to <code>displacement</code>
         * over <code>collection</code>
         */
        @Override
        public void undo() throws CannotUndoException {
            if (element == null) {
                throw new CannotUndoException();
            }
            element.translate(-displacement.x, -displacement.y);
            graph.commitLayoutModified();
        }

        /**
         * Redoes a movement by applying a <code>displacement</code> over
         * <code>collection</code>
         */
        @Override
        public void redo() throws CannotRedoException {
            if (element == null) {
                throw new CannotRedoException();
            }
            element.translate(displacement.x, displacement.y);
            graph.commitLayoutModified();
        }

        @Override
        public boolean canUndo() {
            return element != null;
        }

        @Override
        public boolean canRedo() {
            return element != null;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (element instanceof Node) {
                if (usePluralDescription) {
                    return Hub.string("undoMoveNodes");
                } else {
                    return Hub.string("undoMoveNode");
                }
            } else if (element instanceof GraphLabel) {
                if (usePluralDescription) {
                    return Hub.string("undoMoveLabels");
                } else {
                    return Hub.string("undoMoveLabel");
                }
            } else {
                if (usePluralDescription) {
                    return Hub.string("undoMoveElements");
                } else {
                    return Hub.string("undoMoveElement");
                }
            }
        }
    }

    public static class UndoableCreateEvent extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 6944709340848440216L;

        protected SupervisoryEvent event;

        protected String eventName;

        protected boolean controllable;

        protected boolean observable;

        protected FSAGraph graph;

        public UndoableCreateEvent(FSAGraph graph, String eventName, boolean controllable, boolean observable) {
            this.eventName = eventName;
            this.controllable = controllable;
            this.observable = observable;
            this.graph = graph;
        }

        public SupervisoryEvent getEvent() {
            return event;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (eventName == null) {
                throw new CannotRedoException();
            }
            if (event == null) {
                event = graph.createAndAddEvent(eventName, controllable, observable);
            } else {
                graph.getModel().add(event);
            }
            eventName = null;
        }

        @Override
        public void undo() throws CannotUndoException {
            if (event == null) {
                throw new CannotUndoException();
            }
            graph.getModel().remove(event);
            eventName = event.getSymbol();
            controllable = event.isControllable();
            observable = event.isObservable();
        }

        @Override
        public boolean canUndo() {
            return (event != null);
        }

        @Override
        public boolean canRedo() {
            return (eventName != null);
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                return Hub.string("undoCreateEvents");
            } else {
                return Hub.string("undoCreateEvent");
            }
        }
    }

    public static class UndoableRemoveEvent extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = -3922017791552901537L;

        protected SupervisoryEvent event;

        protected FSAGraph graph;

        public UndoableRemoveEvent(FSAGraph graph, SupervisoryEvent event) {
            this.graph = graph;
            this.event = event;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (event == null) // if the event didn't exist in the model
            {
                return;
            }
            if (!graph.getModel().getEventSet().contains(event)) {
                event = null; // won't do anythin on Undo/Redo
            } else {
                graph.getModel().remove(event);
            }
        }

        @Override
        public void undo() throws CannotUndoException {
            if (event == null) // if the event didn't exist in the model, don't
            // introduce it
            {
                return;
            }
            graph.getModel().add(event);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public boolean canRedo() {
            return true;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                return Hub.string("undoRemoveEvents");
            } else {
                return Hub.string("undoRemoveEvent");
            }
        }
    }

    public static class UndoableModifyEvent extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = -7428178659902586803L;

        protected SupervisoryEvent event;

        protected String alternateName;

        protected boolean alternateControllable;

        protected boolean alternateObservable;

        protected FSAModel model;

        public UndoableModifyEvent(FSAModel model, SupervisoryEvent event, String newName, boolean newControllable,
                boolean newObservable) {
            this.model = model;
            this.event = event;
            alternateName = newName;
            alternateControllable = newControllable;
            alternateObservable = newObservable;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (event == null) {
                throw new CannotRedoException();
            }
            swapEventInfo();
        }

        @Override
        public void undo() throws CannotUndoException {
            if (event == null) {
                throw new CannotUndoException();
            }
            swapEventInfo();
        }

        protected void swapEventInfo() {
            String prevName = event.getSymbol();
            boolean prevControllable = event.isControllable();
            boolean prevObservable = event.isObservable();
            event.setSymbol(alternateName);
            event.setControllable(alternateControllable);
            event.setObservable(alternateObservable);
            alternateName = prevName;
            alternateControllable = prevControllable;
            alternateObservable = prevObservable;
            model.fireFSAEventSetChanged(new FSAMessage(FSAMessage.MODIFY, FSAMessage.EVENT, event.getId(), model));
        }

        @Override
        public boolean canUndo() {
            return event != null;
        }

        @Override
        public boolean canRedo() {
            return event != null;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                return Hub.string("undoModifyEvents");
            } else {
                return Hub.string("undoModifyEvent");
            }
        }
    }

    public static class UndoableCreateEdge extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 7532962746207475970L;

        protected FSAGraph graph;

        protected Node source;

        protected Node target;

        protected Edge edge;

        public UndoableCreateEdge(FSAGraph graph, Node source, Node target) {
            this.graph = graph;
            this.source = source;
            this.target = target;
        }

        public Edge getEdge() {
            return edge;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (source == null || target == null) {
                throw new CannotRedoException();
            }
            if (edge == null) {
                edge = graph.createEdge(source, target);
            } else {
                graph.reviveEdge(edge);
            }
        }

        @Override
        public void undo() throws CannotUndoException {
            if (edge == null) {
                throw new CannotUndoException();
            }
            graph.delete(edge);
        }

        @Override
        public boolean canUndo() {
            return (edge != null);
        }

        @Override
        public boolean canRedo() {
            return (source != null && target != null);
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                if (source == target) {
                    return Hub.string("undoCreateSelfloops");
                } else {
                    return Hub.string("undoCreateEdges");
                }
            } else {
                if (source == target) {
                    return Hub.string("undoCreateSelfloop");
                } else {
                    return Hub.string("undoCreateEdge");
                }
            }
        }
    }

    public static class UndoableDeleteEdge extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 6449615023061749553L;

        protected FSAGraph graph;

        protected Edge edge;

        public UndoableDeleteEdge(FSAGraph graph, Edge edge) {
            this.graph = graph;
            this.edge = edge;
        }

        @Override
        public void redo() throws CannotRedoException {
            graph.delete(edge);
        }

        @Override
        public void undo() throws CannotUndoException {
            graph.reviveEdge(edge);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public boolean canRedo() {
            return true;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                return Hub.string("undoDeleteEdges");
            } else {
                return Hub.string("undoDeleteEdge");
            }
        }
    }

    public static class UndoableModifyEdge extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 8422988858744584606L;

        protected FSAGraph graph;

        protected Edge edge;

        protected GraphicalLayout altLayout;

        public UndoableModifyEdge(FSAGraph graph, Edge edge, GraphicalLayout originalLayout) {
            this.graph = graph;
            this.edge = edge;
            altLayout = originalLayout;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (edge == null) {
                throw new CannotRedoException();
            }
            swapLayout();
        }

        @Override
        public void undo() throws CannotUndoException {
            if (edge == null) {
                throw new CannotUndoException();
            }
            swapLayout();
        }

        protected void swapLayout() {
            GraphicalLayout tLayout = edge.getLayout();
            edge.setLayout(altLayout);
            altLayout = tLayout;
            edge.refresh();
            graph.fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.EDGE, edge.getId(),
                    edge.bounds(), graph));
        }

        @Override
        public boolean canUndo() {
            return (edge != null);
        }

        @Override
        public boolean canRedo() {
            return (edge != null);
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                if (edge.getSourceNode() == edge.getTargetNode()) {
                    return Hub.string("undoModifySelfloops");
                } else {
                    return Hub.string("undoModifyEdges");
                }
            } else {
                if (edge.getSourceNode() == edge.getTargetNode()) {
                    return Hub.string("undoModifySelfloop");
                } else {
                    return Hub.string("undoModifyEdge");
                }
            }
        }
    }

    public static class UndoableModifyInitialArrow extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 336518328098436151L;

        protected FSAGraph graph;

        protected InitialArrow arrow;

        protected Point2D.Float altDirection;

        public UndoableModifyInitialArrow(FSAGraph graph, InitialArrow arrow, Point2D.Float originalDirection) {
            this.graph = graph;
            this.arrow = arrow;
            altDirection = originalDirection;
        }

        @Override
        public void undo() throws CannotUndoException {
            if (arrow == null) {
                throw new CannotUndoException();
            }
            swapDirection();
        }

        @Override
        public void redo() throws CannotRedoException {
            if (arrow == null) {
                throw new CannotRedoException();
            }
            swapDirection();
        }

        protected void swapDirection() {
            Point2D.Float tDirection = (Point2D.Float) arrow.getDirection().clone();
            arrow.setDirection(altDirection);
            altDirection = tDirection;
            if (arrow.getParent() != null && arrow.getParent() instanceof Node) {
                arrow.getParent().refresh();
                graph.fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.NODE,
                        arrow.getParent().getId(), arrow.getParent().bounds(), graph));
            }
        }

        @Override
        public boolean canUndo() {
            return arrow != null;
        }

        @Override
        public boolean canRedo() {
            return arrow != null;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            return Hub.string("undoModifyInitialArrow");
        }
    }

    public static class UndoableCreateNode extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 6808706248787250366L;

        protected FSAGraph graph;

        protected Point2D.Float location;

        protected Node node;

        public UndoableCreateNode(FSAGraph graph, Point2D.Float location) {
            this.graph = graph;
            this.location = location;
        }

        public Node getNode() {
            return node;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (location == null) {
                throw new CannotRedoException();
            }
            if (node == null) {
                node = graph.createNode(location);
            } else {
                graph.reviveNode(node);
            }
        }

        @Override
        public void undo() throws CannotUndoException {
            if (node == null) {
                throw new CannotUndoException();
            }
            graph.delete(node);
        }

        @Override
        public boolean canUndo() {
            return (node != null);
        }

        @Override
        public boolean canRedo() {
            return (location != null);
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                return Hub.string("undoCreateNodes");
            } else {
                return Hub.string("undoCreateNode");
            }
        }
    }

    public static class UndoableDeleteNode extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = -4680293165970601858L;

        protected FSAGraph graph;

        protected Node node;

        public UndoableDeleteNode(FSAGraph graph, Node node) {
            this.graph = graph;
            this.node = node;
        }

        @Override
        public void redo() throws CannotRedoException {
            graph.delete(node);
        }

        @Override
        public void undo() throws CannotUndoException {
            graph.reviveNode(node);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public boolean canRedo() {
            return true;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            if (usePluralDescription) {
                return Hub.string("undoDeleteNodes");
            } else {
                return Hub.string("undoDeleteNode");
            }
        }
    }

    public static class UndoableTranslateGraph extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 1159217930658226725L;

        protected FSAGraph graph;

        protected Point2D.Float displacement;

        public UndoableTranslateGraph(FSAGraph graph, Point2D.Float displacement) {
            this.graph = graph;
            this.displacement = displacement;
        }

        @Override
        public void redo() throws CannotRedoException {
            graph.translate(displacement.x, displacement.y);
        }

        @Override
        public void undo() throws CannotUndoException {
            graph.translate(-displacement.x, -displacement.y);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public boolean canRedo() {
            return true;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            return Hub.string("undoTranslateGraph");
        }
    }

    public static class UndoableSetInitial extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = -7149204849947853214L;

        protected Node node;

        protected boolean state;

        protected String desc;

        protected FSAGraph graph;

        public UndoableSetInitial(FSAGraph graph, Node node, boolean state) {
            this.graph = graph;
            this.node = node;
            this.state = state;
            if (state) {
                desc = Hub.string("undoMakeInitial");
            } else {
                desc = Hub.string("undoRemoveInitial");
            }
        }

        @Override
        public void redo() throws CannotRedoException {
            if (node == null) {
                throw new CannotRedoException();
            }
            graph.setInitial(node, state);
            state = !state;
        }

        @Override
        public void undo() throws CannotUndoException {
            if (node == null) {
                throw new CannotUndoException();
            }
            graph.setInitial(node, state);
            state = !state;
        }

        @Override
        public boolean canUndo() {
            return node != null;
        }

        @Override
        public boolean canRedo() {
            return node != null;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            return desc;
        }
    }

    public static class UndoableSetMarking extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 6728427932360562561L;

        protected Node node;

        protected boolean state;

        protected String desc;

        protected FSAGraph graph;

        public UndoableSetMarking(FSAGraph graph, Node node, boolean state) {
            this.graph = graph;
            this.node = node;
            this.state = state;
            if (state) {
                desc = Hub.string("undoMarkNode");
            } else {
                desc = Hub.string("undoUnmarkNode");
            }
        }

        @Override
        public void redo() throws CannotRedoException {
            if (node == null) {
                throw new CannotRedoException();
            }
            graph.setMarked(node, state);
            state = !state;
        }

        @Override
        public void undo() throws CannotUndoException {
            if (node == null) {
                throw new CannotUndoException();
            }
            graph.setMarked(node, state);
            state = !state;
        }

        @Override
        public boolean canUndo() {
            return node != null;
        }

        @Override
        public boolean canRedo() {
            return node != null;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            return desc;
        }
    }

    public static class UndoableUniformNodeSize extends AbstractGraphUndoableEdit {
        /**
         * 
         */
        private static final long serialVersionUID = 1645531864466069501L;

        protected boolean state;

        protected FSAGraph graph;

        public UndoableUniformNodeSize(FSAGraph graph, boolean state) {
            this.graph = graph;
            this.state = state;
        }

        @Override
        public void redo() throws CannotRedoException {
            if (graph == null) {
                throw new CannotRedoException();
            }
            graph.setUseUniformRadius(state);
            state = !state;
        }

        @Override
        public void undo() throws CannotUndoException {
            if (graph == null) {
                throw new CannotUndoException();
            }
            graph.setUseUniformRadius(state);
            state = !state;
        }

        @Override
        public boolean canUndo() {
            return graph != null;
        }

        @Override
        public boolean canRedo() {
            return graph != null;
        }

        /**
         * Returns the name that should be displayed besides the Undo/Redo menu items,
         * so the user knows which action will be undone/redone.
         */
        @Override
        public String getPresentationName() {
            return Hub.string("undoUniformNodeSize");
        }
    }

    public static class UndoableChangeFontSize extends AbstractGraphUndoableEdit {

        /**
         * 
         */
        private static final long serialVersionUID = 2751687325218507623L;

        protected FSAGraph graph;

        protected float origFontSize;

        protected float fontSize;

        public UndoableChangeFontSize(FSAGraph graph, float fontSize) {
            this.graph = graph;
            this.fontSize = fontSize;
        }

        public void redo() {
            origFontSize = graph.getFontSize();
            graph.setFontSize(fontSize);
            Hub.getUserInterface().getFontSelector().setFontSize(fontSize);
        }

        public void undo() {
            fontSize = graph.getFontSize();
            graph.setFontSize(origFontSize);
            Hub.getUserInterface().getFontSelector().setFontSize(origFontSize);

        }

        public boolean canUndo() {
            return true;
        }

        public boolean canRedo() {
            return true;
        }

        public String getPresentationName() {
            return Hub.string("undoFontSize");
        }

    }

}