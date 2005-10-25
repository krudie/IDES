/**
 * 
 */
package projectPresentation;

import projectModel.Automaton;

/**
 * @author edlund
 *
 */
public class Trim{
    public static void trim(Automaton automaton){
        Accessible.accesible(automaton);
        CoAccessible.coAccesible(automaton);
    }
}
