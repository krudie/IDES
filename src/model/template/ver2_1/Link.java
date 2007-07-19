package model.template.ver2_1;

import java.util.Hashtable;

import model.fsa.FSAEvent;
import model.template.TemplateBlock;
import model.template.TemplateLink;

public class Link implements TemplateLink {

	protected TemplateBlock leftBlock, rightBlock;
	protected FSAEvent leftEvent, rightEvent;
	protected long id;
	
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

	public Link(long id, TemplateBlock leftBlock,
			FSAEvent leftEvent,
			TemplateBlock rightBlock,
			FSAEvent rightEvent)
	{
		this.id=id;
		this.leftBlock=leftBlock;
		this.leftEvent=leftEvent;
		this.rightBlock=rightBlock;
		this.rightEvent=rightEvent;
	}
	
	public TemplateBlock getBlockLeft() {
		return leftBlock;
	}

	public TemplateBlock getBlockRight() {
		return rightBlock;
	}

	public FSAEvent getEventLeft() {
		return leftEvent;
	}

	public FSAEvent getEventRight() {
		return rightEvent;
	}

	public void setBlockLeft(TemplateBlock b) {
		leftBlock=b;
	}

	public void setBlockRight(TemplateBlock b) {
		rightBlock=b;
	}

	public void setEventLeft(FSAEvent e) {
		leftEvent=e;
	}

	public void setEventRight(FSAEvent e) {
		rightEvent=e;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id=id;
	}
}
