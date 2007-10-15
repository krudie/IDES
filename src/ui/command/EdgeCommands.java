package ui.command;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.AbstractAction;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;
import main.Hub;
import main.Workspace;
import org.pietschy.command.ActionCommand;
import org.pietschy.command.undo.UndoableActionCommand;
import presentation.fsa.BezierEdge;
import presentation.fsa.BezierLayout;
import presentation.fsa.CircleNode;
import presentation.fsa.Edge;
import presentation.fsa.ReflexiveEdge;
import presentation.fsa.ReflexiveLayout;
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

	public static class ModifyEdgeAction extends AbstractAction {

		private Edge edge;
		private GraphicalLayout previousLayout;

		public ModifyEdgeAction(){
			super("modify.edge.command");
		}

		/**
		 * @param edge
		 * @param previousLayout
		 */
		public ModifyEdgeAction(Edge edge, GraphicalLayout previousLayout) {
			setEdge(edge);
			setPreviousLayout(previousLayout);
		}

		public void setEdge(Edge edge){
			this.edge = edge;
		}

		public void setPreviousLayout(GraphicalLayout layout){
			this.previousLayout = layout;
		}

		public void execute()
		{
			actionPerformed(null);
		}

		public void actionPerformed(ActionEvent evt) {
			UndoableModifyEdge action = new UndoableModifyEdge(edge,previousLayout);
			//perform action
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);	
		}		
	}


	private static class UndoableModifyEdge extends AbstractUndoableEdit {
		Edge edge;
		GraphicalLayout previousLayout, backupCurrentLayout;
		public UndoableModifyEdge(Edge edge, GraphicalLayout previousLayout) {
			this.edge = edge;
			this.previousLayout = previousLayout;
		}

		public void undo() throws CannotRedoException {
			if(edge.getSourceNode().equals(edge.getTargetNode()))
			{
				((ReflexiveLayout)previousLayout).setEdge((ReflexiveEdge)edge);
				
			}
			edge.setLayout(previousLayout);
			edge.refresh();
			edge.getGraph().commitLayoutModified();
			
		}

		public void redo() throws CannotRedoException {
			if(backupCurrentLayout == null)
			{
				ByteArrayOutputStream fo = new ByteArrayOutputStream();
				try{
					ObjectOutputStream so = new ObjectOutputStream(fo);
					so.writeObject(edge.getLayout());
					so.flush();
					so.close();
					ByteArrayInputStream is = new ByteArrayInputStream(fo.toByteArray());
					ObjectInputStream objectIS = new ObjectInputStream(is);
					backupCurrentLayout = (GraphicalLayout)objectIS.readObject();
					if(edge.getSourceNode().equals(edge.getTargetNode()))
					{
						((ReflexiveLayout)backupCurrentLayout).setEdge((ReflexiveEdge)edge);
					}
				}catch(IOException e){
					Hub.displayAlert(e.getMessage());
				}catch(ClassNotFoundException e)
				{
					Hub.displayAlert(e.getMessage());
				}
			}else{
				edge.setLayout((BezierLayout)(backupCurrentLayout));
				edge.refresh();
				edge.getGraph().commitLayoutModified();
			}
		}

		public boolean canUndo() {
			return true;
		}

		public boolean canRedo() {
			return true;
		}

		public String getPresentationName() {
			return Hub.string("modifyEdge");
		}

	}

}
