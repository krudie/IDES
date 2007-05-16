package io.template.ver2_1;

import io.IOUtilities;
import io.ParsingToolbox;
import io.fsa.ver2_1.AutomatonParser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import main.Annotable;
import main.Hub;
import model.ModelManager;
import model.fsa.FSAModel;
import model.template.TemplateBlock;
import model.template.TemplateChannel;
import model.template.TemplateLink;
import model.template.TemplateModel;
import model.template.TemplateModule;
import model.template.ver2_1.Channel;
import model.template.ver2_1.Link;
import model.template.ver2_1.Module;
import model.template.ver2_1.TemplateDesign;

import presentation.PresentationManager;
import presentation.template.BlockLayout;
import presentation.template.Template;
import presentation.template.TemplateGraph;
import presentation.template.TemplateToolset;
import util.StupidSetWrapper;

public class TemplateParser {

	public static TemplateGraph parse(File f) throws IOException
	{
		TemplateModel model=ModelManager.createModel(TemplateModel.class,ParsingToolbox.removeFileType(f.getName()));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			//custom handler to avoid nasty error messages on STDERR
			builder.setErrorHandler(new ErrorHandler(){
				public void error(SAXParseException exception){}
				public void fatalError(SAXParseException exception){}
				public void warning(SAXParseException exception){}
			});
			Document document = builder.parse(f);
			NodeList elements=document.getDocumentElement().getChildNodes();
			if(elements==null||elements.getLength()<1)
			{
				throw new RuntimeException(Hub.string("dataFormatError"));
			}
			for(int i=0;i<elements.getLength();++i)
			{
				Node tag=elements.item(i);
				if(tag.getNodeType()!=Node.ELEMENT_NODE)
					continue;
				if("data".equals(tag.getNodeName()))
				{
					elements=tag.getChildNodes();
					break;
				}
			}
			if(elements==null||elements.getLength()<1)
			{
				throw new RuntimeException(Hub.string("dataFormatError"));
			}
			for(int i=1;i<elements.getLength();++i)
			{ //read "data" section
				Node tag=elements.item(i);
				if(tag.getNodeType()!=Node.ELEMENT_NODE)
					continue;
				if("routine".equals(tag.getNodeName()))
				{ //read PLC code
					NamedNodeMap attributes=tag.getAttributes();
					if(attributes==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					Node attribute=attributes.getNamedItem("event");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					String name=attribute.getNodeValue();
					if(tag.getChildNodes().getLength()<1)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					String code=tag.getChildNodes().item(0).getTextContent().trim().replaceAll("\\{event\\}","<event>");
					model.setPLCCode(name,code);
				}
				else if("link".equals(tag.getNodeName()))
				{ //read Links
					NamedNodeMap attributes=tag.getAttributes();
					if(attributes==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					Node attribute=attributes.getNamedItem("lefttype");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					boolean leftIsModule="m".equals(attribute.getNodeValue());
					attribute=attributes.getNamedItem("leftblock");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					long leftBlock=Long.parseLong(attribute.getNodeValue());
					attribute=attributes.getNamedItem("leftevent");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					long leftEvent=Long.parseLong(attribute.getNodeValue());
					attribute=attributes.getNamedItem("righttype");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					boolean rightIsModule="m".equals(attribute.getNodeValue());
					attribute=attributes.getNamedItem("rightblock");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					long rightBlock=Long.parseLong(attribute.getNodeValue());
					attribute=attributes.getNamedItem("rightevent");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					long rightEvent=Long.parseLong(attribute.getNodeValue());
					attribute=attributes.getNamedItem("id");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					long id=Long.parseLong(attribute.getNodeValue());
					TemplateBlock left=leftIsModule?model.getModule(leftBlock):model.getChannel(leftBlock);
					TemplateBlock right=rightIsModule?model.getModule(rightBlock):model.getChannel(rightBlock);
					TemplateLink link=new Link(id,left,left.getFSA().getEvent(leftEvent),
							right,right.getFSA().getEvent(rightEvent));
					model.add(link);
				}
				else
				{ //read Modules and Channels
					NamedNodeMap attributes=tag.getAttributes();
					if(attributes==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					Node attribute=attributes.getNamedItem("fsa");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					String fsaName=attribute.getNodeValue();
					attribute=attributes.getNamedItem("id");
					if(attribute==null)
					{
						throw new RuntimeException(Hub.string("dataFormatError"));
					}
					long id=Long.parseLong(attribute.getNodeValue());
					HashSet<Long> ifaceIds=new HashSet<Long>();
					NodeList events=tag.getChildNodes();
					for(int j=1;j<events.getLength();++j)
					{
						Node event=events.item(j);
						if(event.getNodeType()!=Node.ELEMENT_NODE)
							continue;
						attributes=event.getAttributes();
						attribute=attributes.getNamedItem("id");
						if(attribute==null)
						{
							throw new RuntimeException(Hub.string("dataFormatError"));
						}
						ifaceIds.add(new Long(Long.parseLong(attribute.getNodeValue())));
					}
					File fsaFile=new File(f.getParentFile().getAbsolutePath()+File.separator+fsaName+"."+IOUtilities.MODEL_FILE_EXT);
					if(!fsaFile.exists())
					{
						throw new RuntimeException(Hub.string("missingFiles"));
					}
			    	AutomatonParser ap = new AutomatonParser();
					FSAModel fsa;
			    	String errors="";
			    	try
			    	{
			    		fsa = ap.parse(fsaFile);
			    		errors=ap.getParsingErrors();
			    	}catch(Exception e)
			    	{
			    		throw new RuntimeException(Hub.string("cantLoadTemplate")+" "+errors);
			    	}
			    	StupidSetWrapper iface=new StupidSetWrapper();
			    	for(Long l:ifaceIds)
			    	{
			    		iface.add(fsa.getEvent(l.longValue()));
			    	}
					if("module".equals(tag.getNodeName()))
					{
						TemplateModule m=new Module(fsa,iface);
						m.setId(id);
						model.add(m);
					}
					else if("channel".equals(tag.getNodeName()))
					{
						TemplateChannel c=new Channel(fsa,iface);
						c.setId(id);
						model.add(c);
					}
				}
			}
			elements=document.getDocumentElement().getChildNodes();
			if(elements==null||elements.getLength()<1)
			{
				throw new RuntimeException(Hub.string("dataFormatError"));
			}
			for(int i=0;i<elements.getLength();++i)
			{
				Node tag=elements.item(i);
				if(tag.getNodeType()!=Node.ELEMENT_NODE)
					continue;
				if("meta".equals(tag.getNodeName()))
				{
					elements=tag.getChildNodes();
					break;
				}
			}
			if(elements==null||elements.getLength()<1)
			{
				throw new RuntimeException(Hub.string("dataFormatError"));
			}
			for(int i=1;i<elements.getLength();++i)
			{ // read "meta" section
				Node tag=elements.item(i);
				if(tag.getNodeType()!=Node.ELEMENT_NODE)
					continue;
				NamedNodeMap attributes=tag.getAttributes();
				if(attributes==null)
				{
					throw new RuntimeException(Hub.string("dataFormatError"));
				}
				Node attribute=attributes.getNamedItem("x");
				if(attribute==null)
				{
					throw new RuntimeException(Hub.string("dataFormatError"));
				}
				float x=Float.parseFloat(attribute.getNodeValue());
				attribute=attributes.getNamedItem("y");
				if(attribute==null)
				{
					throw new RuntimeException(Hub.string("dataFormatError"));
				}
				float y=Float.parseFloat(attribute.getNodeValue());
				attribute=attributes.getNamedItem("name");
				if(attribute==null)
				{
					throw new RuntimeException(Hub.string("dataFormatError"));
				}
				String label=attribute.getNodeValue();
				attribute=attributes.getNamedItem("id");
				if(attribute==null)
				{
					throw new RuntimeException(Hub.string("dataFormatError"));
				}
				long id=Long.parseLong(attribute.getNodeValue());
				TemplateBlock b=null;
				if("module".equals(tag.getNodeName()))
				{
					b=model.getModule(id);
				}
				else if("channel".equals(tag.getNodeName()))
				{
					b=model.getChannel(id);
				}
				BlockLayout bl=new BlockLayout();
				bl.setLocation(x, y);
				bl.setText(label);
				b.setAnnotation(Annotable.LAYOUT, bl);
			}
		}catch(Exception e){
			throw new IOException(Hub.string("loadTemplateLibFail")+" "+e.getMessage());
		}
		return (TemplateGraph)PresentationManager.getToolset(TemplateModel.class).wrapModel(model);
	}
}
