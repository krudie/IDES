package ui.command;

import io.IOUtilities;
import io.ParsingToolbox;
import io.ctct.CTCTException;
import io.ctct.EventsMap;
import io.ctct.LL_CTCT_Command;
import io.fsa.ver2_1.CommonTasks;
import io.fsa.ver2_1.FileOperations;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Annotable;
import main.Hub;
import main.Workspace;
import main.IncompleteWorkspaceDescriptorException;
import main.Main;
import main.WorkspaceDescriptor;
import model.DESModel;
import model.ModelDescriptor;
import model.ModelManager;
import model.fsa.FSAModel;
import model.fsa.ver2_1.Automaton;
import model.template.TemplateModel;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.file.AbstractFileOpenCommand;
import org.pietschy.command.file.AbstractSaveAsCommand;
import org.pietschy.command.file.ExtensionFileFilter;

import presentation.LayoutShell;
import presentation.fsa.GraphExporter;
import presentation.fsa.FSAGraph;
import presentation.template.TemplateGraph;
import services.General;
import services.latex.LatexManager;
import services.latex.LatexPrerenderer;
import services.latex.LatexRenderException;
import ui.NewModelDialog;
import pluggable.io.IOCoordinator;


/**
 * @author Lenko Grigorov
 */
public class FileCommands {	
	
	public static class NewAutomatonCommand extends ActionCommand {
		
		/**
		 * used to create unique automaton names in a session
		 */
		private static int automatonCount=0;
		
		public NewAutomatonCommand(){
			super("new.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			ModelDescriptor md=new NewModelDialog().selectModel();
			if(md==null)
				return;
//			if(md.getPreferredModelInterface().equals(FSAModel.class))
//			{
//				FSAModel fsa = ModelManager.createModel(FSAModel.class,Hub.string("newAutomatonName")+"-"+automatonCount++);
//				Hub.getWorkspace().addModel(fsa);
//				Hub.getWorkspace().setActiveModel(fsa.getName());
//			}
			DESModel des=md.createModel(General.getRandomId(),Hub.string("newAutomatonName")+"-"+automatonCount++);
			Hub.getWorkspace().addModel(des);
			Hub.getWorkspace().setActiveModel(des.getName());
		}	
	}
	
	public static class OpenAutomatonCommand extends ActionCommand {
		
		public OpenAutomatonCommand() {
			super("open.automaton.command");			
		}

		@Override
		/**
		 * FIXME Don't add workspace files as if they were automata...
		 */
		protected void handleExecute() {
			JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty(FileOperations.LAST_PATH_SETTING_NAME));
			fc.setDialogTitle(Hub.string("openModelTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.MODEL_FILE_EXT, Hub.string("modelFileDescription")));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    	int retVal = fc.showOpenDialog(Hub.getMainWindow());
	    	if(retVal == JFileChooser.APPROVE_OPTION){
				Cursor cursor = Hub.getMainWindow().getCursor();

				Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if(Hub.getWorkspace().getModel(ParsingToolbox.removeFileType(fc.getSelectedFile().getName()))!=null)
				{
					Hub.displayAlert(Hub.string("modelAlreadyOpen"));
				}
				//CHRISTIAN
				//calling IOCoordinator to handle the open command (it will make the correct plugins
				//load the file):
				DESModel model = IOCoordinator.getInstance().load(fc.getSelectedFile());
				if(model != null)
				{
					Hub.getWorkspace().addModel(model);
					Hub.getWorkspace().setActiveModel(model.getName());
				}
				//CHRISTIAN
				Hub.getMainWindow().setCursor(cursor);
			}
		}
	}

	
	public static class SaveAllAutomataCommand extends ActionCommand {
		
		public SaveAllAutomataCommand(){
			super("saveall.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// TODO This should be a while loop
			for(Iterator<LayoutShell> i=Hub.getWorkspace().getLayoutShells();i.hasNext();) {
				LayoutShell gm=i.next();
				DESModel fsa=gm.getModel();
				if( fsa != null && fsa instanceof FSAModel)
					if(FileOperations.saveAutomaton((FSAModel)fsa,(File)fsa.getAnnotation(Annotable.FILE)))	{					
						Hub.getWorkspace().fireRepaintRequired();
					}
			}
			Hub.getMainWindow().setCursor(cursor);
		}	
	}
	
	public static class SaveAutomatonCommand extends ActionCommand {
		
		public SaveAutomatonCommand() {
			super("save.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			DESModel fsa = Hub.getWorkspace().getActiveModel();
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			if( fsa != null && fsa instanceof FSAModel) {
				if(FileOperations.saveAutomaton((FSAModel)fsa,(File)fsa.getAnnotation(Annotable.FILE)))	{
					Hub.getWorkspace().fireRepaintRequired();
				}
			}
			
			Hub.getMainWindow().setCursor(cursor);
		}	
	}
	
	public static class SaveAutomatonAsCommand extends ActionCommand {
		
		public SaveAutomatonAsCommand(){
			super("saveas.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			DESModel fsa = Hub.getWorkspace().getActiveModel();
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if(fsa!=null&&fsa instanceof FSAModel)
				if(FileOperations.saveAutomatonAs((FSAModel)fsa))
				{
					((FSAGraph)Hub.getWorkspace().getActiveLayoutShell()).setNeedsRefresh(false);					
					Hub.getWorkspace().fireRepaintRequired();
				}
			LayoutShell graph=Hub.getWorkspace().getActiveLayoutShell();
			if(graph!=null&&graph instanceof TemplateGraph)
			{
				if(io.template.ver2_1.FileOperations.saveAs((TemplateGraph)graph))
				{
					((TemplateGraph)Hub.getWorkspace().getActiveLayoutShell()).modelSaved();					
					Hub.getWorkspace().fireRepaintRequired();					
				}
			}
			Hub.getMainWindow().setCursor(cursor);
		}	
	}
	
	
	public static class CloseAutomatonCommand extends ActionCommand {
		
		public CloseAutomatonCommand(){
			super("close.automaton.command");
		}
		
		@Override
		protected void handleExecute() {
			Hub.getWorkspace().removeModel(Hub.getWorkspace().getActiveModelName());
		}	
	}
		
	
	public static class NewWorkspaceCommand extends ActionCommand {
			
			public NewWorkspaceCommand(){
				super("new.workspace.command");
			}
			
			@Override
			protected void handleExecute() {
				// TODO
				JOptionPane.showMessageDialog(null, "Create new workspace");
			}	
	}
	
	public static class OpenWorkspaceCommand extends ActionCommand {
		
		public OpenWorkspaceCommand(){
			super("open.workspace.command");
		}
		
		@Override
		protected void handleExecute() {
			if(Hub.getWorkspace().isDirty())
				if(!CommonTasks.handleUnsavedWorkspace())
					return;
			JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty(FileOperations.LAST_PATH_SETTING_NAME));
			fc.setDialogTitle(Hub.string("openWorkspaceTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.WORKSPACE_FILE_EXT, Hub.string("workspaceFileDescription")));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    	int retVal = fc.showOpenDialog(Hub.getMainWindow());
	    	if(retVal == JFileChooser.APPROVE_OPTION){
				Cursor cursor = Hub.getMainWindow().getCursor();
				Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    		WorkspaceDescriptor wd = FileOperations.openWorkspace(fc.getSelectedFile());
	    		if(wd != null){
	    			Hub.getWorkspace().replaceWorkspace(wd);
	    		}
				Hub.getMainWindow().setCursor(cursor);
	    	}
		}	
	}
	
	public static class SaveWorkspaceCommand extends ActionCommand {

		public SaveWorkspaceCommand(){
			super("save.workspace.command");
		}
		@Override
		protected void handleExecute() {
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try
			{
				WorkspaceDescriptor wd=Hub.getWorkspace().getDescriptor();
				if(FileOperations.saveWorkspace(wd,wd.getFile()))
					Hub.getWorkspace().setDirty(false);
			}catch(IncompleteWorkspaceDescriptorException e){}
			Hub.getMainWindow().setCursor(cursor);
		}
	}
	
	public static class SaveWorkspaceAsCommand extends ActionCommand {

		public SaveWorkspaceAsCommand(){
			super("saveas.workspace.command");
		}
		@Override
		protected void handleExecute() {
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try
			{
				WorkspaceDescriptor wd=Hub.getWorkspace().getDescriptor();
				if(FileOperations.saveWorkspaceAs(wd))
					Hub.getWorkspace().setDirty(false);
			}catch(IncompleteWorkspaceDescriptorException e){}
			Hub.getMainWindow().setCursor(cursor);
		}
	}
	
	public static class ExportToGIFCommand extends AbstractSaveAsCommand {

		public ExportToGIFCommand() {
			super(CommandManager.defaultInstance(), "export.gif.command", 
					new ExtensionFileFilter("gif", "Graphical Interchange Format"));
		}

		@Override
		protected void performSave(File arg0) {
			// TODO Auto-generated method stub
			System.out.println("TODO: Save as GIF");
		}
		
	}
	
	public static class ExportToEPSCommand extends ActionCommand {

		public ExportToEPSCommand() {
			super(CommandManager.defaultInstance(), "export.eps.command");
		}

		@Override
		protected void handleExecute() {
			if(Hub.getWorkspace().getActiveModel()==null)
				return;
			if(!LatexManager.isLatexEnabled())
			{
				Hub.displayAlert(Hub.string("enableLatex4Export"));
				return;
			}
			JFileChooser fc=new JFileChooser(Hub.persistentData.getProperty("lastUsedPath"));
			fc.setDialogTitle(Hub.string("exportEPSTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.EPS_FILE_EXT, Hub.string("epsFileDescription")));
			fc.setSelectedFile(new File(Hub.getWorkspace().getActiveModelName()));
			int retVal;
			boolean fcDone=true;
			File file=null;
			do
			{
				retVal = fc.showSaveDialog(Hub.getMainWindow());
				if(retVal != JFileChooser.APPROVE_OPTION)
					break;
				file=fc.getSelectedFile();
	    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.EPS_FILE_EXT))
	    			file=new File(file.getPath()+"."+IOUtilities.EPS_FILE_EXT);
				if(file.exists())
				{
					int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
						Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileExistAsk2"),
						Hub.string("exportEPSTitle"),
						JOptionPane.YES_NO_CANCEL_OPTION);
					fcDone=choice!=JOptionPane.NO_OPTION;
					if(choice!=JOptionPane.YES_OPTION)
						retVal=JFileChooser.CANCEL_OPTION;
				}
			} while(!fcDone);
	    	if(retVal != JFileChooser.APPROVE_OPTION)
	    		return;
			// Modified: June 16, 2006
			// Modifier: Sarah-Jane Whittaker
			String fileContents = GraphExporter.createEPSFileContents();
			FileWriter latexWriter = null;
					
			if (fileContents == null)
			{
				return;
			}
			
			try
			{
				LatexManager.getRenderer().latex2EPS(fileContents,file);
			}
			catch (IOException fileException)
			{
				Hub.displayAlert(Hub.string("problemLatexExport")+file.getPath());
			}
			catch (LatexRenderException e)
			{
				Hub.displayAlert(Hub.string("problemLatexExport")+file.getPath());
			}
		}
		
	}
	
	public static class ExportToLatexCommand extends ActionCommand {

		public ExportToLatexCommand() {
			super(CommandManager.defaultInstance(), "export.latex.command");
		}

		@Override
		protected void handleExecute() {
			if(Hub.getWorkspace().getActiveModel()==null)
				return;
			if(!LatexManager.isLatexEnabled())
			{
				Hub.displayAlert(Hub.string("enableLatex4Export"));
				return;
			}
			JFileChooser fc=new JFileChooser(Hub.persistentData.getProperty("lastUsedPath"));
			fc.setDialogTitle(Hub.string("exportLatexTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.LATEX_FILE_EXT, Hub.string("latexFileDescription")));
			fc.setSelectedFile(new File(Hub.getWorkspace().getActiveModelName()));
			int retVal;
			boolean fcDone=true;
			File file=null;
			do
			{
				retVal = fc.showSaveDialog(Hub.getMainWindow());
				if(retVal != JFileChooser.APPROVE_OPTION)
					break;
				file=fc.getSelectedFile();
	    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.LATEX_FILE_EXT))
	    			file=new File(file.getPath()+"."+IOUtilities.LATEX_FILE_EXT);
				if(file.exists())
				{
					int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
						Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileExistAsk2"),
						Hub.string("exportLatexTitle"),
						JOptionPane.YES_NO_CANCEL_OPTION);
					fcDone=choice!=JOptionPane.NO_OPTION;
					if(choice!=JOptionPane.YES_OPTION)
						retVal=JFileChooser.CANCEL_OPTION;
				}
			} while(!fcDone);
	    	if(retVal != JFileChooser.APPROVE_OPTION)
	    		return;
			// Modified: June 16, 2006
			// Modifier: Sarah-Jane Whittaker
			String fileContents = GraphExporter.createPSTricksFileContents();
			FileWriter latexWriter = null;
					
			if (fileContents == null)
			{
				return;
			}
			
			try
			{
				latexWriter = new FileWriter(file);
				latexWriter.write(fileContents);
				latexWriter.close();
			}
			catch (IOException fileException)
			{
				Hub.displayAlert(Hub.string("problemLatexExport")+file.getPath());
			}
		}
	}

	/**
	 * FIXME: the whole import/export business should be moved to plugins
	 * @author grigorov
	 *
	 */
	public static class ExportToGrailCommand extends ActionCommand {

		public ExportToGrailCommand() {
			super(CommandManager.defaultInstance(), "export.grail.command");
		}

		@Override
		protected void handleExecute() {
			if(Hub.getWorkspace().getActiveModel()==null ||
					!(Hub.getWorkspace().getActiveModel() instanceof FSAModel))
				return;
			JFileChooser fc=new JFileChooser(Hub.persistentData.getProperty("lastUsedPath"));
			fc.setDialogTitle(Hub.string("exportGrailTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.FM_FILE_EXT, Hub.string("fmFileDescription")));
			fc.setSelectedFile(new File(Hub.getWorkspace().getActiveModelName()));
			int retVal;
			boolean fcDone=true;
			File file=null;
			do
			{
				retVal = fc.showSaveDialog(Hub.getMainWindow());
				if(retVal != JFileChooser.APPROVE_OPTION)
					break;
				file=fc.getSelectedFile();
	    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.FM_FILE_EXT))
	    			file=new File(file.getPath()+"."+IOUtilities.FM_FILE_EXT);
				if(file.exists())
				{
					int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
						Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileExistAsk2"),
						Hub.string("exportGrailTitle"),
						JOptionPane.YES_NO_CANCEL_OPTION);
					fcDone=choice!=JOptionPane.NO_OPTION;
					if(choice!=JOptionPane.YES_OPTION)
						retVal=JFileChooser.CANCEL_OPTION;
				}
			} while(!fcDone);
	    	if(retVal != JFileChooser.APPROVE_OPTION)
	    		return;
	    	
	    	String fileContents = "";
	    	
	    	FSAModel a=(FSAModel)Hub.getWorkspace().getActiveModel();
	    	for(Iterator<model.fsa.FSAState> i=a.getStateIterator();i.hasNext();)
	    	{
	    		model.fsa.FSAState s=i.next();
	    		if(s.isInitial())
	    		{
	    			fileContents+="(START) |- "+s.getId()+"\n";
	    		}
	    		if(s.isMarked())
	    		{
	    			fileContents+=""+s.getId()+" -| (FINAL)\n";
	    		}
	    		for(Iterator<model.fsa.FSATransition> j=s.getSourceTransitionsListIterator();j.hasNext();)
	    		{
	    			model.fsa.FSATransition t=j.next();
	    			fileContents+=""+s.getId()+" "+(t.getEvent()==null?"NULL":t.getEvent().getSymbol())+" "+t.getTarget().getId()+"\n";
	    		}
	    	}
	    	
			FileWriter latexWriter = null;
					
			if (fileContents == null)
			{
				return;
			}
			
			try
			{
				latexWriter = new FileWriter(file);
				latexWriter.write(fileContents);
				latexWriter.close();
			}
			catch (IOException fileException)
			{
				Hub.displayAlert(Hub.string("problemLatexExport")+file.getPath());
			}
		}
	}

	/**
	 * FIXME: the whole import/export business should be moved to plugins
	 * @author grigorov
	 *
	 */
	public static class ImportFromGrailCommand extends ActionCommand {

		public ImportFromGrailCommand() {
			super(CommandManager.defaultInstance(), "import.grail.command");
		}

		@Override
		protected void handleExecute() {
			JFileChooser fc=new JFileChooser(Hub.persistentData.getProperty("lastUsedPath"));
			fc.setDialogTitle(Hub.string("importGrailTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.FM_FILE_EXT, Hub.string("fmFileDescription")));
			//fc.setSelectedFile(new File(Hub.getWorkspace().getActiveModelName()));
			int retVal;
			boolean fcDone=false;
			File file=null;
			do
			{
				retVal = fc.showOpenDialog(Hub.getMainWindow());
				if(retVal != JFileChooser.APPROVE_OPTION)
					break;
				file=fc.getSelectedFile();
				if(!file.exists())
				{
					Hub.displayAlert(
						Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileNotExistAsk2"));
				}
				else
					fcDone=true;
			} while(!fcDone);
	    	if(retVal != JFileChooser.APPROVE_OPTION)
	    		return;
	    	
	    	java.io.BufferedReader in=null;
	    	try
	    	{
	    		in=new java.io.BufferedReader(new java.io.FileReader(file));
	    		FSAModel a=ModelManager.createModel(FSAModel.class,file.getName());
	    		long tCount=0;
	    		long eCount=0;
	    		java.util.Hashtable<String,Long> events=new java.util.Hashtable<String, Long>();
	    		String line;
	    		while((line=in.readLine())!=null)
	    		{
	    			String[] parts=line.split(" ");
	    			if(parts[0].startsWith("("))
	    			{
	    				long sId=Long.parseLong(parts[2]);
	    				model.fsa.ver2_1.State s=(model.fsa.ver2_1.State)a.getState(sId);
	    				if(s==null)
	    				{
	    					s=new model.fsa.ver2_1.State(sId);
	    					a.add(s);
	    				}
	    				s.setInitial(true);
	    			}
	    			else if(parts[2].startsWith("("))
	    			{	    				
	    				long sId=Long.parseLong(parts[0]);
	    				model.fsa.ver2_1.State s=(model.fsa.ver2_1.State)a.getState(sId);
	    				if(s==null)
	    				{
	    					s=new model.fsa.ver2_1.State(sId);
	    					a.add(s);
	    				}
	    				s.setMarked(true);
	    			}
	    			else
	    			{
	    				long sId1=Long.parseLong(parts[0]);
	    				model.fsa.ver2_1.State s1=(model.fsa.ver2_1.State)a.getState(sId1);
	    				if(s1==null)
	    				{
	    					s1=new model.fsa.ver2_1.State(sId1);
	    					a.add(s1);
	    				}
	    				long sId2=Long.parseLong(parts[2]);
	    				model.fsa.ver2_1.State s2=(model.fsa.ver2_1.State)a.getState(sId2);
	    				if(s2==null)
	    				{
	    					s2=new model.fsa.ver2_1.State(sId2);
	    					a.add(s2);
	    				}
	    				model.fsa.ver2_1.Event e=null;
	    				Long eId=events.get(parts[1]);
	    				if(eId==null)
	    				{
	    					e=new model.fsa.ver2_1.Event(eCount);
	    					e.setSymbol(parts[1]);
	    					e.setObservable(true);
	    					e.setControllable(true);
	    					eCount++;
	    					a.add(e);
	    					events.put(parts[1], new Long(e.getId()));
	    				}
	    				else
	    					e=(model.fsa.ver2_1.Event)a.getEvent(eId.longValue());
	    				model.fsa.ver2_1.Transition t=new model.fsa.ver2_1.Transition(tCount,s1,s2,e);
	    				a.add(t);
	    				tCount++;
	    			}
	    		}
	    		presentation.fsa.FSAGraph g=new presentation.fsa.FSAGraph(a);
	    		Hub.getWorkspace().addLayoutShell(g);
	    	}catch(java.io.IOException e)
	    	{
	    		Hub.displayAlert(Hub.string("cantParseImport")+file);
	    	}
	    	catch(RuntimeException e)
	    	{
	    		Hub.displayAlert(Hub.string("cantParseImport")+file);
	    	}
	    	finally
	    	{
	    		try
	    		{
	    			if(in!=null)
	    				in.close();
	    		}catch(java.io.IOException e){}
	    	}
	    	
		}
	}

	/**
	 * FIXME: the whole import/export business should be moved to plugins
	 * @author grigorov
	 *
	 */
	public static class ExportToTCTCommand extends ActionCommand {

		public ExportToTCTCommand() {
			super(CommandManager.defaultInstance(), "export.tct.command");
		}

		@Override
		protected void handleExecute() {
			if(Hub.getWorkspace().getActiveModel()==null ||
					!(Hub.getWorkspace().getActiveModel() instanceof FSAModel))
				return;
			JFileChooser fc=new JFileChooser(Hub.persistentData.getProperty("lastUsedPath"));
			fc.setDialogTitle(Hub.string("exportTCTTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.TCT_FILE_EXT, Hub.string("tctFileDescription")));
			fc.setSelectedFile(new File(Hub.getWorkspace().getActiveModelName()));
			int retVal;
			boolean fcDone=true;
			File file=null;
			do
			{
				retVal = fc.showSaveDialog(Hub.getMainWindow());
				if(retVal != JFileChooser.APPROVE_OPTION)
					break;
				file=fc.getSelectedFile();
	    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.TCT_FILE_EXT))
	    			file=new File(file.getPath()+"."+IOUtilities.TCT_FILE_EXT);
				if(file.exists())
				{
					int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
						Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileExistAsk2"),
						Hub.string("exportTCTTitle"),
						JOptionPane.YES_NO_CANCEL_OPTION);
					fcDone=choice!=JOptionPane.NO_OPTION;
					if(choice!=JOptionPane.YES_OPTION)
						retVal=JFileChooser.CANCEL_OPTION;
				}
			} while(!fcDone);
	    	if(retVal != JFileChooser.APPROVE_OPTION)
	    		return;
	    	
	    	FSAModel a=(FSAModel)Hub.getWorkspace().getActiveModel();
	    	
			try
			{
		    	LL_CTCT_Command.GiddesToCTCT(file.getAbsolutePath(),a,LL_CTCT_Command.em);
		    	LL_CTCT_Command.em.saveGlobalMap(new File(file.getParentFile().getAbsolutePath()+File.separator+"global.map"));
			}
			catch (CTCTException fileException)
			{
				Hub.displayAlert(Hub.string("problemLatexExport")+file.getPath());
			}
		}
	}

	/**
	 * FIXME: the whole import/export business should be moved to plugins
	 * @author grigorov
	 *
	 */
	public static class ImportFromTCTCommand extends ActionCommand {

		public ImportFromTCTCommand() {
			super(CommandManager.defaultInstance(), "import.tct.command");
		}

		@Override
		protected void handleExecute() {
			JFileChooser fc=new JFileChooser(Hub.persistentData.getProperty("lastUsedPath"));
			fc.setDialogTitle(Hub.string("importTCTTitle"));
			fc.setFileFilter(new ExtensionFileFilter(IOUtilities.TCT_FILE_EXT, Hub.string("tctFileDescription")));
			//fc.setSelectedFile(new File(Hub.getWorkspace().getActiveModelName()));
			int retVal;
			boolean fcDone=false;
			File file=null;
			do
			{
				retVal = fc.showOpenDialog(Hub.getMainWindow());
				if(retVal != JFileChooser.APPROVE_OPTION)
					break;
				file=fc.getSelectedFile();
				if(!file.exists())
				{
					Hub.displayAlert(
						Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileNotExistAsk2"));
				}
				else
					fcDone=true;
			} while(!fcDone);
	    	if(retVal != JFileChooser.APPROVE_OPTION)
	    		return;
	    	
	    	try
	    	{
	    		FSAModel a=LL_CTCT_Command.CTCTtoGiddes(file.getAbsolutePath(),file.getName().substring(0,file.getName().lastIndexOf(".")));
	    		presentation.fsa.FSAGraph g=new presentation.fsa.FSAGraph(a);
	    		Hub.getWorkspace().addLayoutShell(g);
	    	}catch(CTCTException e)
	    	{
	    		e.printStackTrace();
	    		Hub.displayAlert(Hub.string("cantParseImport")+file);
	    	}
	    	catch(RuntimeException e)
	    	{
	    		e.printStackTrace();
	    		Hub.displayAlert(Hub.string("cantParseImport")+file);
	    	}

		}
	}

	public static class ExportToPNGCommand extends AbstractSaveAsCommand {

		public ExportToPNGCommand() {
			super(CommandManager.defaultInstance(), "export.png.command",
					new ExtensionFileFilter("png", "Portable Network Graphic"));			
		}

		@Override
		protected void performSave(File arg0) {
			// TODO Auto-generated method stub
			System.out.println("TODO: Save as PNG");
		}
		
	}
	
	public static class ExitCommand extends ActionCommand {
		
		public ExitCommand(){
			super("exit.command");
		}
		
		@Override
		protected void handleExecute() {			
			Main.onExit();
		}	
	}
}