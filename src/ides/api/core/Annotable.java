package ides.api.core;

import java.io.File;

/**
 * Interface for objects which can be dynamically annotated by other parts of
 * the code. The intended use is mainly to allow plugins to annotate models and
 * their elemets.
 * 
 * @author Lenko Grigorov
 */
public interface Annotable
{

	/**
	 * Annotation key for the file (of models). Annotation is a {@link File}.
	 */
	public static final String FILE = "file";

	/**
	 * Annotation key for the ids of the elements of which an element is
	 * composed. Annotation is implementation-dependent array (usually
	 * <code>long[]</code> or {@link String}[]).
	 */
	public static final String COMPOSED_OF = "composedOf";

	/**
	 * Annotation key for the names (labels) of the elements of which an element
	 * is composed. Annotation is {@link String}[].
	 */
	public static final String COMPOSED_OF_NAMES = "composedOfLabels";

	/**
	 * Annotation key for the set of events disabled at a state.
	 */
	public static final String CONTROL_MAP = "controlMap";

	/**
	 * Annotation key for the layout information of an element. Annotation is
	 * implementation-dependent.
	 */
	public static final String LAYOUT = "layout";

	/**
	 * Annotation key for the user-defined text annotation of models. Annotation
	 * is {@link String}. Read-only.
	 */
	public static final String TEXT_ANNOTATION = "textAnnotation";

	/**
	 * Returns the annotation for the given key.
	 * 
	 * @param key
	 *            key for the annotation
	 * @return if there is no annotation for the given key, returns
	 *         <code>null</code>, otherwise returns the annotation for the key
	 */
	public Object getAnnotation(String key);

	/**
	 * Sets an annotation for a given key. If there is already an annotation for
	 * the key, it is replaced.
	 * 
	 * @param key
	 *            the key for the annotation
	 * @param annotation
	 *            the annotation
	 */
	public void setAnnotation(String key, Object annotation);

	/**
	 * Removes the annotation for the given key.
	 * 
	 * @param key
	 *            key for the annotation
	 */
	public void removeAnnotation(String key);

	/**
	 * Returns <code>true</code> if there is an annotation for the given key.
	 * Otherwise returns <code>false</code>.
	 * 
	 * @param key
	 *            key for the annotation
	 * @return <code>true</code> if there is an annotation for the given key,
	 *         <code>false</code> otherwise
	 */
	public boolean hasAnnotation(String key);
}
