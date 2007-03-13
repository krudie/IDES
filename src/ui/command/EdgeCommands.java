package ui.command;

import javax.swing.undo.UndoableEdit;

import main.Workspace;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.undo.UndoableActionCommand;

import presentation.fsa.Edge;
import presentation.fsa.SelectionGroup;
import presentation.GraphicalLayout;

public class EdgeCommands {

	public static class CreateEventCommand extends ActionCommand {

		public CreateEventCommand(){
			super("event.create.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			System.out.println("Create an event and add to global and local alphabets.");
		}
		
	}
	
	public static class RemoveEventCommand extends ActionCommand {

		public RemoveEventCommand(){
			super("event.remove.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			System.out.println("Remove an event from local alphabet (leave it in the global alphabet).");
		}
		
	}

	public static class PruneEventsCommand extends ActionCommand {

		public PruneEventsCommand(){
			super("event.prune.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			System.out.println("Remove all events from global alphabet that don't exist in any local alphabet in the workspace.");
		}
		
	}

	public static class ModifyEdgeCommand extends UndoableActionCommand {

		private Edge edge;
		private GraphicalLayout previousLayout;
		
		public ModifyEdgeCommand(){
			super("modify.edge.command");
		}
		
		/**
		 * @param edge
		 * @param previousLayout
		 */
		public ModifyEdgeCommand(Edge edge, GraphicalLayout previousLayout) {
			setEdge(edge);
			setPreviousLayout(previousLayout);
		}

		public void setEdge(Edge edge){
			this.edge = edge;
		}
		
		public void setPreviousLayout(GraphicalLayout layout){
			this.previousLayout = layout;
		}
		
		/* (non-Javadoc)
		 * @see org.pietschy.command.undo.UndoableActionCommand#performEdit()
		 */
		@Override
		protected UndoableEdit performEdit() {
			SelectionGroup sg = new SelectionGroup();
			sg.insert(edge);
			edge.getGraph().commitMovement(sg);
			// TODO UndoableEdit containing the Edge id and a clone of the previous layout OR
			// just of the curve.
			return null;
		}		
	}
}
