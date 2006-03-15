/**
 * 
 */
package ui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
		createAndAddTabbedPane();
		
		// attach listener to drawing area
			    
	    	
		pack();
		setSize(800, 600);
		setVisible(true);
	}
	
	 private void createAndAddTabbedPane() {
		tabbedViews = new JTabbedPane();
		tabbedViews.addTab("Un-named Graph", null);
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
		 JMenu menuScale;
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
	 
		 // assemble the graph menu
		 menuGraph = new JMenu("Graph");
		 menuGraph.setMnemonic(KeyEvent.VK_G);
		 
		 miZoomIn = new JMenuItem("Zoom In");
		 miZoomIn.setMnemonic(KeyEvent.VK_I);
		 miZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuEdit.add(miZoomIn);

		 miZoomOut = new JMenuItem("Zoom Out");
		 miZoomOut.setMnemonic(KeyEvent.VK_O);
		 miZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
		 menuEdit.add(miZoomOut);
		 
		 miScaleBy = new JMenuItem("Scale By...");
		 miScaleBy.setMnemonic(KeyEvent.VK_S);
		 
		 // TODO Think up a memorable accelerator: ctrl+shift+S ?
		 // miScaleBy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuEdit.add(miScaleBy);

		 miCreate = new JMenuItem("Create Nodes or Edges");
		 miCreate.setMnemonic(KeyEvent.VK_C);
		 // miCreate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuEdit.add(miCreate);

		 miModify = new JMenuItem("Modify Nodes, Edges or Labels");
		 miModify.setMnemonic(KeyEvent.VK_M);
		 // miModify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuEdit.add(miModify);

		 miPrintArea = new JMenuItem("Print Area");
		 miPrintArea.setMnemonic(KeyEvent.VK_A);
		 // miPrintArea.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuEdit.add(miPrintArea);

		 miMove = new JMenuItem("Move Graph");
		 miMove.setMnemonic(KeyEvent.VK_V);
		 //miMove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuEdit.add(miMove);

		 miAllEdges = new JMenuItem("Select All Edges");
		 miAllEdges.setMnemonic(KeyEvent.VK_E);
		 //miAllEdges.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuEdit.add(miAllEdges);
 
		 miAllNodes = new JMenuItem("Select All Nodes");
		 miAllNodes.setMnemonic(KeyEvent.VK_N);
		 //miAllNodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		 menuEdit.add(miAllNodes);

		 menuBar.add(menuEdit);
		 
		 // assemble the options menu
		 menuOptions = new JMenu("Options");
		 menuOptions.setMnemonic(KeyEvent.VK_O);		 
		 
		 // miErrReports, miUseLatex, miExportEps, miExportTex, miDrawBorder, stdNodeSize, usePstricks;
		 // NOTE most of these items are toggles, some are dependent.
		 // export eps or tex should be mutually exclusive and disabled(?) if not using LaTeX for labels.
		 // TODO change the order of these items; move frequently used to top of list.
		 miErrReports = new JMenuItem("Send Error Reports");
		 // ...
		 
		 menuBar.add(menuOptions);
		 	 
		 // assemble the help menu
		 menuHelp = new JMenu("Help");
		 menuHelp.setMnemonic(KeyEvent.VK_H);
		 
		 menuBar.add(menuHelp);
		 
		 // add menubar to this window
		 getContentPane().add(menuBar, "North");
	}

	/*
	  * Test: Override the paint method to draw a quadrilateral consisting of 
	  * straight lines, a quadratic curve and a bezier curve.
	  */
/*	  public void paint(Graphics g)  {
	    
	    Graphics2D g2d = (Graphics2D)g;
	    GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	    
	    path.moveTo(20.0f,50.0f);  // first point
	    path.lineTo(0.0f,125.0f);  // straight line
	    path.quadTo(100.0f,100.0f,225.0f,125.0f);  // quadratic curve
	    path.curveTo(260.0f,100.0f,130.0f,50.0f,225.0f,0.0f);  // cubic (bezier) curve (ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2) starts at current point
	    path.closePath();
	    
	    AffineTransform at = new AffineTransform();
	    at.setToRotation(-Math.PI/8.0);
	    g2d.transform(at);
	    at.setToTranslation(0.0f,150.0f);
	    g2d.transform(at);
	    g2d.setColor(Color.blue);
	    g2d.setStroke(new BasicStroke(3));
	                  
	    // g2d.fill(path);
	    g2d.draw(path);
	    
	  }  
	*/
	
	private JTabbedPane tabbedViews;
}
