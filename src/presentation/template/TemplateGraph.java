package presentation.template;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import main.Annotable;
import model.DESModelSubscriber;
import model.fsa.FSAEvent;
import model.template.TemplateBlock;
import model.template.TemplateChannel;
import model.template.TemplateLink;
import model.template.TemplateMessage;
import model.template.TemplateModel;
import model.template.TemplateModule;
import model.template.TemplateSubscriber;
import model.template.ver2_1.Link;
import presentation.LayoutShell;

public class TemplateGraph implements LayoutShell, /* DESModelPublisher, */
TemplateSubscriber, Annotable
{

	public static final Color COLOR_NORM = Color.BLACK;

	public static final Color COLOR_SELECT = Color.RED;

	public static final Color COLOR_HILIGHT = Color.BLUE;

	protected TemplateModel model;

	protected boolean needsSave = false;

	/**
	 * LayoutShellPublisher part which maintains a collection of, and sends
	 * change notifications to, all interested observers (subscribers).
	 */
	protected ArrayList<DESModelSubscriber> lssubscribers = new ArrayList<DESModelSubscriber>();

	/**
	 * FSAGraphPublisher part which maintains a collection of, and sends change
	 * notifications to, all interested observers (subscribers).
	 */
	protected ArrayList<TemplateGraphSubscriber> subscribers = new ArrayList<TemplateGraphSubscriber>();

	private Collection<GraphBlock> blocks;

	private Collection<GraphLink> links;

	protected TemplateLibrary library;

	// TODO make the state use a common annotation repository
	protected Hashtable<String, Object> annotations = new Hashtable<String, Object>();

	/**
	 * Returns the annotation for the given key.
	 * 
	 * @param key
	 *            key for the annotation
	 * @return if there is no annotation for the given key, returns
	 *         <code>null</code>, otherwise returns the annotation for the
	 *         key
	 */
	public Object getAnnotation(String key)
	{
		return annotations.get(key);
	}

	/**
	 * Sets an annotation for a given key. If there is already an annotation for
	 * the key, it is replaced.
	 * 
	 * @param key
	 *            the key for the annotation
	 * @param annotation
	 *            the annotation
	 */
	public void setAnnotation(String key, Object annotation)
	{
		annotations.put(key, annotation);
	}

	/**
	 * Removes the annotation for the given key.
	 * 
	 * @param key
	 *            key for the annotation
	 */
	public void removeAnnotation(String key)
	{
		annotations.remove(key);
	}

	/**
	 * Returns <code>true</code> if there is an annotation for the given key.
	 * Otherwise returns <code>false</code>.
	 * 
	 * @param key
	 *            key for the annotation
	 * @return <code>true</code> if there is an annotation for the given key,
	 *         <code>false</code> otherwise
	 */
	public boolean hasAnnotation(String key)
	{
		return annotations.containsKey(key);
	}

	public TemplateGraph(TemplateModel model, TemplateLibrary lib)
	{
		library = lib;
		this.model = model;
		updateLists();
		model.addSubscriber(this);
	}

	public TemplateModel getModel()
	{
		return model;
	}

	public Class<?> getModelInterface()
	{
		return TemplateModel.class;
	}

	public String getName()
	{
		return model.getName();
	}

	public boolean needsSave()
	{
		return needsSave;
	}

	public void modelSaved()
	{
		needsSave = false;
		fireSaveStatusChanged();
	}

	public void release()
	{
		model.removeSubscriber(this);
	}

	public void templateStructureChanged(TemplateMessage message)
	{
		updateLists();
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.MODIFY,
				TemplateGraphMessage.GRAPH,
				0,
				this.getBounds(false),
				this));
	}

	public void createModule(String templateName, Point2D.Float location)
	{
		TemplateModule m = library.instantiateModule(templateName);
		m.setId(model.getFreeModuleId());
		Set<String> allNames = new TreeSet<String>();
		for (GraphBlock b : blocks)
		{
			allNames.add(b.getName());
		}
		int idx = 0;
		while (allNames.contains(templateName + " " + idx))
		{
			++idx;
		}
		m.setAnnotation(Annotable.LAYOUT, new BlockLayout(
				location,
				templateName + " " + idx));
		model.removeSubscriber(this);
		model.add(m);
		model.addSubscriber(this);
		GraphBlock gb = new GraphBlock(m);
		blocks.add(gb);
		changeName(gb, templateName + " " + idx);
		Rectangle2D l = new Rectangle2D.Float();
		l.add(location);
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.ADD,
				TemplateGraphMessage.MODULE,
				m.getId(),
				l,
				this));
	}

	public void remove(GraphBlock b)
	{
		if (!blocks.contains(b))
		{
			return;
		}
		model.removeSubscriber(this);
		if (b.getBlock() instanceof TemplateModule)
		{
			model.remove((TemplateModule)b.getBlock());
		}
		else
		{
			model.remove((TemplateChannel)b.getBlock());
		}
		model.addSubscriber(this);
		blocks.remove(b);
		updateLists();
		Rectangle2D l = new Rectangle2D.Float();
		l.add(b.getLayout().getLocation());
		int type;
		if (b instanceof TemplateModule)
		{
			type = TemplateGraphMessage.MODULE;
		}
		else
		{
			type = TemplateGraphMessage.CHANNEL;
		}
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.REMOVE,
				type,
				b.getId(),
				l,
				this));
	}

	public void remove(GraphLink link)
	{
		if (!links.contains(link))
		{
			return;
		}
		model.removeSubscriber(this);
		model.remove(link.getLink());
		model.addSubscriber(this);
		links.remove(link);
		Rectangle2D l = new Rectangle2D.Float();
		l.add(link.bounds());
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.REMOVE,
				TemplateGraphMessage.LINK,
				link.getId(),
				l,
				this));
	}

	public void createChannel(String templateName, Point2D.Float location)
	{
		TemplateChannel c = library.instantiateChannel(templateName);
		c.setId(model.getFreeChannelId());
		Set<String> allNames = new TreeSet<String>();
		for (GraphBlock b : blocks)
		{
			allNames.add(b.getName());
		}
		int idx = 0;
		while (allNames.contains(templateName + " " + idx))
		{
			++idx;
		}
		c.setAnnotation(Annotable.LAYOUT, new BlockLayout(
				location,
				templateName + " " + idx));
		model.removeSubscriber(this);
		model.add(c);
		model.addSubscriber(this);
		Rectangle2D l = new Rectangle2D.Float();
		l.add(location);
		GraphBlock gb = new GraphBlock(c);
		blocks.add(gb);
		changeName(gb, templateName + " " + idx);
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.ADD,
				TemplateGraphMessage.CHANNEL,
				c.getId(),
				l,
				this));
	}

	public void createLink(GraphBlock left, FSAEvent leftEvent,
			GraphBlock right, FSAEvent rightEvent)
	{
		TemplateLink link = new Link(
				model.getFreeLinkId(),
				left.getBlock(),
				leftEvent,
				right.getBlock(),
				rightEvent);
		link.setAnnotation(Annotable.LAYOUT, new LinkLayout());
		model.removeSubscriber(this);
		model.add(link);
		model.addSubscriber(this);
		Rectangle2D l = new Rectangle2D.Float();
		l.add(left.getLocation());
		l.add(right.getLocation());
		GraphLink gl = new GraphLink(link, left, right);
		links.add(gl);
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.ADD,
				TemplateGraphMessage.LINK,
				link.getId(),
				l,
				this));
	}

	protected void changeName(GraphBlock b, String text)
	{
		b.getBlock().getFSA().setName(text);
		for (FSAEvent e : b.getBlock().getFSA().getEventSet())
		{
			String name = e.getSymbol();
			String code = model.getPLCCode(name);
			model.setPLCCode(name, null);
			if (name.contains(":"))
			{
				name = name.split(":", 2)[1];
			}
			name = text + ":" + name;
			e.setSymbol(name);
			if (code != null)
			{
				model.setPLCCode(name, code);
			}
		}
		BlockLayout layout = b.getLayout();
		layout.setText(text);
		b.setLayout(layout);
	}

	public void label(GraphBlock b, String text)
	{
		changeName(b, text);
		Rectangle2D l = new Rectangle2D.Float();
		l.add(b.getLayout().getLocation());
		int type;
		if (b instanceof TemplateModule)
		{
			type = TemplateGraphMessage.MODULE;
		}
		else if (b instanceof TemplateChannel)
		{
			type = TemplateGraphMessage.CHANNEL;
		}
		else
		{
			type = TemplateGraphMessage.UNKNOWN_TYPE;
		}
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.MODIFY,
				type,
				b.getId(),
				l,
				this));
	}

	public String getLabel(GraphBlock b)
	{
		return b.getLayout().getText();
	}

	public Collection<GraphBlock> getBlocks()
	{
		return blocks;
	}

	public Collection<GraphLink> getLinks()
	{
		return links;
	}

	public void relocate(GraphBlock b, Point2D.Float location)
	{
		BlockLayout layout = b.getLayout();
		Rectangle2D l = new Rectangle2D.Float();
		l.add(layout.getLocation());
		l.add(location);
		layout.setLocation(location.x, location.y);
		b.setLayout(layout);
		int type;
		if (b instanceof TemplateModule)
		{
			type = TemplateGraphMessage.MODULE;
		}
		else if (b instanceof TemplateChannel)
		{
			type = TemplateGraphMessage.CHANNEL;
		}
		else
		{
			type = TemplateGraphMessage.UNKNOWN_TYPE;
		}
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.MODIFY,
				type,
				b.getId(),
				l,
				this));
	}

	public void commitRelocate(Collection<GraphBlock> items)
	{
		fireTemplateGraphChanged(new TemplateGraphMessage(
				TemplateGraphMessage.MODIFY,
				TemplateGraphMessage.GRAPH,
				0,
				getBounds(false),
				this));
	}

	protected BlockLayout getLayout(TemplateBlock b)
	{
		BlockLayout layout = (BlockLayout)b.getAnnotation(Annotable.LAYOUT);
		if (layout == null)
		{
			layout = new BlockLayout();
		}
		return layout;
	}

	protected LinkLayout getLayout(TemplateLink l)
	{
		LinkLayout layout = (LinkLayout)l.getAnnotation(Annotable.LAYOUT);
		if (layout == null)
		{
			layout = new LinkLayout();
		}
		return layout;
	}

	public Rectangle getBounds(boolean initAtZeroZero)
	{
		Rectangle graphBounds = initAtZeroZero ? new Rectangle()
				: getElementBounds();

		// Start with the modules
		for (Iterator<TemplateModule> i = model.getModuleIterator(); i
				.hasNext();)
		{
			graphBounds = graphBounds.union(getLayout(i.next()).getBounds());
		}

		for (Iterator<TemplateChannel> i = model.getChannelIterator(); i
				.hasNext();)
		{
			graphBounds = graphBounds.union(getLayout(i.next()).getBounds());
		}

		for (Iterator<TemplateLink> i = model.getLinkIterator(); i.hasNext();)
		{
			graphBounds = graphBounds.union(getLayout(i.next()).getBounds());
		}

		return graphBounds;
	}

	private Rectangle getElementBounds()
	{
		if (model.getModuleCount() > 0)
		{
			return getLayout(model.getModuleIterator().next()).getBounds();
		}
		if (model.getChannelCount() > 0)
		{
			return getLayout(model.getChannelIterator().next()).getBounds();
		}
		if (model.getLinkCount() > 0)
		{
			return getLayout(model.getLinkIterator().next()).getBounds();
		}
		return new Rectangle();
	}

	public void translate(float x, float y)
	{
		for (Iterator<TemplateModule> i = model.getModuleIterator(); i
				.hasNext();)
		{
			getLayout(i.next()).translate(x, y);
		}

		for (Iterator<TemplateChannel> i = model.getChannelIterator(); i
				.hasNext();)
		{
			getLayout(i.next()).translate(x, y);
		}

		for (Iterator<TemplateLink> i = model.getLinkIterator(); i.hasNext();)
		{
			getLayout(i.next()).translate(x, y);
		}
		// Rectangle2D l=new Rectangle2D.Float();
		// l.add(getBounds(false));
		// fireTemplateGraphChanged(new TemplateGraphMessage(
		// TemplateGraphMessage.MODIFY,
		// TemplateGraphMessage.GRAPH,
		// getId(),
		// l,
		// this
		// ));
	}

	/**
	 * Attaches the given subscriber to this publisher. The given subscriber
	 * will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(DESModelSubscriber subscriber)
	{
		lssubscribers.add(subscriber);
	}

	/**
	 * Removes the given subscriber to this publisher. The given subscriber will
	 * no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(DESModelSubscriber subscriber)
	{
		lssubscribers.remove(subscriber);
	}

	/**
	 * Returns all current subscribers to this publisher.
	 * 
	 * @return all current subscribers to this publisher
	 */
	public DESModelSubscriber[] getLayoutShellSubscribers()
	{
		return lssubscribers.toArray(new DESModelSubscriber[] {});
	}

	/**
	 * Notifies all subscribers that there has been a change to an element of
	 * the save status of the design.
	 */
	private void fireSaveStatusChanged()
	{
		// DESModelMessage message=new DESModelMessage(
		// needsSave?DESModelMessage.DIRTY:DESModelMessage.CLEAN,
		// this);
		// for(DESModelSubscriber s : lssubscribers) {
		// s.saveStatusChanged(message);
		// }
	}

	/**
	 * Attaches the given subscriber to this publisher. The given subscriber
	 * will receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void addSubscriber(TemplateGraphSubscriber subscriber)
	{
		subscribers.add(subscriber);
	}

	/**
	 * Removes the given subscriber to this publisher. The given subscriber will
	 * no longer receive notifications of changes from this publisher.
	 * 
	 * @param subscriber
	 */
	public void removeSubscriber(TemplateGraphSubscriber subscriber)
	{
		subscribers.remove(subscriber);
	}

	/**
	 * Returns all current subscribers to this publisher.
	 * 
	 * @return all current subscribers to this publisher
	 */
	public TemplateGraphSubscriber[] getTemplateGraphSubscribers()
	{
		return subscribers.toArray(new TemplateGraphSubscriber[] {});
	}

	/**
	 * Notifies all subscribers that there has been a change to an element of
	 * this graph publisher.
	 * 
	 * @param message
	 */
	private void fireTemplateGraphChanged(TemplateGraphMessage message)
	{
		if (!needsSave)
		{
			needsSave = true;
			fireSaveStatusChanged();
		}
		for (TemplateGraphSubscriber s : subscribers)
		{
			s.templateGraphChanged(message);
		}
	}

	private void updateLists()
	{
		HashMap<TemplateBlock, GraphBlock> backMap = new HashMap<TemplateBlock, GraphBlock>();
		blocks = new LinkedList<GraphBlock>();
		for (Iterator<TemplateModule> i = model.getModuleIterator(); i
				.hasNext();)
		{
			GraphBlock b = new GraphBlock(i.next());
			blocks.add(b);
			backMap.put(b.getBlock(), b);
		}
		for (Iterator<TemplateChannel> i = model.getChannelIterator(); i
				.hasNext();)
		{
			GraphBlock b = new GraphBlock(i.next());
			blocks.add(b);
			backMap.put(b.getBlock(), b);
		}
		links = new LinkedList<GraphLink>();
		for (Iterator<TemplateLink> i = model.getLinkIterator(); i.hasNext();)
		{
			TemplateLink tl = i.next();
			links.add(new GraphLink(tl, backMap.get(tl.getBlockLeft()), backMap
					.get(tl.getBlockRight())));
		}
	}

	public void draw(Graphics2D g)
	{
		Map<String, String> labels = new HashMap<String, String>();
		for (GraphLink l : links)
		{
			String key = "";
			if (l.getLink().getBlockLeft().getId() < l
					.getLink().getBlockRight().getId())
			{
				key = l.getLink().getBlockLeft().getId() + "."
						+ l.getLink().getBlockRight().getId();
			}
			else
			{
				key = l.getLink().getBlockRight().getId() + "."
						+ l.getLink().getBlockLeft().getId();
			}
			String label = labels.get(key);
			if (label == null)
			{
				label = "";
			}
			label += l.getLink().getEventLeft().getSymbol() + " = "
					+ l.getLink().getEventRight().getSymbol() + ";";
			labels.put(key, label);
			l.draw(g);
		}
		g.setColor(Color.BLACK);
		for (GraphLink l : links)
		{
			String key = "";
			if (l.getLink().getBlockLeft().getId() < l
					.getLink().getBlockRight().getId())
			{
				key = l.getLink().getBlockLeft().getId() + "."
						+ l.getLink().getBlockRight().getId();
			}
			else
			{
				key = l.getLink().getBlockRight().getId() + "."
						+ l.getLink().getBlockLeft().getId();
			}
			String label = labels.get(key);
			String[] parts = label.split(";");
			for (int i = 0; i < parts.length; ++i)
			{
				g.drawString(parts[i], (int)l.getLocation().x, (int)l
						.getLocation().y
						+ i * g.getFontMetrics().getHeight());
			}
		}
		for (GraphBlock b : blocks)
		{
			b.draw(g);
		}
	}
}