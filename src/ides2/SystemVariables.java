package ides2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import userinterface.geometric.Point;

/**
 * This class provides a simple read/write interface to a plain text file. It is
 * used by the GraphingPlatform to read/write system variables at open/close of
 * the application. It functions as an os independent registry.
 * 
 * @author Michael Wood
 */
public class SystemVariables {
    /**
     * The name of the simple text file where the system variables will be
     * recorded
     */
    private static final String settings_file_name = "settings.txt";

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // system variables ///////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The physical location of the main JAR
     */
    public static String application_path = "";

    /**
     * The physical location used for system settings files and undo/redo files,
     * etc.
     */
    public static String system_path = "";

    /**
     * The last used physical location for save/load
     */
    public static String last_used_path = "";

    /**
     * Records the last "snap to grid" value.
     */
    public static int grid = 20;

    /**
     * Records the last "show_all_edges" state.
     */
    public static boolean show_all_edges = false;

    /**
     * Records the last "show_all_labels" state.
     */
    public static boolean show_all_labels = false;

    /**
     * Records whether or not crashing errors should automatically open a
     * webpage to report their details.
     */
    public static boolean use_error_reporting = true;

    /**
     * Force all nodes to the largest used radius
     */
    public static boolean use_standard_node_size = false;

    /**
     * The size for the node text eding window
     */
    public static Point floating_text_size = new Point(200, 100);
    
    /**
     * The path for graphviz
     */
    private static String graphvizPath = new String();
    
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // SystemVariables construction///////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the SystemVariables.
     */
    public SystemVariables() {       
        // calcualte the application path
        String class_paths = System.getProperty("java.class.path");
        if (class_paths.indexOf(System.getProperty("path.separator")) > 0) {
            class_paths = class_paths.substring(0, class_paths.indexOf(System
                    .getProperty("path.separator")));
        }
        if (class_paths.endsWith(".jar")) {
            class_paths = class_paths.substring(0, class_paths
                    .lastIndexOf(File.separator));
        }
        if (class_paths.length() > 0 && !class_paths.endsWith(File.separator)) {
            class_paths = class_paths + File.separator;
        }
        application_path = class_paths;

        // calcualte and create if necessary the system and tex paths.
        system_path = application_path + "system" + File.separator;
        (new File(system_path)).mkdir();

        fetchValues();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // miscelaneous///////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void fetchValues() {
        try {
            String this_token = null, next_token = null;
            BufferedReader in = new BufferedReader(new FileReader(system_path
                    + settings_file_name));
            String this_line = in.readLine();
            while (this_line != null) {
                StringTokenizer st = new StringTokenizer(this_line, "=");
                if (st.hasMoreTokens()) {
                    this_token = st.nextToken().trim();
                    if (st.hasMoreTokens()) {
                        next_token = st.nextToken().trim();
                        if (this_token.equals("grid")) {
                            grid = Integer.parseInt(next_token);
                        } else if (this_token.equals("last_save_path")) {
                            last_used_path = next_token;
                        } else if (this_token.equals("show_all_edges")) {
                            show_all_edges = (next_token.equals("true"));
                        } else if (this_token.equals("show_all_labels")) {
                            show_all_labels = (next_token.equals("true"));
                        } else if (this_token.equals("use_error_reporting")) {
                            use_error_reporting = (next_token.equals("true"));
                        } else if (this_token.equals("use_standard_node_size")) {
                            use_standard_node_size = (next_token.equals("true"));
                        } else if(this_token.equals("graphvizPath")){
                            graphvizPath = next_token;
                        }
                    }
                }
                this_line = in.readLine();
            }
            in.close();
        } catch (Exception e) {
        }
    }

    /**
     * Stores the values set in the systemvariable to a file
     */
    public void saveValues() {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(system_path + settings_file_name)));

            out.println("grid=" + grid);
            out.println("last_save_path=" + last_used_path);
            out.println("show_all_edges=" + show_all_edges);
            out.println("show_all_labels=" + show_all_labels);
            out.println("use_error_reporting=" + use_error_reporting);
            out.println("use_standard_node_size=" + use_standard_node_size);
            out.println("graphvizPath=" + graphvizPath);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the graphviz path in the systems variables, so it will be stored correctly
     * @param graphvizPath the graphvizpath to store
     */
    public static void setGraphvizPath(String graphvizPath){
        SystemVariables.graphvizPath = graphvizPath;
    }

    /**
     * Returns the current set graphvizpath. It should always be read from here to avoid multiple paths to graphviz
     * @return the graphviz path 
     */
    public static String getGraphvizPath(){
        return graphvizPath;
    }
}