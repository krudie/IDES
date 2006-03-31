package presentation.fsa;

import java.awt.Point;

import model.Event;
import presentation.Glyph;
import presentation.GlyphLabel;

public class EdgeLabel extends GlyphLabel {

	private Event event;

	public EdgeLabel(Event e, Glyph parent, Point location) {
		// TODO Axl & Kristian's model is unwieldy.  
		// Need a cleaner interface to talk to my presentation elements.
		// Improve interfaces in the model package and make their code conform.
		super(e.getSymbol(), parent, location);
		event = null;
	}
	
	public EdgeLabel(String text, Glyph parent, Point location) {
		super(text, parent, location);
		event = null;
	}

	public EdgeLabel(String text, Point location) {
		super(text, location);		
		event = null;		
	}

}