/**
 * 
 */
package ui;

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

import pluggable.operation.OperationManager;

import main.Hub;
import model.fsa.FSAModel;
import util.EscapeDialog;

/**
 * @author Lenko Grigorov
 *
 */
public class OperationDialog extends EscapeDialog {

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
		JList modelList=new JList();
		Vector models=new Vector();
		for(Iterator<FSAModel> i=Hub.getWorkspace().getAutomata();i.hasNext();)
			models.add(i.next().getName());
		modelList.setListData(models);
		JScrollPane spm=new JScrollPane(modelList);
		spm.setBorder(BorderFactory.createTitledBorder(Hub.string("modelListTitle")));
		controlBox.add(spm);
		
		controlBox.add(Box.createRigidArea(new Dimension(5,0)));
		
		JList opList=new JList();
		Vector ops=new Vector();
		for(Iterator<String> i=OperationManager.getAllOperations().iterator();i.hasNext();)
			ops.add(i.next());
		opList.setListData(ops);
		JScrollPane spo=new JScrollPane(opList);
		spo.setBorder(BorderFactory.createTitledBorder(Hub.string("operationsListTitle")));
		controlBox.add(spo);
		
		controlBox.add(Box.createRigidArea(new Dimension(5,0)));

		JTextField nameField=new JTextField(15);
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
		//ActionListener commitListener = new CommitListener();		
		okButton = new JButton(Hub.string("OK"));
		//okButton.addActionListener(commitListener);
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

	protected void onEscapeEvent()
	{
		dispose();
	}

}
