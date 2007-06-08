package presentation.template;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.Hub;
import model.fsa.FSAModel;
import model.fsa.FSASupervisor;
import util.EscapeDialog;

public class SupervisorSelectDialog extends EscapeDialog {

	private boolean cancelled=true;
	private int needToSelect=0;
	private JList modelList=new JList();
	private JLabel explanation=new JLabel("Select 0 supervisors:");
	
	public SupervisorSelectDialog()
	{
		// TODO title string should come from Hub.string() 
		super(Hub.getMainWindow(),"Select supervisors",true);
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onEscapeEvent();
		    }
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		Box mainBox=Box.createVerticalBox();
	
		mainBox.add(explanation);
		modelList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		mainBox.add(new JScrollPane(modelList));
		
		JButton okButton=new JButton();
		JButton cancelButton=new JButton();		
		okButton = new JButton(Hub.string("OK"));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(modelList.getSelectedIndices().length!=needToSelect)
				{
					//TODO explanation string should come from Hub.string()
					Hub.displayAlert("Please select "+needToSelect+" supervisors.");
				}
				else
				{
					cancelled=false;
					onEscapeEvent();
				}
			}
		});
		cancelButton = new JButton(Hub.string("cancel"));
		cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					cancelled=true;
					onEscapeEvent();
				}
			}
		);
		JPanel p = new JPanel(new FlowLayout());
		p.add(okButton);		
		p.add(cancelButton);
		mainBox.add(p);
		
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		getContentPane().add(mainBox);
		pack();
		
		okButton.setPreferredSize(new Dimension(
				Math.max(okButton.getWidth(),cancelButton.getWidth()),okButton.getHeight()));
		okButton.invalidate();
		cancelButton.setPreferredSize(new Dimension(
				Math.max(okButton.getWidth(),cancelButton.getWidth()),cancelButton.getHeight()));
		cancelButton.invalidate();
	}
	
	public Collection<FSASupervisor> getSupervisors(int count)
	{
		needToSelect=count;
		Collection<FSAModel> models=Hub.getWorkspace().getModelsOfType(FSAModel.class);
		TreeSet<String> names=new TreeSet<String>();
		for(FSAModel m:models)
		{
			names.add(m.getName());
		}
		modelList.setListData(names.toArray());
		//TODO explanation string should come from Hub.string()
		explanation.setText("Select "+count+" supervisors.");
		setVisible(true);
		if(cancelled)
		{
			return null;
		}
		HashSet<FSASupervisor> ret=new HashSet<FSASupervisor>();
		Object[] selected=modelList.getSelectedValues();
		for(int i=0;i<selected.length;++i)
		{
			ret.add((FSASupervisor)Hub.getWorkspace().getModel((String)selected[i]));
		}
		return ret;
	}
	
	public void onEscapeEvent()
	{
		dispose();
	}
}