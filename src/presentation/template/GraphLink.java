package presentation.template;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import javax.swing.JLabel;

import main.Annotable;
import model.template.TemplateLink;

import presentation.Geometry;
import presentation.GraphicalLayout;
import presentation.PresentationElement;

public class GraphLink implements PresentationElement {

	protected LinkLayout layout;
	protected TemplateLink link;
	protected GraphBlock leftBlock;
	protected GraphBlock rightBlock;
	protected boolean selected=false;
	protected boolean highlight=false;
	protected boolean visible=true;
	
	public GraphLink(TemplateLink link, GraphBlock leftBlock,
			GraphBlock rightBlock)
	{
		this.link=link;
		this.leftBlock=leftBlock;
		this.rightBlock=rightBlock;
	}
	
	public TemplateLink getLink()
	{
		return link;
	}
	
	public Rectangle bounds() {
		return getLayout().getBounds();
	}

	public void draw(Graphics g) {
    	if(isSelected())
    		g.setColor(TemplateGraph.COLOR_SELECT);
    	else if(isHighlighted())
    		g.setColor(TemplateGraph.COLOR_HILIGHT);
    	else
    		g.setColor(TemplateGraph.COLOR_NORM);
		g.drawLine((int)leftBlock.getLocation().x,
				(int)leftBlock.getLocation().y,
				(int)rightBlock.getLocation().x,
				(int)rightBlock.getLocation().y);
		String text=""+link.getEventLeft().getSymbol()+" = "+link.getEventRight().getSymbol();
		g.drawString(text,(int)getLocation().x,(int)getLocation().y);
	}

	public Long getId() {
		return (long)hashCode();
	}

	public LinkLayout getLayout() {
		LinkLayout layout=(LinkLayout)link.getAnnotation(Annotable.LAYOUT);
		if(layout==null)
		{
			layout=new LinkLayout();
		}
		return layout;
	}

	public Float getLocation() {
		//TODO obviously this needs to be reformatted.
		return new Point2D.Float(Math.min(leftBlock.getLocation().x,
				rightBlock.getLocation().x)+Math.abs(leftBlock.getLocation().x-
						rightBlock.getLocation().x)/2,
						(Math.abs(leftBlock.getLocation().x-
						rightBlock.getLocation().x)/2)*Geometry.slope(
								leftBlock.getLocation(), rightBlock.getLocation())+
								(Math.min(leftBlock.getLocation().x,
										rightBlock.getLocation().x)==leftBlock.getLocation().x?
										leftBlock.getLocation().y:
										rightBlock.getLocation().y));
	}

	public boolean intersects(Point2D p) {
		return false;
	}

	public boolean isHighlighted() {
		return highlight;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean needsRefresh() {
		return false;
	}

	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void setHighlighted(boolean b) {
		highlight=b;
	}

	public void setLayout(GraphicalLayout layout) {
		if(layout instanceof LinkLayout)
			link.setAnnotation(Annotable.LAYOUT, layout);
	}

	public void setLocation(Float p) {
		LinkLayout layout=getLayout();
		layout.setLocation(p.x, p.y);
		setLayout(layout);
	}

	public void setNeedsRefresh(boolean b) {

	}

	public void setSelected(boolean b) {
		selected=b;
	}

	public void setVisible(boolean b) {
		visible=b;
	}

	public void translate(float x, float y) {
		LinkLayout layout=getLayout();
		layout.translate(x, y);
		setLayout(layout);
	}

}
