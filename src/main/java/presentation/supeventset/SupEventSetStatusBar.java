package presentation.supeventset;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ides.api.model.supeventset.SupEventSetMessage;
import ides.api.model.supeventset.SupEventSetSubscriber;
import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.model.DESModelMessage;
import ides.api.plugin.model.DESModelSubscriber;
import ides.api.plugin.presentation.Presentation;

/**
 * @author Valerie Sugarman
 */
public class SupEventSetStatusBar extends JLabel implements Presentation, SupEventSetSubscriber, DESModelSubscriber {

    /**
     * 
     */
    private static final long serialVersionUID = 6997383625518223767L;

    protected SupervisoryEventSet model;

    private boolean trackModel;

    public SupEventSetStatusBar(SupervisoryEventSet model) {
        this.model = model;
        setTrackModel(true);
        refresh();
    }

    public void refresh() {
        setText(model.getName() + ":  " + model.size() + " events");
    }

    public void forceRepaint() {
        refresh();
        repaint();
    }

    public JComponent getGUI() {
        return this;
    }

    public DESModel getModel() {
        return model;
    }

    public void release() {
        setTrackModel(false);

    }

    public void setTrackModel(boolean b) {
        if (trackModel != b) {
            trackModel = b;
            if (trackModel) {
                model.addSubscriber((SupEventSetSubscriber) this);
                model.addSubscriber((DESModelSubscriber) this);
            } else {
                model.removeSubscriber((SupEventSetSubscriber) this);
                model.removeSubscriber((DESModelSubscriber) this);
            }
        }

    }

    public void modelNameChanged(DESModelMessage message) {
        refresh();
    }

    public void saveStatusChanged(DESModelMessage message) {

    }

    public void supEventSetChanged(SupEventSetMessage message) {
        refresh();
    }

}
