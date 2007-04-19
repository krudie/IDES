package presentation.template;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


import main.Annotable;
import model.DESModel;
import model.fsa.FSAState;
import model.template.TemplateBlock;
import model.template.TemplateChannel;
import model.template.TemplateLink;
import model.template.TemplateMessage;
import model.template.TemplateModel;
import model.template.TemplateModule;
import model.template.TemplateSubscriber;
import model.template.ver2_1.Channel;
import model.template.ver2_1.Module;
import presentation.LayoutShell;
import presentation.LayoutShellMessage;
import presentation.LayoutShellPublisher;
import presentation.LayoutShellSubscriber;

public class TemplateGraph implements LayoutShell, LayoutShellPublisher, TemplateSubscriber {
	
	public static final Color COLOR_NORM=Color.BLACK;
	public static final Color COLOR_SELECT=Color.RED;
	public static final Color COLOR_HILIGHT=Color.BLUE;
	
	protected TemplateModel model;
	protected boolean needsSave=false;
	
	/** LayoutShellPublisher part which maintains a collection of, and 
	 * sends change notifications to, all interested observers (subscribers). 
	 */
	protected ArrayList<LayoutShellSubscriber> lssubscribers = new ArrayList<LayoutShellSubscriber>();
	/** FSAGraphPublisher part which maintains a collection of, and 
	 * sends change notifications to, all interested observers (subscribers). 
	 */
	protected ArrayList<TemplateGraphSubscriber> subscribers = new ArrayList<TemplateGraphSubscriber>();
	
	private Collection<GraphBlock> blocks;
	
	public TemplateGraph(TemplateModel model)
	{
		this.model=model;
		updateLists();
		model.addSubscriber(this);
	}

	public DESModel getModel() {
		return model;
	}

	public Class getModelInterface() {
		return TemplateModel.class;
	}

	public boolean needsSave() {
		return needsSave;
	}
	
	public void modelSaved() {
		needsSave = false;
		fireSaveStatusChanged();
	}

	public void release()
	{
		model.removeSubscriber(this);
	}

	public void templateStructureChanged(TemplateMessage message)
	{
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.MODIFY,
				TemplateGraphMessage.GRAPH,
				0,
				this.getBounds(false),
				this)
				);
	}

	public void createModule(Point2D.Float location)
	{
		TemplateModule m=new Module(null,null);
		m.setId(model.getFreeModuleId());
		model.removeSubscriber(this);
		model.add(m);
		model.addSubscriber(this);
		m.setAnnotation(Annotable.LAYOUT, new BlockLayout(location));
		blocks.add(new GraphBlock(m));
		Rectangle2D l=new Rectangle2D.Float();
		l.add(location);
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.ADD,
				TemplateGraphMessage.MODULE,
				m.getId(),
				l,
				this
				));
	}
	
	public void remove(TemplateModule m)
	{
		if(model.getModule(m.getId())!=m)
			return;
		model.removeSubscriber(this);
		model.remove(m);
		model.addSubscriber(this);
		for(Iterator<GraphBlock> i=blocks.iterator();i.hasNext();)
		{
			if(i.next().getBlock()==m)
			{
				i.remove();
				break;
			}
		}
		Rectangle2D l=new Rectangle2D.Float();
		l.add(getLayout(m).getLocation());
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.REMOVE,
				TemplateGraphMessage.MODULE,
				m.getId(),
				l,
				this
				));
	}

	public void createChannel(Point2D.Float location)
	{
		TemplateChannel c=new Channel(null,null);
		c.setId(model.getFreeChannelId());
		model.removeSubscriber(this);
		model.add(c);
		model.addSubscriber(this);
		c.setAnnotation(Annotable.LAYOUT, new BlockLayout(location));
		Rectangle2D l=new Rectangle2D.Float();
		l.add(location);
		blocks.add(new GraphBlock(c));
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.ADD,
				TemplateGraphMessage.CHANNEL,
				c.getId(),
				l,
				this
				));
	}
	
	public void remove(TemplateChannel c)
	{
		if(model.getChannel(c.getId())!=c)
			return;
		model.removeSubscriber(this);
		model.remove(c);
		model.addSubscriber(this);
		for(Iterator<GraphBlock> i=blocks.iterator();i.hasNext();)
		{
			if(i.next().getBlock()==c)
			{
				i.remove();
				break;
			}
		}
		Rectangle2D l=new Rectangle2D.Float();
		l.add(getLayout(c).getLocation());
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.REMOVE,
				TemplateGraphMessage.CHANNEL,
				c.getId(),
				l,
				this
				));
	}

	public void label(TemplateBlock b, String text)
	{
		BlockLayout layout=getLayout(b);
		layout.setText(text);
		b.setAnnotation(Annotable.LAYOUT, layout);
		Rectangle2D l=new Rectangle2D.Float();
		l.add(getLayout(b).getLocation());
		int type;
		if(b instanceof TemplateModule)
			type=TemplateGraphMessage.MODULE;
		else if(b instanceof TemplateChannel)
			type=TemplateGraphMessage.CHANNEL;
		else
			type=TemplateGraphMessage.UNKNOWN_TYPE;
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.MODIFY,
				type,
				b.getId(),
				l,
				this
				));
	}
	
	public String getLabel(TemplateBlock b)
	{
		BlockLayout layout=getLayout(b);
		return layout.getText();
	}
	
	public Collection<GraphBlock> getBlocks()
	{
		return blocks;
	}
	
	public void relocate(TemplateBlock b, Point2D.Float location)
	{
		BlockLayout layout=getLayout(b);
		Rectangle2D l=new Rectangle2D.Float();
		l.add(layout.getLocation());
		l.add(location);
		layout.setLocation(location.x, location.y);
		b.setAnnotation(Annotable.LAYOUT, layout);
		int type;
		if(b instanceof TemplateModule)
			type=TemplateGraphMessage.MODULE;
		else if(b instanceof TemplateChannel)
			type=TemplateGraphMessage.CHANNEL;
		else
			type=TemplateGraphMessage.UNKNOWN_TYPE;
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.MODIFY,
				type,
				b.getId(),
				l,
				this
				));
	}
	
	protected BlockLayout getLayout(TemplateBlock b)
	{
		BlockLayout layout=(BlockLayout)b.getAnnotation(Annotable.LAYOUT);
		if(layout==null)
		{
			layout=new BlockLayout();
		}
		return layout;
	}

	protected LinkLayout getLayout(TemplateLink l)
	{
		LinkLayout layout=(LinkLayout)l.getAnnotation(Annotable.LAYOUT);
		if(layout==null)
		{
			layout=new LinkLayout();
		}
		return layout;
	}

	public Rectangle getBounds(boolean initAtZeroZero)
	{
		Rectangle graphBounds = initAtZeroZero ? 
				new Rectangle() : getElementBounds();
		
		// Start with the modules
		for (Iterator<TemplateModule> i=model.getModuleIterator();i.hasNext();)
		{
			graphBounds = graphBounds.union(getLayout(i.next()).getBounds());
		}
	
		for (Iterator<TemplateChannel> i=model.getChannelIterator();i.hasNext();)
		{
			graphBounds = graphBounds.union(getLayout(i.next()).getBounds());
		}
		
		for (Iterator<TemplateLink> i=model.getLinkIterator();i.hasNext();)
		{
			graphBounds = graphBounds.union(getLayout(i.next()).getBounds());
		}
	
		return graphBounds;
	}

	private Rectangle getElementBounds()
	{
		if(model.getModuleCount()>0)
			return getLayout(model.getModuleIterator().next()).getBounds();
		if(model.getChannelCount()>0)
			return getLayout(model.getChannelIterator().next()).getBounds();
		if(model.getLinkCount()>0)
			return getLayout(model.getLinkIterator().next()).getBounds();
		return new Rectangle();
	}
	
	public void translate(float x, float y)
	{
		for (Iterator<TemplateModule> i=model.getModuleIterator();i.hasNext();)
		{
			getLayout(i.next()).translate(x, y);
		}
	
		for (Iterator<TemplateChannel> i=model.getChannelIterator();i.hasNext();)
		{
			getLayout(i.next()).translate(x, y);
		}
		
		for (Iterator<TemplateLink> i=model.getLinkIterator();i.hasNext();)
		{
			getLayout(i.next()).translate(x, y);
		}
//		Rectangle2D l=new Rectangle2D.Float();
//		l.add(getBounds(false));
//		fireTemplateGraphChanged(new TemplateGraphMessage(
//				TemplateGraphMessage.MODIFY,
//				TemplateGraphMessage.GRAPH,
//				getId(),
//				l,
//				this
//				));
	}

	/**
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(LayoutShellSubscriber subscriber) {
		lssubscribers.add(subscriber);
	}

	/**
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(LayoutShellSubscriber subscriber) {
		lssubscribers.remove(subscriber);
	}

	/**
	 * Returns all current subscribers to this publisher.
	 * @return all current subscribers to this publisher
	 */
	public LayoutShellSubscriber[] getLayoutShellSubscribers()
	{
		return lssubscribers.toArray(new LayoutShellSubscriber[]{});
	}
	
	/**
	 * Notifies all subscribers that there has been a change to an element of 
	 * the save status of the design.
	 */
	private void fireSaveStatusChanged() {
		LayoutShellMessage message=new LayoutShellMessage(
				needsSave?LayoutShellMessage.DIRTY:LayoutShellMessage.CLEAN,
						this);
		for(LayoutShellSubscriber s : lssubscribers)	{
			s.saveStatusChanged(message);
		}
	}
	
	/**
	 * Attaches the given subscriber to this publisher.
	 * The given subscriber will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(TemplateGraphSubscriber subscriber) {
		subscribers.add(subscriber);
	}
	
	/**
	 * Removes the given subscriber to this publisher.
	 * The given subscriber will no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(TemplateGraphSubscriber subscriber) {
		subscribers.remove(subscriber);
	}
	
	/**
	 * Returns all current subscribers to this publisher.
	 * @return all current subscribers to this publisher
	 */
	public TemplateGraphSubscriber[] getTemplateGraphSubscribers()
	{
		return subscribers.toArray(new TemplateGraphSubscriber[]{});
	}
	
	/**
	 * Notifies all subscribers that there has been a change to an element of 
	 * this graph publisher.
	 * 
	 * @param message
	 */
	private void fireTemplateGraphChanged(TemplateGraphMessage message) {
		if(message.getEventType()==TemplateGraphMessage.ADD||
				message.getEventType()==TemplateGraphMessage.REMOVE||
				message.getElementType()==TemplateGraphMessage.GRAPH)
		{
			updateLists();
		}
		if(!needsSave)
		{
			needsSave = true;
			fireSaveStatusChanged();
		}
		for(TemplateGraphSubscriber s : subscribers)	{
			s.templateGraphChanged(message);
		}
	}
	
	private void updateLists()
	{
		blocks=new LinkedList<GraphBlock>();
		for (Iterator<TemplateModule> i=model.getModuleIterator();i.hasNext();)
		{
			blocks.add(new GraphBlock(i.next()));
		}
		for (Iterator<TemplateChannel> i=model.getChannelIterator();i.hasNext();)
		{
			blocks.add(new GraphBlock(i.next()));
		}
	}
	
	public void draw(Graphics2D g)
	{
		for(GraphBlock b:blocks)
		{
			b.draw(g);
		}
	}
}