/**
 * 
 */
package io.fsa.ver2_1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Hub;
import model.fsa.FSAModel;
import pluggable.io.FormatTranslationException;
import pluggable.io.IOCoordinator;
import pluggable.io.IOPluginManager;
import pluggable.io.ImportExportPlugin;
import pluggable.ui.ToolsetManager;
import presentation.GraphicalLayout;
import presentation.fsa.FSAGraph;

/**
 * @author christiansilvano
 */
public class JPEGPlugin implements ImportExportPlugin
{

	public static final Stroke WIDE_STROKE = new BasicStroke(
			2,
			BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER);

	protected String description = "JPEG image";

	protected String ext = "jpg";

	protected final static int BORDER_SIZE = 10;

	/**
	 * Registers itself to the IOPluginManager
	 */
	public void initializeImportExport()
	{
		IOPluginManager.getInstance().registerExport(this, FSAModel.class);
	}

	/**
	 * Unregisters itself from the IOPluginManager
	 */
	public void unload()
	{

	}

	/**
	 * Exports a file to a different format
	 * 
	 * @param src -
	 *            the source file
	 * @param dst -
	 *            the destination
	 */
	public void exportFile(File src, File dst)
			throws FormatTranslationException
	{
		// Loading the model from the file:
		FSAModel a = null;
		try
		{
			a = (FSAModel)IOCoordinator.getInstance().load(src);
		}
		catch (IOException e)
		{
			throw new FormatTranslationException(e);
		}

		boolean useFrame = Hub.persistentData
				.getBoolean(GraphExporter.STR_EXPORT_PROP_USE_FRAME);

		FSAGraph graph = (FSAGraph)ToolsetManager
				.getToolset(FSAModel.class).wrapModel(a);
		Rectangle bounds = graph.getBounds(false);
		if (bounds.height == 0 || bounds.width == 0)
		{
			bounds = new Rectangle(0, 0, 1, 1);
		}
		bounds.height += BORDER_SIZE * 2;
		bounds.width += BORDER_SIZE * 2;
		BufferedImage image = new BufferedImage(
				bounds.width,
				bounds.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2D = image.createGraphics();
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setStroke(GraphicalLayout.WIDE_STROKE);
		g2D.setColor(Color.WHITE);
		g2D.fillRect(0, 0, bounds.width, bounds.height);
		if (useFrame)
		{
			g2D.setColor(Color.BLACK);
			g2D.setStroke(WIDE_STROKE);
			g2D.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
		}
		g2D.translate(-bounds.x + BORDER_SIZE, -bounds.y + BORDER_SIZE);
		// FIXME implement better avoid layout
		if (graph.isAvoidLayoutDrawing())
		{
			graph.forceLayoutDisplay();
		}
		graph.draw(g2D);
		g2D.dispose();
		try
		{
			ImageIO.write(image, "jpg", dst);
		}
		catch (IOException e)
		{
			throw new FormatTranslationException(e);
		}
	}

	/**
	 * Import a file from a different format to the IDES file system
	 * 
	 * @param importFile -
	 *            the source file
	 * @return
	 */
	public void importFile(File src, File dst)
	{
		return;
	}

	/**
	 * Return a human readable description of the plugin
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * 
	 */
	public String getFileExtension()
	{
		return ext;
	}
}