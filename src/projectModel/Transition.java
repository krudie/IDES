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

	public void setSourceState(State s){
		this.sourceS = s;
	}
	public State getSourceState(){
		return this.sourceS;
	}
	
	public void setTargetState(State s){
		this.targetS = s;
	}
	public State getTargetState(State s){
		return this.targetS;
	}
	
	public void setEvent(Event e){
		this.e = e;
	}
	public Event getEvent(){
		return e;
	}
}
