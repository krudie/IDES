/**
 * 
 */
package io;

import ides.api.core.Annotable;
import ides.api.core.Hub;
import ides.api.plugin.io.FileIOPlugin;
import ides.api.plugin.io.FileLoadException;
import ides.api.plugin.io.FileSaveException;
import ides.api.plugin.io.FormatTranslationException;
import ides.api.plugin.io.IOPluginManager;
import ides.api.plugin.io.IOSubsytem;
import ides.api.plugin.io.ImportExportPlugin;
import ides.api.plugin.model.DESModel;
import io.fsa.ver2_1.AutomatonParser20;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author christiansilvano
 */
public final class IOCoordinator implements IOSubsytem
{
	// Singleton instance:
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
		// Get the plugin capable of saving a model of the type "type"
		// Currently there must be just one data saver for a model type.
		FileIOPlugin dataSaver = IOPluginManager.instance().getDataSaver(model
				.getModelType().getMainPerspective());
		if (dataSaver == null)
		{
			throw new FileSaveException(Hub.string("errorCannotSaveType")
					+ model.getModelType().getDescription());
		}

		// Read the dataType and version from the plugin modelDescriptor
		String type = dataSaver.getIOTypeDescriptor();
		String version = dataSaver.getSaveDataVersion();

		// Get all the plugins capable of saving the metaTags for ""type""
		// There can be several different meta savers for a specific data type.
		Set<FileIOPlugin> metaSavers = IOPluginManager
				.instance().getMetaSavers(model
						.getModelType().getMainPerspective());
		if (metaSavers == null)
		{
			metaSavers = new HashSet<FileIOPlugin>();
		}
		Iterator<FileIOPlugin> metaIt = metaSavers.iterator();

		// Open ""file"" and start writing the header of the IDES file format
		WrappedPrintStream ps = null;
		ps = new WrappedPrintStream(IOUtilities.getPrintStream(file));
		ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		ps.println("<model version=\"" + version + "\" type=\"" + type
				+ "\" id=\"" + model.getName() + "\">");
		ps.println("<data>");

		// Make the dataSaver plugin save the data information on the
		// file (protect the original content)
		dataSaver.saveData(ps, model, file.getAbsolutePath());
		// The data information is stored:
		ps.println("</data>");
		// 3 - Make the metaSavers one by one save the meta information on the
		// file
		while (metaIt.hasNext())
		{
			FileIOPlugin plugin = metaIt.next();
			Iterator<String> tags = plugin.getMetaTags().iterator();
			while (tags.hasNext())
			{
				String tag = tags.next();
				ps.println("<meta tag=\"" + tag + "\" version=\""
						+ plugin.getSaveMetaVersion(tag) + "\">");
				plugin.saveMeta(ps, model, tag);
				ps.println("</meta>");
			}
		}
		ps.println("</model>");
		ps.closeWrappedPrintStream();
	}

	// Get the "type" of the model in file and ask the plugin that manage this
	// kind of "type" to load the DES.
	public DESModel load(File file) throws IOException
	{
		// try to deal with files from IDES ver 2.0
		if (isFileVer20(file))
		{
			AutomatonParser20 parser20 = new AutomatonParser20();
			DESModel model = parser20.parse(file);
			if (model == null || !"".equals(parser20.getParsingErrors()))
			{
				throw new FileLoadException(parser20.getParsingErrors(), model);
			}
			return model;
		}

		// set when FileLoadException is encountered in loadData or loadMeta
		String errorMsg = "";
		boolean errorEncountered = false;

		DESModel returnModel = null;
		TagRecovery xmlParser = new TagRecovery(file);
		String type = xmlParser.getType();
		Set<String> metaTags = xmlParser.getMetaTags();
		// System.out.println(type);
		if (type == null)
		{
			throw new FileLoadException(Hub.string("xmlParsingDefNotFound"));
		}

		// Create a FileInputStream with "file"
		FileInputStream fis = new FileInputStream(file);
		// Create a BufferedHeader (to "remember" the last read position in the
		// FileInputStream)
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		// LOADING DATA TO THE MODEL
		FileIOPlugin plugin = IOPluginManager.instance().getDataLoader(type);
		if (plugin == null)
		{
			throw new FileLoadException(Hub.string("pluginNotFoundFile"));
		}
		DataProtector dp = new DataProtector(br);
		InputStream dataStream = dp.getXmlContent("data", file);
		try
		{
			returnModel = plugin.loadData(xmlParser.getVersion(),
					dataStream,
					file.getAbsolutePath());
		}
		catch (FileLoadException e)
		{
			if (e.getPartialModel() == null)
			{
				throw e;
			}
			else
			{
				errorEncountered = true;
				errorMsg += e.getMessage();
				returnModel = e.getPartialModel();
			}
		}
		// LOADING METADATA TO THE MODEL:
		Iterator<String> mIt = metaTags.iterator();
		while (mIt.hasNext())// For each metaTag in the file
		{
			// Get a stream countaining the metaInformation
			InputStream metaStream = dp.getXmlContent("meta", file);
			// Get a string with the "tag" for the current meta section
			String meta = mIt.next();
			// Get all the plugins which loads metadata from the pair: (type,
			// meta)
			FileIOPlugin metaPlugin = IOPluginManager
					.instance().getMetaLoaders(type, meta);
			if (metaPlugin == null)
			{
				errorEncountered = true;
				errorMsg += Hub.string("pluginNotFoundFile") + "\n";
			}
			else
			{
				try
				{
					metaPlugin.loadMeta(xmlParser.getMetaVersion(meta),
							metaStream,
							returnModel,
							meta);
				}
				catch (FileLoadException e)
				{
					errorEncountered = true;
					errorMsg += e.getMessage();
				}
			}
		}
		br.close();
		fis.close();
		if (returnModel != null)
		{
			returnModel.setName(ParsingToolbox.removeFileType(file.getName()));
			returnModel.setAnnotation(Annotable.FILE, file);
		}
		if (errorEncountered)
		{
			throw new FileLoadException(errorMsg, returnModel);
		}
		return returnModel;
	}

	private class DataProtector
	{
		private int offset;

		BufferedReader head = null;

		public DataProtector(BufferedReader bh)
		{
			offset = 0;
			head = bh;
		}

		/**
		 * Finds the positions of the first and last byte between a "tag" in the
		 * file, and returns an InputStream created using these positions. E.g.:
		 * "INFORMATION<tag>CONTENT</tag>INFORMATION", would return an
		 * InputStream with the content "INFORMATION" inside <code>file</code>.
		 * 
		 * @param tag
		 *            , the tag from which the information will be got from.
		 * @param f
		 *            , a File countaining the information.
		 */
		public InputStream getXmlContent(String tag, File file)
		{
			int startOfTag = 0, endOfTag = 0;
			Boolean tagStarted = false, parseFinished = false, foundFirst = false;
			ArrayList<Character> buffer = new ArrayList<Character>(1 + tag
					.length());
			try
			{
				// Set the head cursor to the position it was at the last time
				// it was parsed by this class
				head.reset();
				head.skip(offset);
			}
			catch (IOException e)
			{

			}
			int currentChar = 0;
			while (!parseFinished && currentChar > -1)
			{
				currentChar = -1;
				try
				{
					// Read the current byte
					currentChar = head.read();
					offset++;
				}
				catch (IOException e)
				{
					// IOERROR
				}

				// LOOK FOR THE BEGGINING OF THE TAG
				if (!foundFirst)
				{
					// If a XML tag starting symbol "<" was found, start
					// buffering the bytes:
					if (currentChar == (int)(new Character('<')) & !tagStarted)
					{
						tagStarted = true;
						// startOfTag = offset-1;
					}

					// Buffer the bytes:
					if (tagStarted)
					{
						// If the size of the buffer is less than the needed
						// size to store ("<" + tag),
						// keeps buffering
						if (buffer.size() < tag.length() + "<".length())
						{
							if ((char)currentChar == '>')
							{
								buffer.clear();
								tagStarted = false;
							}
							else
							{
								buffer.add((char)currentChar);
							}
						}
						else
						{
							tagStarted = false;
							String readTag = "";
							for (int i = 0; i < buffer.size(); i++)
							{
								readTag += buffer.get(i);
							}
							buffer.clear();
							if (readTag.equals("<" + tag))
							{
								// System.out.println(readTag + ", <" + tag);
								startOfTag = offset + 1;
								buffer.clear();
								foundFirst = true;
								tagStarted = false;
							}
						}
					}

				}
				else
				{// LOOK FOR THE END OF THE TAG
					// If a XML tag starting symbol "<" was found, start
					// buffering the bytes:
					if (currentChar == ('<') & !tagStarted)
					{
						tagStarted = true;
						endOfTag = offset - 1;
					}

					// Buffer the bytes:
					if (tagStarted)
					{
						// If the size of the buffer is less than the needed
						// size to store ("<" + tag),
						// keeps buffering
						if (buffer.size() < tag.length() + "</".length())
						{
							if ((char)currentChar == '>')
							{
								buffer.clear();
								tagStarted = false;
							}
							else
							{
								buffer.add((char)currentChar);
							}
						}
						else
						{
							tagStarted = false;
							String readTag = "";
							for (int i = 0; i < buffer.size(); i++)
							{
								readTag += buffer.get(i);
							}
							buffer.clear();
							if (readTag.equals("</" + tag))
							{
								buffer.clear();
								parseFinished = true;
							}
						}
					}
				}
			}
			try
			{
				// Wrapped FileInputStream with limited access to the file
				// content.
				// This InputStream will limit the access to the stream, so the
				// plugins will just "see" what
				// regards to them.
				return new ProtectedInputStream(file, startOfTag, endOfTag
						- startOfTag);
			}
			catch (IOException e)
			{
				// This error should not happen, since file is the descriptor
				// for a file selected by the user,
				// so it can't not exist.
				return null;
			}
		}
	}

	private class TagRecovery extends AbstractParser
	{
		protected static final String ATTRIBUTE_TYPE = "type",
				ATTRIBUTE_TAG = "tag", ATTRIBUTE_VERSION = "version",
				ELEMENT_MODEL = "model", ELEMENT_META = "meta";

		private String dataType = "";

		private String dataVersion = "";

		private Set<String> metaTags = new HashSet<String>();

		private Map<String, String> metaTagsVersions = new HashMap<String, String>();

		private boolean gotModelElement = false;

		private LinkedList<String> metaSectionStack = new LinkedList<String>();

		public TagRecovery(File file)
		{
			// Initialize parser:
			try
			{
				xmlReader = SAXParserFactory
						.newInstance().newSAXParser().getXMLReader();
				xmlReader.setContentHandler(this);
				parse(file);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		private void parse(File file)
		{
			parsingErrors = "";
			try
			{
				xmlReader.parse(new InputSource(new FileInputStream(file)));
			}
			catch (FileNotFoundException fnfe)
			{
				parsingErrors += fnfe.getMessage() + "\n";
			}
			catch (IOException ioe)
			{
				parsingErrors += ioe.getMessage() + "\n";
			}
			catch (SAXException saxe)
			{
				parsingErrors += saxe.getMessage() + "\n";
			}
			catch (NullPointerException npe)
			{
				parsingErrors += npe.getMessage() + "\n";
			}
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes atts)
		{
			if (!metaSectionStack.isEmpty())
			{
				metaSectionStack.addFirst(qName);
			}
			if (metaSectionStack.isEmpty() && ELEMENT_META.equals(qName))
			{
				if (atts.getValue(ATTRIBUTE_TAG) != null)
				{
					metaTags.add(atts.getValue(ATTRIBUTE_TAG));
					if (atts.getValue(ATTRIBUTE_VERSION) != null)
					{
						metaTagsVersions.put(atts.getValue(ATTRIBUTE_TAG), atts
								.getValue(ATTRIBUTE_VERSION));
					}
					else
					{
						metaTagsVersions.put(atts.getValue(ATTRIBUTE_TAG), "");
					}
				}
				metaSectionStack.addFirst(qName);
			}
			else if (!gotModelElement && ELEMENT_MODEL.equals(qName))
			{
				if (atts.getValue(ATTRIBUTE_TYPE) != null)
				{
					dataType = atts.getValue(ATTRIBUTE_TYPE);
					if (atts.getValue(ATTRIBUTE_VERSION) != null)
					{
						dataVersion = atts.getValue(ATTRIBUTE_VERSION);
					}
					else
					{
						dataVersion = "";
					}
					gotModelElement = true;
				}
			}
		}

		public void endElement(String uri, String localName, String qName)
		{
			if (!metaSectionStack.isEmpty())
			{
				metaSectionStack.removeFirst();
			}
		}

		public String getType()
		{
			return dataType;
		}

		public String getVersion()
		{
			return dataVersion;
		}

		public Set<String> getMetaTags()
		{
			return new HashSet<String>(metaTags);
		}

		public String getMetaVersion(String tag)
		{
			return metaTagsVersions.get(tag);
		}
	}

	public DESModel importFile(File src, String description) throws IOException
	{
		ImportExportPlugin plugin = IOPluginManager
				.instance().getImporter(description);
		if (plugin == null)
		{
			throw new FormatTranslationException(Hub
					.string("pluginNotFoundFile"));
		}
		DESModel model = null;
		File dst = File.createTempFile("IDESimport", IOSubsytem.MODEL_FILE_EXT);
		try
		{
			plugin.importFile(src, dst);
			model = load(dst);
			model.removeAnnotation(Annotable.FILE);
			model.setName(ParsingToolbox.removeFileType(src.getName()));
		}
		catch (FileLoadException e)
		{
			if (e.getPartialModel() != null)
			{
				e.getPartialModel().removeAnnotation(Annotable.FILE);
				e.getPartialModel().setName(ParsingToolbox.removeFileType(src
						.getName()));
			}
			throw e;
		}
		finally
		{
			dst.delete();
		}
		return model;
	}

	public void export(DESModel model, File dst, String description)
			throws IOException
	{
		if (model == null)
		{
			throw new FormatTranslationException(Hub.string("internalError"));
		}
		Set<ImportExportPlugin> plugins = IOPluginManager
				.instance().getExporters(model
						.getModelType().getMainPerspective());
		if (plugins == null)
		{
			plugins = new HashSet<ImportExportPlugin>();
		}
		ImportExportPlugin plugin = null;
		for (ImportExportPlugin p : plugins)
		{
			if (p.getFileDescription().equals(description))
			{
				plugin = p;
				break;
			}
		}
		if (plugin == null)
		{
			throw new FormatTranslationException(Hub
					.string("pluginNotFoundFile"));
		}
		File src = File.createTempFile("IDESexport", IOSubsytem.MODEL_FILE_EXT);
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
		BufferedReader in = new BufferedReader(new FileReader(file));
		in.readLine();
		String line = in.readLine();
		in.close();
		if (line != null && line.startsWith("<automaton>"))
		{
			return true;
		}
		return false;
	}
}
