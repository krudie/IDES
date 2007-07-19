package model.template.ver2_1;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;
import model.fsa.FSAModel;
import model.template.TemplateBlock;
import model.template.TemplateLink;
import model.template.TemplateModule;

public class Block implements TemplateBlock {

	protected long id;
	protected FSAModel fsa;
	protected FSAEventSet ifaceEvents;
	protected LinkedList<TemplateLink> links;

    //TODO make the state use a common annotation repository
    protected Hashtable<String, Object> annotations=new Hashtable<String,Object>();
    
	/**
	 * Returns the annotation for the given key.
	 * @param key key for the annotation
	 * @return if there is no annotation for the given key,
	 * returns <code>null</code>, otherwise returns the annotation
	 * for the key
	 */
	public Object getAnnotation(String key)
	{
		return annotations.get(key);
	}

	/**
	 * Sets an annotation for a given key. If there is already
	 * an annotation for the key, it is replaced. 
	 * @param key the key for the annotation
	 * @param annotation the annotation
	 */
	public void setAnnotation(String key, Object annotation)
	{
		annotations.put(key, annotation);
	}
	
	/**
	 * Removes the annotation for the given key.
	 * @param key key for the annotation
	 */
	public void removeAnnotation(String key)
	{
		annotations.remove(key);
	}
	
	/**
	 * Returns <code>true</code> if there is an annotation
	 * for the given key. Otherwise returns <code>false</code>.
	 * @param key key for the annotation
	 * @return <code>true</code> if there is an annotation
	 * for the given key, <code>false</code> otherwise
	 */
	public boolean hasAnnotation(String key)
	{
		return annotations.containsKey(key);
	}
	
	public Block(FSAModel fsa, FSAEventSet events)
	{
		this.fsa=fsa;
		ifaceEvents=events;
		links=new LinkedList<TemplateLink>();
	}
	
	public void addLink(TemplateLink link) {
		if(!links.contains(link))
			links.add(link);
	}

	public FSAModel getFSA() {
		return fsa;
	}

	public FSAEventSet getInterfaceEvents() {
		return ifaceEvents;
	}

	public Collection<TemplateLink> getLinks() {
		return links;
	}

	public Collection<TemplateLink> getLinksForEvent(FSAEvent e) {
		LinkedList<TemplateLink> ls=new LinkedList<TemplateLink>();
		for(TemplateLink l:links)
		{
			if(l.getBlockLeft()==this&&l.getEventLeft()==e)
			{
				ls.add(l);
			}
			else if(l.getBlockRight()==this&&l.getEventRight()==e)
			{
				ls.add(l);
			}
		}
		return ls;
	}

	public void removeLink(TemplateLink link) {
		links.remove(link);
	}

	public void setInterfaceEvents(FSAEventSet events) {
		ifaceEvents=events;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id=id;
	}
}
