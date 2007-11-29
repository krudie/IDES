/**
 * 
 */
package ui;

import main.Annotable;
import io.IOUtilities;
import io.fsa.ver2_1.FileOperations;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.GridLayout;
import javax.swing.border.LineBorder;

import main.Hub;
import model.DESModel;
import model.fsa.FSAModel;
import pluggable.operation.Operation;
import pluggable.operation.OperationManager;
import presentation.fsa.FSAGraph;
import util.EscapeDialog;
import java.util.Set;
import io.ParsingToolbox;

import pluggable.io.IOCoordinator;
import pluggable.io.IOPluginManager;
import pluggable.io.ImportExportPlugin;
import java.util.Iterator;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
/**
 * @author christiansilvano
 *
 */
public class ImportExportDialog extends EscapeDialog{
	
	//reference to the selected plugin handling import/export operations
	protected ImportExportPlugin selectedPlugin = null;
	//graphical element to list export plugins
	protected JList exportList=null;
	//graphical element to list import plugins
	protected JList importList=null;
	//a DESModel, it can be either the currently open model or a model from an user-selected file
	protected DESModel currentModel = null;
	//graphical element to hold the address for an input file to export
	protected JTextField srcFileExport = new JTextField(20);
	//graphical element to hold the address for an output file to export
	protected JTextField dstFileExport = new JTextField(20);
	//graphical element to hold the address for an input file to export
	protected JTextField srcFileImport = new JTextField(20);
	//graphical element to hold the address for an output file to export
	protected JTextField dstFileImport = new JTextField(20);
	JTabbedPane tabbedPane = new JTabbedPane();
	
	public ImportExportDialog()
	{
		super(Hub.getMainWindow(),Hub.string("importExportDialogTitle"),true);
		currentModel = Hub.getWorkspace().getActiveModel();
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onEscapeEvent();
		    }
		});
	
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		//Start drawing GUI
		Box mainBox=Box.createVerticalBox();
		JComponent importPanel = createImportPanel();
		tabbedPane.addTab("Import", null, importPanel,
			"Does nothing");
		JComponent exportPanel = createExportPanel();
		tabbedPane.addTab("Export", null, exportPanel,
			"Exports from an IDES model to another format");
		importPanel.setPreferredSize(new Dimension(700, 250));
		JButton okButton=new JButton();
		JButton cancelButton=new JButton();		
		okButton = new JButton(Hub.string("OK"));
		//Adds an action for the "OK" button
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(selectedPlugin !=null)
				{
					if(tabbedPane.getSelectedIndex() == 1)
					{
						selectedPlugin.exportFile(new File(srcFileExport.getText()), new File(dstFileExport.getText()));
					}else{
						selectedPlugin.importFile(new File(srcFileImport.getText()), new File(dstFileImport.getText()));
					}
					
					dispose();
				}
			}
		});
		cancelButton = new JButton(Hub.string("cancel"));
		//Adds an action for the "Cancel" button
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				onEscapeEvent();
			}
		}
		);
		JPanel p = new JPanel(new FlowLayout());
		mainBox.add(tabbedPane);
		p.add(okButton);		
		p.add(cancelButton);
		mainBox.add(p);
		
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		getContentPane().add(mainBox);
		pack();
		okButton.setPreferredSize(new Dimension(
				Math.max(okButton.getWidth(),cancelButton.getWidth()),okButton.getHeight()));
		okButton.invalidate();
		cancelButton.setPreferredSize(new Dimension(
				Math.max(okButton.getWidth(),cancelButton.getWidth()),cancelButton.getHeight()));
		cancelButton.invalidate();
		
		
		//If needed:
		tabbedPane.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				System.out.println(tabbedPane.getSelectedIndex());
			}
		});
		
		setVisible(true);
	}
		
	//Draw the graphical components for the Import pane
	protected JComponent createImportPanel() {
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout(1, 1));
		Box leftBox=Box.createVerticalBox();
		Box rightBox=Box.createVerticalBox();
		//Source file:
		JLabel srcLabel = new JLabel("Source File:");
		
		JButton buttonSelectSrc = new JButton("...");
		buttonSelectSrc.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//Open a window for the user to choose the file to open:
				JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME"));
				fc.setDialogTitle(Hub.string("openModelTitle"));
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int retVal = fc.showOpenDialog(Hub.getMainWindow());
				if(retVal == JFileChooser.APPROVE_OPTION){
					srcFileImport.setText(fc.getSelectedFile().getPath());
					
					
					importList.setSelectedIndex(0);
					String dst = new String(ParsingToolbox.removeFileType(fc.getSelectedFile().getPath()) + "." + IOUtilities.MODEL_FILE_EXT);
//					System.out.println(dst);
					dstFileImport.setText(dst);
					refreshImportPluginList();
					
				}
			}
		}
		);
		
		Box leftLine1 = Box.createHorizontalBox();
		srcLabel.setHorizontalAlignment(JLabel.CENTER);
		srcFileImport.setMaximumSize(new Dimension(500, 25));
		String path = (currentModel.getAnnotation(Annotable.FILE) != null? 
				((File)currentModel.getAnnotation(Annotable.FILE)).getPath():
		"");
		srcFileImport.setText(path);
		leftLine1.add(srcFileImport);
		leftLine1.add(buttonSelectSrc);
		//destination
		JLabel dstLabel = new JLabel("Destination:");
		
		Box leftLine2 = Box.createHorizontalBox();
		srcLabel.setHorizontalAlignment(JLabel.CENTER);
		
		dstFileImport.setMaximumSize(new Dimension(500, 25));
		JButton selectDst = new JButton("...");
		selectDst.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
//				System.out.println("b");
			}
		}
		);
		leftLine2.add(dstFileImport);
		leftLine2.add(selectDst);
		
		leftBox.add(srcLabel);
		leftBox.add(leftLine1);
		leftBox.add(dstLabel);
		leftBox.add(leftLine2);
		
		//right box
		importList = new JList();
		importList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		refreshImportPluginList();
		importList.addListSelectionListener(
				new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{
						String ext = ParsingToolbox.getFileType(srcFileImport.getText());
						Set<ImportExportPlugin> importers = IOPluginManager.getInstance().getImporters(ext);
						Iterator<ImportExportPlugin> it = importers.iterator();
						while(it.hasNext())
						{
							ImportExportPlugin plugin = it.next();
							if(plugin.getDescription().equals((String)importList.getSelectedValue())){
								selectedPlugin = plugin;
							}
						}
						if(selectedPlugin != null)
						{
							dstFileImport.setText(new String(ParsingToolbox.removeFileType(srcFileImport.getText()) + "." + IOUtilities.MODEL_FILE_EXT));
						}
					}
				}
		);
		
		JScrollPane spo=new JScrollPane(importList);
		spo.setPreferredSize(new Dimension(225,260));
		spo.setBorder(BorderFactory.createTitledBorder(Hub.string("exportListTitle")));
		rightBox.add(spo);
		panel.add(leftBox);
		panel.add(rightBox);
		refreshImportPluginList();
//		System.out.println(selectedPlugin);
		return panel;
	}
		
	//Draw the graphical components for the Export pane
	protected JComponent createExportPanel()
	{
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout(1, 1));
		Box leftBox=Box.createVerticalBox();
		Box rightBox=Box.createVerticalBox();
		//Source file:
		JLabel srcLabel = new JLabel("Source File:");
		
		JButton buttonSelectSrc = new JButton("...");
		buttonSelectSrc.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//Open a window for the user to choose the file to open:
				JFileChooser fc = new JFileChooser(Hub.persistentData.getProperty("LAST_PATH_SETTING_NAME"));
				fc.setDialogTitle(Hub.string("openModelTitle"));
				fc.setFileFilter(new IOUtilities.ExtensionFilter(new String[]{IOUtilities.MODEL_FILE_EXT}, Hub.string("modelFileDescription")));
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int retVal = fc.showOpenDialog(Hub.getMainWindow());
				if(retVal == JFileChooser.APPROVE_OPTION){
					srcFileExport.setText(fc.getSelectedFile().getPath());
					try{
						currentModel = IOCoordinator.getInstance().load(fc.getSelectedFile());
					}catch(IOException e){
						Hub.displayAlert(e.getMessage());
						return;
					}
					if(currentModel == null)
					{
						currentModel = Hub.getWorkspace().getActiveModel();
						//Throw an error saying that the selected file is invalid.
						srcFileExport.setText("Please choose a valid file.");
					}
					refreshExportPluginList();
					exportList.setSelectedIndex(0);
					if(selectedPlugin!=null){
						String dst = new String(ParsingToolbox.removeFileType(fc.getSelectedFile().getPath()) + "." + selectedPlugin.getExportExtension());
						dstFileExport.setText(dst);
					}else{
						dstFileExport.setText("");
					}
				}
			}
		}
		);
		
		Box leftLine1 = Box.createHorizontalBox();
		srcLabel.setHorizontalAlignment(JLabel.CENTER);
		srcFileExport.setMaximumSize(new Dimension(500, 25));
		String path = (currentModel.getAnnotation(Annotable.FILE) != null? 
				((File)currentModel.getAnnotation(Annotable.FILE)).getPath():
		"");
		srcFileExport.setText(path);
		leftLine1.add(srcFileExport);
		leftLine1.add(buttonSelectSrc);
		//destination
		JLabel dstLabel = new JLabel("Destination:");
		
		Box leftLine2 = Box.createHorizontalBox();
		srcLabel.setHorizontalAlignment(JLabel.CENTER);
		
		dstFileExport.setMaximumSize(new Dimension(500, 25));
		JButton selectDst = new JButton("...");
		selectDst.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
//				System.out.println("b");
			}
		}
		);
		leftLine2.add(dstFileExport);
		leftLine2.add(selectDst);
		
		leftBox.add(srcLabel);
		leftBox.add(leftLine1);
		leftBox.add(dstLabel);
		leftBox.add(leftLine2);
		
		//right box
		exportList = new JList();
		exportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		refreshExportPluginList();
		exportList.addListSelectionListener(
				new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{
						Set<ImportExportPlugin> exporters = IOPluginManager.getInstance().getExporters(currentModel.getModelDescriptor().getIOTypeDescription());
						Iterator<ImportExportPlugin> it = exporters.iterator();
						while(it.hasNext())
						{
							ImportExportPlugin plugin = it.next();
							if(plugin.getDescription().equals((String)exportList.getSelectedValue())){
								selectedPlugin = plugin;
							}
						}
						if(selectedPlugin != null)
						{
							dstFileExport.setText(new String(ParsingToolbox.removeFileType(srcFileExport.getText()) + "." + selectedPlugin.getExportExtension()));
						}
					}
				}
		);
		
		JScrollPane spo=new JScrollPane(exportList);
		spo.setPreferredSize(new Dimension(225,260));
		spo.setBorder(BorderFactory.createTitledBorder(Hub.string("exportListTitle")));
		rightBox.add(spo);
		panel.add(leftBox);
		panel.add(rightBox);
		refreshExportPluginList();
//		System.out.println(selectedPlugin);
		return panel;
	}
	
	/**
	 * Refresh the list of exporters with the registered plugins capable of exporting something
	 * from the currently selected file.
	 *
	 */
	protected void refreshExportPluginList()
	{
		Vector<String> descriptors = new Vector<String>();
		if(srcFileExport.getText().equals(""))
		{
			descriptors.add("Please select a source file");
			exportList.setListData(descriptors);
			return;
		}
		
		//Query the IOPluginManager for all the plugins capable of exporting from the selected file. 
		String modelType = currentModel.getModelDescriptor().getIOTypeDescription();
		Set<ImportExportPlugin> correctPlugins = IOPluginManager.getInstance().getExporters(modelType);
		Iterator<ImportExportPlugin> it = correctPlugins.iterator();
		while(it.hasNext())
		{
			ImportExportPlugin plugin = it.next();
			descriptors.add(plugin.getDescription());
		}
		//Refresh the list with the descriptors of the plugins and select the first one:
		exportList.setListData(descriptors);
		exportList.setSelectedIndex(0);
	}

	/**
	 * Refresh the list of importers with the registered plugins capable of exporting something
	 * from the currently selected file.
	 *
	 */
	protected void refreshImportPluginList()
	{
		if(srcFileImport.getText() != null)
		{
			String ext = ParsingToolbox.getFileType(srcFileImport.getText());
			Set<ImportExportPlugin> correctPlugins = IOPluginManager.getInstance().getImporters(ext);
			Iterator<ImportExportPlugin> it = correctPlugins.iterator();
			if(correctPlugins.iterator() == null)
			{
				return;
			}
			Vector<String> descriptors = new Vector<String>(); 
			while(it.hasNext())
			{
				ImportExportPlugin plugin = it.next();
				descriptors.add(plugin.getDescription());
			}
			//Refresh the list with the descriptors of the plugins and select the first one:
			if(descriptors != null)
			{
				importList.setListData(descriptors);
				importList.setSelectedIndex(0);
			}else{
					return;
				 }
		}
	}


	protected void onEscapeEvent()
	{
		dispose();
	}

}
		

		
        
	    
        
        
        
        
        
        
        
        
        
        
		
	    
        
	

	
