/**
 * 
 */
package presentation.fsa.actions;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import presentation.fsa.FSAGraph;
import presentation.fsa.FSAGraphMessage;
import presentation.fsa.InitialArrow;
import presentation.fsa.Node;

/**
 * This class holds static classes regarding actions over nodes. Normally, when
 * the user wants to explicitly execute an action (e.g.: by clicking in a
 * button), an AbstractAction will be encapsulating an UndoableAction that knows
 * how to undo/redo the action to be done. So everytime an action that can be
 * undone is started, two steps will be executed: 1- One
 * <code>AbstractAction</code> executes an <action>UndoableAction</action> that
 * can redo\/undo the result of the action. 2- The <code>AbstractAction</code>
 * then, notifies the UndoManager in the CommandManager about a performed
 * action. One of the reasons for making the UndoableAction being called by an
 * AbstractAction is the fact that by doing this, one AbstractAction could
 * encapsulate several UndoableActions making then a "composite" UndoableAction.
 * Also it is simpler (in my opinion (Christian)), to have to simpler classes
 * one to create an UndoableAction and talk to the CommandManager, and other
 * which is the UndoableAction itself. Having everything in just one class would
 * make this class be too big and more difficult to write.
 * 
 * @author Christian Silvano
 */
public class NodeActions {

    /**
     * A command that creates an UndoableAction that sets the value of a boolean
     * attribute for a DES element.
     * 
     * @author Christian Silvano
     */
    public static class SetInitialAction extends AbstractGraphAction {

        /**
         * 
         */
        private static final long serialVersionUID = -4349136639423993817L;

        protected FSAGraph graph;

        protected Node node;

        protected boolean state;

        public SetInitialAction(FSAGraph graph, Node node, boolean state) {
            this(null, graph, node, state);
        }

        public SetInitialAction(CompoundEdit parentEdit, FSAGraph graph, Node node, boolean state) {
            this.parentEdit = parentEdit;
            this.graph = graph;
            this.node = node;
            this.state = state;
        }

        public void actionPerformed(ActionEvent e) {
            UndoableEdit action = new GraphUndoableEdits.UndoableSetInitial(graph, node, state);
            // perform the action
            action.redo();
            postEditAdjustCanvas(graph, action);
        }
    }

    /**
     * A command that creates an UndoableAction that sets the value of a boolean
     * attribute for a DES element.
     * 
     * @author Christian Silvano
     */
    public static class SetMarkingAction extends AbstractGraphAction {

        /**
         * 
         */
        private static final long serialVersionUID = 2064321326077614081L;

        protected FSAGraph graph;

        protected Node node;

        protected boolean state;

        public SetMarkingAction(FSAGraph graph, Node node, boolean state) {
            this(null, graph, node, state);
        }

        public SetMarkingAction(CompoundEdit parentEdit, FSAGraph graph, Node node, boolean state) {
            this.parentEdit = parentEdit;
            this.graph = graph;
            this.node = node;
            this.state = state;
        }

        public void actionPerformed(ActionEvent e) {
            UndoableEdit action = new GraphUndoableEdits.UndoableSetMarking(graph, node, state);
            // perform the action
            action.redo();
            postEdit(action);
        }
    }

    public static class ModifyInitialArrowAction extends AbstractGraphAction {
        /**
         * 
         */
        private static final long serialVersionUID = 6329674238564295671L;

        protected FSAGraph graph;

        protected InitialArrow arrow;

        protected Point2D.Float direction;

        public ModifyInitialArrowAction(FSAGraph graph, InitialArrow arrow, Point2D.Float originalDirection) {
            this(null, graph, arrow, originalDirection);
        }

        public ModifyInitialArrowAction(CompoundEdit parentEdit, FSAGraph graph, InitialArrow arrow,
                Point2D.Float originalDirection) {
            this.parentEdit = parentEdit;
            this.graph = graph;
            this.arrow = arrow;
            direction = originalDirection;
        }

        public void actionPerformed(ActionEvent event) {
            if (arrow != null) {
                UndoableEdit action = new GraphUndoableEdits.UndoableModifyInitialArrow(graph, arrow, direction);
                // no need to "redo" the edit since the initial arrow has
                // already been modified
                postEditAdjustCanvas(graph, action);
                graph.fireFSAGraphChanged(new FSAGraphMessage(FSAGraphMessage.MODIFY, FSAGraphMessage.NODE,
                        arrow.getParent().getId(), arrow.getParent().bounds(), graph));
            }
        }
    }

}
