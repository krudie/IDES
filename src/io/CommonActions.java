/**
 * 
 */
package io;

import io.fsa.ver2_1.FileOperations;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Annotable;
import main.Hub;
import model.DESModel;

import org.pietschy.command.file.ExtensionFileFilter;

import pluggable.io.IOCoordinator;
import pluggable.io.IOPluginManager;
import pluggable.io.ImportExportPlugin;

/**
 * @author christiansilvano
 *
 */
public class CommonActions {

	public static void load()
	{
		JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME"));
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
			DESModel model = null;
			File file = fc.getSelectedFile();
			try{
				model = IOCoordinator.getInstance().load(file);
			}catch(IOException e)
			{
				Hub.displayAlert(e.getMessage());
				return;
			}
			if(model != null)
			{
				model.setName(ParsingToolbox.removeFileType(file.getName()));
				model.setAnnotation(Annotable.FILE,file);
				Hub.getWorkspace().addModel(model);
				Hub.getWorkspace().setActiveModel(model.getName());
			}
			Hub.persistentData.setProperty("LAST_PATH_SETTING_NAME",file.getParent());
			Hub.getMainWindow().setCursor(cursor);
		}	
	}

	public static void save()
	{
		//Make the model be saved by the IOCoordinator.
		//IOCoordinator will select the plugins which saves data and metadata information for model.
		DESModel model = Hub.getWorkspace().getActiveModel();
		if( model != null)
		{		
			String path;
			try{
				path = (String)((File)model.getAnnotation(Annotable.FILE)).getPath();
			}catch(NullPointerException e){
				saveAs();
				return;
			}

			File file = new File(path);
			IOCoordinator.getInstance().save(model, file);
			Hub.persistentData.setProperty("LAST_PATH_SETTING_NAME", file.getParentFile().getAbsolutePath());

			String newName=ParsingToolbox.removeFileType(file.getName());
			if(!newName.equals(model.getName())
					&&Hub.getWorkspace().getModel(newName)!=null)
				Hub.getWorkspace().removeModel(newName);

			model.setName(newName);
			model.setAnnotation(Annotable.FILE,file);

		}
	}

	public static void saveAs()
	{
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
			try
			{
				Hub.persistentData.setProperty("LAST_PATH_SETTING_NAME", file.getParentFile().getAbsolutePath());
			}catch(NullPointerException e)
			{
				//cancel button pressed... that's OK.
			}
		}
	}

	public static void importModel()
	{
		JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME"));
		fc.setDialogTitle(Hub.string("importTitle"));

		Vector<String> ext = new Vector<String>();
		Vector<String> desc = new Vector<String>();
		Iterator<ImportExportPlugin> it = IOPluginManager.getInstance().getImporters().iterator();
		while(it.hasNext())
		{
			ImportExportPlugin plugin = it.next();
			if(plugin.getExportExtension() == null)
			{
				//TODO THROW ERROR: Plugin Error
			}
			ext.add(plugin.getExportExtension());
			desc.add(plugin.getDescription());
		}
		if(ext.size() <= 0)
		{
			//TODO: SHOW MESSAGE:
			//NO PLUGINS REGISTERED TO IMPORT!
			//actually this should never happen since IDES already register some "basic" plugins
			//in the Main.main() method.
			return;
		}
		Iterator<String> extIt = ext.iterator();
		Iterator<String> descIt = desc.iterator(); 
		while(extIt.hasNext())
		{
			fc.addChoosableFileFilter(new ExtensionFileFilter(extIt.next(), descIt.next()));
			//fc.setFileFilter();
		}


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
			DESModel model = null;
			File file = fc.getSelectedFile();
			try{
				String prefix = io.ParsingToolbox.removeFileType(file.getName());
				//The suffix will be the IDES file format (obviously)
				String suffix = IOUtilities.MODEL_FILE_EXT;
				File dst = File.createTempFile(prefix, suffix);
				IOCoordinator.getInstance().importFile(file, dst, fc.getFileFilter().getDescription());
				model = IOCoordinator.getInstance().load(dst);
				if(model != null)
				{
					model.setName(ParsingToolbox.removeFileType(Hub.string("newModelName")));
					model.setAnnotation(Annotable.FILE,null);
					Hub.getWorkspace().addModel(model);
					Hub.getWorkspace().setActiveModel(model.getName());
				}
			}catch(IOException e)
			{
				//TODO show that an IO error has occurred
			}


			Hub.getMainWindow().setCursor(cursor);
		}
	}

}
