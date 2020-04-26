package util;

import java.util.Vector;

import javax.swing.AbstractButton;

/**
 * A class that binds UI elements ({@link AbstractButton}s) to a Boolean value.
 * Whenever the value changes, all bound elements will be updated to reflect the
 * new value.
 * 
 * @author Lenko Grigorov
 */
public class BooleanUIBinder {

    protected Vector<AbstractButton> uiElements = new Vector<AbstractButton>();

    protected boolean value = false;

    /**
     * Registers an {@link AbstractButton} to be updated when the encapsulated
     * Boolean value changes.
     * 
     * @param element the UI element to be registered for automatic updates
     */
    public void bind(AbstractButton element) {
        if (!uiElements.contains(element)) {
            uiElements.add(element);
            element.setSelected(value);
        }
    }

    /**
     * Unregisters an {@link AbstractButton} so that it is no longer updated when
     * the encapsulated Boolean value changes.
     * 
     * @param element the UI element to be unregistered from automatic updates
     */
    public void unbind(AbstractButton element) {
        uiElements.remove(element);
    }

    /**
     * Sets the encapsulated Boolean value and updates all bound UI elements.
     * 
     * @param b the new value for the encapsulated Boolean variable
     */
    public void set(boolean b) {
        value = b;
        for (AbstractButton button : uiElements) {
            button.setSelected(b);
        }
    }

    /**
     * Retrieves the value of the encapsulated Boolean variable.
     * 
     * @return the value of the encapsulated Boolean variable
     */
    public boolean get() {
        return value;
    }
}
