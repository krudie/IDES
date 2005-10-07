/*
 * Created on Dec 2, 2004
 */
package userinterface.graphcontrol;

import ides2.SystemVariables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import userinterface.GraphingPlatform;
import userinterface.ResourceManager;
import userinterface.geometric.Box;
import userinterface.geometric.Point;
import userinterface.graphcontrol.graphparts.Edge;
import userinterface.graphcontrol.graphparts.Node;

/**
 * This class handles all the IO for the GraphController. This includes: file
 * save/load, edit undo/redo and export to latex.
 * 
 * @author MichaelWood
 */
public class GraphControllerIO {
    /**
     * The platform in which this GraphObject exists.
     */
    private GraphingPlatform gp = null;

    /**
     * Records if any unsaved chanes have been made to the current graph.
     */
    public boolean unsaved_changes = false;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GraphControllerIO construction
    // /////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the GraphControllerIO.
     * 
     * @param gp
     *            The platform in which this GraphControllerIO will exist.
     */
    public GraphControllerIO(GraphingPlatform gp) {
        this.gp = gp;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Miscelaneous
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Notify the system that the current data structure has been changed. Main
     * use is enable/disable of save, etc buttons.
     */
    public void markUnsavedChanges() {
        gp.mc.file_save_automaton.enable();
        unsaved_changes = true;
    }

    /**
     * Reset all variables to the initial state.
     */
    public void resetState() {
        unsaved_changes = false;
        gp.mc.file_save_automaton.disable();
    }
}
