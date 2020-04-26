package presentation.supeventset;

import ides.api.model.supeventset.SupervisoryEventSet;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.presentation.Presentation;
import ides.api.plugin.presentation.Toolset;
import ides.api.plugin.presentation.UIDescriptor;
import ides.api.plugin.presentation.UnsupportedModelException;

/**
 * @author Valerie Sugarman
 */
public class SupEventSetToolset implements Toolset {

    public Presentation getModelThumbnail(DESModel model, int width, int height) throws UnsupportedModelException {
        if (!(model instanceof SupervisoryEventSet)) {
            throw new UnsupportedModelException();
        }

        return new SupEventSetThumbnail((SupervisoryEventSet) model, width, height);
    }

    public UIDescriptor getUIElements(DESModel model) throws UnsupportedModelException {
        if (!(model instanceof SupervisoryEventSet)) {
            throw new UnsupportedModelException();
        }

        return new SupEventSetUIDescriptor((SupervisoryEventSet) model);
    }

}
