package userinterface;

import java.util.ListIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import projectModel.Automaton;
import projectModel.Event;
import projectModel.State;
import projectModel.SubElement;
import projectModel.Transition;

import userinterface.general.Ascii;
import userinterface.geometric.UnitVector;
import userinterface.graphcontrol.GraphController;

import userinterface.graphcontrol.EventSpecification;
import userinterface.graphcontrol.graphparts.Curve;
import userinterface.graphcontrol.graphparts.Edge;
import userinterface.graphcontrol.graphparts.Node;

import userinterface.menu.MenuController;

public class GraphingPlatform {

    // private Composite da;
    public TabFolder tabFolder;

    private TabItem graphFolderItem, languageSpec;

    public GraphController gc;

    public Display display;

    public Shell shell;

    public MenuController mc;
    
    private Automaton automaton;

    /**
     * The object that contains the transition data and exists in the info in
     * the specifications tab
     */
    public EventSpecification es = null;

    /**
     * Indicies of TabItems within the TabFolder
     */
    public static final int GRAPH_CANVAS_TAB = 0,
                            SPECIFICATIONS_TAB = 1;

    public GraphingPlatform(Composite parent, Shell shell, MenuController mc) {

        this.mc = mc;
        this.shell = shell;
        display = Display.getDefault();

        // tabfolders
        tabFolder = new TabFolder(parent, SWT.NONE);

        graphFolderItem = new TabItem(tabFolder, SWT.NONE);
        graphFolderItem.setText(ResourceManager.getString("window.graph_tab.text"));

        languageSpec = new TabItem(tabFolder, SWT.NONE);
        languageSpec.setText(ResourceManager.getString("window.specifications_tab.text"));

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // specifications area ////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Note: this area has to be created before the graph area, because the
        // graph area reads values from these objects.

        // the transition data area
        Composite cmp_transitions = new Composite(tabFolder, SWT.NULL);

        // add it to the TabFolder
        languageSpec.setControl(cmp_transitions);

        // create a layout for the content composite (for the widgits inside the composite)
        GridLayout glEvents = new GridLayout();
        glEvents.marginHeight = 3;
        glEvents.marginWidth = 3;
        glEvents.verticalSpacing = 0;
        glEvents.horizontalSpacing = 0;
        cmp_transitions.setLayout(glEvents); // attach it to the composite

        // add the transition data object
        es = new EventSpecification(cmp_transitions);

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // graph area /////////////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // the graphing area
        Composite cmpGraphing = new Composite(tabFolder, SWT.BORDER);

        // add it to the TabFolder
        graphFolderItem.setControl(cmpGraphing);

        // create a layout for the content composite (for the widgits inside the
        // composite)
        GridLayout gl_graphing = new GridLayout();
        gl_graphing.marginHeight = 0;
        gl_graphing.marginWidth = 0;
        gl_graphing.verticalSpacing = 0;
        gl_graphing.horizontalSpacing = 0;
        gl_graphing.numColumns = 2;
        cmpGraphing.setLayout(gl_graphing); // attach it to the composite

        gc = new GraphController(this, cmpGraphing);

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // selection between tabs /////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        tabFolder.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (es.isChanged()) {
                    es.setChanged(false);
                    gc.gm.accomodateLabels();
                    gc.repaint();
                }
            }
        });
    }

    public void setEnabled(boolean state) {
        tabFolder.setEnabled(state);
    }

    public void open(String automatonName) {
        setEnabled(true);
        MainWindow.getMenu().graphic_zoom.setEnabled(true);
        MainWindow.getMenu().graphic_create.setEnabled(true);
        MainWindow.getMenu().graphic_modify.setEnabled(true);
        MainWindow.getMenu().graphic_grab.setEnabled(true);
        MainWindow.getMenu().graphic_grid.setEnabled(true);
        MainWindow.getMenu().graphic_alledges.setEnabled(true);
        MainWindow.getMenu().graphic_alllabels.setEnabled(true);
        
        automaton = Userinterface.getProjectPresentation().getAutomatonByName(automatonName);
        
        graphFolderItem.setText(automaton.getName());
        
        ListIterator<State> si = automaton.getStateIterator();
        while(si.hasNext()){
            State s = si.next();
            SubElement g = s.getSubElement("graphic");
            
            int x = Ascii.safeInt(g.getAttribute("x"));
            int y = Ascii.safeInt(g.getAttribute("y"));
            int r = Ascii.safeInt(g.getAttribute("r"));
            int a = Ascii.safeInt(g.getAttribute("a"));
            float dx = Ascii.safeFloat(g.getAttribute("dx"));
            float dy = Ascii.safeFloat(g.getAttribute("dy"));
            String l = Ascii.unEscapeReturn(g.getAttribute("l"));
            
            gc.gm.addNode(new Node(this,gc.gm,x,y,r,a,dx,dy,l));
            gc.repaint();
            gc.gm.accomodateLabels();
        }
        
        ListIterator<Transition> ti = automaton.getTransitionIterator();
        while(ti.hasNext()){
            Transition t = ti.next();
            gc.gm.addEdge(new Edge(this, gc.gm, gc.gm.getNodeById(t.getSource().getId()),
                    gc.gm.getNodeById(t.getTarget().getId()),
                    t.getSubElement("qubicCurve"),
                    Ascii.safeInt("0"),
                    Ascii.safeInt("0")
                    ,0));
            
        }
    }
    
    public void save(){
        //remove everything in the automaton
        
        ListIterator<State> si = automaton.getStateIterator();
        while(si.hasNext()){
            si.next();
            si.remove();
        }
        ListIterator<Transition> ti = automaton.getTransitionIterator();
        while(ti.hasNext()){
            ti.next();
            ti.remove();
        }
        ListIterator<Event> ei = automaton.getEventIterator();
        while(ei.hasNext()){
            ei.next();
            ei.remove();
        }
        
        //rebuild states
        for(int i = 0; i < gc.gm.getNodeSize(); i++){
            Node n = gc.gm.getNodeById(i);
            State s = new State(i);
            SubElement g = new SubElement("graphic");
            s.addSubElement("graphic", g);
                  
            g.setAttribute("x", ""+n.getX());
            g.setAttribute("y", ""+n.getY());
            g.setAttribute("r", ""+n.getR());
            g.setAttribute("a", "0");

            UnitVector u = n.getStartArrow();
            g.setAttribute("dx", ""+u.x);
            g.setAttribute("dy", ""+u.y);
            g.setAttribute("l", n.getGlyphLabel().string_representation);
            
            automaton.addState(s);
        }
        
        //rebuilt transitions
        for(int i = 0; i < gc.gm.getEdgeSize(); i++){
            Edge e = gc.gm.getEdgeById(i);
            Transition t = new Transition(i, 
                    automaton.getState(gc.gm.getId(e.getSource())), 
                    automaton.getState(gc.gm.getId(e.getTarget())));
            
            t.addSubElement("qubicCurve",e.getCurve().toSubElement("qubicCurve"));
            
            automaton.addTransition(t);
        }
        
    }
    
    public void updateTitle(){
        graphFolderItem.setText(automaton.getName());
    }
}
