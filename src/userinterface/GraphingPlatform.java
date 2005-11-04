package userinterface;

import ides2.SystemVariables;

import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
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
import userinterface.graphcontrol.graphparts.Edge;
import userinterface.graphcontrol.graphparts.Node;

import userinterface.menu.MenuController;

public class GraphingPlatform{

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
    public static final int GRAPH_CANVAS_TAB = 0, SPECIFICATIONS_TAB = 1;

    public GraphingPlatform(Composite parent, Shell shell, MenuController mc){

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
        // specifications
        // area////////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Note: this area has to be created before the graph area, because the
        // graph area reads values from these objects.

        // the transition data area
        Composite cmp_transitions = new Composite(tabFolder, SWT.NULL);

        // add it to the TabFolder
        languageSpec.setControl(cmp_transitions);

        // create a layout for the content composite (for the widgits inside the
        // composite)
        GridLayout glEvents = new GridLayout();
        glEvents.marginHeight = 3;
        glEvents.marginWidth = 3;
        glEvents.verticalSpacing = 0;
        glEvents.horizontalSpacing = 0;
        cmp_transitions.setLayout(glEvents); // attach it to the composite

        // add the transition data object
        es = new EventSpecification(cmp_transitions);

        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // graph area
        // /////////////////////////////////////////////////////////////////////////////////////////////////////
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
        // selection between tabs
        // /////////////////////////////////////////////////////////////////////////////////////////
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        tabFolder.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e){
                if(es.isChanged()){
                    es.setChanged(false);
                    gc.gm.accomodateLabels();
                    gc.repaint();
                }
            }
        });
    }

    public void setEnabled(boolean state){
        tabFolder.setEnabled(state);
    }

    public void open(String automatonName){

        Automaton tempautomaton = Userinterface.getProjectPresentation().getAutomatonByName(
                automatonName);

        // checks if it needs to be laid out
        if(tempautomaton.getStateIterator().hasNext()
                && !tempautomaton.getStateIterator().next().hasSubElement("graphic")){

            if(tempautomaton.getStateCount() > 30){
                MessageBox layout = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
                layout.setText(ResourceManager.getString("layout.warning.title"));
                layout.setMessage(ResourceManager.getMessage("layout.warning", Integer
                        .toString(tempautomaton.getStateCount())));
                int response = layout.open();
                switch(response){
                case SWT.YES:
                    automaton = tempautomaton;
                    break;
                case SWT.NO:
                    return;
                }
            }

            try{
                Userinterface.getProjectPresentation().layout(automatonName);
            }
            catch(Exception e){
                // asks the user if the path is correct
                // we have bad parameters; therefore, open popup window and
                // request valid info.
                FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);

                fileDialog.setText(ResourceManager.getString("graphviz.filedialog.title"));
                fileDialog.setFilterPath(SystemVariables.getGraphvizPath());
                String newPath = fileDialog.open();

                if(newPath == null){
                    MessageBox warning = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    warning.setMessage(ResourceManager.getString("graphviz.stillmissing"));
                    warning.setText(ResourceManager.getString("graphviz.stillmissing.title"));
                    warning.open();
                    return;
                }

                SystemVariables.setGraphvizPath(newPath);
                open(automatonName);
                return;
            }
        }

        gc.resetState();
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

            int id = s.getId();
            SubElement g = s.getSubElement("graphic");
            SubElement circle = g.getSubElement("circle");
            int x = Ascii.safeInt(circle.getAttribute("x"));
            int y = Ascii.safeInt(circle.getAttribute("y"));
            int r = Ascii.safeInt(circle.getAttribute("r"));

            SubElement arrow = g.getSubElement("arrow");
            float dx = Ascii.safeFloat(arrow.getAttribute("x"));
            float dy = Ascii.safeFloat(arrow.getAttribute("y"));

            SubElement name = s.getSubElement("name");
            String l = (name.getChars() != null) ? name.getChars() : "";

            Node n = new Node(this, gc.gm, x, y, r, 0, dx, dy, l, id);

            SubElement properties = s.getSubElement("properties");

            if(properties.hasSubElement("initial")) n.addAttribute(Node.START_STATE);

            if(properties.hasSubElement("marked")) n.addAttribute(Node.MARKED_STATE);

            gc.gm.addNode(n);
        }

        ListIterator<Event> ei = automaton.getEventIterator();

        while(ei.hasNext()){
            Event e = ei.next();
            SubElement properties = e.getSubElement("properties");
            es.createNewEvent(e.getSubElement("name").getChars(), e.getSubElement("description")
                    .getChars(), properties.hasSubElement("controllable"), properties
                    .hasSubElement("observable"));
        }

        ListIterator<Transition> ti = automaton.getTransitionIterator();
        Vector<Edge> group = new Vector<Edge>();

        while(ti.hasNext()){
            Transition t = ti.next();
            SubElement graphic = t.getSubElement("graphic");
            SubElement label = graphic.getSubElement("label");
            Event event = t.getEvent();

            // find the target node
            Node target = null;
            for(int i = 0; i < gc.gm.getNodeSize(); i++){
                target = gc.gm.getNodeById(i);
                if(target.getModelID() == t.getTarget().getId()) break;
            }

            // find the source node
            Node source = null;
            for(int i = 0; i < gc.gm.getNodeSize(); i++){
                source = gc.gm.getNodeById(i);
                if(source.getModelID() == t.getSource().getId()) break;
            }

            if(label.hasAttribute("group")){
                int gn = Ascii.safeInt(graphic.getSubElement("label").getAttribute("group"));
                if((group.size() > gn) && (group.elementAt(gn) != null)) group.get(gn).addLabel(
                        es.getEvent(event.getId()));
                else{
                    if(group.size() <= gn) group.setSize(gn + 1);
                    group.setElementAt(new Edge(this, gc.gm, source, target, graphic
                            .getSubElement("bezier"), Ascii.safeInt(label.getAttribute("x")), Ascii
                            .safeInt(label.getAttribute("y")), 0), gn);
                    if(event != null) group.get(gn).addLabel(es.getEvent(event.getId()));
                    gc.gm.addEdge(group.get(gn));
                }
            }
            else{
                Edge e = new Edge(this, gc.gm, source, target, graphic.getSubElement("bezier"),
                        Ascii.safeInt(label.getAttribute("x")), Ascii.safeInt(label
                                .getAttribute("y")), 0);
                if(event != null){
                    e.addLabel(es.getEvent(event.getId()));
                }
                gc.gm.addEdge(e);
            }

        }

        es.setChanged(false);
        gc.gm.accomodateLabels();
        gc.repaint();
        gc.io.resetState();
    }

    public void save(){
        // remove everything in the automaton

        if(automaton == null) return;

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
        // run the garbage collector. A lot has just been deleted.
        System.gc();

        // rebuild states
        for(int i = 0; i < gc.gm.getNodeSize(); i++){
            Node n = gc.gm.getNodeById(i);
            State s = new State(i);
            SubElement graphic = new SubElement("graphic");
            s.addSubElement(graphic);

            // circle
            SubElement circle = new SubElement("circle");
            graphic.addSubElement(circle);
            circle.setAttribute("x", Integer.toString(n.getX()));
            circle.setAttribute("y", Integer.toString(n.getY()));
            circle.setAttribute("r", Integer.toString(n.getR()));
            SubElement arrow = new SubElement("arrow");
            graphic.addSubElement(arrow);
            UnitVector u = n.getStartArrow();
            arrow.setAttribute("x", Float.toString(u.x));
            arrow.setAttribute("y", Float.toString(u.y));

            // properties
            SubElement properties = new SubElement("properties");
            s.addSubElement(properties);

            if(n.isMarkedState()){
                SubElement marked = new SubElement("marked");
                properties.addSubElement(marked);
            }

            if(n.isStartState()){
                SubElement initial = new SubElement("initial");
                properties.addSubElement(initial);
            }

            // name
            SubElement name = new SubElement("name");
            name.setChars(n.getGlyphLabel().string_representation.trim());
            s.addSubElement(name);

            automaton.add(s);
        }
        // rebuild events
        for(int i = 0; i < es.getEventCount(); i++){

            if(es.getName(i) == null) continue;

            Event e = new Event(i);
            automaton.add(e);

            SubElement name = new SubElement("name");
            e.addSubElement(name);
            name.setChars(es.getName(i));

            SubElement description = new SubElement("description");
            e.addSubElement(description);
            description.setChars(es.getDescription(i));

            SubElement properties = new SubElement("properties");
            e.addSubElement(properties);

            if(es.getControllable(i)){
                SubElement controllable = new SubElement("controllable");
                properties.addSubElement(controllable);
            }

            if(es.getObservable(i)){
                SubElement observable = new SubElement("observable");
                properties.addSubElement(observable);
            }
        }

        // rebuild transitions
        int gn = 0, j = 0;
        for(int i = 0; i < gc.gm.getEdgeSize(); i++){
            Edge e = gc.gm.getEdgeById(i);
            String[] events = e.getEventNames();
            // if the transition is triggered by one or zero events
            // make one transition
            if(events.length <= 1){
                Transition t = new Transition(i + j,
                        automaton.getState(gc.gm.getId(e.getSource())), automaton.getState(gc.gm
                                .getId(e.getTarget())));
                automaton.add(t);

                SubElement graphic = new SubElement("graphic");
                t.addSubElement(graphic);

                graphic.addSubElement(e.getCurve().toSubElement("bezier"));

                SubElement label = new SubElement("label");
                graphic.addSubElement(label);
                label.setAttribute("x", Integer.toString(e.getLabelDisplacement().getX()));
                label.setAttribute("y", Integer.toString(e.getLabelDisplacement().getY()));

                if(events.length == 1) t.setEvent(automaton.getEvent(es.getId(events[0])));
            }
            // otherwise make a lot of transitions.
            else{
                for(int k = 0; k < events.length; k++){
                    Transition t = new Transition(i + j++, automaton.getState(gc.gm.getId(e
                            .getSource())), automaton.getState(gc.gm.getId(e.getTarget())));
                    automaton.add(t);

                    t.setEvent(automaton.getEvent(es.getId(events[k])));

                    SubElement graphic = new SubElement("graphic");
                    t.addSubElement(graphic);

                    graphic.addSubElement(e.getCurve().toSubElement("bezier"));

                    SubElement label = new SubElement("label");
                    graphic.addSubElement(label);
                    label.setAttribute("group", Integer.toString(gn));
                    label.setAttribute("x", Integer.toString(e.getLabelDisplacement().getX()));
                    label.setAttribute("y", Integer.toString(e.getLabelDisplacement().getY()));
                }
                j--;
                gn++;
            }
        }
        gc.io.resetState();
    }

    public void updateTitle(){
        if(automaton != null){
            graphFolderItem.setText(automaton.getName());
        }
    }

    public void delete(String name){
        if(automaton != null && name.equals(automaton.getName())){
            gc.resetState();
            tabFolder.setEnabled(false);
            graphFolderItem.setText(ResourceManager.getString("window.graph_tab.text"));
        }
    }

    public String getOpenAutomatonName(){
        return (automaton != null) ? automaton.getName() : "";
    }
}
