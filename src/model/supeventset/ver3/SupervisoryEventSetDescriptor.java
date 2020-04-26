package model.supeventset.ver3;

import java.awt.Image;
import java.awt.Toolkit;

import ides.api.core.Hub;
import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.DESModelType;

/**
 * DESModelType descriptor for SupervisoryEventSet (which is implemented in
 * EventSet)
 * 
 * @author Valerie Sugarman
 *
 */
public class SupervisoryEventSetDescriptor implements DESModelType {
    public Class<?>[] getModelPerspectives() {
        return new Class[] { SupervisoryEventSet.class };
    }

    public Class<?> getMainPerspective() {
        return SupervisoryEventSet.class;
    }

    public String getIOTypeDescription() {
        return "SupEventSet";
    }

    public String getDescription() {
        return "Event Set";
    }

    public Image getIcon() {
        return Toolkit.getDefaultToolkit().createImage(Hub.getIDESResource("images/icons/model_supeventset.gif"));
    }

    public DESModel createModel(String name) {
        SupervisoryEventSet s = new EventSet(name);
        return s;
    }
}
