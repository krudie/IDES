package presentation.template;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToolBar;

import main.Hub;
import model.DESModel;
import pluggable.ui.Toolset;
import pluggable.ui.UIDescriptor;
import pluggable.ui.UnsupportedModelException;
import presentation.Presentation;

public class TemplateToolset implements Toolset
{

	protected TemplateLibrary library = null;

	protected class TemplateUIDescriptor implements UIDescriptor
	{
		protected TemplateGraph shell;

		protected Presentation[] views;

		protected Presentation[] right;

		public TemplateUIDescriptor(TemplateGraph ls)
		{
			if (library == null)
			{
				library = new TemplateLibrary();
			}
			shell = ls;
			views = new Presentation[2];
			views[0] = new DesignDrawingView(shell);
			((DesignDrawingView)views[0]).setName(Hub.string("design"));
			views[1] = new CodeChooser(shell);
			((CodeChooser)views[1]).setName(Hub.string("plcSettings"));
			right = new Presentation[1];
			right[0] = library;
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

		public boolean supportsZoom()
		{
			return false;
		}

		public boolean supportsLaTeX()
		{
			return false;
		}

	}

	public UIDescriptor getUIElements(DESModel model)
	{
		if (!(model instanceof TemplateGraph))
		{
			throw new UnsupportedModelException();
		}
		return new TemplateUIDescriptor((TemplateGraph)model);
	}

	public Presentation getModelThumbnail(DESModel model, int width, int height)
	{
		if (!(model instanceof TemplateGraph))
		{
			throw new UnsupportedModelException();
		}
		return new DesignView((TemplateGraph)model);
	}

	public UIDescriptor getUI(DESModel model)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
