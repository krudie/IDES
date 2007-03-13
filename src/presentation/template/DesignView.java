package presentation.template;

import java.awt.Dimension;

import javax.swing.JComponent;

import presentation.ModelWrap;
import presentation.Presentation;

public class DesignView extends JComponent implements Presentation {

	public Dimension getPreferredSize()	{
		return new Dimension(10,10);
	}
	public JComponent getGUI()
	{
		return this;
	}
	public ModelWrap getModelWrap()
	{
		return null;
	}
	public void setTrackModel(boolean b)
	{
		
	}
}
