package model.template;

import java.util.Collection;

import main.Annotable;
import model.DESElement;
import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;
import model.fsa.FSAModel;

public interface TemplateBlock extends DESElement, Annotable
{
	/**
	 * Returns the underlying FSA.
	 * 
	 * @return the underlying FSA
	 */
	public FSAModel getFSA();

	/**
	 * Returns the set of interface events, which can be linked to other events.
	 * 
	 * @return the set of interface events
	 * @see #setInterfaceEvents(FSAEventSet)
	 */
	public FSAEventSet getInterfaceEvents();

	/**
	 * Sets the set of interface events.
	 * 
	 * @param events
	 *            the new set of interface events
	 * @see #getInterfaceEvents()
	 */
	public void setInterfaceEvents(FSAEventSet events);

	/**
	 * Returns all the links connected to this template block.
	 * 
	 * @return the links connected to this template block
	 */
	public Collection<TemplateLink> getLinks();

	/**
	 * Adds a link to this template block.
	 * 
	 * @param link
	 *            the link to be added
	 * @see #addLinkRight(TemplateLink)
	 */
	public void addLink(TemplateLink link);

	/**
	 * Removes a link from this template block, if it exists.
	 * 
	 * @param link
	 *            the link to be removed
	 */
	public void removeLink(TemplateLink link);

	/**
	 * Returns the links connected to a given interface event.
	 * 
	 * @param e
	 *            the interface event to use for look-up
	 * @return the links connected to the given interface event
	 */
	public Collection<TemplateLink> getLinksForEvent(FSAEvent e);
}
