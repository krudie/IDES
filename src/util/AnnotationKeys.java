package util;

import java.io.File;

import ides.api.core.Annotable;

/**
 * Contains the {@link String}s to be used as keys for the {@link Annotable}
 * interface.
 * 
 * @author Valerie Sugarman
 */
public class AnnotationKeys {
    /**
     * Annotation key for the file (of models). Annotation is a {@link File}.
     */
    public static final String FILE = "file";

    /**
     * Annotation key for the ids of the elements of which an element is composed.
     * Annotation is implementation-dependent array (usually <code>long[]</code> or
     * {@link String}[]).
     */
    public static final String COMPOSED_OF = "composedOf";

    /**
     * Annotation key for the names (labels) of the elements of which an element is
     * composed. Annotation is {@link String}[].
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
     * Annotation key for the user-defined text annotation of models. Annotation is
     * {@link String}. Read-only.
     */
    public static final String TEXT_ANNOTATION = "textAnnotation";

}
