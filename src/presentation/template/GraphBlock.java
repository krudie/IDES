package presentation.template;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import main.Annotable;
import model.template.TemplateBlock;
import model.template.TemplateModule;
import presentation.GraphicalLayout;
import presentation.PresentationElement;

public class GraphBlock implements PresentationElement
{

	protected TemplateBlock block;

	protected boolean selected = false;

	protected boolean highlight = false;

	protected boolean visible = true;

	public GraphBlock(TemplateBlock b)
	{
		block = b;
	}

	public TemplateBlock getBlock()
	{
		return block;
	}

	public BlockLayout getLayout()
	{
		BlockLayout layout = (BlockLayout)block.getAnnotation(Annotable.LAYOUT);
		if (layout == null)
		{
			layout = new BlockLayout();
		}
		return layout;
	}

	public void setLayout(GraphicalLayout layout)
	{
		if (layout instanceof BlockLayout)
		{
			block.setAnnotation(Annotable.LAYOUT, layout);
		}
	}

	public Rectangle bounds()
	{
		return getLayout().getBounds();
	}

	public boolean intersects(Point2D p)
	{
		return getLayout().getBounds().contains(p);
	}

	public void draw(Graphics g)
	{
		BlockLayout l = getLayout();
		g.setColor(Color.WHITE);
		if (block instanceof TemplateModule)
		{
			g.fillRect((int)(l.getLocation().x - l.getWidth() / 2),
					(int)(l.getLocation().y - l.getHeight() / 2),
					(int)l.getWidth(),
					(int)l.getHeight());
		}
		else
		{
			int max = Math.max((int)l.height, (int)l.width);
			g.fillOval((int)(l.getLocation().x - max / 2), (int)(l
					.getLocation().y - max / 2), max, max);
		}
		if (isSelected())
		{
			g.setColor(TemplateGraph.COLOR_SELECT);
		}
		else if (isHighlighted())
		{
			g.setColor(TemplateGraph.COLOR_HILIGHT);
		}
		else
		{
			g.setColor(TemplateGraph.COLOR_NORM);
		}
		if (block instanceof TemplateModule)
		{
			g.drawRect((int)(l.getLocation().x - l.getWidth() / 2),
					(int)(l.getLocation().y - l.getHeight() / 2),
					(int)l.getWidth(),
					(int)l.getHeight());
		}
		else
		{
			int max = Math.max((int)l.height, (int)l.width);
			g.drawOval((int)(l.getLocation().x - max / 2), (int)(l
					.getLocation().y - max / 2), max, max);
		}
		if (!"".equals(l.getText()))
		{
			g.setFont(l.getFont());
			Point2D labelLocation = l.getLabelLocation();
			g.drawString(l.getText(),
					(int)labelLocation.getX(),
					(int)labelLocation.getY());
		}
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean b)
	{
		selected = b;
	}

	public boolean isHighlighted()
	{
		return highlight;
	}

	public void setHighlighted(boolean b)
	{
		highlight = b;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean b)
	{
		visible = b;
	}

	public void translate(float x, float y)
	{
		BlockLayout layout = getLayout();
		layout.translate(x, y);
		setLayout(layout);
	}

	public void setLocation(Point2D.Float p)
	{
		BlockLayout layout = getLayout();
		layout.setLocation(p.x, p.y);
		setLayout(layout);
	}

	public Point2D.Float getLocation()
	{
		return getLayout().getLocation();
	}

	public void setNeedsRefresh(boolean b)
	{

	}

	public boolean needsRefresh()
	{
		return false;
	}

	public void refresh()
	{

	}

	public Long getId()
	{
		return (long)hashCode();
	}

	public String getName()
	{
		return getLayout().getText();
	}
}
