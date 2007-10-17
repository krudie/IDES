package ui.command;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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
import presentation.fsa.EdgeLabellingDialog;
import presentation.fsa.GraphDrawingView;
import presentation.fsa.InitialArrow;
import presentation.fsa.ReflexiveEdge;
import presentation.fsa.ReflexiveLayout;
import presentation.fsa.SelectionGroup;
import presentation.Geometry;
import presentation.GraphicalLayout;

public class EdgeCommands {

	public static class CreateEventCommand extends AbstractAction {
		private static final ImageIcon icon = new ImageIcon();
		private static final String name = "Label with events";

		private GraphDrawingView view;
		private Edge edge;

		public CreateEventCommand(GraphDrawingView view,Edge edge){
			super(name,icon);
			icon.setImage(Toolkit.getDefaultToolkit().createImage(Hub.getResource("images/icons/machine_alpha.gif")));
			this.edge = edge;
			this.view = view;
		}

		public void actionPerformed(ActionEvent evt) {
			if(edge != null & view != null)
			{
				EdgeLabellingDialog.showDialog(view, edge);
			}
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

	/**
	 * This methos is triggered every-time an <code>Edge</code> have one of the control points
	 * of its layout (<code>BezierLayout</code>) edited.
	 * @author christiansilvano
	 *
	 */
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

			if(edge.getSourceNode() == null)
			{//Initial edge

			}else
			{
				if(edge.getSourceNode().equals(edge.getTargetNode()))
				{//Reflexive edge
					((ReflexiveLayout)previousLayout).setEdge((ReflexiveEdge)edge);

				}else
				{//Regular bezier edge


				}
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
					if(edge.getSourceNode() == null)
					{//Initial Edge

					}
					else{
						if(edge.getSourceNode().equals(edge.getTargetNode()))
						{//If it is a reflexiveEdge
							((ReflexiveLayout)backupCurrentLayout).setEdge((ReflexiveEdge)edge);
						}
						else
						{//Regular Bezier Edge

						}
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


	/**
	 * If this edge is not straight,  make it have a symmetrical appearance.
	 * Make the two vectors - from P1 to CTRL1 and from P2 to CTRL2, be of 
	 * the same length and have the same angle. So the edge will look it has 
	 * a symmetrical curve. 
	 * There are two cases:
	 * The 2 control points are on the same side of the curve (a curve with the 
	 * form of a bow); and the 2 control points are on different sides of the 
	 * edge (a curve like a wave). In one of the cases, theangles of the vectors
	 * should be A=B, in the other A=-B.
	 *
	 *The command is undoable, so it is encapsuladed on an UndoableEdit.
	 *
	 */
	public static class SymmetrizeEdgeAction extends AbstractAction
	{
		private static final ImageIcon icon = new ImageIcon();
		private static final String name = Hub.string("symmetrize");
		private BezierLayout layout;


		private GraphDrawingView view;
		private Edge edge;

		public SymmetrizeEdgeAction(GraphDrawingView view,Edge edge){
			super(name,icon);
			this.edge = edge;
			this.view = view;
		}

		public void actionPerformed(ActionEvent evt){
			BezierLayout l = (BezierLayout)edge.getLayout();
			UndoableSymmetrizeEdge action = new UndoableSymmetrizeEdge(view,l);
			//Perform the operation
			action.redo();
			// notify the listeners
			CommandManager_new.getInstance().undoSupport.postEdit(action);
		}
	}
	/**
	 * Undoable Action related to the command Symmetrize Edge
	 * @author christiansilvano
	 *
	 */
	private static class UndoableSymmetrizeEdge extends AbstractUndoableEdit{
		BezierLayout backupLayout;
		GraphDrawingView view;
		BezierEdge edge;

		public UndoableSymmetrizeEdge(GraphDrawingView view, BezierLayout layout)
		{
			edge = layout.getEdge();
			
			//Backing up the original edge, so the action can be undone as many times as it is 
			//necessary.
			Point2D.Float[] bezierControls = new Point2D.Float[4];
			bezierControls[0] = (Point2D.Float)((BezierLayout)layout).getCurve().getP1();
			bezierControls[1] = (Point2D.Float)((BezierLayout)layout).getCurve().getCtrlP1();
			bezierControls[2] = (Point2D.Float)((BezierLayout)layout).getCurve().getCtrlP2();
			bezierControls[3] = (Point2D.Float)((BezierLayout)layout).getCurve().getP2();
			backupLayout = new BezierLayout(bezierControls);
			backupLayout.setEdge(edge);
			this.view = view;
		}

		/**
		 * Restores the edge to the original layout by applying the backup of the original layout
		 * to the edge.
		 */
		public void undo() throws CannotRedoException{
			edge.setLayout(backupLayout);
			backupLayout.setDirty(true);
			view.repaint();
		}

		public void redo() throws CannotRedoException{
			//Performs the symmetrization over the original curve.
			Point2D.Float[] points=new Point2D.Float[4];
			points[0]=Geometry.translate(backupLayout.getCurve().getP1(),-backupLayout.getCurve().getX1(),-backupLayout.getCurve().getY1());
			points[1]=Geometry.translate(backupLayout.getCurve().getCtrlP1(),-backupLayout.getCurve().getX1(),-backupLayout.getCurve().getY1());
			points[2]=Geometry.translate(backupLayout.getCurve().getCtrlP2(),-backupLayout.getCurve().getX1(),-backupLayout.getCurve().getY1());
			points[3]=Geometry.translate(backupLayout.getCurve().getP2(),-backupLayout.getCurve().getX1(),-backupLayout.getCurve().getY1());

			float edgeAngle=(float)Math.atan(Geometry.slope(backupLayout.getCurve().getP1(),backupLayout.getCurve().getP2()));
			points[0]=Geometry.rotate(points[0],-edgeAngle);
			points[1]=Geometry.rotate(points[1],-edgeAngle);
			points[2]=Geometry.rotate(points[2],-edgeAngle);
			points[3]=Geometry.rotate(points[3],-edgeAngle);

			double quadrantFix1=(points[0].x-points[1].x>0)?Math.PI:0;
			double quadrantFix2=(points[2].x-points[3].x>0)?Math.PI:0;

			float a1=(float)Math.atan(Geometry.slope(points[0],points[1]));
			float a2=(float)Math.atan(Geometry.slope(points[3],points[2]));
			float angle=(float)(Math.abs(a1)+Math.abs(a2))/2F;
			float distance=(float)(points[0].distance(points[1])+points[2].distance(points[3]))/2F;

			points[1]=Geometry.rotate(new Point2D.Float(distance,0),(angle*Math.signum(a1)+quadrantFix1));
			points[2]=Geometry.rotate(new Point2D.Float(distance,0),(angle*Math.signum(a2)+quadrantFix2+Math.PI));
			points[2].x+=points[3].x;
			points[2].y+=points[3].y;

			a1=(float)Math.atan(Geometry.slope(points[0],points[1]));
			a2=(float)Math.atan(Geometry.slope(points[3],points[2]));

			points[1]=Geometry.rotate(points[1],edgeAngle);
			points[2]=Geometry.rotate(points[2],edgeAngle);
			points[0]=new Point2D.Float((float)backupLayout.getCurve().getX1(), (float)backupLayout.getCurve().getY1());
			points[1]=Geometry.translate(points[1],backupLayout.getCurve().getX1(),backupLayout.getCurve().getY1());
			points[2]=Geometry.translate(points[2],backupLayout.getCurve().getX1(),backupLayout.getCurve().getY1());
			points[3]=new Point2D.Float((float)backupLayout.getCurve().getX2(), (float)backupLayout.getCurve().getY2());
			
			//Apply a new layout to the edge
			edge.setLayout(new BezierLayout(points));
			//Make the new layout have a reference to its edge.
			((BezierLayout)edge.getLayout()).setEdge(edge);
			//Refresh the graph
			view.repaint();
		}

		public boolean canUndo()
		{
			return true;
		}

		public boolean canRedo()
		{
			return true;
		}

		public String getPresentationName()
		{
			return Hub.string("symmetrizeEdge");
		}

	}


}
