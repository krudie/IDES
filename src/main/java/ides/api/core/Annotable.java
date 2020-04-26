package ides.api.core;

/**
 * Interface for objects which can be dynamically annotated by other parts of
 * the code. The intended use is mainly to allow plugins to annotate models and
 * their elemets.
 * 
 * @author Lenko Grigorov
 */
public interface Annotable {
    /**
     * Returns the annotation for the given key.
     * 
     * @param key key for the annotation
     * @return if there is no annotation for the given key, returns
     *         <code>null</code>, otherwise returns the annotation for the key
     */
    public Object getAnnotation(String key);

    /**
     * Sets an annotation for a given key. If there is already an annotation for the
     * key, it is replaced.
     * 
     * @param key        the key for the annotation
     * @param annotation the annotation
     */
    public void setAnnotation(String key, Object annotation);

    /**
     * Removes the annotation for the given key.
     * 
     * @param key key for the annotation
     */
    public void removeAnnotation(String key);

    /**
     * Returns <code>true</code> if there is an annotation for the given key.
     * Otherwise returns <code>false</code>.
     * 
     * @param key key for the annotation
     * @return <code>true</code> if there is an annotation for the given key,
     *         <code>false</code> otherwise
     */
    public boolean hasAnnotation(String key);
}
