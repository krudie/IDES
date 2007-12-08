package presentation.fsa.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import presentation.fsa.FSAGraph;
import presentation.fsa.GraphView;
import presentation.fsa.SelectionGroup;

import services.undo.UndoManager;

public abstract class AbstractGraphAction extends AbstractAction {

	protected static final int GRAPH_BORDER_THICKNESS=10;
	protected CompoundEdit parentEdit=null; 
	protected boolean usePluralDescription=false;

	public AbstractGraphAction()
	{
		super();
	}
	
	public AbstractGraphAction(String name)
	{
		super(name);
	}
	
	public AbstractGraphAction(String name, Icon icon)
	{
		super(name,icon);
	}
	
	protected void postEdit(UndoableEdit edit)
	{
		if(usePluralDescription&&edit instanceof AbstractGraphUndoableEdit)
		{
			((AbstractGraphUndoableEdit)edit).setLastOfMultiple(true);
		}
		if(parentEdit!=null)
		{
			parentEdit.addEdit(edit);
		}
		else
		{
			UndoManager.addEdit(edit);
		}
	}
	
	protected void postEditAdjustCanvas(FSAGraph graph, UndoableEdit edit)
	{
		postEdit(addBoundsAdjust(graph,edit));
	}
	
	protected UndoableEdit addBoundsAdjust(FSAGraph graph, UndoableEdit edit)
	{
		Rectangle graphBounds=graph.getBounds(true);
		if(graphBounds.x<0||graphBounds.y<0)
		{
			CompoundEdit adjEdit=new CompoundEdit();
			adjEdit.addEdit(edit);
			UndoableEdit translation=new GraphUndoableEdits.UndoableTranslateGraph(graph,new Point2D.Float(-graphBounds.x+GRAPH_BORDER_THICKNESS,-graphBounds.y+GRAPH_BORDER_THICKNESS));
			translation.redo();
			adjEdit.addEdit(translation);
			adjEdit.addEdit(new GraphUndoableEdits.UndoableDummyLabel(edit.getPresentationName()));
			adjEdit.end();
			return adjEdit;
		}
		return edit;
	}
	
	public void setLastOfMultiple(boolean b)
	{
		usePluralDescription=b;
	}
	
	public void execute() {
		actionPerformed(null);
	}

}
