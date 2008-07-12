package presentation.template;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

import main.Hub;
import presentation.GraphicalLayout;

public class BlockLayout extends GraphicalLayout
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4968264218279552949L;

	public static final float DEFAULT_HEIGHT = 20;

	public static final float DEFAULT_WIDTH = 40;

	protected static Font font = new Font("times", Font.PLAIN, 12);

	protected static int labelPadding = 5;

	protected float textOffX = 0;

	protected float textOffY = 0;

	protected float width;

	protected float height;

	public BlockLayout()
	{
		super();
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
	}

	/**
	 * Creates a graphical layout instance with the given string as label text.
	 * 
	 * @param text
	 *            the text to display on the label
	 */
	public BlockLayout(String text)
	{
		super(text);
		recomputeBounds();
	}

	/**
	 * Creates a graphical layout instance at the given location with empty
	 * string as text.
	 * 
	 * @param location
	 */
	public BlockLayout(Point2D.Float location)
	{
		super(location);
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
	}

	/**
	 * Creates a graphical layout instance at the given location and the given
	 * string as label text.
	 * 
	 * @param location
	 * @param text
	 */
	public BlockLayout(Point2D.Float location, String text)
	{
		super(location, text);
		recomputeBounds();
	}

	/**
	 * Creates a graphical layout instance at the given location, with the given
	 * label text, colour and highlight colour.
	 * 
	 * @param location
	 * @param text
	 * @param color
	 * @param highlightColor
	 */
	public BlockLayout(Point2D.Float location, String text, Color color,
			Color highlightColor)
	{
		super(location, text, color, highlightColor);
		recomputeBounds();
	}

	public float getWidth()
	{
		return width;
	}

	public void setWidth(float width)
	{
		this.width = width;
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
	}

	public Rectangle getBounds()
	{
		return new Rectangle(
				(int)(getLocation().x - width / 2),
				(int)(getLocation().y - height / 2),
				(int)width,
				(int)height);
	}

	@Override
	public void setText(String s)
	{
		super.setText(s);
		recomputeBounds();
	}

	public Font getFont()
	{
		return font;
	}

	public Point2D getLabelLocation()
	{
		return new Point2D.Float(getLocation().x + textOffX, getLocation().y
				+ textOffY);
	}

	protected void recomputeBounds()
	{
		Frame mainWindow = Hub.getMainWindow();
		Graphics mainGraphics = mainWindow.getGraphics();
		FontMetrics mainMetrics = mainGraphics.getFontMetrics(font);
		textOffX = -mainMetrics.stringWidth(getText()) / 2f;
		textOffY = mainMetrics.getHeight() / 2f;
		width = (-textOffX + labelPadding) * 2;
		height = (textOffY + labelPadding) * 2;
	}
}
