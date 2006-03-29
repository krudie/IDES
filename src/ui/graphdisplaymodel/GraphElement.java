package ui.graphdisplaymodel;

/**
 * TODO Before proceeding with graph model implementation, 
 * review work by Christian and Axl.
 * 
 * This should be an interface.
 * 
 * @author helen bretzke
 *
 */
public class GraphElement {
	
	private String name;
	
	public GraphElement(){
		name = "anonymous";
	}
	
	public GraphElement(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
