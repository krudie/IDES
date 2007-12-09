package services.latex;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import presentation.fsa.FSAGraph;
import presentation.fsa.GraphLabel;
import presentation.fsa.Node;
import presentation.fsa.actions.AbstractGraphUndoableEdit;
import presentation.fsa.actions.GraphActions;
import presentation.fsa.actions.GraphUndoableEdits;
import services.undo.UndoManager;

import main.Hub;

/**
 * The class for the "Use LaTeX rendering" menu item.
 * 
 * @author Lenko Grigorov
 */
public class UseLatexAction extends AbstractAction {

	/**
	 * Default constructor; handy for exporting this command for group setup.
	 */
	public UseLatexAction(){
		super(Hub.string("comUseLaTeX"));
		putValue(SHORT_DESCRIPTION, Hub.string("comHintUseLaTeX"));
	}
	
	/**
	 * Changes the property state.
	 */
	public void actionPerformed(ActionEvent evt) {
		LatexManager.setLatexEnabled(!LatexManager.isLatexEnabled());
	}
}
