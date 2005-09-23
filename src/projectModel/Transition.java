package projectModel;

/**
 * 
 * @author Axel Gottlieb Michelsen
 *
 */
public class Transition extends AutomatonElement{
	private State sourceS, targetS;
	private Event e;
	
	public Transition(State sourceS, State targetS){
		this.sourceS = sourceS;
		this.targetS = targetS;
	}

	public Transition(State sourceS, State targetS, Event e){
		this.sourceS = sourceS;
		this.targetS = targetS;
		this.e = e;
	}

	public void setSource(State s){
		this.sourceS = s;
	}
	public State getSource(){
		return this.sourceS;
	}
	
	public void setTarget(State s){
		this.targetS = s;
	}
	public State getTarget(){
		return this.targetS;
	}
	
	public void setEvent(Event e){
		this.e = e;
	}
	public Event getEvent(){
		return e;
	}
}
