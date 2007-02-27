package model;

import java.awt.Image;


public interface ModelDescriptor {
	public Class[] getModelInterfaces();
	public Class getPreferredModelInterface();
	public String getTypeDescription();
	public Image getIcon();
	public DESModel createModel(String id);
}
