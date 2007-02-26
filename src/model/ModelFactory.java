package model;

import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;

/**
 * A factory for constructing models. This isolates
 * the rest of the program from the specific implementations.
 * 
 * @author Lenko Grigorov
 */
public class ModelFactory {

	/**
	 * Constructs an FSA model with an empty name.
	 * @return a new FSA model
	 */
	public static FSAModel getFSA()
	{
		return new Automaton("");
	}

	/**
	 * Constructs an FSA model with a given name.
	 * @param name the name for the new FSA model
	 * @return the new FSA model
	 */
	public static FSAModel getFSA(String name)
	{
		return new Automaton(name);
	}

}
