/**
 * 
 */
package ui;

import io.fsa.ver1.FileOperations;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pluggable.operation.Operation;
import pluggable.operation.OperationManager;
import presentation.fsa.FSAGraph;

import main.Hub;
import model.DESModel;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import util.EscapeDialog;

/**
 * @author Lenko Grigorov
 *
 */
public class OperationDialog extends EscapeDialog {

	protected JList opList=new JList();
	protected Vector<JComboBox> inputs=null;
	//FIXME: listInput should be used with unbounded-input operations
	protected JList listInput=null;
	protected Vector<JTextField> outputNames=null;
	private Box inputsBox;
	private Box outputsBox;
	private ActionListener al=new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			setSuggestedValue();
		}
	};
	
	public OperationDialog()
	{
		super(Hub.getMainWindow(),Hub.string("operationsDialogTitle"),true);
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onEscapeEvent();
		    }
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		Box mainBox=Box.createVerticalBox();
		
		Box controlBox=Box.createHorizontalBox();
		
		Vector<String> ops=new Vector<String>();
		for(Iterator<String> i=OperationManager.getOperationNames().iterator();i.hasNext();)
			ops.add(i.next());
		Collections.sort(ops);
		opList.setListData(ops);
		opList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		opList.addListSelectionListener(
				new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{
						if(!e.getValueIsAdjusting())
						{
							resetInputOutputBoxes(inputsBox,outputsBox,OperationManager.getOperation(opList.getSelectedValue().toString()));
						}
					}
				}
		);
		JScrollPane spo=new JScrollPane(opList);
		spo.setPreferredSize(new Dimension(225,350));
		spo.setBorder(BorderFactory.createTitledBorder(Hub.string("operationsListTitle")));
		controlBox.add(spo);
		
		controlBox.add(Box.createRigidArea(new Dimension(5,0)));
		
		inputsBox=Box.createVerticalBox();
		JScrollPane sp=new JScrollPane(inputsBox);
		sp.setPreferredSize(new Dimension(225,350));
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(BorderFactory.createTitledBorder(Hub.string("modelListTitle")));
		controlBox.add(sp);

		outputsBox=Box.createVerticalBox();
		sp=new JScrollPane(outputsBox);
		sp.setPreferredSize(new Dimension(225,350));
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(BorderFactory.createTitledBorder(Hub.string("outputTitle")));
		controlBox.add(sp);
		
		mainBox.add(controlBox);
		
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		JButton okButton=new JButton();
		JButton cancelButton=new JButton();		
		okButton = new JButton(Hub.string("OK"));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				boolean emptyInput=false;
				for(int i=0;i<inputs.size();++i)
					if(inputs.elementAt(i)==null||inputs.elementAt(i).getSelectedItem().toString().equals(""))
						emptyInput=true;
				boolean emptyField=false;
				for(int i=0;i<outputNames.size();++i)
					if(outputNames.elementAt(i)!=null&&outputNames.elementAt(i).getText().equals(""))
						emptyField=true;
				if(opList.isSelectionEmpty()||emptyInput||emptyField)
					Hub.displayAlert(Hub.string("missingOperationParams"));
				else
				{
					Operation op=OperationManager.getOperation(opList.getSelectedValue().toString());
					Object[] inputModels=new Object[inputs.size()];
					for(int i=0;i<inputs.size();++i)
						inputModels[i]=Hub.getWorkspace().getFSAModel(inputs.elementAt(i).getSelectedItem().toString());
					Object[] outputs=op.perform(inputModels);
					boolean closeWindow = true;
					for(int i=0;i<outputs.length;++i)
					{
						if(outputs[i] instanceof FSAModel)
						{
							((FSAModel)outputs[i]).setName(outputNames.elementAt(i).getText());
							FSAGraph g=new FSAGraph((Automaton)outputs[i]);
							g.labelCompositeNodes();
							Hub.getWorkspace().addFSAGraph(g);
						}
						else if(outputs[i] instanceof Boolean)
						{
							Hub.displayAlert(op.getDescriptionOfOutputs()[i]+": "+(Boolean)outputs[i]);
							closeWindow = false;
						}
						else
						{
							Hub.displayAlert(Hub.string("cantInterpretOutput"));
						}
					}
					if (closeWindow) onEscapeEvent();
				}
			}
		});
		cancelButton = new JButton(Hub.string("cancel"));
		cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
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
		
		setVisible(true);
	}
	
	protected void setSuggestedValue()
	{
		String suggestedName=opList.getSelectedValue().toString()+"(";
		for(int i=0;i<inputs.size()-1;++i)
			suggestedName+=inputs.elementAt(i).getSelectedItem().toString()+",";
		if(!inputs.isEmpty())
			suggestedName+=inputs.lastElement().getSelectedItem().toString();
		suggestedName+=")";
		if(outputNames.size()==1&&outputNames.firstElement()!=null)
			outputNames.firstElement().setText(suggestedName);
		else
			for(int i=0;i<outputNames.size();++i)
				if(outputNames.elementAt(i)!=null)
					outputNames.elementAt(i).setText(suggestedName+" ("+i+")");
	}

	protected void onEscapeEvent()
	{
		dispose();
	}

	protected void resetInputOutputBoxes(Box in, Box out, Operation o)
	{
		in.removeAll();
		inputs=new Vector<JComboBox>();
		String[] descs=o.getDescriptionOfInputs();
		Class[] types=o.getTypeOfInputs();
		for(int i=0;i<descs.length;++i)
		{
			Vector v=new Vector(Hub.getWorkspace().getModelsOfType(types[i]));
			for(int j=0;j<v.size();++j)
			{
				if(v.elementAt(j) instanceof DESModel)
					v.setElementAt(((DESModel)v.elementAt(j)).getName(),j);
				else
					v.setElementAt(v.elementAt(j).toString(),j);
			}
			v.add(0,"");
			JPanel p=new JPanel();
			JComboBox cb=new JComboBox(v);
			cb.addActionListener(al);
			inputs.add(cb);
			p.add(cb);
			p.setBorder(BorderFactory.createTitledBorder(descs[i]));
			in.add(p);
			in.add(Box.createRigidArea(new Dimension(0,5)));
		}
		in.add(Box.createVerticalGlue());
		out.removeAll();
		outputNames=new Vector<JTextField>();
		descs=o.getDescriptionOfOutputs();
		types=o.getTypeOfOutputs();
		for(int i=0;i<descs.length;++i)
			if(types[i].equals(FSAModel.class))
			{
				JPanel p=new JPanel();
				JTextField tf=new JTextField(20);
				outputNames.add(tf);
				p.add(tf);
				p.setBorder(BorderFactory.createTitledBorder(Hub.string("nameFor")+descs[i]));
				out.add(p);
				out.add(Box.createRigidArea(new Dimension(0,5)));
			}
			else
				outputNames.add(null);
		out.add(Box.createVerticalGlue());
		pack();
		repaint();
	}
}
