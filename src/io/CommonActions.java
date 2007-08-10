/**
 * 
 */
package io;

import io.fsa.ver2_1.CommonTasks;
import io.fsa.ver2_1.FileOperations;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

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
				Hub.displayAlert(Hub.string("errorsParsingXMLFileL1" + file.getName() + "\n" 
						+ Hub.string("errorsParsingXMLFileL2")));
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

	public static void save(DESModel model, File file)
	{
		//Make the model be saved by the IOCoordinator.
		//IOCoordinator will select the plugins which saves data and metadata information for model.
		if( model != null)
		{		
			if(file == null)
			{
				String path;
				try{
					path = (String)((File)model.getAnnotation(Annotable.FILE)).getPath();
				}catch(NullPointerException e){
					saveAs(model);
					return;
				}
				file = new File(path);
			}
			try
			{
				IOCoordinator.getInstance().save(model, file);
			}catch(IOException e)
			{
				Hub.displayAlert(e.getMessage());
			}

			String name=ParsingToolbox.removeFileType(file.getName());
			model.setAnnotation(Annotable.FILE,file);
			model.setName(name);
			if(!name.equals(model.getName()) && Hub.getWorkspace().getModel(name)!=null)
			{
				Hub.getWorkspace().removeModel(name);
			}
			Hub.persistentData.setProperty("LAST_PATH_SETTING_NAME", file.getParentFile().getAbsolutePath());
			model.modelSaved();
		}
	}

	public static void saveAs(DESModel model)
	{
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
				save(model, file);
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
				return;
			}
			ext.add(plugin.getExportExtension());
			desc.add(plugin.getDescription());
		}
		if(ext.size() <= 0)
		{
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
		}
		String lastFilter = Hub.persistentData.getProperty("LAST_USED_IMPORT_FILTER");
		FileFilter[] f = fc.getChoosableFileFilters();
		for(int i = 0; i < f.length; i++)
		{
			FileFilter last = f[i];
			if(last.getDescription().equals(lastFilter))
			{
				fc.setFileFilter(last);
			}
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
				File dst = File.createTempFile("tmp" + prefix, suffix);
				IOCoordinator.getInstance().importFile(file, dst, fc.getFileFilter().getDescription());
				if(dst.exists())
				{
					model = IOCoordinator.getInstance().load(dst);
				}
				if(model != null)
				{
					model.setName(prefix);
					Hub.getWorkspace().addModel(model);
					Hub.getWorkspace().setActiveModel(model.getName());
				}
				dst.delete();
			}catch(IOException e)
			{
				Hub.displayAlert(Hub.string("cantParseImport"));
			}
			Hub.getMainWindow().setCursor(cursor);
			try
			{
				Hub.persistentData.setProperty("LAST_PATH_SETTING_NAME", file.getParentFile().getAbsolutePath());
				Hub.persistentData.setProperty("LAST_USED_IMPORT_FILTER", fc.getFileFilter().getDescription());
			}catch(NullPointerException e)
			{
				//cancel button pressed... that's OK.
			}
		}
	}

	public static void exportModel()
	{		
		DESModel model = Hub.getWorkspace().getActiveModel();
		//Src is the file that will be used by the exporter.
		File src = null;
		if(model != null)
		{
			try{
				src = File.createTempFile("export", IOUtilities.MODEL_FILE_EXT);
				IOCoordinator.getInstance().save(model, src);
			}catch(IOException e)
			{
				Hub.displayAlert(Hub.string("ProblemsWhileExporting") + e.getMessage());
				return;
			}
		}else{
			//The model can't be null
			return;
		}

		JFileChooser fc;
		String path = Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME");
		if(path == null)
		{
			fc=new JFileChooser();
		}else
		{
			fc=new JFileChooser(path);	
		}
		fc.setDialogTitle(Hub.string("exportTitle"));
		fc.setSelectedFile(new File(path + "/" + model.getName()));
		Iterator<ImportExportPlugin> pluginIt = IOPluginManager.getInstance().getExporters().iterator();
		while(pluginIt.hasNext())
		{
			ImportExportPlugin p = pluginIt.next();
			fc.addChoosableFileFilter(new ExtensionFileFilter(p.getExportExtension(), p.getDescription()));
		}
		String lastFilter = Hub.persistentData.getProperty("LAST_USED_EXPORT_FILTER");
		FileFilter[] f = fc.getChoosableFileFilters();
		for(int i = 0; i < f.length; i++)
		{
			FileFilter last = f[i];
			if(last.getDescription().equals(lastFilter))
			{
				fc.setFileFilter(last);
			}
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
			try{
				IOCoordinator.getInstance().exportFile(src, file, fc.getFileFilter().getDescription());
			}
			catch(IOException e)
			{
				Hub.displayAlert(Hub.string("problemWhileExporting") + e.getMessage());
			}
		}
		try
		{
			Hub.persistentData.setProperty("LAST_PATH_SETTING_NAME", file.getParentFile().getAbsolutePath());
			Hub.persistentData.setProperty("LAST_USED_EXPORT_FILTER", fc.getFileFilter().getDescription());
		}catch(NullPointerException e)
		{
			//cancel button pressed... that's OK.
		}
		src.delete();
	}
}
