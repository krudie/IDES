/**
 * 
 */
package ui;

import ides.api.core.Hub;
import ides.api.model.fsa.FSAModel;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.operation.Operation;
import ides.api.plugin.operation.OperationManager;
import ides.api.utilities.EscapeDialog;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pluggable.layout.CompositeStateLabeller;

/**
 * @author Lenko Grigorov
 * @author Chris Dragert
 */
public class OperationDialog extends EscapeDialog
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6177704794804850005L;

	protected JList opList = new JList();

	protected Vector<JComboBox> inputs = null;

	// FIXME: listInput should be used with unbounded-input operations
	protected JList listInput = null;

	protected Vector<JTextField> outputNames = null;

	private Box inputsBox;

	private Box outputsBox;

	private Box descriptionBox;

	private ActionListener al = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			setSuggestedValue();
		}
	};

	public OperationDialog()
	{
		super(Hub.getMainWindow(), Hub.string("operationsDialogTitle"), true);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				onEscapeEvent();
			}
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		Box mainBox = Box.createVerticalBox();

		Box controlBox = Box.createHorizontalBox();

		Vector<String> ops = new Vector<String>();
		for (Iterator<String> i = OperationManager
				.instance().getOperationNames().iterator(); i.hasNext();)
		{
			ops.add(i.next());
		}
		Collections.sort(ops);
		opList.setListData(ops);
		opList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		opList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					resetBoxes(inputsBox,
							outputsBox,
							descriptionBox,
							OperationManager.instance().getOperation(opList
									.getSelectedValue().toString()));
				}
			}
		});
		JScrollPane spo = new JScrollPane(opList);
		spo.setPreferredSize(new Dimension(225, 350));
		spo.setBorder(BorderFactory.createTitledBorder(Hub
				.string("operationsListTitle")));
		controlBox.add(spo);

		controlBox.add(Box.createRigidArea(new Dimension(5, 0)));

		inputsBox = Box.createVerticalBox();
		JScrollPane sp = new JScrollPane(inputsBox);
		sp.setPreferredSize(new Dimension(225, 350));
		sp
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(BorderFactory.createTitledBorder(Hub
				.string("modelListTitle")));
		controlBox.add(sp);

		outputsBox = Box.createVerticalBox();
		sp = new JScrollPane(outputsBox);
		sp.setPreferredSize(new Dimension(225, 350));
		sp
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(BorderFactory
				.createTitledBorder(Hub.string("outputTitle")));
		controlBox.add(sp);

		descriptionBox = Box.createVerticalBox();
		sp = new JScrollPane(descriptionBox);
		sp.setPreferredSize(new Dimension(225, 350));
		sp
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setBorder(BorderFactory.createTitledBorder("Description"));
		controlBox.add(sp);

		mainBox.add(controlBox);

		mainBox.add(Box.createRigidArea(new Dimension(0, 5)));

		JButton okButton = new JButton();
		JButton cancelButton = new JButton();
		okButton = new JButton(Hub.string("OK"));
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				boolean emptyInput = false;
				for (int i = 0; i < inputs.size(); ++i)
				{
					if (inputs.elementAt(i) == null
							|| inputs
									.elementAt(i).getSelectedItem().toString()
									.equals(""))
					{
						emptyInput = true;
					}
				}
				boolean emptyField = false;
				for (int i = 0; i < outputNames.size(); ++i)
				{
					if (outputNames.elementAt(i) != null
							&& outputNames.elementAt(i).getText().equals(""))
					{
						emptyField = true;
					}
				}
				if (opList.isSelectionEmpty() || emptyInput || emptyField)
				{
					Hub.displayAlert(Hub.string("missingOperationParams"));
				}
				else
				{
					// call function to perform selected operation
					Operation op = OperationManager
							.instance().getOperation(opList
									.getSelectedValue().toString());
					Object[] inputModels = new Object[inputs.size()];
					for (int i = 0; i < inputs.size(); ++i)
					{
						inputModels[i] = Hub.getWorkspace().getModel(inputs
								.elementAt(i).getSelectedItem().toString());
					}
					Object[] outputs = op.perform(inputModels);
					String[] outputDesc = op.getDescriptionOfOutputs().clone();
					boolean closeWindow = false;

					for (int i = 0; i < outputs.length; ++i)
					{
						if (outputs[i] instanceof Boolean)
						{
							JOptionPane.showMessageDialog(Hub.getMainWindow(),
									outputDesc[i],
									Hub.string("result"),
									JOptionPane.PLAIN_MESSAGE);
						}
						else if (outputs[i] instanceof FSAModel)
						{
							FSAModel fsa = (FSAModel)outputs[i];
							fsa.setName(outputNames.elementAt(i).getText());
							CompositeStateLabeller.labelStates(fsa);
							Hub.getWorkspace().addModel(fsa);
							Hub.getWorkspace().setActiveModel(fsa.getName());
							closeWindow = true;
						}
						else
						{
							Hub.displayAlert(Hub.string("cantInterpretOutput"));
						}
					}
					if (closeWindow)
					{
						onEscapeEvent();
					}
				}
			}
		});
		cancelButton = new JButton(Hub.string("cancel"));
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				onEscapeEvent();
			}
		});
		JPanel p = new JPanel(new FlowLayout());
		p.add(okButton);
		p.add(cancelButton);
		mainBox.add(p);

		mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(mainBox);
		pack();

		okButton.setPreferredSize(new Dimension(Math.max(okButton.getWidth(),
				cancelButton.getWidth()), okButton.getHeight()));
		okButton.invalidate();
		cancelButton
				.setPreferredSize(new Dimension(Math.max(okButton.getWidth(),
						cancelButton.getWidth()), cancelButton.getHeight()));
		cancelButton.invalidate();

		setVisible(true);
	}

	protected void setSuggestedValue()
	{
		String suggestedName = opList.getSelectedValue().toString() + "(";
		for (int i = 0; i < inputs.size() - 1; ++i)
		{
			suggestedName += inputs.elementAt(i).getSelectedItem().toString()
					+ ",";
		}
		if (!inputs.isEmpty())
		{
			suggestedName += inputs.lastElement().getSelectedItem().toString();
		}
		suggestedName += ")";
		if (outputNames.size() == 1 && outputNames.firstElement() != null)
		{
			outputNames.firstElement().setText(suggestedName);
		}
		else
		{
			for (int i = 0; i < outputNames.size(); ++i)
			{
				if (outputNames.elementAt(i) != null)
				{
					outputNames.elementAt(i).setText(suggestedName + " (" + i
							+ ")");
				}
			}
		}
	}

	@Override
	protected void onEscapeEvent()
	{
		dispose();
	}

	protected void resetBoxes(Box in, Box out, Box descriptionBox, Operation o)
	{
		// configure input box
		in.removeAll();
		inputs = new Vector<JComboBox>();
		String[] descs = o.getDescriptionOfInputs();
		Class<?>[] types = o.getTypeOfInputs();
		// FIXME this is a temporary patch for infinite inputs
		if (o.getNumberOfInputs() == -1)
		{
			descs = new String[o.getDescriptionOfInputs().length + 1];
			System.arraycopy(o.getDescriptionOfInputs(),
					0,
					descs,
					0,
					descs.length - 1);
			descs[descs.length - 1] = descs[descs.length - 2];
			types = new Class[o.getTypeOfInputs().length + 1];
			System
					.arraycopy(o.getTypeOfInputs(),
							0,
							types,
							0,
							types.length - 1);
			types[types.length - 1] = types[types.length - 2];
		}
		for (int i = 0; i < descs.length; ++i)
		{
			Vector<Object> v = new Vector<Object>(Hub
					.getWorkspace().getModelsOfType(types[i]));
			for (int j = 0; j < v.size(); ++j)
			{
				if (v.elementAt(j) instanceof DESModel)
				{
					v.setElementAt(((DESModel)v.elementAt(j)).getName(), j);
				}
				else
				{
					v.setElementAt(v.elementAt(j).toString(), j);
				}
			}
			v.add(0, "");
			JPanel p = new JPanel();
			JComboBox cb = new JComboBox(v);
			cb.addActionListener(al);
			inputs.add(cb);
			p.add(cb);
			p.setBorder(BorderFactory.createTitledBorder(descs[i]));
			in.add(p);
			in.add(Box.createRigidArea(new Dimension(0, 5)));
		}
		in.add(Box.createVerticalGlue());

		// configure output box
		out.removeAll();
		outputNames = new Vector<JTextField>();
		descs = o.getDescriptionOfOutputs();
		types = o.getTypeOfOutputs();
		for (int i = 0; i < descs.length; ++i)
		{
			if (types[i].equals(FSAModel.class))
			{
				JPanel p = new JPanel();
				JTextField tf = new JTextField(20);
				outputNames.add(tf);
				p.add(tf);
				p.setBorder(BorderFactory.createTitledBorder(Hub
						.string("nameFor")));
				out.add(p);
				out.add(Box.createRigidArea(new Dimension(0, 5)));
			}
			else
			{
				outputNames.add(null);
			}
		}
		out.add(Box.createVerticalGlue());

		// configure description box
		descriptionBox.removeAll();
		descriptionBox.add(Box.createRigidArea(new Dimension(0, 7)));

		// looks okay but isn't graceful
		JTextArea descriptionTextArea = new JTextArea();
		descriptionTextArea.setText(o.getDescription());
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		// descriptionTextArea.setEnabled(false);
		descriptionTextArea.setBackground(descriptionBox.getBackground());
		descriptionTextArea.setForeground(descriptionBox.getForeground());
		descriptionTextArea.setBorder(BorderFactory.createTitledBorder(""));
		descriptionTextArea.setFont(descriptionBox.getFont());

		descriptionBox.add(descriptionTextArea);
		descriptionBox.add(Box.createRigidArea(new Dimension(0, 136)));

		// now pack and repaint the dialog
		pack();
		repaint();
	}
}
