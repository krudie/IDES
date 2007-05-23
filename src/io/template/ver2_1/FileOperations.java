package io.template.ver2_1;

import io.IOUtilities;
import io.ParsingToolbox;
import io.fsa.ver2_1.AutomatonParser;
import io.fsa.ver2_1.AutomatonParser20;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Annotable;
import main.Hub;
import model.fsa.FSAModel;

import org.pietschy.command.file.ExtensionFileFilter;

import presentation.template.TemplateGraph;

public class FileOperations {

	public static final String LAST_PATH_SETTING_NAME="lastUsedPath";

	public static boolean saveAs(TemplateGraph g) {
		JFileChooser fc;
		
		if((File)g.getAnnotation(Annotable.FILE)!=null){
			fc=new JFileChooser(((File)g.getAnnotation(Annotable.FILE)).getParent());
		}else{
			fc=new JFileChooser(Hub.persistentData.getProperty(LAST_PATH_SETTING_NAME));
		}
		
		fc.setDialogTitle(Hub.string("saveModelTitle"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		fc.setFileFilter(new ExtensionFileFilter(IOUtilities.MODEL_FILE_EXT, 
//				Hub.string("modelFileDescription")));
		
		if((File)g.getAnnotation(Annotable.FILE)!=null){
			fc.setSelectedFile((File)g.getAnnotation(Annotable.FILE));
		}else{
			fc.setSelectedFile(new File(g.getName()));
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
    	
		//MacOS fix
		if(file==null)
		{
			Hub.displayAlert(Hub.string("cantSaveTemplate"));
		}
		if(file.isFile())
		{
			file=file.getParentFile();
		}
		//end MacOS fix
		
		if(retVal == JFileChooser.APPROVE_OPTION){
    		XMLExporter.graph2XML(g,file);
    		return true;
		}
    	return false;
	}
	
	public static TemplateGraph openAutomaton(File f) {
        TemplateGraph graph = null;
        if(!f.canRead())
        {
        	Hub.displayAlert(Hub.string("fileCantRead")+f.getPath());
        	return graph;
        }
        String errors="";
        try
        {
        	graph = TemplateParser.parse(f);
        }catch(Exception e)
        {
        	graph=null;
        	errors+=e.getMessage();
//        	e.printStackTrace();
        }
        if(!"".equals(errors))
        {
        	Hub.displayAlert(Hub.string("errorsParsingXMLFileL1")+f.getPath()+
        			"\n"+Hub.string("errorsParsingXMLFileL2"));
        }
        if(graph!=null)
        {
        	graph.setAnnotation(Annotable.FILE,f);
        }
        Hub.persistentData.setProperty(LAST_PATH_SETTING_NAME,f.getParent());
        return graph;
	}
}
