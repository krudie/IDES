/**
 * 
 */
package ui.actions;

import io.IOUtilities;
import io.ParsingToolbox;
import io.fsa.ver2_1.FileOperations;
import io.fsa.ver2_1.GraphExporter;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Hub;
import main.IncompleteWorkspaceDescriptorException;
import main.Main;
import main.WorkspaceDescriptor;
import model.fsa.FSAModel;
import model.fsa.FSAState;
import model.fsa.ver2_1.Automaton;
import model.fsa.ver2_1.State;

import operations.fsa.ver2_1.Composition;

import pluggable.layout.LayoutManager;
import presentation.fsa.FSAGraph;
import presentation.fsa.GraphLabel;
import presentation.fsa.Node;
import services.latex.LatexManager;
import services.latex.LatexRenderException;
import ui.OperationDialog;

/**
 *
 * @author Lenko Grigorov
 */
public class OperationsActions {
	
	public static class ShowDialogAction extends AbstractAction {

		public ShowDialogAction() {
			super(Hub.string("comOperationsDialog"));			
			putValue(SHORT_DESCRIPTION, Hub.string("comHintOperationsDialog"));
		}

		public void actionPerformed(ActionEvent evt) {
			new OperationDialog();
		}
		
	}
}
