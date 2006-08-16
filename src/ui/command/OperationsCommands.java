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

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.file.AbstractSaveAsCommand;
import org.pietschy.command.file.ExtensionFileFilter;

import algorithms.fsa.ver1.Composition;

import presentation.fsa.FSMGraph;
import presentation.fsa.GraphExporter;
import services.latex.LatexManager;
import services.latex.LatexRenderException;

/**
 *
 * @author Lenko Grigorov
 */
public class OperationsCommands {
	
	public static class ProductCommand extends ActionCommand {

		public ProductCommand() {
			super("product.command");			
		}

		@Override
		protected void handleExecute() {
			Iterator<FSAModel> i=Hub.getWorkspace().getAutomata();
			FSAModel a1=i.next();
			FSAModel a2=i.next();
			Automaton a=new Automaton("P("+a1.getName()+","+a2.getName()+")");
			Composition.product(a1,a2,a);
			FileOperations.saveAutomatonAs(a);
		}
		
	}
}
