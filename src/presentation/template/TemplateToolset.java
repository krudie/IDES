package presentation.template;

import javax.swing.JComponent;

import model.DESModel;
import model.template.TemplateModel;

import pluggable.ui.Toolset;
import pluggable.ui.UnsupportedModelException;
import presentation.ModelWrap;
import presentation.Presentation;
import presentation.fsa.FSAGraph;

public class TemplateToolset implements Toolset {

	public JComponent[] getEditPanes(ModelWrap mw) {
		if(!(mw instanceof TemplateGraph))
			throw new UnsupportedModelException();
		return new JComponent[]{new DesignView()};
	}

	public String[] getEditPanesCaptions(ModelWrap mw) {
		if(!(mw instanceof TemplateGraph))
			throw new UnsupportedModelException();
		return new String[]{"Template design"};
	}

	public Presentation getModelThumbnail(ModelWrap mw, int width, int height) {
		if(!(mw instanceof TemplateGraph))
			throw new UnsupportedModelException();
		return new DesignView();
	}

	public ModelWrap wrapModel(DESModel model)
	{
		if(!(model instanceof TemplateModel))
			throw new UnsupportedModelException();
		return null;
	}

}
