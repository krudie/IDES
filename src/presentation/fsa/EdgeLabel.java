package presentation.fsa;

import java.awt.Point;

import model.fsa.FSAEvent;
import presentation.Glyph;

public class EdgeLabel extends GraphLabel {

	private FSAEvent event;

	public EdgeLabel(FSAEvent e, Glyph parent, Point location) {
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
