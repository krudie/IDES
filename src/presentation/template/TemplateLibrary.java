package presentation.template;

import io.IOUtilities;
import io.ParsingToolbox;
//import io.fsa.ver2_1.AutomatonParser;
//import io.fsa.ver2_1.XMLexporter;
import io.template.ver2_1.PLCExporter;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import operations.fsa.ver2_1.LocalModular;
import operations.fsa.ver2_1.SupRed;
import operations.template.ver2_1.SupervisorGen;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import edu.uci.ics.jung.visualization.PersistentLayout.Point;

import main.Annotable;
import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;
import model.fsa.FSAModel;
import model.fsa.FSASupervisor;
import model.fsa.ver2_1.Automaton;
import model.template.TemplateChannel;
import model.template.TemplateModel;
import model.template.TemplateModule;
import model.template.ver2_1.Channel;
import model.template.ver2_1.Module;

import presentation.LayoutShell;
import presentation.Presentation;
import presentation.fsa.ContextAdaptorHack;
import presentation.fsa.FSAGraph;
import services.General;
import util.StupidSetWrapper;

public class TemplateLibrary implements Presentation, KeyListener {

	protected static final String TEMPLATES_DIR="templates";
	protected static final String LIB_FILE="library.xml";
	
	JPanel gui;
	
	private JList moduleList;
	private JList channelList;
	
	protected TreeSet<Template> moduleTemplates=new TreeSet<Template>();
	protected TreeSet<Template> channelTemplates=new TreeSet<Template>();
	
	protected class TemplateTransferHandler extends TransferHandler {
	    
	    protected Transferable createTransferable(JComponent c) {
	    	if(c==moduleList)
	    	{
		        return new StringSelection("M"+
		        		moduleList.getSelectedValue().toString());
	    	}
	    	else if(c==channelList)
	    	{
		        return new StringSelection("C"+
		        		channelList.getSelectedValue().toString());
	    	}
	    	else
	    	{
	    		return null;
	    	}
	    }
	    
	    public int getSourceActions(JComponent c) {
	        return COPY;
	    }
	}
	
	public TemplateLibrary()
	{
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
		
		MouseListener mel=new MouseAdapter()
		{
			public void mouseClicked(MouseEvent me)
			{
				if(me.getClickCount()>1)
				{
					JList list=(JList)me.getSource();
					if(list==moduleList)
					{
						int idx=moduleList.locationToIndex(me.getPoint());
						String name=moduleList.getModel().getElementAt(idx).toString();
						Template selected=null;
						for(Template t:moduleTemplates)
						{
							if(t.getName().equals(name))
							{
								selected=t;
								break;
							}
						}
						if(selected==null)
						{
							return;
						}
						Hub.getWorkspace().addModel(loadFSA(selected.getFSAName()));
					}
					else
					{
						int idx=channelList.locationToIndex(me.getPoint());
						String name=channelList.getModel().getElementAt(idx).toString();
						Template selected=null;
						for(Template t:channelTemplates)
						{
							if(t.getName().equals(name))
							{
								selected=t;
								break;
							}
						}
						if(selected==null)
						{
							return;
						}
						Hub.getWorkspace().addModel(loadFSA(selected.getFSAName()));
					}
					
				}
			}
		};
		TransferHandler th=new TemplateTransferHandler();
		moduleList=new JList();
		moduleList.addKeyListener(this);
		moduleList.setDragEnabled(true);
		moduleList.setTransferHandler(th);
		moduleList.addMouseListener(mel);
		channelList=new JList();
		channelList.addKeyListener(this);
		channelList.setDragEnabled(true);
		channelList.setTransferHandler(th);
		channelList.addMouseListener(mel);
		
		main.add(new JLabel(Hub.string("modules")));
		JScrollPane sp=new JScrollPane(moduleList);
		main.add(sp);
		main.add(new JLabel(Hub.string("channels")));
		sp=new JScrollPane(channelList);
		main.add(sp);
		
		Box extraBox=Box.createHorizontalBox();
		JButton supconBut=new JButton("Supcon");
		supconBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Collection<FSASupervisor> sups=getSups();
				for(FSASupervisor sup:sups)
				{
					FSAGraph g=new FSAGraph(sup);
					Hub.getWorkspace().addLayoutShell(g);
				}
			}
		});
		extraBox.add(supconBut);
		JButton plcBut=new JButton("PLC");
		plcBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TemplateModel model=(TemplateModel)Hub.getWorkspace().getActiveModel();
				Collection<FSASupervisor> sups=getSups();
				Collection<FSAModel> modules=new HashSet<FSAModel>();
				for(Iterator<TemplateModule> i=model.getModuleIterator();i.hasNext();)
				{
					modules.add(i.next().getFSA());
				}
				PLCExporter.export(modules, sups, model.getPLCCodeMap());
			}
		});
		extraBox.add(plcBut);
		JButton customBut=new JButton("PLC Select");
		customBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				TemplateModel model=(TemplateModel)Hub.getWorkspace().getActiveModel();
				Collection<FSASupervisor> sups=new SupervisorSelectDialog().getSupervisors(model.getChannelCount());
				if(sups==null)
				{
					return;
				}
				Collection<FSAModel> modules=new HashSet<FSAModel>();
				for(Iterator<TemplateModule> i=model.getModuleIterator();i.hasNext();)
				{
					modules.add(i.next().getFSA());
				}
				PLCExporter.export(modules, sups, model.getPLCCodeMap());
			}
		});
		extraBox.add(customBut);
		main.add(extraBox);
		
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
	}
	
	private Collection<FSASupervisor> getSups()
	{
		TemplateModel model=(TemplateModel)Hub.getWorkspace().getActiveModel();
		Object[] ret=new SupervisorGen().perform(new Object[]{model});
		List<FSASupervisor> sups=(List<FSASupervisor>)ret[0];
		List<FSAModel> syncs=(List<FSAModel>)ret[1];
//		Collection<FSASupervisor> rsups=new Vector<FSASupervisor>();
//		Iterator<FSAModel> j=syncs.iterator();
//		for(Iterator<FSASupervisor> i=sups.iterator();i.hasNext();)
//		{
//			rsups.add(((FSASupervisor)new SupRed().perform(new Object[]{j.next(),i.next()})[0]));
//		}
		if(!((Boolean)new LocalModular().perform(sups.toArray())[0]).booleanValue())
		{
			Hub.displayAlert("Supervisors aren't locally modular.");
		}
		return sups;
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
	
	public TemplateModule instantiateModule(String name)
	{
		Template selected=null;
		for(Template t:moduleTemplates)
		{
			if(t.getName().equals(name))
			{
				selected=t;
				break;
			}
		}
		if(selected==null)
		{
			return null;
		}
//    	AutomatonParser ap = new AutomatonParser();
		FSAModel fsa=loadFSA(selected.getFSAName());
//    	String errors="";
//    	try
//    	{
//    		fsa = ap.parse(new File(TEMPLATES_DIR+File.separator+selected.getFSAName()+"."+IOUtilities.MODEL_FILE_EXT));
//    		errors=ap.getParsingErrors();
//    	}catch(Exception e)
//    	{
//    		throw new RuntimeException(Hub.string("cantLoadTemplate")+" "+errors);
//    	}
    	fsa.setName(General.getRandomId());
    	FSAEventSet iface=new StupidSetWrapper();
    	for(Long id:selected.getInterfaceEventIds())
    	{
    		iface.add(fsa.getEvent(id.longValue()));
    	}
		return new Module(fsa,iface);
	}
	
	public TemplateChannel instantiateChannel(String name)
	{
		Template selected=null;
		for(Template t:channelTemplates)
		{
			if(t.getName().equals(name))
			{
				selected=t;
				break;
			}
		}
		if(selected==null)
		{
			return null;
		}
//    	AutomatonParser ap = new AutomatonParser();
		FSAModel fsa=loadFSA(selected.getFSAName());
//    	String errors="";
//    	try
//    	{
//    		fsa = ap.parse(new File(TEMPLATES_DIR+File.separator+selected.getFSAName()+"."+IOUtilities.MODEL_FILE_EXT));
//    		errors=ap.getParsingErrors();
//    	}catch(Exception e)
//    	{
//    		throw new RuntimeException(Hub.string("cantLoadTemplate")+" "+errors);
//    	}
    	fsa.setName(General.getRandomId());
    	FSAEventSet iface=new StupidSetWrapper();
    	for(Long id:selected.getInterfaceEventIds())
    	{
    		iface.add(fsa.getEvent(id.longValue()));
    	}
		return new Channel(fsa,iface);		
	}
	
	protected FSAModel loadFSA(String name)
	{
//    	AutomatonParser ap = new AutomatonParser();
//		FSAModel fsa;
//    	String errors="";
//    	try
//    	{
//    		fsa = ap.parse(new File(TEMPLATES_DIR+File.separator+name+"."+IOUtilities.MODEL_FILE_EXT));
//    		errors=ap.getParsingErrors();
//    	}catch(Exception e)
//    	{
//    		throw new RuntimeException(Hub.string("cantLoadTemplate")+" "+errors);
//    	}
		return null;
	}
	
	protected void addTemplate()
	{
		AddTemplateDialog dialog=new AddTemplateDialog();
		Template template=dialog.createTemplate();
		PrintStream ps =null;
		if(template!=null)
		{
			FSAModel fsa=((FSAModel)Hub.getWorkspace().getModel(template.getFSAName())).clone();
			try
			{
				if(fsa==null)
				{
					throw new RuntimeException();
				}
				int idx=0;
				while(new File(TEMPLATES_DIR+File.separator+fsa.getName()+idx+"."+IOUtilities.MODEL_FILE_EXT).exists())
				{
					++idx;
				}
				fsa.setName(fsa.getName()+idx);
				template.setFSAName(fsa.getName());
		        ps = IOUtilities.getPrintStream(
		        		new File(TEMPLATES_DIR+File.separator+fsa.getName()+"."+IOUtilities.MODEL_FILE_EXT));
		        if(ps==null)
		        {
		        	throw new RuntimeException(Hub.string("checkInvalidChars"));
		        }
		        //TODO make the exporter be an inner class, like it is done in the FSAFileIOPlugin
//				XMLexporter.automatonToXML(fsa, ps);
				ps.close();
				if(template.getType()==Template.TYPE_MODULE)
					moduleTemplates.add(template);
				else
					channelTemplates.add(template);
				storeLibrary();
				updateLists();
	        } catch(Exception e)
	        {
	        	try
	        	{
	        		ps.close();
	        	}catch(Exception ex){}
	        	Hub.displayAlert(Hub.string("cantSaveTemplate")+" "+e.getMessage());
	        	new File(TEMPLATES_DIR+File.separator+fsa.getName()+"."+IOUtilities.MODEL_FILE_EXT).delete();
	        }
		}
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
        	ps.println("\t<module fsa=\""+t.getFSAName()+
        			"\" name=\""+t.getName()+"\">");
        	for(Long id:t.getInterfaceEventIds())
        	{
        		ps.println("\t\t<event id=\""+id.longValue()+"\"/>");
        	}
        	ps.println("\t</module>");
        }
        for(Template t:channelTemplates)
        {
        	ps.println("\t<channel fsa=\""+t.getFSAName()+
        			"\" name=\""+t.getName()+"\">");
        	for(Long id:t.getInterfaceEventIds())
        	{
        		ps.println("\t\t<event id=\""+id.longValue()+"\"/>");
        	}
        	ps.println("\t</channel>");
        }
        ps.println("</templatelibrary>");
        ps.close();
	}
	
	protected void loadLibrary() throws IOException
	{
		boolean missingFiles=false;
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
				HashSet<Long> iface=new HashSet<Long>();
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
					iface.add(new Long(Long.parseLong(attribute.getNodeValue())));
				}
				if(new File(TEMPLATES_DIR+File.separator+fsaName+"."+IOUtilities.MODEL_FILE_EXT).exists())
				{
					if("module".equals(tag.getNodeName()))
					{
						moduleTemplates.add(new Template(fsaName,iface,Template.TYPE_MODULE,name));
					}
					else if("channel".equals(tag.getNodeName()))
					{
						channelTemplates.add(new Template(fsaName,iface,Template.TYPE_CHANNEL,name));
					}
				}
				else
				{
					missingFiles=true;
				}
			}
		}catch(Exception e){
			throw new IOException(Hub.string("loadTemplateLibFail")+" "+e.getMessage());
		}
		if(missingFiles)
		{
			Hub.displayAlert(Hub.string("missingFiles"));
		}
	}

	public void keyTyped(KeyEvent ke)
	{
		if(ke.getKeyChar() == KeyEvent.VK_DELETE){
			if(ke.getComponent()==moduleList&&
					moduleList.getSelectedIndex()>=0)
			{
				int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
						Hub.string("confirmDeleteTemplate"),Hub.string("deleteTemplateTitle"),
						JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice!=JOptionPane.YES_OPTION)
					return;
				String moduleName=moduleList.getSelectedValue().toString();
				Template toRemove=null;
				for(Template m:moduleTemplates)
				{
					if(m.getName().equals(moduleName))
					{
						toRemove=m;
						break;
					}
				}
				moduleTemplates.remove(toRemove);
				new File(TEMPLATES_DIR+File.separator+
						toRemove.getFSAName()+"."+
						IOUtilities.MODEL_FILE_EXT).delete();
				storeLibrary();
				updateLists();
			}
			else if(ke.getComponent()==channelList&&
					channelList.getSelectedIndex()>=0)
			{
				int choice=JOptionPane.showConfirmDialog(Hub.getMainWindow(),
						Hub.string("confirmDeleteTemplate"),Hub.string("deleteTemplateTitle"),
						JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice!=JOptionPane.YES_OPTION)
					return;
				String channelName=channelList.getSelectedValue().toString();
				Template toRemove=null;
				for(Template c:channelTemplates)
				{
					if(c.getName().equals(channelName))
					{
						toRemove=c;
						break;
					}
				}
				channelTemplates.remove(toRemove);
				new File(TEMPLATES_DIR+File.separator+
						toRemove.getFSAName()+"."+
						IOUtilities.MODEL_FILE_EXT).delete();
				storeLibrary();
				updateLists();
			}
		}
	}

	public void keyPressed(KeyEvent ke)
	{
		
	}

	public void keyReleased(KeyEvent ke)
	{
		
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
	public void forceRepaint(){}
}
