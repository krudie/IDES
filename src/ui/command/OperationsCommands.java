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
import model.fsa.FSAState;
import model.fsa.ver1.Automaton;
import model.fsa.ver1.State;

import operations.fsa.ver1.Composition;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.file.AbstractSaveAsCommand;
import org.pietschy.command.file.ExtensionFileFilter;


import pluggable.layout.LayoutManager;
import presentation.fsa.FSMGraph;
import presentation.fsa.GraphExporter;
import presentation.fsa.GraphLabel;
import presentation.fsa.Node;
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
				if(a.getAutomataCompositionList().length>1)
				{
					FSMGraph g1=Hub.getWorkspace().getGraphById(a.getAutomataCompositionList()[0]);
					FSMGraph g2=Hub.getWorkspace().getGraphById(a.getAutomataCompositionList()[1]);				
					for(Node n:g.getNodes())
					{
						State s=(State)n.getState();
						g.labelNode(n,"("+g1.getNode(s.getStateCompositionList()[0]).getLabel().getText()+
							","+g2.getNode(s.getStateCompositionList()[1]).getLabel().getText()+")");
					}
				}
				else
				{
					FSMGraph g1=Hub.getWorkspace().getGraphById(a.getAutomataCompositionList()[0]);
					for(Node n:g.getNodes())
					{
						State s=(State)n.getState();
						String label="(";
						for(int i=0;i<s.getStateCompositionList().length;++i)
							label+=g1.getNode(s.getStateCompositionList()[i]).getLabel().getText()+",";
						if(label.endsWith(","))
							label=label.substring(0,label.length()-1);
						label+=")";
						g.labelNode(n,label);
					}
				}
				Hub.getWorkspace().addFSAGraph(g);
			}
		}
		
	}
}
