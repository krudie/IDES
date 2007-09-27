/**
 * 
 */
package presentation.fsa;

import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

import main.Hub;
import presentation.Geometry;
import ui.command.CommandManager_new;
import ui.tools.CreationTool;

/**
 * This class is a collection of undoable actions that affects FSA presentations.
 * Each undoable action is formed by a trigger function (the action itself) and a static class
 * that 
 * @author christiansilvano
 *
 */
public class UndoableEdits {

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
	public static void symmetrizeBezierLayout(BezierLayout l){
		UndoableSymmetrizeEdge action = new UndoableSymmetrizeEdge(l);
		//Perform the operation
		action.redo();
		// notify the listeners
		CommandManager_new.getInstance().undoSupport.postEdit(action);
	}
	
	/**
	 * Delete a selection of GraphElements from the canvas
	 * @param g , the group that is selected
	 * @param w , a reference to the GraphDrawingView 
	 */
	public static void deleteSelection(SelectionGroup g, GraphDrawingView w)
	{
		UndoableDelete action = new UndoableDelete(g, w);
		//Perform the operation
		action.redo();
		// notify the listeners
		CommandManager_new.getInstance().undoSupport.postEdit(action);
	}
	
	/**
	 * Undoable Action related to the command Symmetrize Edge
	 * @author christiansilvano
	 *
	 */
	private static class UndoableSymmetrizeEdge extends AbstractUndoableEdit{

		Point2D.Float CTRL1, CTRL2;
		BezierLayout layout;
		public UndoableSymmetrizeEdge(BezierLayout l)
		{
			layout = l;
			CTRL1 = (Point2D.Float)layout.getCurve().getCtrlP1();
			CTRL2 = (Point2D.Float)layout.getCurve().getCtrlP2();
		}

		public void undo() throws CannotRedoException{
			Point2D.Float[] points=new Point2D.Float[4];
			points[0] = (Point2D.Float)layout.getCurve().getP1();
			points[1] = CTRL1;
			points[2] = CTRL2;
			points[3] = (Point2D.Float)layout.getCurve().getP2();
			layout.getCurve().setCurve(points, 0);
			layout.setCurve(layout.getCurve());
			layout.setDirty(true);
		}

		public void redo() throws CannotRedoException{
			Point2D.Float[] points=new Point2D.Float[4];
			points[0]=Geometry.translate(layout.getCurve().getP1(),-layout.getCurve().getX1(),-layout.getCurve().getY1());
			points[1]=Geometry.translate(layout.getCurve().getCtrlP1(),-layout.getCurve().getX1(),-layout.getCurve().getY1());
			points[2]=Geometry.translate(layout.getCurve().getCtrlP2(),-layout.getCurve().getX1(),-layout.getCurve().getY1());
			points[3]=Geometry.translate(layout.getCurve().getP2(),-layout.getCurve().getX1(),-layout.getCurve().getY1());

			float edgeAngle=(float)Math.atan(Geometry.slope(layout.getCurve().getP1(),layout.getCurve().getP2()));
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
			points[0]=new Point2D.Float((float)layout.getCurve().getX1(), (float)layout.getCurve().getY1());
			points[1]=Geometry.translate(points[1],layout.getCurve().getX1(),layout.getCurve().getY1());
			points[2]=Geometry.translate(points[2],layout.getCurve().getX1(),layout.getCurve().getY1());
			points[3]=new Point2D.Float((float)layout.getCurve().getX2(), (float)layout.getCurve().getY2());
			layout.getCurve().setCurve(points, 0);
			layout.setCurve(layout.getCurve());
			layout.setDirty(true);
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


	private static class UndoableDelete extends AbstractUndoableEdit{

		SelectionGroup backup, group;
		GraphDrawingView graph;
		public UndoableDelete(SelectionGroup g, GraphDrawingView w)
		{
			graph = w;
			group = g;
			//TODO: make backup be a "clone" of g
			backup = g.copy();
			Iterator<GraphElement> ge = backup.children();
 		}

		public void undo() throws CannotRedoException{
			if(backup !=null)
			{
				for(Iterator i =backup.children();i.hasNext();)
				{
					GraphElement ge=(GraphElement)i.next();
					if(ge instanceof CircleNode)
					{
						CircleNode node = (CircleNode)ge;
						graph.graphModel.reCreateNode(node);
					}
				}
				
				for(Iterator i =backup.children();i.hasNext();)
				{
					GraphElement ge=(GraphElement)i.next();
					if(ge instanceof BezierEdge)
					{
						BezierEdge edge = (BezierEdge)ge;
						graph.graphModel.reCreateEdge(edge);
					}
				}
			}
		}

		public void redo() throws CannotRedoException{
			if(group!=null)
			{
				if(((CreationTool)graph.getTools()[GraphDrawingView.CREATE]).isDrawingEdge())
					((CreationTool)graph.getTools()[GraphDrawingView.CREATE]).abortEdge();
				for(Iterator i=group.children();i.hasNext();)
				{
					GraphElement ge=(GraphElement)i.next();
					graph.graphModel.delete(ge);
				}
				graph.setAvoidNextDraw(false);
			}
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
			return Hub.string("deleteSelection");
		}

	}
	
	


}
