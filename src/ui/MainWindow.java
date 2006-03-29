/**
 * 
 */
package ui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.*;

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
		
		// create tabbed panes and their components: canvas, graph specs panel, LaTeX output text area.
		drawingBoard = new DrawingBoard();
		createAndAddTabbedPane();
		
		// attach listener to drawing area
			    
	    	
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

	private void createAndAddMenuBar() {
		 
		 // TODO make these into instance variables so listeners can access them
		 // TODO decide how to design the listeners
		 JMenuBar menuBar = new JMenuBar();
		 
		 JMenu menuFile, menuExport;
		 JMenuItem miNewSystem, miOpen, miSave, miSaveAs, miExit;
		 JMenuItem miLatex, miGif, miPng;
		 
		 JMenu menuEdit;
		 JMenuItem miUndo, miRedo, miCopy, miPaste, miDelete;
		 
		 JMenu menuGraph;
		 // TODO add a submenu for all zoom and scale operations
		 // ? How about a 'Transform' submenu ?
		 JMenu menuTransform;
		 JMenuItem miZoomIn, miZoomOut, miScaleBy, miCreate, miModify, miPrintArea, miMove, miAllEdges, miAllNodes;
		 
		 JMenu menuOptions; 
		 JMenuItem miErrReports, miUseLatex, miExportEps, miExportTex, miDrawBorder, stdNodeSize, usePstricks;
		 
		 JMenu menuHelp;
		  
		 
		 // TODO add icons to each of these
		 // assemble the file menu
		 menuFile = new JMenu("File");
		 menuFile.setMnemonic(KeyEvent.VK_F);
		 
		 menuExport = new JMenu("Export");
		 miLatex = new JMenuItem("LaTeX");
		 miGif = new JMenuItem("GIF");
		 miPng = new JMenuItem("PNG");
		 menuExport.add(miLatex);
		 menuExport.add(miGif);
		 menuExport.add(miPng);
		 
		 menuFile.addSeparator();
		 
		 miNewSystem = new JMenuItem("New System");
		 miNewSystem.setMnemonic(KeyEvent.VK_N);
		 miNewSystem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		 menuFile.add(miNewSystem);
		 
		 miOpen = new JMenuItem("Open");
		 miOpen.setMnemonic(KeyEvent.VK_O);
		 miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		 menuFile.add(miOpen);
		 
		 miSave = new JMenuItem("Save");
		 miSave.setMnemonic(KeyEvent.VK_S);
		 miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		 menuFile.add(miSave);
		 
		 miSaveAs = new JMenuItem("Save As...");
		 miSaveAs.setMnemonic(KeyEvent.VK_A);		 
		 menuFile.add(miSaveAs);
		 
		 miExit = new JMenuItem("Exit");
		 miExit.setMnemonic(KeyEvent.VK_X);
		 menuFile.add(miExit);
		 
		 menuBar.add(menuFile);
		 
		 // assemble the edit menu
		 menuEdit = new JMenu("Edit");
		 menuEdit.setMnemonic(KeyEvent.VK_E);
		 
		 miUndo = new JMenuItem("Undo");
		 miUndo.setMnemonic(KeyEvent.VK_U);
		 miUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		 menuEdit.add(miUndo);
		 
		 miRedo = new JMenuItem("Redo");
		 miRedo.setMnemonic(KeyEvent.VK_R);
		 miRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		 menuEdit.add(miRedo);
		 
		 menuEdit.addSeparator();
		 
		 miCopy = new JMenuItem("Copy");
		 miCopy.setMnemonic(KeyEvent.VK_C);
		 miCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		 menuEdit.add(miCopy);
		 
		 miPaste = new JMenuItem("Paste");
		 miPaste.setMnemonic(KeyEvent.VK_V);
		 miPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		 menuEdit.add(miPaste);
		 
		 miDelete = new JMenuItem("Delete");
		 miDelete.setMnemonic(KeyEvent.VK_D);
		 miDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		 menuEdit.add(miDelete);
	 
		 menuBar.add(menuEdit);
		 
		 // assemble the graph menu
		 menuGraph = new JMenu("Graph");
		 menuGraph.setMnemonic(KeyEvent.VK_G);
		 
		 miZoomIn = new JMenuItem("Zoom In");
		 miZoomIn.setMnemonic(KeyEvent.VK_I);
		 miZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miZoomIn);

		 miZoomOut = new JMenuItem("Zoom Out");
		 miZoomOut.setMnemonic(KeyEvent.VK_O);
		 miZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miZoomOut);
		 
		 miScaleBy = new JMenuItem("Scale By...");
		 miScaleBy.setMnemonic(KeyEvent.VK_S);
		 
		 // TODO Think up a memorable accelerator: ctrl+shift+S ?
		 // miScaleBy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miScaleBy);

		 miCreate = new JMenuItem("Create Nodes or Edges");
		 miCreate.setMnemonic(KeyEvent.VK_C);
		 // miCreate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miCreate);

		 miModify = new JMenuItem("Modify Nodes, Edges or Labels");
		 miModify.setMnemonic(KeyEvent.VK_M);
		 // miModify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miModify);

		 miPrintArea = new JMenuItem("Select Print Area");
		 miPrintArea.setMnemonic(KeyEvent.VK_A);
		 // miPrintArea.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miPrintArea);

		 miMove = new JMenuItem("Move Graph");
		 miMove.setMnemonic(KeyEvent.VK_V);
		 //miMove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miMove);

		 miAllEdges = new JMenuItem("Select All Edges");
		 miAllEdges.setMnemonic(KeyEvent.VK_E);
		 //miAllEdges.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miAllEdges);
 
		 miAllNodes = new JMenuItem("Select All Nodes");
		 miAllNodes.setMnemonic(KeyEvent.VK_N);
		 //miAllNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuGraph.add(miAllNodes);

		 menuBar.add(menuGraph);
		 // assemble the options menu
		 menuOptions = new JMenu("Options");
		 menuOptions.setMnemonic(KeyEvent.VK_O);		 
		 
		 // miErrReports, miUseLatex, miExportEps, miExportTex, miDrawBorder, stdNodeSize, usePstricks;
		 // NOTE most of these items are toggles, some are dependent.
		 // export eps or tex should be mutually exclusive and disabled(?) if not using LaTeX for labels.
		 // TODO change the order of these items; move frequently used to top of list.
		 miErrReports = new JMenuItem("Send Error Reports");
		 // ...
		 menuOptions.add(miErrReports);
		 menuBar.add(menuOptions);
		 	 
		 // assemble the help menu
		 menuHelp = new JMenu("Help");
		 menuHelp.setMnemonic(KeyEvent.VK_H);
		 
		 menuBar.add(menuHelp);
		 
		 // add menubar to this window
		 getContentPane().add(menuBar, "North");
	}
	
	
	// User interaction modes to determine mouse and keyboard responses.
	public final static int DEFAULT_MODE = 0;
	public final static int SELECT_AREA_MODE = 1;
	public final static int ZOOM_MODE = 2;
	public final static int CREATE_MODE = 3;
	public final static int MODIFY_MODE = 4;
	public final static int MOVE_MODE = 5;
	public final static int TEXT_MODE = 6;

	// the current user interaction mode; 
	// determines response to mouse and keyboard actions; 
	// determines cursor appearance
	private int interactionMode = DEFAULT_MODE;
	
	private JTabbedPane tabbedViews;
	private DrawingBoard drawingBoard;
	private JPanel graphSpecsView;
	private JPanel latexView;
}
