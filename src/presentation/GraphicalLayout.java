package presentation;

import java.awt.Point;

public interface GraphicalLayout {
	
	// FIXME seems kinda silly to have accessor methods in an interface...
	public Point getLocation();	
	public String getText();	
}
