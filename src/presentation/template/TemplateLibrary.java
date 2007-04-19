package presentation.template;

import io.IOUtilities;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import edu.uci.ics.jung.visualization.PersistentLayout.Point;

import main.Hub;

import presentation.LayoutShell;
import presentation.Presentation;

public class TemplateLibrary implements Presentation {

	protected static final String TEMPLATES_DIR="templates";
	protected static final String LIB_FILE="library.xml";
	
	JPanel gui;
	
	private JList moduleList;
	private JList channelList;
	
	protected TreeSet<Template> moduleTemplates=new TreeSet<Template>();
	protected TreeSet<Template> channelTemplates=new TreeSet<Template>();
	
	public TemplateLibrary()
	{
//		System.setProperty("org.apache.commons.logging.simplelog.log.", "error");
		
		Box main=Box.createVerticalBox();
		JButton addButton=new JButton(Hub.string("add"));
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addTemplate();
			}
		});
		main.add(addButton);
		
		moduleList=new JList();
		moduleList.setDragEnabled(true);
		channelList=new JList();
		channelList.setDragEnabled(true);
//		items.setPreferredSize(new Dimension(100,100));
//		items.setSize(new Dimension(100,100));
//		items.setMaximumSize(new Dimension(100,100));
		main.add(new JLabel(Hub.string("modules")));
		JScrollPane sp=new JScrollPane(moduleList);
		main.add(sp);
		main.add(new JLabel(Hub.string("channels")));
		sp=new JScrollPane(channelList);
		main.add(sp);
		
		gui=new JPanel();
		gui.add(main);
		
		try
		{
			loadLibrary();
		}catch(IOException e)
		{
			Hub.displayAlert(e.getMessage());
		}
		updateLists();
//		sp.setSize(new Dimension(100,100));
//		sp.setPreferredSize(new Dimension(100,100));//sp.getPreferredSize().height));
//		sp.setMaximumSize(new Dimension(100,100));
	}
	
	public JComponent getGUI() {
		return gui;
	}

	public LayoutShell getLayoutShell() {
		return null;
	}

	public String getName() {
		return Hub.string("templateLibrary");
	}

	public void release() {
	}

	public void setTrackModel(boolean b) {
	}
	
	protected void addTemplate()
	{
		AddTemplateDialog dialog=new AddTemplateDialog();
		Template template=dialog.createTemplate();
		if(template!=null)
		{
			if(template.getType()==Template.TYPE_MODULE)
				moduleTemplates.add(template);
			else
				channelTemplates.add(template);
		}
		storeLibrary();
		updateLists();
	}
	
	protected void updateLists()
	{
		Vector<String> names=new Vector<String>(moduleTemplates.size());
		for(Template t:moduleTemplates)
		{
			names.add(t.getName());
		}
		moduleList.setListData(names);
		names=new Vector<String>(channelTemplates.size());
		for(Template t:channelTemplates)
		{
			names.add(t.getName());
		}
		channelList.setListData(names);
	}
	
	protected void storeLibrary()
	{
		if(!new File(TEMPLATES_DIR).exists())
		{
			new File(TEMPLATES_DIR).mkdir();
		}
		PrintStream ps = IOUtilities.getPrintStream(new File(TEMPLATES_DIR+File.separator+LIB_FILE));
        ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ps.println("<templatelibrary version=\"2.1\">");
        for(Template t:moduleTemplates)
        {
        	ps.println("\t<module fsa=\""+t.getFSA()+
        			"\" name=\""+t.getName()+"\">");
        	ps.println("\t</module>");
        }
        for(Template t:channelTemplates)
        {
        	ps.println("\t<channel fsa=\""+t.getFSA()+
        			"\" name=\""+t.getName()+"\">");
        	ps.println("\t</channel>");
        }
        ps.println("</templatelibrary>");
        ps.close();
	}
	
	protected void loadLibrary() throws IOException
	{
		moduleTemplates.clear();
		channelTemplates.clear();
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
			Document document = builder.parse(new File(TEMPLATES_DIR+File.separator+LIB_FILE));
			NodeList templates=document.getDocumentElement().getChildNodes();
			if(templates==null||templates.getLength()<1)
			{
				throw new RuntimeException(Hub.string("dataFormatError"));
			}
			for(int i=1;i<templates.getLength();++i)
			{
				Node tag=templates.item(i);
				if(tag.getNodeType()!=Node.ELEMENT_NODE)
					continue;
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
				attribute=attributes.getNamedItem("name");
				if(attribute==null)
				{
					throw new RuntimeException(Hub.string("dataFormatError"));
				}
				String name=attribute.getNodeValue();
				if("module".equals(tag.getNodeName()))
				{
					moduleTemplates.add(new Template(fsaName,Template.TYPE_MODULE,name));
				}
				else if("channel".equals(tag.getNodeName()))
				{
					channelTemplates.add(new Template(fsaName,Template.TYPE_CHANNEL,name));
				}
			}
		}catch(Exception e){
			throw new IOException(Hub.string("loadTemplateLibFail")+" "+e.getMessage());
		}
	}

	// for DEBUG purposes
//	static void print(Node node, PrintStream out) {
//	    int type = node.getNodeType();
//	    switch (type) {
//	      case Node.ELEMENT_NODE:
//	        out.print("<" + node.getNodeName());
//	        NamedNodeMap attrs = node.getAttributes();
//	        int len = attrs.getLength();
//	        for (int i=0; i<len; i++) {
//	            Attr attr = (Attr)attrs.item(i);
//	            out.print(" " + attr.getNodeName() + "=\"" +
//	                      attr.getNodeValue() + "\"");
//	        }
//	        out.print('>');
//	        NodeList children = node.getChildNodes();
//	        len = children.getLength();
//	        for (int i=0; i<len; i++)
//	          print(children.item(i), out);
//	        out.print("</" + node.getNodeName() + ">");
//	        break;
//	      case Node.ENTITY_REFERENCE_NODE:
//	        out.print("&" + node.getNodeName() + ";");
//	        break;
//	      case Node.CDATA_SECTION_NODE:
//	        out.print("<![CDATA[" + node.getNodeValue() + "]]>");
//	        break;
//	      case Node.TEXT_NODE:
//	        out.print(node.getNodeValue());
//	        break;
//	      case Node.PROCESSING_INSTRUCTION_NODE:
//	        out.print("<?" + node.getNodeName());
//	        String data = node.getNodeValue();
//	        if (data!=null && data.length()>0)
//	           out.print(" " + data);
//	        out.println("?>");
//	        break;
//	    }
//	  }
}
