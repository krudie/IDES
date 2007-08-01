/**
 * 
 */
package pluggable.io;
import main.Hub;
import model.DESModel;
import io.IOUtilities;
import io.ProtectedInputStream;
import io.WrappedPrintStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	public boolean save(DESModel model, File file)
	{	
		//Read the dataType from the plugin modelDescriptor
		String type = model.getModelDescriptor().getIOTypeDescription();

		//Get the plugin capable of saving a model of the type "type"
		//Currently there must be just one data saver for a model type.
		FileIOPlugin dataSaver = IOPluginManager.getInstance().getDataSaver(type);

		//Get all the plugins capable of saving the metaTags for ""type""
		//There can be several different meta savers for a specific data type.
		Set<FileIOPlugin> metaSavers = IOPluginManager.getInstance().getMetaSavers(type);
		Iterator<FileIOPlugin> metaIt = metaSavers.iterator();

		//Open  ""file"" and start writing the header of the IDES file format
		WrappedPrintStream ps = null;
		try{
			ps=new WrappedPrintStream(IOUtilities.getPrintStream(file));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
			Iterator<String> tags = plugin.getMetaTags(type).iterator();
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
		//4 - close the file.
		//5 - Return true if the operation was a success, otherwise return false.
		//TODO THROW IO EXCEPTION OR PLUGIN EXCEPTIONS IF SOMETHING HAPPENS and return false
		return true;
	}

	//Get the "type" of the model in file and ask the plugin that manage this
	//kind of "type" to load the DES.
	public DESModel load(File file) throws IOException
	{
		if(!file.exists())
		{
			//TODO Show an error msg.
		}

		DESModel returnModel = null;
		XmlParser xmlParser = new XmlParser();
		String type = xmlParser.getType(file);
		Set<String> metaTags = xmlParser.getMetaTags(file);
		//System.out.println(type);
		if(type == null)
		{
			throw new IOException(Hub.string("errorsParsingXMLFileL1"));
		}

		//Create a FileInputStream with "file"
		FileInputStream fis = new FileInputStream(file);
		//Create a BufferedHeader (to "remember" the last read position in the FileInputStream)
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		//LOADING DATA TO THE MODEL
		FileIOPlugin plugin = IOPluginManager.getInstance().getDataLoader(type);
		if(plugin == null)
		{
			//TODO THROW A PLUGIN EXCEPTION
			return null;
		}
		DataProtector dp = new DataProtector(br);
		InputStream dataStream = dp.getXmlContent("data", file);

		if(dataStream == null)
		{
			//TODO THROW IO EXCEPTION
		}	

		returnModel = plugin.loadData(dataStream, file.getParentFile());
//		System.out.println("DATA LOADED!");
		//LOADING METADATA TO THE MODEL:
		Iterator<String> mIt = metaTags.iterator();
		while(mIt.hasNext())//For each metaTag in the file
		{
			//Get a stream countaining the metaInformation
			InputStream metaStream = dp.getXmlContent("meta", file);
			//Get a string with the "tag" for the current meta section 
			String meta = mIt.next();
			//Get all the plugins which loads metadata from the pair: (type, meta)
			Set<FileIOPlugin>plugins = IOPluginManager.getInstance().getMetLoaders(type, meta);
			if(plugins == null)
			{
				//TODO Show a message to the user saying that there are no plugins capable of loading the information
				// for the metadata: meta
			}else{
				//Make every plugin load its metadata
				Iterator<FileIOPlugin> pIt = plugins.iterator();
				while(pIt.hasNext())
				{
					FileIOPlugin p = pIt.next();
					p.loadMeta(metaStream, returnModel);
				}
			}
		}
		br.close();
		fis.close();
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
			while(!parseFinished)
			{
				int currentChar = -1;
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

	private class XmlParser extends AbstractParser{
		Set<String> metaData = new HashSet<String>();	
		protected static final String ATTRIBUTE_TYPE = "type", ATTRIBUTE_TAG = "tag";
		protected static final String NOTHING = "nothing";		
		private String dataType = new String();
		private Set<String> metaTags = new HashSet<String>();

		public XmlParser()
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
			dataType = NOTHING;
			if(metaTags.size() == 0)
			{
				return null;
			}
			return returnSet;
		}
	}

	public void importFile(File src, File dst, String description) throws IOException
	{
		Set<ImportExportPlugin> plugins = IOPluginManager.getInstance().getImporters(description);
		switch(plugins.size())
		{
		case 0:
			//TODO: Show a message that no plugin was registered to import
		case 1:
			plugins.iterator().next().importFile(src, dst);
		default:
			//TODO throw an error	
		}
		
		if(dst != null)
		{

		}
	}

}


