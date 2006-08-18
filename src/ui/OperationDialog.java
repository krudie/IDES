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
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pluggable.operation.Operation;
import pluggable.operation.OperationManager;

import main.Hub;
import model.fsa.FSAModel;
import model.fsa.ver1.Automaton;
import util.EscapeDialog;

/**
 * @author Lenko Grigorov
 *
 */
public class OperationDialog extends EscapeDialog {

	protected JList modelList=new JList();
	protected JList opList=new JList();
	protected JTextField nameField=new JTextField(20);
	
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
		Vector models=new Vector();
		for(Iterator<FSAModel> i=Hub.getWorkspace().getAutomata();i.hasNext();)
			models.add(i.next().getName());
		modelList.setListData(models);
		modelList.addListSelectionListener(
				new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{
						if(!e.getValueIsAdjusting()&&!opList.isSelectionEmpty())
							setSuggestedValue();
					}
				}
		);
		JScrollPane spm=new JScrollPane(modelList);
		spm.setBorder(BorderFactory.createTitledBorder(Hub.string("modelListTitle")));
		controlBox.add(spm);
		
		controlBox.add(Box.createRigidArea(new Dimension(5,0)));
		
		Vector ops=new Vector();
		for(Iterator<String> i=OperationManager.getOperationNames().iterator();i.hasNext();)
			ops.add(i.next());
		opList.setListData(ops);
		opList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		opList.addListSelectionListener(
				new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{
						if(!e.getValueIsAdjusting())
							setSuggestedValue();
					}
				}
		);
		JScrollPane spo=new JScrollPane(opList);
		spo.setBorder(BorderFactory.createTitledBorder(Hub.string("operationsListTitle")));
		controlBox.add(spo);
		
		controlBox.add(Box.createRigidArea(new Dimension(5,0)));

		nameField.setMaximumSize(new Dimension(nameField.getMaximumSize().width,
				nameField.getPreferredSize().height));
		JPanel namePanel=new JPanel();
		namePanel.add(nameField);
		namePanel.setBorder(BorderFactory.createTitledBorder(Hub.string("outputModelName")));
		controlBox.add(namePanel);
		
		mainBox.add(controlBox);
		
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		JButton okButton=new JButton();
		JButton cancelButton=new JButton();		
		okButton = new JButton(Hub.string("OK"));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(opList.isSelectionEmpty()||nameField.getText().equals(""))
					Hub.displayAlert(Hub.string("missingOperationParams"));
				else
				{
					Operation op=OperationManager.getOperation(opList.getSelectedValue().toString());
					Object[] inputs=modelList.getSelectedValues();
					for(int i=0;i<inputs.length;++i)
						inputs[i]=Hub.getWorkspace().getFSAModel(inputs[i].toString());
					Automaton a=(Automaton)op.perform(inputs)[0];
					a.setName(nameField.getText());
					FileOperations.saveAutomatonAs(a);
					onEscapeEvent();
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
		for(int i=0;i<=modelList.getSelectedValues().length-2;++i)
			suggestedName+=modelList.getSelectedValues()[i]+",";
		if(!modelList.isSelectionEmpty())
			suggestedName+=modelList.getSelectedValues()
				[modelList.getSelectedValues().length-1];
		suggestedName+=")";
		nameField.setText(suggestedName);		
	}

	protected void onEscapeEvent()
	{
		dispose();
	}

}
