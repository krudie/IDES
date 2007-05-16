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

	protected TemplateLibrary library=null;
	
	protected class TemplateUIDescriptor implements UIDescriptor
	{
		protected TemplateGraph shell;
		protected Presentation[] views;
		protected Presentation[] right;
		
		public TemplateUIDescriptor(TemplateGraph ls)
		{
			if(library==null)
			{
				library=new TemplateLibrary();
			}		
			shell=ls;
			views=new Presentation[2];
			views[0]=new DesignDrawingView(shell);
			((DesignDrawingView)views[0]).setName(Hub.string("design"));
			views[1]=new CodeChooser(shell);
			((CodeChooser)views[1]).setName(Hub.string("plcSettings"));
			right=new Presentation[1];
			right[0]=library;
		}
		
		public Presentation[] getMainPanePresentations()
		{
			return views;
		}
		
		public Presentation[] getLeftPanePresentations()
		{
			return new Presentation[0];
		}
		public Presentation[] getRightPanePresentations()
		{
			return right;
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
		if(library==null)
		{
			library=new TemplateLibrary();
		}		
		return new TemplateGraph((TemplateModel)model,library);
	}

}
