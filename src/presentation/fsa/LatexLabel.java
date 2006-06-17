package presentation.fsa;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Hub;

import presentation.GraphicalLayout;
import presentation.PresentationElement;
import services.cache.Cache;
import services.cache.NotInCacheException;
import services.latex.LatexManager;
import services.latex.LatexRenderException;
import services.latex.Renderer;

public class LatexLabel extends GraphLabel {
	
	protected BufferedImage rendered=null;
	
	public LatexLabel(String text){
		super(text);
	}
	
	/**
	 * @param text string to display in this label
	 * @param location the x,y coordinates of the top left corner of this label 
	 */
	public LatexLabel(String text, Point2D location){
		super(text,location);
	}
	
	/**
	 * TODO decide whether the DrawingBoard is a special kind of Glyph.
	 * 
	 * @param text string to display in this label
	 * @param parent glyph in which this label is displayed
	 * @param location the x,y coordinates of the top left corner of this label
	 */
	public LatexLabel(String text, PresentationElement parent, Point2D location) {
		super(text,parent,location);
	}

	public void draw(Graphics g) {
		if(visible&&!layout.getText().equals("")){
			if(rendered==null)
			{
				BufferedImage image=null;
				try
				{
					byte[] data=(byte[])Cache.get(layout.getText());
					image=ImageIO.read(new ByteArrayInputStream(data));
				}catch(NotInCacheException e)
				{
					try
					{
						image=LatexManager.getRenderer().renderString(layout.getText());
						ByteArrayOutputStream pngStream=new ByteArrayOutputStream();
						ImageIO.write(image,"png",pngStream);
						pngStream.close();
						Cache.put(layout.getText(),pngStream.toByteArray());
					//rendered=new BufferedImage(ColorModel.getRGBdefault(),
						//	rendered.getRaster(),false,null);
					} catch(LatexRenderException ex)
					{
						throw new RuntimeException(ex);
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
				ColorConvertOp conv=new ColorConvertOp(image.getColorModel().getColorSpace(),ColorModel.getRGBdefault().getColorSpace(),null);
				rendered=conv.createCompatibleDestImage(image,ColorModel.getRGBdefault());
				conv.filter(image,rendered);
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
			((Graphics2D)g).drawImage(rendered,null,(int)layout.getLocation().x,(int)layout.getLocation().y);
//			g.setColor(layout.getColor());		
//			g.setFont(font);
//			
//			// FIXME this computes the position for a Node label but won't work for an edge; see bounds()
//			FontMetrics metrics = g.getFontMetrics();
//			int width = metrics.stringWidth( layout.getText() );
//			int height = metrics.getHeight();
//			int x = (int)layout.getLocation().x - width/2;
//			int y = (int)layout.getLocation().y; // + height/2;
//			
//			g.drawString(layout.getText(), x, y);
		}
	}
}
