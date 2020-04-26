/**
 * 
 */
package pluggable.layout.jung;

import java.util.Hashtable;

import edu.uci.ics.jung.graph.Vertex;
import ides.api.model.fsa.FSAState;

/**
 * @author Lenko Grigorov
 */
public class BridgeMapper {

    public static Hashtable<FSAState, Vertex> stateMap = new Hashtable<FSAState, Vertex>();

    public static Hashtable<Vertex, FSAState> stateMapInverse = new Hashtable<Vertex, FSAState>();
}
