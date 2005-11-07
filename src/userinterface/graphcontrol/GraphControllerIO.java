/*
 * Created on Dec 2, 2004
 */
package userinterface.graphcontrol;

import userinterface.GraphingPlatform;

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
    // GraphControllerIO construction /////////////////////////////////////////////////////////////////////////////////
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
    // Miscelaneous ///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Notify the system that the current data structure has been changed. Main
     * use is enable/disable of save, etc buttons.
     */
    public void markUnsavedChanges() {
        gp.mc.file_save_automaton.setEnabled(true);
        unsaved_changes = true;
        //update the status
        gp.getStatus().setText("   "+gp.getOpenAutomatonName()+", states: "+
                gp.gc.gm.getNodeSize()+", transitions: "+gp.gc.gm.getEdgeSize());
    }

    /**
     * Reset all variables to the initial state.
     */
    public void resetState() {
        unsaved_changes = false;
        gp.mc.file_save_automaton.setEnabled(false);
    }
    
    public boolean isUnsaved(){
        return unsaved_changes;
    }
}
