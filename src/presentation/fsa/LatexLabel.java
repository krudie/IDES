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
import java.io.IOException;

import presentation.GraphicalLayout;
import presentation.PresentationElement;
import util.LatexRenderException;
import util.Renderer;

public class LatexLabel extends GraphLabel {
	
	//protected static Renderer latexRenderer=Renderer.getRenderer(new java.io.File("c:/texmf/miktex/bin"),new java.io.File("C:/Program Files/gs/gs8.53/bin/gswin32c.exe"));
	protected static Renderer latexRenderer=Renderer.getRenderer(new java.io.File("/usr/bin"),new java.io.File("/usr/bin/gs"));
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
		if(visible){
			if(rendered==null)
				try
				{
					BufferedImage image=latexRenderer.renderString(layout.getText());
					ColorConvertOp conv=new ColorConvertOp(image.getColorModel().getColorSpace(),ColorModel.getRGBdefault().getColorSpace(),null);
					rendered=conv.createCompatibleDestImage(image,ColorModel.getRGBdefault());
					conv.filter(image,rendered);
					//rendered=new BufferedImage(ColorModel.getRGBdefault(),
						//	rendered.getRaster(),false,null);
				} catch(LatexRenderException e)
				{
					throw new RuntimeException(e);
				}
				catch(IOException e)
				{
					throw new RuntimeException(e);
				}
			WritableRaster raster=rendered.getAlphaRaster();
			Color bg=((Graphics2D)g).getBackground();
			int bgShade=(bg.getRed()+bg.getGreen()+bg.getBlue())/3;
			for(int i=0;i<raster.getWidth();++i)
				for(int j=0;j<raster.getHeight();++j)
				{
					if(rendered.getRaster().getSample(i,j,0)>bgShade)
						raster.setSample(i,j,0,0);
					else
						raster.setSample(i,j,0,255);
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
