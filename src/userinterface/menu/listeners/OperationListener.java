/**
 * 
 */
package userinterface.menu.listeners;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

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
        String[] selectedNames = MainWindow.getProjectExplorer().getSelectedAutomaton();
        String name = ResourceManager.getString(ResourceManager.OPERATIONS_SUPC) + "(";

        if(selectedNames.length < 2) return;
        name += selectedNames[0] + "," + selectedNames[1] + ")";
        
        
        name = MainWindow.getProjectExplorer().getTitle(name);

        Automaton[] automata = new Automaton[selectedNames.length];
        for(int i=0; i<automata.length; i++){
            automata[i] = Userinterface.getProjectPresentation().getAutomatonByName(selectedNames[i]);
        }
        
        
        Userinterface.getProjectPresentation().newAutomaton(name);
        
        SuperVisory.supC(Userinterface.getProjectPresentation().getAutomatonByName(selectedNames[0]),
                         Userinterface.getProjectPresentation().getAutomatonByName(selectedNames[1]),
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
