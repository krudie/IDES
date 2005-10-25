/**
 * 
 */
package userinterface.menu.listeners;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import projectModel.Automaton;

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
                projectPresentation.Trim.trim(automaton);
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

                projectPresentation.CoAccessible.coAccesible(automaton);
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
                projectPresentation.Accessible.accesible(automaton);
            }

        }
        MainWindow.getProjectExplorer().updateProject();
    }

    public void product(){
        String[] selectedNames = MainWindow.getProjectExplorer().getSelectedAutomaton();
        String name = ResourceManager.getString(ResourceManager.OPERATIONS_PRODUCT) + "(";

        if(selectedNames.length < 2) return;
        name += selectedNames[0]+", "+selectedNames[1];
        
        
        /*for(int i = 1; i < selectedNames.length; i++){
            if(MainWindow.getGraphingPlatform().getOpenAutomatonName().equals(selectedNames[i])){
                MainWindow.getGraphingPlatform().save();
            }
            name += ", " + selectedNames[i];
        }*/
        name += ")";
        
        name = MainWindow.getProjectExplorer().getTitle(name);

        Userinterface.getProjectPresentation().newAutomaton(name);
        Userinterface.getProjectPresentation().product(Userinterface.getProjectPresentation().getAutomatonByName(selectedNames[0]),
                Userinterface.getProjectPresentation().getAutomatonByName(selectedNames[1]), Userinterface.getProjectPresentation().getAutomatonByName(name));

        MainWindow.getProjectExplorer().updateProject();
        return;
        /*    
         for(int i = 2; i < selectedNames.length; i++){
         Userinterface.getProjectPresentation().product(Userinterface.getProjectPresentation().getAutomatonByName(name),
         Userinterface.getProjectPresentation().getAutomatonByName(selectedNames[i]), Userinterface.getProjectPresentation().getAutomatonByName(name));
         }
         MainWindow.getProjectExplorer().updateProject();
        */
    }
}
