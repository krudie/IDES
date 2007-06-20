/**
 * 
 */
package pluggable.io;
import main.Annotable;
import main.Hub;
import model.DESModel;
import model.fsa.FSAModel;
import io.ParsingToolbox;
import io.fsa.ver2_1.AutomatonParser;
import io.fsa.ver2_1.AutomatonParser20;
import io.fsa.ver2_1.FileOperations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pluggable.io.IOPluginManager;
import pluggable.io.FileIOPlugin;
import presentation.template.TemplateGraph;
import main.Hub;
import io.AbstractParser;
/**
 * @author christiansilvano
 * \TODO make it thread-safe
 */
public final class IOCoordinator extends AbstractParser{
	//Singleton instance:
	private static IOCoordinator instance = null;
	Set<String> metaData = new HashSet<String>();	
	protected static final String ATTRIBUTE_TYPE = "type", ATTRIBUTE_TAG = "tag";
	protected static final String NOTHING = "nothing";
	
	private String dataType = new String();
	private Set<String> metaTags = new HashSet<String>();
	
	private IOCoordinator()
	{
		dataType = NOTHING;

		//Initialize parser:
		try {
            xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            xmlReader.setContentHandler(this);
        } catch (ParserConfigurationException pce) {
            System.err
                    .println("AbstractParser: could not configure parser, message: "
                            + pce.getMessage());
        } catch (SAXException se) {
            System.err
                    .println("AbstractParser: could not do something, message: "
                            + se.getMessage());
        }
        
        
	}
	
	public static IOCoordinator getInstance()
	{
		if (instance == null)
		{
			instance = new IOCoordinator(); 
		}
		return instance;
	}
		
	public boolean save(DESModel model, File file)
	{
		//Read the dataType from the plugin modelDescriptor
		String type = model.getModelDescriptor().getIOTypeDescription();

		//Get the plugin capable of saving a model of the type "type"
		//Currently there must be just one data saver for a model type.
		FileIOPlugin dataSaver = IOPluginManager.getInstance().getDataSaver(type);
		//Get all the plugins capable of saving the metaTags addressed for
		//type and tag. There can be several different meta savers for a specific
		//data type.
		Set<FileIOPlugin> metaSavers = IOPluginManager.getInstance().getMetaSavers(type);
 
		//1 - Open the file.
		//2 - Make the dataSaver plugin save the data information on the file
		//3 - Make the metaSavers one by one save the meta information on the file
		//4 - close the file.
		//5 - Return true if the operation was a success, otherwise return false.

		
		
		System.out.println(model.getModelDescriptor().getTypeDescription());
		return (FileOperations.saveAutomaton((FSAModel)model,(File)model.getAnnotation(Annotable.FILE)));	
	}
	
	//Get the "type" of the model in file and ask the plugin that manage this
	//kind of "type" to load the DES.
	public DESModel load(File file)
	{
		String type = getType(file);
		//System.out.println(type);
		if(type == null)
		{
			//File error, maybe the file is not an IDES file
			//THROW ERROR
			return null;
		}
		
		DESModel returnModel = null;
		//Ask the plugin manager for the correct plugin to handle file loading
		//The variable dataType is equivalent of the IOTypeDescription of the model to 
		//be loaded.			
		FileIOPlugin plugin = IOPluginManager.getInstance().getDataLoader(type);
		if(plugin == null)
		{
			//error (see IOPluginManager.getDataLoader(String)
			return null;
		}
		returnModel = plugin.loadData(file, file.getParentFile());
		
		//Return the model loaded by the plugin
		return returnModel;
	}
	
	  
	private void parse(File file){
	        parsingErrors = "";
	        try{
	            xmlReader.parse(new InputSource(new FileInputStream(file)));
	        }
	        catch(FileNotFoundException fnfe){
	            parsingErrors += file.getName() + ": " + fnfe.getMessage() + "\n";
	        }
	        catch(IOException ioe){
	            parsingErrors += file.getName() + ": " + ioe.getMessage() + "\n";
	        }
	        catch(SAXException saxe){
	            parsingErrors += file.getName() + ": " + saxe.getMessage() + "\n";
	        }
	        catch(NullPointerException npe){
	        	parsingErrors += file.getName() + ": " + npe.getMessage() + "\n";
	        }
	        
	    }
	    
	   
	    public void startElement(String uri, String localName, String qName, Attributes atts)
	    {
	    	dataType = (atts.getValue(ATTRIBUTE_TYPE) != null ? atts.getValue(ATTRIBUTE_TYPE) :dataType);
	    	if(atts.getValue(ATTRIBUTE_TAG) != null)
	    	{
	    		metaTags.add(atts.getValue(ATTRIBUTE_TAG));
	    	}
	    }
	    
	
	    
	    
	    
	    
	    //PARSE METHODS:
	    
	    private String getType(File file)
	    {
	    	parse(file);
	    	String returnString = dataType;
	    	metaTags.clear();
	    	dataType = NOTHING;
	    	if(returnString == NOTHING)
	    	{
	    		return null;
	    	}
	    	return returnString;
	    }
	    
	    private Set<String> getMetaTags(File file)
	    {
	    	parse(file);
	    	Set<String> returnSet = metaTags;
	    	metaTags.clear();
	    	dataType = NOTHING;
	    	if(metaTags.size() == 0)
	    	{
	    		return null;
	    	}
	    	return returnSet;
	    }

	    
	    
 }	

