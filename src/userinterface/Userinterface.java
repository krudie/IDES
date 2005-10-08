package userinterface;

import org.eclipse.swt.widgets.*;

import projectPresentation.ProjectPresentation;

public class Userinterface{

    private Display display = null;

    private static ProjectPresentation pj;

    public Userinterface(ProjectPresentation projectPresentation){

        pj = projectPresentation;

        Splash splash = null;
        try{
            display = Display.getDefault();
            splash = new Splash(display);
            new MainWindow(splash);
            display.dispose();
        }
        catch(Exception e){
            if(splash != null) splash.dispose();
            if(display != null) display.dispose();

            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static ProjectPresentation getProjectPresentation(){
        return pj;
    }

}
