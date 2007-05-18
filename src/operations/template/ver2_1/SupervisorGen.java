package operations.template.ver2_1;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import operations.fsa.ver2_1.Selfloop;
import operations.fsa.ver2_1.SupCon;
import operations.fsa.ver2_1.SynchronousProduct;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAEventSet;
import model.fsa.FSAModel;
import model.fsa.FSASupervisor;
import model.fsa.ver2_1.Event;
import model.template.TemplateBlock;
import model.template.TemplateChannel;
import model.template.TemplateLink;
import model.template.TemplateModel;
import model.template.TemplateModule;
import pluggable.operation.Operation;
import presentation.fsa.FSAGraph;
import util.StupidSetWrapper;

public class SupervisorGen implements Operation {

	public String[] getDescriptionOfInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getDescriptionOfOutputs() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return "Supervisor Generator";
	}

	public int getNumberOfInputs() {
		return 1;
	}

	public int getNumberOfOutputs() {
		// TODO Auto-generated method stub
		return 2;
	}

	public Class[] getTypeOfInputs() {
		return new Class[]{TemplateModel.class};
	}

	public Class[] getTypeOfOutputs() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] perform(Object[] inputs) {
		TemplateModel model=(TemplateModel)inputs[0];
		HashMap<TemplateChannel,Set<TemplateBlock>> clusters=new HashMap<TemplateChannel, Set<TemplateBlock>>();
		for(Iterator<TemplateChannel> i=model.getChannelIterator();i.hasNext();)
		{
			TemplateChannel c=i.next();
			HashSet<TemplateBlock> cluster=new HashSet<TemplateBlock>();
			for(TemplateLink l:c.getLinks())
			{
				cluster.add(l.getBlockLeft());
				cluster.add(l.getBlockRight());
			}
			cluster.remove(c);
			clusters.put(c,cluster);
		}
		Set<Set<TemplateLink>> linkClasses=new HashSet<Set<TemplateLink>>();
		Set<TemplateLink> checkedLinks=new HashSet<TemplateLink>();
		for(Iterator<TemplateLink> i=model.getLinkIterator();i.hasNext();)
		{
			TemplateLink l=i.next();
			if(checkedLinks.contains(l))
				continue;
			Set<TemplateLink> eqLinks=new HashSet<TemplateLink>();
			eqLinks.add(l);
			addEqLinks(eqLinks,l.getBlockLeft(),l.getEventLeft());
			addEqLinks(eqLinks,l.getBlockRight(),l.getEventRight());
			linkClasses.add(eqLinks);
			checkedLinks.add(l);
		}
		for(Set<TemplateLink> set:linkClasses)
		{
			Set<String> eventNames=new HashSet<String>();
			Set<FSAEvent> events=new HashSet<FSAEvent>();
			for(TemplateLink l:set)
			{
				if(l.getBlockLeft() instanceof TemplateModule)
				{
					eventNames.add(l.getEventLeft().getSymbol());
				}
				if(l.getBlockRight() instanceof TemplateModule)
				{
					eventNames.add(l.getEventRight().getSymbol());
				}
				events.add(l.getEventLeft());
				events.add(l.getEventRight());
			}
			String commonName="";
			for(String name:eventNames)
			{
				commonName+=name+"=";
			}
			if(commonName.endsWith("="))
				commonName=commonName.substring(0,commonName.length()-1);
			for(FSAEvent e:events)
			{
				e.setSymbol(commonName);
			}
		}
		List<FSASupervisor> ret=new Vector<FSASupervisor>();
		List<FSAModel> retS=new Vector<FSAModel>();
		for(TemplateChannel c:clusters.keySet())
		{
			Set<TemplateBlock> set=clusters.get(c);
			if(set.size()<1)
				continue;
			SynchronousProduct sp=new SynchronousProduct();
			Iterator<TemplateBlock> i=set.iterator();
			FSAModel product=set.iterator().next().getFSA();
			for(;i.hasNext();)
			{
				TemplateBlock b=i.next();
				product=(FSAModel)sp.perform(new Object[]{product,b.getFSA()})[0];
			}
			FSAGraph g=new FSAGraph(product);
			g.setName("sync "+c.getFSA().getName());
//			g.labelCompositeNodes();
			Hub.getWorkspace().addLayoutShell(g);
			//TreeSets are used so that the events are compared
			//according to their labels (symbols)
			Set<FSAEvent> diff=new TreeSet<FSAEvent>();
			for(FSAEvent e:product.getEventSet())
			{
				diff.add(new Event(e));
			}
			Set<FSAEvent> toRemove=new TreeSet<FSAEvent>(c.getFSA().getEventSet());
			for(Iterator<FSAEvent> j=diff.iterator();j.hasNext();)
			{
				if(toRemove.contains(j.next()))
					j.remove();
			}
			FSAModel csl=(FSAModel)new Selfloop().perform(new Object[]{c.getFSA(),new StupidSetWrapper(diff)})[0];
			g=new FSAGraph(csl);
			g.setName("selfloop "+csl.getName());
			Hub.getWorkspace().addLayoutShell(g);
			SupCon supcon=new SupCon();
			FSAModel sup=(FSAModel)supcon.perform(new Object[]{product,csl})[0];
			sup.setName("supcon "+c.getFSA().getName());
			ret.add((FSASupervisor)sup);
			retS.add(product);
		}
		return new Object[]{ret,retS};
	}

	private void addEqLinks(Set<TemplateLink> eqLinks,TemplateBlock b,FSAEvent e)
	{
		Collection<TemplateLink> links=b.getLinksForEvent(e);
		for(TemplateLink l:links)
		{
			if(eqLinks.contains(l))
				continue;
			eqLinks.add(l);
			if(b==l.getBlockLeft())
			{
				addEqLinks(eqLinks,l.getBlockRight(),l.getEventRight());
			}
			else
			{
				addEqLinks(eqLinks,l.getBlockLeft(),l.getEventLeft());
			}
		}
	}
}