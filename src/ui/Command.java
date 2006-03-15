package ui;

public interface Command {
	
	public void undo();
	public void redo();
	
	// need a context for the action i.e. the drawing canvas
		
}
