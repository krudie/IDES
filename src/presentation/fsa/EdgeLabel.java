package presentation.fsa;

import model.Event;
import presentation.Glyph;
import presentation.Label;

public class EdgeLabel extends Label {

	private Event event;

	public EdgeLabel(Event e, Glyph parent) {
		// TODO Axl & Kristian's model is unwieldy.  
		// Need a cleaner interface to talk to my presentation elements.
		// Improve interfaces in the model package and make their code conform.
		super(e.getSymbol(), parent);
		event = null;
	}
	
	public EdgeLabel(String text, Glyph parent) {
		super(text, parent);
		event = null;
	}

	public EdgeLabel(String text) {
		super(text);
		event = null;		
	}

}
