package presentation.template.tools;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import presentation.template.DesignDrawingView;

public class DrawingTool
{

	protected DesignDrawingView context;

	protected Cursor cursor;

	public void setContext(DesignDrawingView ddv)
	{
		context = ddv;
	}

	public Cursor getCursor()
	{
		return cursor;
	}

	public void handleRightClick(MouseEvent m)
	{
		if (context != null)
		{
			context.requestFocus();
		}
	}

	public void handleMouseClicked(MouseEvent m)
	{
		if (context != null)
		{
			context.requestFocus();
		}
	}

	public void handleMouseDragged(MouseEvent m)
	{
		if (context != null)
		{
			context.requestFocus();
		}
	}

	public void handleMouseMoved(MouseEvent m)
	{
	}

	public void handleMousePressed(MouseEvent m)
	{
		if (context != null)
		{
			context.requestFocus();
		}
	}

	public void handleMouseReleased(MouseEvent m)
	{
		if (context != null)
		{
			context.requestFocus();
		}
	}

	public void handleKeyTyped(KeyEvent ke)
	{
	}

	public void handleKeyPressed(KeyEvent ke)
	{
	}

	public void handleKeyReleased(KeyEvent ke)
	{
	}

}
