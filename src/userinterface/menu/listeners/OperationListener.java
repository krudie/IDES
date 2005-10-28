/**
 * 
 */
package userinterface.menu.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import projectModel.Automaton;
import projectPresentation.Composition;
import projectPresentation.SuperVisory;

import userinterface.MainWindow;
import userinterface.ResourceManager;
import userinterface.Userinterface;

/**
 * @author edlund
 * 
 */
public class OperationListener extends AbstractListener{

    
    private Shell shell;

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Operations construction/////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Construct the ListenersFile.
     * @param shell The main shell
     */
    public OperationListener(Shell shell){
        this.shell = shell;
    }
    
    
    public SelectionListener getListener(String resource_handle){

        if(resource_handle.equals(ResourceManager.OPERATIONS_ACCESIBLE)){
            return new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    accesible();
                }
            };
        }

        if(resource_handle.equals(ResourceManager.OPERATIONS_COACCESIBLE)){
            return new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    coaccesible();
                }
            };
        }

        if(resource_handle.equals(ResourceManager.OPERATIONS_TRIM)){
            return new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    trim();
                }
            };
        }
        if(resource_handle.equals(ResourceManager.OPERATIONS_PRODUCT)){
            return new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    product();
                }
            };
        }
        if(resource_handle.equals(ResourceManager.OPERATIONS_PARALLEL)){
            return new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    parallel();
                }
            };
        }
        if(resource_handle.equals(ResourceManager.OPERATIONS_SUPC)){
            return new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    supC();
                }
            };
        }
        if(resource_handle.equals(ResourceManager.OPERATIONS_PREFIXCLOSURE)){
            return new SelectionAdapter(){
                public void widgetSelected(SelectionEvent e){
                    prefixClosure();
                }
            };
        }
        return null;
    }

    private void trim(){
        String selectedNames[] = MainWindow.getProjectExplorer().getSelectedAutomaton();
        String newName;
        for(int i = 0; i < selectedNames.length; i++){
            if(selectedNames[i] != null){
                newName = MainWindow.getProjectExplorer().getTitle(ResourceManager.getString(ResourceManager.OPERATIONS_TRIM) + "(" + selectedNames[i] + ")");

                if(MainWindow.getGraphingPlatform().getOpenAutomatonName().equals(selectedNames[i])){
                    MainWindow.getGraphingPlatform().save();
                }

                Automaton automaton = Userinterface.getProjectPresentation().copyAutomaton(selectedNames[i], newName);
                projectPresentation.Unary.trim(automaton);
            }

        }
        MainWindow.getProjectExplorer().updateProject();
    }

    private void coaccesible(){
        String selectedNames[] = MainWindow.getProjectExplorer().getSelectedAutomaton();
        String newName;
        for(int i = 0; i < selectedNames.length; i++){
            if(selectedNames[i] != null){

                if(MainWindow.getGraphingPlatform().getOpenAutomatonName().equals(selectedNames[i])){
                    MainWindow.getGraphingPlatform().save();
                }

                newName = MainWindow.getProjectExplorer().getTitle(ResourceManager.getString(ResourceManager.OPERATIONS_COACCESIBLE) + "(" + selectedNames[i] + ")");
                Automaton automaton = Userinterface.getProjectPresentation().copyAutomaton(selectedNames[i], newName);

                projectPresentation.Unary.coAccesible(automaton);
            }

        }
        MainWindow.getProjectExplorer().updateProject();
    }

    public void accesible(){
        String selectedNames[] = MainWindow.getProjectExplorer().getSelectedAutomaton();
        String newName;
        for(int i = 0; i < selectedNames.length; i++){
            if(selectedNames[i] != null){
                if(MainWindow.getGraphingPlatform().getOpenAutomatonName().equals(selectedNames[i])){
                    MainWindow.getGraphingPlatform().save();
                }
                newName = MainWindow.getProjectExplorer().getTitle(ResourceManager.getString(ResourceManager.OPERATIONS_ACCESIBLE) + "(" + selectedNames[i] + ")");
                Automaton automaton = Userinterface.getProjectPresentation().copyAutomaton(selectedNames[i], newName);
                projectPresentation.Unary.accesible(automaton);
            }

        }
        MainWindow.getProjectExplorer().updateProject();
    }

    public void product(){
        String[] selectedNames = MainWindow.getProjectExplorer().getSelectedAutomaton();
        String name = ResourceManager.getString(ResourceManager.OPERATIONS_PRODUCT) + "(";

        if(selectedNames.length < 2) return;
        name += selectedNames[0];
              
        for(int i = 1; i < selectedNames.length; i++){
            if(MainWindow.getGraphingPlatform().getOpenAutomatonName().equals(selectedNames[i])){
                MainWindow.getGraphingPlatform().save();
            }
            name += ", " + selectedNames[i];
        }
        name += ")";
        
        name = MainWindow.getProjectExplorer().getTitle(name);

        Automaton[] automata = new Automaton[selectedNames.length];
        for(int i=0; i<automata.length; i++){
            automata[i] = Userinterface.getProjectPresentation().getAutomatonByName(selectedNames[i]);
        }
        
        Automaton answer = Composition.product(automata, name);
        
        if(answer == null) return;

        Userinterface.getProjectPresentation().addAutomaton(answer);
        MainWindow.getProjectExplorer().updateProject();
    }
        
    
    public void parallel(){
        String[] selectedNames = MainWindow.getProjectExplorer().getSelectedAutomaton();
        String name = ResourceManager.getString(ResourceManager.OPERATIONS_PARALLEL) + "(";

        if(selectedNames.length < 2) return;
        name += selectedNames[0];
              
        for(int i = 1; i < selectedNames.length; i++){
            if(MainWindow.getGraphingPlatform().getOpenAutomatonName().equals(selectedNames[i])){
                MainWindow.getGraphingPlatform().save();
            }
            name += ", " + selectedNames[i];
        }
        name += ")";
        
        name = MainWindow.getProjectExplorer().getTitle(name);

        Automaton[] automata = new Automaton[selectedNames.length];
        for(int i=0; i<automata.length; i++){
            automata[i] = Userinterface.getProjectPresentation().getAutomatonByName(selectedNames[i]);
        }
        
        Automaton answer = Composition.parallel(automata, name);
        
        if(answer == null) return;

        Userinterface.getProjectPresentation().addAutomaton(answer);
        MainWindow.getProjectExplorer().updateProject();
    }
    
    public void supC(){
        
        String[] automataNames = MainWindow.getProjectExplorer().getAutomata();
        
        if(automataNames == null) return;
        final Shell chooser = new Shell(shell, SWT.SHELL_TRIM);
        chooser.setText(ResourceManager.getString("operations.supCpopup.title"));
        chooser.setSize(400, 250);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 4;
        layout.marginWidth = 4;
        layout.verticalSpacing = 4;
        layout.horizontalSpacing = 4;
        layout.numColumns = 2;
        chooser.setLayout(layout);
        
        
        Label plantLabel = new Label(chooser, SWT.NONE);
        plantLabel.setText(ResourceManager.getString("operations.plant"));
        plantLabel.pack();
        
        final Combo plantCombo = new Combo (chooser, SWT.READ_ONLY);
        
        
        plantCombo.setItems (automataNames);
        plantCombo.setSize (200, 200);
        plantCombo.pack();
        
        Label legalLabel = new Label(chooser, SWT.NONE);
        legalLabel.setText(ResourceManager.getString("operations.legal"));
        legalLabel.pack();
        
        final Combo legalCombo = new Combo (chooser, SWT.READ_ONLY);
        legalCombo.setItems (automataNames);
        legalCombo.setSize (200, 200);
        
        Button ok = new Button (chooser, SWT.PUSH);        
        ok.setText (ResourceManager.getString("ok"));
        ok.addListener(SWT.Activate, new Listener(){

            public void handleEvent(Event arg0){
                //close the window and proceed
                System.out.println(plantCombo.getText());
                supCoperation(plantCombo.getText(), legalCombo.getText());
                chooser.close();
                chooser.dispose();
            }
            
        });
        Button cancel = new Button (chooser, SWT.PUSH);
        
        cancel.addListener(SWT.Activate, new Listener(){
            public void handleEvent(Event arg0){
                //close the window and and exit the supC
                chooser.close();
                chooser.dispose();
            }
            
        });
        cancel.setText (ResourceManager.getString("cancel"));
                                
        
        chooser.pack();
        chooser.open();
        
        

    }
   
    
    private void supCoperation(String plant, String legal){
        
        if(plant == null || legal == null) return;
        
        String name = ResourceManager.getString(ResourceManager.OPERATIONS_SUPC) + "(";        
        name += plant + "," + legal + ")";
                
        name = MainWindow.getProjectExplorer().getTitle(name);

        if(MainWindow.getGraphingPlatform().getOpenAutomatonName().equals(plant) || MainWindow.getGraphingPlatform().getOpenAutomatonName().equals(legal)){
            MainWindow.getGraphingPlatform().save();
        }
        
        Userinterface.getProjectPresentation().newAutomaton(name);
        
        SuperVisory.supC(Userinterface.getProjectPresentation().getAutomatonByName(plant),
                         Userinterface.getProjectPresentation().getAutomatonByName(legal),
                         Userinterface.getProjectPresentation().getAutomatonByName(name));
        
        MainWindow.getProjectExplorer().updateProject();
    }
    
    
    private void prefixClosure(){
        String selectedNames[] = MainWindow.getProjectExplorer().getSelectedAutomaton();
        String newName;
        for(int i = 0; i < selectedNames.length; i++){
            if(selectedNames[i] != null){

                if(MainWindow.getGraphingPlatform().getOpenAutomatonName().equals(selectedNames[i])){
                    MainWindow.getGraphingPlatform().save();
                }

                newName = MainWindow.getProjectExplorer().getTitle(ResourceManager.getString(ResourceManager.OPERATIONS_PREFIXCLOSURE) + "(" + selectedNames[i] + ")");
                Automaton automaton = Userinterface.getProjectPresentation().copyAutomaton(selectedNames[i], newName);

                projectPresentation.Unary.prefixClosure(automaton);
            }

        }
        MainWindow.getProjectExplorer().updateProject();
    }
}
