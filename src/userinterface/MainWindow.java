package userinterface;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

import userinterface.menu.MenuController;

public class MainWindow{

    /**
     * The display object for this platform.
     */
    private Display display = null;

    /**
     * The shell which contains the main gui.
     */
    private Shell shell = null;

    /**
     * A shell for displaying popup error windows.
     */
    private static Shell errorShell = null;

    /**
     * Strings for error reporting before the resourse bundle can be loaded.
     */
    public static final String FATAL_ERROR = "Fatal Error: ",
                               NULL_RESOURCE = "The resource bundle was null.  \nThis component requires the resource bundle.",
                               LOST_RESOURCE = "The resource bundle [resource_bundle.properties] did not load.  \nIt should be located at the root of the source code.";

    /**
     * The menucontroller object of the program
     */
    private static MenuController menu;

    /**
     * The projectexplorer which holds the project tree
     */
    private static ProjectExplorer pe;

    
    /**
     * The graphing platform which is the main class of the software written by Mike Wood
     */
    private static GraphingPlatform gp;
    
    
    /**
     * The constructor for the class 
     * @param splash takes the splash screen as an argument.
     */
    public MainWindow(Splash splash){
        try{
            display = Display.getDefault();
            // Shell for showing error messages
            errorShell = new Shell(display, SWT.NO_TRIM);
            shell = new Shell(display, SWT.SHELL_TRIM);
            new ResourceManager();
            initComponents();

            shell.addListener(SWT.Close, new Listener(){
                public void handleEvent(Event event){
                    event.doit = false;
                    if(menu != null && menu.getFileListener() != null){
                        menu.getFileListener().exit(new SelectionEvent(event));
                    }
                }
            });
            
            shell.layout();
            shell.open();
            splash.dispose();
            while(!shell.isDisposed()){
                if(!display.readAndDispatch()) display.sleep();
            }
            errorShell.dispose();
            display.dispose();
        }
        catch(Exception e){
            if(errorShell != null){
                errorShell.dispose();
            }
            if(shell != null){
                shell.dispose();
            }
            if(display != null){
                display.dispose();
            }

            Display error_display = Display.getDefault();
            Shell errorShell = new Shell(error_display, SWT.NO_TRIM);
            MessageBox messageBox = new MessageBox(errorShell, SWT.ICON_ERROR | SWT.CLOSE);

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();

            messageBox.setMessage(e.getMessage() + "\n\n" + stacktrace);
            messageBox.setText(FATAL_ERROR);
            messageBox.open();

            try{
                Runtime
                        .getRuntime()
                        .exec("rundll32 url.dll,FileProtocolHandler http://www.aggressivesoftware.com/research/ides/bugs/default.asp?bug="+ URLEncoder.encode(e.getMessage() + "\n\n" + stacktrace,"UTF-8"));
            }
            catch(Exception ex){
            }

            throw new RuntimeException(e);
        }
    }

    /**
     * Initialisation method for all the components on the gui     
     */
    private void initComponents(){

        // the window
        shell.setText(ResourceManager.getString("window.title"));
        shell.setImage(ResourceManager.getHotImage(ResourceManager.LOGO));
        shell.setSize(800, 620);

        // the base layout
        GridLayout gl_base = new GridLayout();
        gl_base.verticalSpacing = 0;
        gl_base.marginHeight = 0;
        gl_base.marginWidth = 0;
        gl_base.numColumns = 3;
        shell.setLayout(gl_base); // attach it to the shell

        menu = new MenuController(shell);

        SashForm mainSash = new SashForm(shell, SWT.HORIZONTAL | SWT.SMOOTH);
        
        GridData sashLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
        sashLayoutData.horizontalSpan = 3;
        mainSash.setLayoutData(sashLayoutData);
        
        
        SashForm leftSash = new SashForm(mainSash, SWT.VERTICAL);

        pe = new ProjectExplorer(leftSash, shell);
        //new ObjectExplorer(leftSash);
        
        
        gp = new GraphingPlatform(mainSash, shell, menu);
        gp.setEnabled(false);

        mainSash.setWeights(new int[] {30, 70});
        
        
    }

    /**
     * A static method for showing error pop ups. Should only be used when a serious error occurs that crashes the program.
     * @param error_title The title of the popup box
     * @param error_message The message in the popup box
     */    
    public static void fatalErrorPopup(String error_title, String error_message){
        MessageBox error_popup = new MessageBox(errorShell, SWT.ICON_ERROR | SWT.CLOSE);
        error_popup.setMessage(error_message);
        error_popup.setText(error_title);
        error_popup.open();
        throw new RuntimeException(error_title + "\n" + error_message);
    }

    /**
     * Returns the project explorer
     * @return the project explorer of the program
     */
    public static ProjectExplorer getProjectExplorer(){
        return pe;
    }

    /**
     * Returns the graphing platform, which is the main entrance to the drawing part of the GUI
     * @return The graphing platform for the program
     */
    public static GraphingPlatform getGraphingPlatform(){
        return gp;
    }

    /**
     * The menupart of the program
     * @return The menucontroller object
     */
    public static MenuController getMenu(){
        return menu;
    }

    /**
     * A method for showing non fatal error messages
     * @param errorTitle error_title The title of the popup box
     * @param errorMessage The message in the popup box
     */
    public static void errorPopup(String errorTitle, String errorMessage){
        MessageBox errorPopup = new MessageBox(errorShell, SWT.ICON_ERROR | SWT.CLOSE);
        errorPopup.setMessage(errorMessage);
        errorPopup.setText(errorTitle);
        errorPopup.open();
    }
    

}
