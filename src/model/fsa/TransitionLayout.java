package model.fsa;

import java.awt.Point;
import java.awt.geom.Point2D;
import presentation.GraphicalLayout;

public class TransitionLayout implements GraphicalLayout {

	private Point2D[] bezierControls;
	
	public TransitionLayout(){
		bezierControls = new Point2D.Float[4];
	}
	
	public TransitionLayout(Point2D[] curve){
		bezierControls = curve;
	}
	
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	public Point getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public Point2D[] getCurve() {
		return bezierControls;
	}

	public void setCurve(Point2D[] bezierControls) {
		this.bezierControls = bezierControls;
	}
	
	public void setCurve(Point2D p1, Point2D c1, Point2D c2, Point2D p2){
		bezierControls[0] = p1;
		bezierControls[1] = c1;
		bezierControls[2] = c2;
		bezierControls[3] = p2;
	}

}
