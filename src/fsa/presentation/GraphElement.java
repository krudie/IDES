package fsa.presentation;

/**
 * TODO This should be an interface and follow the Composite pattern to 
 * support groups of graph elements.
 * 
 * @author helen bretzke
 *
 */
public class GraphElement {
	
	private String name;
	private int id;
	
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
