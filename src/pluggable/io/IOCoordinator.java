/**
 * 
 */
package pluggable.io;
import main.Annotable;
import main.Hub;
import model.DESModel;
import io.IOUtilities;
import io.ParsingToolbox;
import io.ProtectedInputStream;
import io.WrappedPrintStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import io.AbstractParser;
import io.fsa.ver2_1.AutomatonParser20;
/**
 * @author christiansilvano
 */
public final class IOCoordinator{
	//Singleton instance:
	private static IOCoordinator instance = null;
	private IOCoordinator()
	{    
	}

	public static IOCoordinator getInstance()
	{
		if (instance == null)
		{
			instance = new IOCoordinator(); 
		}
		return instance;
	}

	public void save(DESModel model, File file) throws IOException
	{	
		//Get the plugin capable of saving a model of the type "type"
		//Currently there must be just one data saver for a model type.
		FileIOPlugin dataSaver = IOPluginManager.getInstance().getDataSaver(model.getModelDescriptor().getPreferredModelInterface());

		//Read the dataType from the plugin modelDescriptor
		String type = dataSaver.getIOTypeDescriptor();

		//Get all the plugins capable of saving the metaTags for ""type""
		//There can be several different meta savers for a specific data type.
		Set<FileIOPlugin> metaSavers = IOPluginManager.getInstance().getMetaSavers(model.getModelDescriptor().getPreferredModelInterface());
		Iterator<FileIOPlugin> metaIt = metaSavers.iterator();

		//Open  ""file"" and start writing the header of the IDES file format
		WrappedPrintStream ps = null;
		ps=new WrappedPrintStream(IOUtilities.getPrintStream(file));
		ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		ps.println("<model version=\"2.1\" type=\""+ type + "\" id=\""+model.getId()+"\">");
		ps.println("<data>");

		//Make the dataSaver plugin save the data information on the 
		//file (protect the original content)
		dataSaver.saveData(ps, model, file.getParentFile());
		//The data information is stored: 
		ps.println("</data>");
		//3 - Make the metaSavers one by one save the meta information on the file
		while(metaIt.hasNext())
		{
			FileIOPlugin plugin = metaIt.next();
			Iterator<String> tags = plugin.getMetaTags().iterator();
			while(tags.hasNext())
			{
				String tag = tags.next();
				ps.println("<meta tag=\""+ tag +"\" version=\"2.1\">");
				plugin.saveMeta(ps, model, type, tag);
				ps.println("</meta>");
			}
		}
		ps.println("</model>");
		ps.closeWrappedPrintStream();
	}

	//Get the "type" of the model in file and ask the plugin that manage this
	//kind of "type" to load the DES.
	public DESModel load(File file) throws IOException
	{
		//try to deal with files from IDES ver 2.0
		if(isFileVer20(file))
		{
			AutomatonParser20 parser20=new AutomatonParser20();
			DESModel model=parser20.parse(file);
			if(model==null||!"".equals(parser20.getParsingErrors()))
			{
				throw new FileLoadException(parser20.getParsingErrors(),model);
			}
			return model;
		}
		
		// set when FileLoadException is encountered in loadData or loadMeta
		String errorMsg="";
		boolean errorEncountered=false;
		
		DESModel returnModel = null;
		TagRecovery xmlParser = new TagRecovery();
		String type = xmlParser.getType(file);
		Set<String> metaTags = xmlParser.getMetaTags(file);
		//System.out.println(type);
		if(type == null)
		{
			throw new FileLoadException(Hub.string("xmlParsingDefNotFound"));
		}

		//Create a FileInputStream with "file"
		FileInputStream fis = new FileInputStream(file);
		//Create a BufferedHeader (to "remember" the last read position in the FileInputStream)
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		//LOADING DATA TO THE MODEL
		FileIOPlugin plugin = IOPluginManager.getInstance().getDataLoader(type);
		if(plugin == null)
		{
			throw new FileLoadException(Hub.string("pluginNotFoundFile"));
		}
		DataProtector dp = new DataProtector(br);
		InputStream dataStream = dp.getXmlContent("data", file);
		try
		{
			returnModel = plugin.loadData(dataStream, file.getParentFile());
		}catch(FileLoadException e)
		{
			if(e.getPartialModel()==null)
			{
				throw e;
			}
			else
			{
				errorEncountered=true;
				errorMsg+=e.getMessage();
				returnModel=e.getPartialModel();
			}
		}
		//LOADING METADATA TO THE MODEL:
		Iterator<String> mIt = metaTags.iterator();
		while(mIt.hasNext())//For each metaTag in the file
		{
			//Get a stream countaining the metaInformation
			InputStream metaStream = dp.getXmlContent("meta", file);
			//Get a string with the "tag" for the current meta section 
			String meta = mIt.next();
			//Get all the plugins which loads metadata from the pair: (type, meta)
			FileIOPlugin metaPlugin = IOPluginManager.getInstance().getMetaLoaders(type, meta);
			if(metaPlugin==null)
			{
				errorEncountered=true;
				errorMsg+=Hub.string("pluginNotFoundFile")+"\n";
			}else{
					try
					{
						metaPlugin.loadMeta(metaStream, returnModel);
					}catch(FileLoadException e)
					{
						errorEncountered=true;
						errorMsg+=e.getMessage();
					}
			}
		}
		br.close();
		fis.close();
		if(returnModel !=null)
		{
			returnModel.setName(ParsingToolbox.removeFileType(file.getName()));
			returnModel.setAnnotation(Annotable.FILE,file);
		}
		if(errorEncountered)
		{
			throw new FileLoadException(errorMsg,returnModel);
		}
		return returnModel;
	}


	private class DataProtector{
		private int offset;
		BufferedReader head = null;
		public DataProtector(BufferedReader bh)
		{
			offset = 0;
			head = bh;
		}

		/**
		 * Finds the positions of the first and last byte between a "tag" in the file, and returns an InputStream
		 * created using these positions.
		 * E.g.: "INFORMATION<tag>CONTENT</tag>INFORMATION", would return an InputStream with the content "INFORMATION" inside <code>file</code>.
		 * @param tag , the tag from which the information will be got from.
		 * @param f , a File countaining the information. 
		 */
		public InputStream getXmlContent(String tag, File file){
			int startOfTag = 0, endOfTag = 0;
			Boolean tagStarted = false, parseFinished = false, foundFirst=false;
			ArrayList<Character> buffer = new ArrayList<Character>(1 + tag.length()); 
			try{
				//Set the head cursor to the position it was at the last time it was parsed by this class
				head.reset();
				head.skip((long)offset);
			}catch(IOException e)
			{

			}
			int currentChar=0;
			while(!parseFinished&&currentChar>-1)
			{
				currentChar = -1;
				try{
					//Read the current byte
					currentChar = head.read();
					offset++;
				}catch(IOException e)
				{
					//IOERROR
				}


				//LOOK FOR THE BEGGINING OF THE TAG
				if(!foundFirst)	
				{
					//If a XML tag starting symbol "<" was found, start buffering the bytes: 
					if(currentChar == (int)(new Character('<')) & !tagStarted)
					{
						tagStarted = true;
//						startOfTag = offset-1;
					}

					//Buffer the bytes:
					if(tagStarted)
					{
						//If the size of the buffer is less than the needed size to store ("<" + tag),
						//keeps buffering
						if(buffer.size() < tag.length() + "<".length())
						{
							if((char)currentChar == '>')
							{
								buffer.clear();
								tagStarted = false;
							}else
							{
								buffer.add((char)currentChar);
							}
						}else
						{
							tagStarted = false;
							String readTag = "";
							for(int i = 0; i < buffer.size(); i++)
							{
								readTag += buffer.get(i);
							}
							buffer.clear();
							if(readTag.equals("<" + tag))
							{
//								System.out.println(readTag + ", <" + tag);
								startOfTag = offset+1; 
								buffer.clear();
								foundFirst = true;
								tagStarted = false;
							}
						}
					}

				}else{//LOOK FOR THE END OF THE TAG
					//If a XML tag starting symbol "<" was found, start buffering the bytes: 
					if(currentChar == (int)('<') & !tagStarted)
					{
						tagStarted = true;
						endOfTag = offset-1;
					}

					//Buffer the bytes:
					if(tagStarted)
					{
						//If the size of the buffer is less than the needed size to store ("<" + tag),
						//keeps buffering
						if(buffer.size() < tag.length() + "</".length())
						{
							if((char)currentChar == '>')
							{
								buffer.clear();
								tagStarted = false;
							}else
							{
								buffer.add((char)currentChar);
							}
						}else
						{
							tagStarted = false;
							String readTag = "";
							for(int i = 0; i < buffer.size(); i++)
							{
								readTag += buffer.get(i);
							}
							buffer.clear();
							if(readTag.equals("</" + tag))
							{
								buffer.clear();
								parseFinished= true;
							}
						}
					}
				}
			}  
			try{
				//Wrapped FileInputStream with limited access to the file content.
				//This InputStream will limit the access to the stream, so the plugins will just "see" what
				//regards to them.
				return new ProtectedInputStream(file, startOfTag, endOfTag-startOfTag);
			}catch(FileNotFoundException e)
			{
				//This error should not happen, since file is the descriptor for a file selected by the user,
				//so it can't not exist.
				return null;
			}
		}
	}

	private class TagRecovery extends AbstractParser{
		Set<String> metaData = new HashSet<String>();	
		protected static final String ATTRIBUTE_TYPE = "type", ATTRIBUTE_TAG = "tag";
		private String dataType = null;
		private Set<String> metaTags = null;

		public TagRecovery()
		{
			//Initialize parser:
			try {
				xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
				xmlReader.setContentHandler(this);
			} catch (ParserConfigurationException pce) {
//				System.err
//				.println("AbstractParser: could not configure parser, message: "
//						+ pce.getMessage());
			} catch (SAXException se) {
//				System.err
//				.println("AbstractParser: could not do something, message: "
//						+ se.getMessage());
			}

		}
		private void parse(File file){
			parsingErrors = "";
			try{
				xmlReader.parse(new InputSource(new FileInputStream(file)));
			}
			catch(FileNotFoundException fnfe){
				parsingErrors += fnfe.getMessage() + "\n";
			}
			catch(IOException ioe){
				parsingErrors += ioe.getMessage() + "\n";
			}
			catch(SAXException saxe){
				parsingErrors += saxe.getMessage() + "\n";
			}
			catch(NullPointerException npe){
				parsingErrors += npe.getMessage() + "\n";
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
			dataType=null;
			parse(file);
			return dataType;
		}

		private Set<String> getMetaTags(File file)
		{
			metaTags=new HashSet<String>();
			parse(file);
			return metaTags;
		}
	}

	public DESModel importFile(File src, String description) throws IOException
	{
		ImportExportPlugin plugin = IOPluginManager.getInstance().getImporter(description);
		if(plugin==null)
		{
			throw new FormatTranslationException(Hub.string("pluginNotFoundFile"));
		}
		DESModel model=null;
		File dst = File.createTempFile("IDESimport", IOUtilities.MODEL_FILE_EXT);
		try
		{
			plugin.importFile(src, dst);
			model=load(dst);
			model.removeAnnotation(Annotable.FILE);
			model.setName(ParsingToolbox.removeFileType(src.getName()));
		}
		catch(FileLoadException e)
		{
			if(e.getPartialModel()!=null)
			{
				e.getPartialModel().removeAnnotation(Annotable.FILE);
				e.getPartialModel().setName(ParsingToolbox.removeFileType(src.getName()));
			}
			throw e;
		}
		finally
		{
			dst.delete();
		}
		return model;
	}

	public void export(DESModel model, File dst, String description) throws IOException
	{
		if(model == null)
		{
			throw new FormatTranslationException(Hub.string("internalError"));
		}
		Set<ImportExportPlugin> plugins = IOPluginManager.getInstance().getExporters(model.getModelDescriptor().getPreferredModelInterface());
		ImportExportPlugin plugin=null;
		for(ImportExportPlugin p:plugins)
		{
			if(p.getDescription().equals(description))
			{
				plugin=p;
				break;
			}
		}
		if(plugin==null)
		{
			throw new FormatTranslationException(Hub.string("pluginNotFoundFile"));
		}
		File src = File.createTempFile("IDESexport", IOUtilities.MODEL_FILE_EXT);
		try
		{
			save(model, src);
			plugin.exportFile(src, dst);
		}
		finally
		{
			src.delete();
		}
	}
	
	protected boolean isFileVer20(File file) throws IOException
	{
		BufferedReader in=new BufferedReader(new FileReader(file));
		in.readLine();
		String line=in.readLine();
		in.close();
		if(line!=null && line.startsWith("<automaton>"))
		{
			return true;
		}
		return false;
	}
}


