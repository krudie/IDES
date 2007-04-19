package presentation.template;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import pluggable.operation.Operation;
import pluggable.operation.OperationManager;
import presentation.fsa.FSAGraph;

import main.Hub;
import model.fsa.FSAModel;
import model.template.TemplateBlock;
import util.EscapeDialog;

public class AddTemplateDialog extends EscapeDialog {

	private JComboBox fsaList=new JComboBox();
	private JTextField nameField=new JTextField();
	private JRadioButton moduleButton;
	private boolean doCreate=false;
	
	public AddTemplateDialog()
	{
		super(Hub.getMainWindow(),Hub.string("addTemplate"),true);
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onEscapeEvent();
		    }
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		Box mainBox=Box.createVerticalBox();
		
		mainBox.add(new JLabel(Hub.string("FSAtoTemplateExplanation")));
		mainBox.add(fsaList);
		
		Box nameBox=Box.createHorizontalBox();
		nameBox.add(new JLabel(Hub.string("templateName")));
		nameBox.add(nameField);
		mainBox.add(nameBox);
		
		ButtonGroup typeGroup=new ButtonGroup();
		moduleButton=new JRadioButton(Hub.string("module"));
		typeGroup.add(moduleButton);
		moduleButton.setSelected(true);
		JRadioButton channelButton=new JRadioButton(Hub.string("channel"));
		typeGroup.add(channelButton);
		
		mainBox.add(moduleButton);
		mainBox.add(channelButton);
		
		JButton okButton=new JButton();
		JButton cancelButton=new JButton();		
		okButton = new JButton(Hub.string("OK"));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if("".equals((String)fsaList.getSelectedItem())
						||nameField.getText().equals(""))
				{
					Hub.displayAlert(Hub.string("createTemplateIncomplete"));
				}
				else
				{
					doCreate=true;
					onEscapeEvent();
				}
			}
		});
		cancelButton = new JButton(Hub.string("cancel"));
		cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					doCreate=false;
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

	public Template createTemplate()
	{
		Collection<FSAModel> fsas=Hub.getWorkspace().getModelsOfType(FSAModel.class);
		fsaList.removeAllItems();
		TreeSet<String> sortedFSAs=new TreeSet<String>();
		for(FSAModel fsa:fsas)
		{
			sortedFSAs.add(fsa.getName());
		}
		for(String s:sortedFSAs)
		{
			fsaList.addItem(s);
		}
		doCreate=false;
		setLocation(Hub.getCenteredLocationForDialog(getSize()));
		setVisible(true);
		if(!doCreate)
		{
			return null;
		}
		Template newTemplate=new Template(
				Hub.getWorkspace().getModel((String)fsaList.getSelectedItem()).getName(),
				moduleButton.isSelected()?Template.TYPE_MODULE:Template.TYPE_CHANNEL,
				nameField.getText());
		return newTemplate;
	}
	
	public void onEscapeEvent()
	{
		dispose();
	}
	
}
