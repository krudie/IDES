/**
 * 
 */
package ui.command;

import io.IOUtilities;
import io.ParsingToolbox;
import io.fsa.ver1.CommonTasks;
import io.fsa.ver1.FileOperations;

import java.awt.Cursor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Hub;
import main.IncompleteWorkspaceDescriptorException;
import main.Main;
import main.WorkspaceDescriptor;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;

import operations.fsa.ver1.Composition;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.file.AbstractSaveAsCommand;
import org.pietschy.command.file.ExtensionFileFilter;


import pluggable.layout.LayoutManager;
import presentation.fsa.FSMGraph;
import presentation.fsa.GraphExporter;
import services.latex.LatexManager;
import services.latex.LatexRenderException;
import ui.OperationDialog;

/**
 *
 * @author Lenko Grigorov
 */
public class OperationsCommands {
	
	public static class ProductCommand extends ActionCommand {

		public ProductCommand() {
			super("operations.menu");			
		}

		@Override
		protected void handleExecute() {
			OperationDialog od=new OperationDialog();
			Automaton a=(Automaton)od.queryOperation();
			if(a!=null)
			{
				FSMGraph g=new FSMGraph(a);
				FileOperations.saveAutomatonAs(a);
			}
		}
		
	}
}
