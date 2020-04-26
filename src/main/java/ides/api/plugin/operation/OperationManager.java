package ides.api.plugin.operation;

import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

/**
 * The manager of DES operations available in IDES.
 * 
 * @author Lenko Grigorov
 */
public class OperationManager {
    // prevent instantiation
    private OperationManager() {
    }

    @Override
    public Object clone() {
        throw new RuntimeException("Cloning of " + this.getClass().toString() + " not supported.");
    }

    /**
     * Instance for the non-static methods.
     */
    private static OperationManager me = null;

    /**
     * Provides access to the instance of the manager.
     * 
     * @return the operations manager
     */
    public static OperationManager instance() {
        if (me == null) {
            me = new OperationManager();
        }
        return me;
    }

    /**
     * The registered operations.
     */
    protected TreeMap<String, Operation> operations = new TreeMap<String, Operation>();

    /**
     * Get the names of all registered operations.
     * 
     * @return the list of all registered operations
     */
    public List<String> getOperationNames() {
        Vector<String> list = new Vector<String>(operations.size());
        list.addAll(operations.keySet());
        return list;
    }

    /**
     * Register a DES operation with IDES.
     * 
     * @param o the operation to register
     */
    public void register(Operation o) {
        operations.put(o.getName(), o);
    }

    /**
     * Access a registered operation by name.
     * 
     * @param name the name of the operation
     * @return the operation if an operation with the given name exists;
     *         <code>null</code> otherwise
     */
    public Operation getOperation(String name) {
        return operations.get(name);
    }

    /**
     * Access a registered filter operation by name.
     * 
     * @param name the name of the operation
     * @return the operation if a filter operation with the given name exists;
     *         <code>null</code> otherwise
     * @see FilterOperation
     */
    public FilterOperation getFilterOperation(String name) {
        if (operations.get(name) instanceof FilterOperation) {
            return (FilterOperation) operations.get(name);
        } else {
            return null;
        }
    }
}
