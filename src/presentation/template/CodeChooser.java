package presentation.template;

import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.fsa.FSAEvent;
import model.template.TemplateModule;

import presentation.LayoutShell;
import presentation.Presentation;

public class CodeChooser extends JPanel implements Presentation,
		TemplateGraphSubscriber, ListSelectionListener {
	
	protected TemplateGraph graph;
	private JList eventList;
	protected JTextArea codeField;
	private String previousSel=null;

	public CodeChooser(TemplateGraph graph) {
		this.graph=graph;
		graph.addSubscriber(this);
		Box mainBox=Box.createVerticalBox();
		eventList=new JList();
		eventList.addListSelectionListener(this);
		mainBox.add(new JScrollPane(eventList));
		codeField=new JTextArea(10,40);
		addComponentListener(new ComponentAdapter()
		{
			public void componentHidden(ComponentEvent ce)
			{
				saveCode();
			}
		});
		mainBox.add(new JScrollPane(codeField));
		add(mainBox);
		updateList();
	}

	public JComponent getGUI() {
		return this;
	}

	public LayoutShell getLayoutShell() {
		return graph;
	}

	public void setTrackModel(boolean b) {
		if(b)
		{
			TemplateGraphSubscriber[] listeners=graph.getTemplateGraphSubscribers();
			boolean found=false;
			for(int i=0;i<listeners.length;++i)
			{
				if(listeners[i]==this)
				{
					found=true;
					break;
				}
			}
			if(!found)
			{
				graph.addSubscriber(this);
			}
		}
		else
		{
			graph.removeSubscriber(this);
		}
	}

	public void release() {
		setTrackModel(false);
	}

	public void templateGraphChanged(TemplateGraphMessage message) {
		updateList();
	}

	public void templateGraphSelectionChanged(TemplateGraphMessage message) {
	}
	
	protected void updateList()
	{
		Collection<FSAEvent> events=new TreeSet<FSAEvent>();
		for(Iterator<TemplateModule> i=graph.getModel().getModuleIterator();i.hasNext();)
		{
			events.addAll(i.next().getFSA().getEventSet());
		}
		eventList.setListData(events.toArray());
		this.invalidate();
	}
	
	public void valueChanged(ListSelectionEvent lse)
	{
		if(lse.getValueIsAdjusting())
		{
			return;
		}
		if(previousSel!=null)
		{
			graph.getModel().setPLCCode(previousSel,codeField.getText());
		}
		if(eventList.getSelectedValue()==null)
		{
			codeField.setText("");
			previousSel=null;
		}
		else
		{
			FSAEvent event=(FSAEvent)eventList.getSelectedValue();
			codeField.setText(graph.getModel().getPLCCode(event.getSymbol()));
			previousSel=event.getSymbol();
		}
	}
	
	public void saveCode()
	{
		if(eventList.getSelectedValue()!=null)
		{
			graph.getModel().setPLCCode(((FSAEvent)eventList.getSelectedValue()).getSymbol(),codeField.getText());
		}
	}
}
