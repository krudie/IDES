package presentation.template;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import main.Hub;
import model.DESModel;
import model.template.TemplateModel;

import pluggable.ui.Toolset;
import pluggable.ui.UIDescriptor;
import pluggable.ui.UnsupportedModelException;
import presentation.LayoutShell;
import presentation.Presentation;
import presentation.fsa.FSAGraph;

public class TemplateToolset implements Toolset {

	protected class TemplateUIDescriptor implements UIDescriptor
	{
		protected TemplateGraph shell;
		protected Presentation[] views;
		
		public TemplateUIDescriptor(TemplateGraph ls)
		{
			shell=ls;
			views=new Presentation[1];
			views[0]=new DesignDrawingView(shell);
			((DesignDrawingView)views[0]).setName(Hub.string("design"));
		}
		
		public Presentation[] getMainPanePresentations()
		{
			return views;
		}
		
		public Presentation[] getLeftPanePresentations()
		{
			return null;
		}
		public Presentation[] getRightPanePresentations()
		{
			return null;
		}

		public JMenu[] getMenus()
		{
			return null;
		}

		public JToolBar getToolbar()
		{
			return null;
		}

		public JComponent getStatusBar()
		{
			return null;
		}

		public JMenu getPopupMenu()
		{
			return null;
		}

	}

	public UIDescriptor getUIElements(LayoutShell mw)
	{
		if(!(mw instanceof TemplateGraph))
			throw new UnsupportedModelException();
		return new TemplateUIDescriptor((TemplateGraph)mw);
	}
	
	public Presentation getModelThumbnail(LayoutShell mw, int width, int height) {
		if(!(mw instanceof TemplateGraph))
			throw new UnsupportedModelException();
		return new DesignView((TemplateGraph)mw);
	}

	public LayoutShell wrapModel(DESModel model)
	{
		if(!(model instanceof TemplateModel))
			throw new UnsupportedModelException();
		return new TemplateGraph((TemplateModel)model);
	}

}
