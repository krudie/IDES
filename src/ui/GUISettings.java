package ui;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;

/**
 * A singleton GUISettings class that reads, saves and makes accessible everything the GUI needs to know.
 * 
 * STOP Don't waste time on this if reimplementing using XML settings loading.
 * TODO make a list of all GUI settings required by this application and write load and save ops.
 */
public class GUISettings {

	private Font font;
	private FontMetrics fontMetrics;
	private BasicStroke wideStroke = new BasicStroke(2);
	private BasicStroke fineStroke = new BasicStroke(1);
	private BasicStroke dashedStroke = new BasicStroke(
											            1, 
											            BasicStroke.CAP_BUTT,
											            BasicStroke.JOIN_MITER,
											            50,
											            new float[] {5, 2}, 
											            0
											          );
	
	// ??? Is is bad to be hogging static space in this way?
	private static GUISettings instance = null;
	
	public static GUISettings instance(){
		if(instance == null){
			instance = new GUISettings();
			// TODO load settings from file
			// if file is missing, use defaults.
			
		}
		return instance;
	}

	public BasicStroke getDashedStroke() {
		return dashedStroke;
	}

	public void setDashedStroke(BasicStroke dashedStroke) {
		this.dashedStroke = dashedStroke;
	}

	public BasicStroke getFineStroke() {
		return fineStroke;
	}

	public void setFineStroke(BasicStroke fineStroke) {
		this.fineStroke = fineStroke;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public FontMetrics getFontMetrics() {
		return fontMetrics;
	}

	public void setFontMetrics(FontMetrics fontMetrics) {
		this.fontMetrics = fontMetrics;
	}

	public BasicStroke getWideStroke() {
		return wideStroke;
	}

	public void setWideStroke(BasicStroke wideStroke) {
		this.wideStroke = wideStroke;
	}
	
}
