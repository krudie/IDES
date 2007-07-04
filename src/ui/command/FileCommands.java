package ui.command;

import io.IOUtilities;
import io.ParsingToolbox;
import io.ctct.CTCTException;
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
import main.IncompleteWorkspaceDescriptorException;
import main.Main;
import main.WorkspaceDescriptor;
import model.DESModel;
import model.ModelDescriptor;
import model.ModelManager;
import model.fsa.FSAModel;

import org.pietschy.command.ActionCommand;
import org.pietschy.command.CommandManager;
import org.pietschy.command.file.AbstractSaveAsCommand;
import org.pietschy.command.file.ExtensionFileFilter;

import presentation.LayoutShell;
import presentation.fsa.GraphExporter;
import services.General;
import services.latex.LatexManager;
import services.latex.LatexRenderException;
import ui.NewModelDialog;
import ui.OperationDialog;
import pluggable.io.IOCoordinator;
import ui.ImportExportDialog;

/**
 * @author Lenko Grigorov
 */
public class FileCommands {	
	
	public static class NewCommand extends ActionCommand {
		
		/**
		 * used to create unique  names in a session
		 */
		private static int Count=0;
		
		public NewCommand(){
			super("new.command");
		}
		
		@Override
		protected void handleExecute() {
			ModelDescriptor md=new NewModelDialog().selectModel();
			if(md==null)
				return;
			DESModel des=md.createModel(General.getRandomId(),Hub.string("newModelName")+"-"+Count++);
			Hub.getWorkspace().addModel(des);
			Hub.getWorkspace().setActiveModel(des.getName());
		}	
	}
	
	public static class OpenCommand extends ActionCommand {
		
		public OpenCommand() {
			super("open.command");			
		}

		@Override
		/**
		 * FIXME Don't add workspace files as if they were automata...
		 */
		protected void handleExecute() {
			//Open a window for the user to choose the file to open:
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
				
				//calling IOCoordinator to handle the selected file
				//It will make the correct plugins load the file):
				DESModel model = IOCoordinator.getInstance().load(fc.getSelectedFile());

				if(model != null)
				{
					Hub.getWorkspace().addModel(model);
					Hub.getWorkspace().setActiveModel(model.getName());
				}

				Hub.getMainWindow().setCursor(cursor);
			}
		}
	}

	
	public static class SaveAllCommand extends ActionCommand {
		
		public SaveAllCommand(){
			super("saveall.command");
		}
		
		@Override
		protected void handleExecute() {
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Iterator<LayoutShell> iterator = Hub.getWorkspace().getLayoutShells();			
			while(iterator.hasNext())
			{
				LayoutShell gm=iterator.next();
				DESModel model=gm.getModel();
				if( model != null)
				{
					IOCoordinator.getInstance().save(model, (File)model.getAnnotation(Annotable.FILE));
					Hub.getWorkspace().fireRepaintRequired();
				}
			}
			Hub.getMainWindow().setCursor(cursor);
		}	
	}
	
	public static class SaveCommand extends ActionCommand {
		
		public SaveCommand() {
			super("save.command");
		}
		
		@Override
		protected void handleExecute() {
			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			//Make the model be saved by the IOCoordinator.
			//IOCoordinator will select the plugins which saves data and metadata information for model.
			DESModel model = Hub.getWorkspace().getActiveModel();
			
			if( model != null)
			{
				if(model.getAnnotation(Annotable.FILE) == null)
				{
					JFileChooser fc;
					String path = Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME");
					if(path == null)
					{
						fc=new JFileChooser();
					}else
					{
						fc=new JFileChooser(path);	
					}
			        fc.setDialogTitle(Hub.string("saveModelTitle"));
					fc.setFileFilter(new ExtensionFileFilter(IOUtilities.MODEL_FILE_EXT, 
							Hub.string("modelFileDescription")));
					
					if((File)model.getAnnotation(Annotable.FILE)!=null){
						fc.setSelectedFile((File)model.getAnnotation(Annotable.FILE));
					}else{
						fc.setSelectedFile(new File(model.getName()));
					}
					
					int retVal;
					boolean fcDone=true;
					File file=null;
					do
					{
						retVal = fc.showSaveDialog(Hub.getMainWindow());
						if(retVal != JFileChooser.APPROVE_OPTION)
							break;
						file=fc.getSelectedFile();
			    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.MODEL_FILE_EXT))
			    			file=new File(file.getPath()+"."+IOUtilities.MODEL_FILE_EXT);
						if(file.exists())
						{
							int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
								Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileExistAsk2"),
								Hub.string("saveModelTitle"),
								JOptionPane.YES_NO_CANCEL_OPTION);
							fcDone=choice!=JOptionPane.NO_OPTION;
							if(choice!=JOptionPane.YES_OPTION)
								retVal=JFileChooser.CANCEL_OPTION;
						}
					} while(!fcDone);					
				
					if(retVal != JFileChooser.CANCEL_OPTION)
					{
						model.setAnnotation(Annotable.FILE, file);
						IOCoordinator.getInstance().save(model, (File)model.getAnnotation(Annotable.FILE));
						Hub.getWorkspace().fireRepaintRequired();
					}
				}else
				{
					model.setAnnotation(Annotable.FILE, model.getAnnotation(Annotable.FILE));
					IOCoordinator.getInstance().save(model, (File)model.getAnnotation(Annotable.FILE));
					Hub.getWorkspace().fireRepaintRequired();
				}
			}
			Hub.getMainWindow().setCursor(cursor);
		}	
	}
	
	public static class SaveAsCommand extends ActionCommand {
		
		public SaveAsCommand(){
			super("saveas.command");
		}
		
		@Override
		protected void handleExecute() {

			Cursor cursor = Hub.getMainWindow().getCursor();
			Hub.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			//Make the model be saved by the IOCoordinator.
			//IOCoordinator will select the plugins which saves data and metadata information for model.
			DESModel model = Hub.getWorkspace().getActiveModel();
			if( model != null)
			{
					JFileChooser fc;
					String path = Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME");
					if(path == null)
					{
						fc=new JFileChooser();
					}else
					{
						fc=new JFileChooser(path);	
					}
			        fc.setDialogTitle(Hub.string("saveModelTitle"));
					fc.setFileFilter(new ExtensionFileFilter(IOUtilities.MODEL_FILE_EXT, 
							Hub.string("modelFileDescription")));
					
					if((File)model.getAnnotation(Annotable.FILE)!=null){
						fc.setSelectedFile((File)model.getAnnotation(Annotable.FILE));
					}else{
						fc.setSelectedFile(new File(model.getName()));
					}
					
					int retVal;
					boolean fcDone=true;
					File file=null;
					do
					{
						retVal = fc.showSaveDialog(Hub.getMainWindow());
						if(retVal != JFileChooser.APPROVE_OPTION)
							break;
						file=fc.getSelectedFile();
			    		if(!file.getName().toLowerCase().endsWith("."+IOUtilities.MODEL_FILE_EXT))
			    			file=new File(file.getPath()+"."+IOUtilities.MODEL_FILE_EXT);
						if(file.exists())
						{
							int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
								Hub.string("fileExistAsk1")+file.getPath()+Hub.string("fileExistAsk2"),
								Hub.string("saveModelTitle"),
								JOptionPane.YES_NO_CANCEL_OPTION);
							fcDone=choice!=JOptionPane.NO_OPTION;
							if(choice!=JOptionPane.YES_OPTION)
								retVal=JFileChooser.CANCEL_OPTION;
						}
					} while(!fcDone);					
				
				if(retVal != JFileChooser.CANCEL_OPTION)
				{
					model.setAnnotation(Annotable.FILE, file);
					IOCoordinator.getInstance().save(model, (File)model.getAnnotation(Annotable.FILE));
				}
				}
			
			Hub.getMainWindow().setCursor(cursor);
		}	
			
	}
	
	
	public static class CloseCommand extends ActionCommand {
		
		public CloseCommand(){
			super("close.command");
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
			JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty(Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME")));
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
				latexWriter = new FileWriter(file);
				latexWriter.write(fileContents);
				latexWriter.close();
			}
			catch (IOException fileException)
			{
				Hub.displayAlert(Hub.string("problemLatexExport")+file.getPath());
			}
//			FileWriter latexWriter = null;
//					
//			if (fileContents == null)
//			{
//				return;
//			}
//			
//			try
//			{
//				LatexManager.getRenderer().latex2EPS(fileContents,file);
//			}
//			catch (IOException fileException)
//			{
//				Hub.displayAlert(Hub.string("problemLatexExport")+file.getPath());
//			}
//			catch (LatexRenderException e)
//			{
//				Hub.displayAlert(Hub.string("problemLatexExport")+file.getPath());
//			}
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
	
	
	public static class ImportExportCommand extends ActionCommand {

		public ImportExportCommand() {
			super("import.export.command");			
		}

		@Override
		protected void handleExecute() {
			ImportExportDialog ieDialog=new ImportExportDialog();
			
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