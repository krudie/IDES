/**
 * 
 */
package ui;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import main.SystemVariables;

import ui.listeners.MenuListenerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * @author helen bretzke
 *
 */
public class MainWindow extends JFrame {

	public MainWindow() {
		super("IDES : Integrated Discrete-Event System Software 2.1"); // Call super.
		
		// create and add the menu
		createAndAddMenuBar();
		
		// create and add the toolbars
		
		// create tabbed panes and their components: 
		// canvas, graph specs panel, LaTeX and EPS output text areas.
		drawingBoard = new DrawingBoard();
		createAndAddTabbedPane();
		
		// TODO attach listener to drawing area
			    
	    	
		pack();
		setSize(800, 600);
		setVisible(true);
	}
	
	 private void createAndAddTabbedPane() {
		tabbedViews = new JTabbedPane();
		JScrollPane sp = new JScrollPane(drawingBoard, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);		
		tabbedViews.addTab("Un-named Graph", sp);
		tabbedViews.addTab("Graph Specification", null);
		tabbedViews.addTab("LaTeX Output", null);		
		getContentPane().add(tabbedViews, "Center");
	}

	 private void createAndAddToolBar() {
		 // Create a horizontal toolbar
		    JToolBar toolbar = new JToolBar();
		    toolbar.setRollover(true);
		    
		 // add buttons with icons and listeners
		    
		 // add toolbar to this window
		    
		    
	 }
	 
	private void createAndAddMenuBar() {
		 		 
		 JMenuBar menuBar = new JMenuBar();
		 
		 JMenu menuFile, menuExport;
		 JMenuItem miNewSystem, miOpen, miSave, miSaveAs, miExit;
		 JMenuItem miLatex, miGif, miPng;
		 
		 JMenu menuEdit;
		 JMenuItem miUndo, miRedo, miCut, miCopy, miPaste, miDelete;
		 
		 JMenu menuGraph;
		 // TODO add a submenu for all zoom and scale operations
		 // ? How about a 'Transform' submenu ?
		 JMenu menuTransform;
		 JMenuItem miZoomIn, miZoomOut, miScaleBy, miCreate, miModify, miPrintArea, miMove, miAllEdges, miAllLabels;
		 
		 JMenu menuOptions; 
		 JMenuItem miErrReports, miUseLatex, miExportEps, miExportTex, miDrawBorder, miStdNodeSize, miUsePstricks;
		 
		 JMenu menuHelp;
		  
		 String imagePath = SystemVariables.instance().getApplication_path() + "/src/images/icons/"; 
		 	 
		 // assemble the file menu
//		 TODO add listeners
		 ActionListener fileMenuListener = MenuListenerFactory.getFileMenuListener();
		 
		 menuFile = new JMenu("File");
		 menuFile.setMnemonic(KeyEvent.VK_F);
		 
		 menuExport = new JMenu("Export");
		 miLatex = new JMenuItem("LaTeX", new ImageIcon(imagePath + "file_export_latex.gif"));
		 miGif = new JMenuItem("GIF", new ImageIcon(imagePath + "file_export_gif.gif"));
		 miPng = new JMenuItem("PNG", new ImageIcon(imagePath + "file_export_png.gif"));
		 menuExport.add(miLatex);
		 menuExport.add(miGif);
		 menuExport.add(miPng);		 
		 menuFile.add(menuExport);
		 
		 menuFile.addSeparator();
		 		 
		 miNewSystem = new JMenuItem("New System", new ImageIcon(imagePath + "file_new.gif"));
		 miNewSystem.setMnemonic(KeyEvent.VK_N);
		 miNewSystem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		 menuFile.add(miNewSystem);
		 
		 miOpen = new JMenuItem("Open", new ImageIcon(imagePath + "file_open.gif"));
		 miOpen.setMnemonic(KeyEvent.VK_O);
		 miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		 miOpen.addActionListener(fileMenuListener);
		 menuFile.add(miOpen);
		 
		 miSave = new JMenuItem("Save", new ImageIcon(imagePath + "file_save.gif"));
		 miSave.setMnemonic(KeyEvent.VK_S);
		 miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		 menuFile.add(miSave);
		 
		 miSaveAs = new JMenuItem("Save As...", new ImageIcon(imagePath + "file_saveas.gif"));
		 miSaveAs.setMnemonic(KeyEvent.VK_A);		 
		 menuFile.add(miSaveAs);
		 
		 miExit = new JMenuItem("Exit");
		 miExit.setMnemonic(KeyEvent.VK_X);
		 menuFile.add(miExit);
		 
		 menuBar.add(menuFile);
		 
		 // assemble the edit menu
//		 TODO add listeners
		 menuEdit = new JMenu("Edit");
		 menuEdit.setMnemonic(KeyEvent.VK_E);
		 
		 miUndo = new JMenuItem("Undo", new ImageIcon(imagePath + "edit_undo.gif"));
		 miUndo.setMnemonic(KeyEvent.VK_U);
		 miUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		 menuEdit.add(miUndo);
		 
		 miRedo = new JMenuItem("Redo", new ImageIcon(imagePath + "edit_redo.gif"));
		 miRedo.setMnemonic(KeyEvent.VK_R);
		 miRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		 menuEdit.add(miRedo);
		 
		 menuEdit.addSeparator();
		 
		 miCut = new JMenuItem("Cut", new ImageIcon(imagePath + "edit_cut16.gif"));
		 miCut.setMnemonic(KeyEvent.VK_T);
		 miCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		 miCut.addActionListener(MenuListenerFactory.getCutListener());
		 menuEdit.add(miCut);
		 
		 miCopy = new JMenuItem("Copy", new ImageIcon(imagePath + "edit_copy.gif"));
		 miCopy.setMnemonic(KeyEvent.VK_C);
		 miCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		 miCopy.addActionListener(MenuListenerFactory.getCopyListener());
		 menuEdit.add(miCopy);
		 
		 miPaste = new JMenuItem("Paste", new ImageIcon(imagePath + "edit_paste.gif"));
		 miPaste.setMnemonic(KeyEvent.VK_V);
		 miPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		 menuEdit.add(miPaste);
		 
		 miDelete = new JMenuItem("Delete", new ImageIcon(imagePath + "edit_delete.gif"));
		 miDelete.setMnemonic(KeyEvent.VK_D);
		 miDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		 miDelete.addActionListener(MenuListenerFactory.getDeleteListener());
		 menuEdit.add(miDelete);
	 		 		 
		 menuBar.add(menuEdit);
		 
		 // assemble the graph menu
//		 TODO add listeners
		 menuGraph = new JMenu("Graph");
		 menuGraph.setMnemonic(KeyEvent.VK_G);
		 
		 miZoomIn = new JMenuItem("Zoom In", new ImageIcon(imagePath + "graphic_zoomin.gif"));
		 miZoomIn.setMnemonic(KeyEvent.VK_I);
		 miZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miZoomIn);

		 miZoomOut = new JMenuItem("Zoom Out", new ImageIcon(imagePath + "graphic_zoomout.gif"));
		 miZoomOut.setMnemonic(KeyEvent.VK_O);
		 miZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miZoomOut);
		 
		 miScaleBy = new JMenuItem("Scale By...", new ImageIcon(imagePath + "graphic_zoom.gif"));
		 miScaleBy.setMnemonic(KeyEvent.VK_S);
		 
		 // TODO Think up a memorable accelerator: ctrl+shift+S ?
		 // miScaleBy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miScaleBy);

		 miCreate = new JMenuItem("Create Nodes or Edges", new ImageIcon(imagePath + "graphic_create.gif"));
		 miCreate.setMnemonic(KeyEvent.VK_C);
		 // miCreate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miCreate);

		 miModify = new JMenuItem("Modify Nodes, Edges or Labels", new ImageIcon(imagePath + "graphic_modify.gif"));
		 miModify.setMnemonic(KeyEvent.VK_M);
		 // miModify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miModify);

		 miPrintArea = new JMenuItem("Select Print Area", new ImageIcon(imagePath + "graphic_printarea.gif"));
		 miPrintArea.setMnemonic(KeyEvent.VK_A);
		 // miPrintArea.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miPrintArea);

		 miMove = new JMenuItem("Move Graph", new ImageIcon(imagePath + "graphic_grab.gif")); // ??? is this the right image?
		 miMove.setMnemonic(KeyEvent.VK_V);
		 //miMove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miMove);

		 miAllEdges = new JMenuItem("Select All Edges", new ImageIcon(imagePath + "graphic_alledges.gif"));
		 miAllEdges.setMnemonic(KeyEvent.VK_E);
		 //miAllEdges.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miAllEdges);
 
		 miAllLabels = new JMenuItem("Select All Labels", new ImageIcon(imagePath + "graphic_alllabels.gif"));
		 miAllLabels.setMnemonic(KeyEvent.VK_N);
		 //miAllNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miAllLabels);

		 menuBar.add(menuGraph);
		 
		 // assemble the options menu
		 menuOptions = new JMenu("Options");
		 menuOptions.setMnemonic(KeyEvent.VK_O);		 
		 
		 // NOTE these items are toggles, some are dependent.
		 // TODO export eps or tex should be mutually exclusive and disabled(?) if not using LaTeX for labels.
		 // TODO change the order of these items; move frequently used to top of list.
		 // TODO add listeners
		 miDrawBorder = new JCheckBoxMenuItem("Draw a border when exporting");
		 miStdNodeSize =  new JCheckBoxMenuItem("Use standard node size");
		 miUsePstricks =  new JCheckBoxMenuItem("Use pstricks in LaTeX output");
		 miUseLatex = new JCheckBoxMenuItem("Use LaTeX for Labels");
		 miExportEps = new JCheckBoxMenuItem("Export to EPS");
		 miExportTex = new JCheckBoxMenuItem("Export to TEX");		 
		 miErrReports = new JCheckBoxMenuItem("Send Error Reports");
		
		 // TODO add listeners
		 
		 
		 menuOptions.add(miDrawBorder);
		 menuOptions.add(miStdNodeSize);
		 menuOptions.add(miUsePstricks);
		 menuOptions.add(miUseLatex);		 
		 menuOptions.add(miExportEps);
		 menuOptions.add(miExportTex);
		 menuOptions.add(miErrReports);
		 menuBar.add(menuOptions);
		 	 
		 // TODO assemble the help menu
		 menuHelp = new JMenu("Help");
		 menuHelp.setMnemonic(KeyEvent.VK_H);
		 
		 menuBar.add(menuHelp);
		 
		 // add menubar to this window
		 getContentPane().add(menuBar, "North");
	}
	
	/**
	 * The views.
	 */
	private JTabbedPane tabbedViews;
	private DrawingBoard drawingBoard;
	private JPanel graphSpecsView;
	private JPanel latexView;
}
