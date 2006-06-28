package presentation.fsa;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import main.Hub;

import presentation.GraphicalLayout;
import presentation.PresentationElement;
import services.cache.Cache;
import services.cache.NotInCacheException;
import services.latex.LatexManager;
import services.latex.LatexRenderException;

/**
 * TODO Change so that doesn't extend label; waste of space and rounds the location to int coords.
 * 
 * @author helen
 *
 */
@SuppressWarnings("serial")
public class GraphLabel extends GraphElement {
	protected Rectangle bounds;
	protected boolean visible = true;
	protected PresentationElement parent = null;  // either the DrawingBoard, a node or an edge	
	protected Font font;
	protected BufferedImage rendered=null;
	
	public GraphLabel(String text){
		layout = new GraphicalLayout(text);
		// TODO change to a dynamic value read from a config file and stored in 
		// SystemVariables? ResourceManager?
		font = new Font("times", Font.ITALIC, 12);
		bounds = new Rectangle();
	}
	
	public GraphLabel(GraphicalLayout layout){		
		this.layout = layout;	
		bounds = new Rectangle();
	}
	
	/**
	 * @param text string to display in this label
	 * @param location the x,y coordinates of the top left corner of this label 
	 */
	public GraphLabel(String text, Point2D location){
		this(text);		
		layout.setLocation((float)location.getX(), (float)location.getY());		
	}
	
	/**
	 * TODO decide whether the DrawingBoard is a special kind of Glyph.
	 * 
	 * @param text string to display in this label
	 * @param parent glyph in which this label is displayed
	 * @param location the x,y coordinates of the top left corner of this label
	 */
	public GraphLabel(String text, PresentationElement parent, Point2D location) {	
		this(text, location);		
		this.parent = parent;
	}
	
	public void draw(Graphics g) {
		
		if(LatexManager.isLatexEnabled())
		{
			if(!visible||"".equals(layout.getText()))
				return;
			try
			{
				renderIfNeeded();
			}catch(LatexRenderException e)
			{
				LatexManager.handleRenderingProblem();
				return;
			}
			((Graphics2D)g).drawImage(rendered,null,(int)layout.getLocation().x,(int)layout.getLocation().y);
		}
		else
		{
			g.setFont(font);
			FontMetrics metrics = g.getFontMetrics();
			int width = metrics.stringWidth( layout.getText() );
			int height = metrics.getHeight();
			bounds.setSize(width, height);
			bounds.setLocation(new Point((int)(layout.getLocation().x - width/2), 
					(int)(layout.getLocation().y - height/2)));
			
			int x = (int)layout.getLocation().x - width/2;
			int y = (int)layout.getLocation().y + metrics.getAscent()/2;
			
			if(visible){
				// g.setColor(layout.getColor());		
				g.drawString(layout.getText(), x, y);
			}
		}
	}

	public Rectangle bounds() {
		if(LatexManager.isLatexEnabled())
		{
			if(rendered!=null)
			{
				bounds.height=rendered.getHeight();
				bounds.width=rendered.getWidth();
			}
			else //arbitrary dimensions: has to be recomputed after rendering
			{
				bounds.height=10;
				bounds.width=10;
			}					
		}
		else{
		//Lenko writes: TODO update bounds on label change
		// NOTE Unless we use deprecated getFontMetrics method, we have to compute 
		// the bounds until via a graphics context object in the draw method.
		if(bounds.getWidth() == 0 || bounds.getHeight() == 0){
			Toolkit tk = Toolkit.getDefaultToolkit();
			FontMetrics metrics = tk.getFontMetrics(font);
			bounds.setSize(metrics.stringWidth(layout.getText()), metrics.getHeight() );
			bounds.setLocation(new Point((int)(layout.getLocation().x - bounds.width/2), 
										(int)(layout.getLocation().y - bounds.height/2)));			
		}
		}
		return bounds;				
	}
	
	public boolean intersects(Point2D p) {		
		return bounds().intersects(p.getX(), p.getY(), 1, 1);
	}

	public void insert(PresentationElement child, long index) {}
	public void insert(PresentationElement g) {}
	public void remove(PresentationElement child) {}
	public PresentationElement child(int index) {	return null; }
	public Iterator children() { return null; }
	
	public PresentationElement parent() {		
		return parent;
	}

	public void setText(String s){
		layout.setText(s);		
	}

	public boolean isVisible() {		
		return visible;
	}

	public void setVisible(boolean b) {
		visible = b;
	}

	/**
	 * Renders the label using LaTeX.
	 * @throws LatexRenderException if rendering fails
	 * @see #rendered
	 * @see #renderIfNeeded()
	 */
	public void render() throws LatexRenderException
	{
		dirty=true;
		if(layout.getText()==null||"".equals(layout.getText()))
		{
			rendered=LatexManager.getRenderer().getEmptyImage();
			return;
		}
		try
		{
			byte[] data=(byte[])Cache.get(getClass().getName()+layout.getText());
			rendered=ImageIO.read(new ByteArrayInputStream(data));
		}catch (NotInCacheException e)
		{
			try
			{
				BufferedImage image=LatexManager.getRenderer().renderString(layout.getText());
				ColorConvertOp conv=new ColorConvertOp(image.getColorModel().getColorSpace(),ColorModel.getRGBdefault().getColorSpace(),null);
				rendered=conv.createCompatibleDestImage(image,ColorModel.getRGBdefault());
				conv.filter(image,rendered);
				ByteArrayOutputStream pngStream=new ByteArrayOutputStream();
				ImageIO.write(rendered,"png",pngStream);
				pngStream.close();
				Cache.put(getClass().getName()+layout.getText(),pngStream.toByteArray());
			}
			catch(IOException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		//adjust the transparency of the label
		WritableRaster raster=rendered.getAlphaRaster();
		Color bg=Hub.getMainWindow().getBackground();
		int bgShade=(bg.getRed()+bg.getGreen()+bg.getBlue())/3;
		for(int i=0;i<raster.getWidth();++i)
			for(int j=0;j<raster.getHeight();++j)
			{
				if(rendered.getRaster().getSample(i,j,0)>bgShade)
					raster.setSample(i,j,0,0);
				else
					raster.setSample(i,j,0,255);
			}
	}
	
	/**
	 * If the label has not been rendered yet, it gets rendered using LaTeX
	 * @throws LatexRenderException if rendering fails
	 * @see #rendered
	 * @see #render()
	 */
	public void renderIfNeeded() throws LatexRenderException
	{
		if(rendered==null)
			render();
	}
}