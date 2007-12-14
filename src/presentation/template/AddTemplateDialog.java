package presentation.template;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import util.EscapeDialog;

public class AddTemplateDialog extends EscapeDialog implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2569162407615616293L;

	private JComboBox fsaList = new JComboBox();

	private JTextField nameField = new JTextField();

	private JRadioButton moduleButton;

	private JList eventList;

	private boolean doCreate = false;

	private Vector<JCheckBox> checkBoxes = new Vector<JCheckBox>();

	public AddTemplateDialog()
	{
		super(Hub.getMainWindow(), Hub.string("addTemplate"), true);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				onEscapeEvent();
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		Box mainBox = Box.createVerticalBox();

		mainBox.add(new JLabel(Hub.string("FSAtoTemplateExplanation")));
		mainBox.add(fsaList);
		fsaList.addActionListener(this);

		Box nameBox = Box.createHorizontalBox();
		nameBox.add(new JLabel(Hub.string("templateName")));
		nameBox.add(nameField);
		mainBox.add(nameBox);

		ButtonGroup typeGroup = new ButtonGroup();
		moduleButton = new JRadioButton(Hub.string("module"));
		typeGroup.add(moduleButton);
		moduleButton.setSelected(true);
		moduleButton.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent ce)
			{
				eventList.setEnabled(moduleButton.isSelected());
				for (JCheckBox cb : checkBoxes)
				{
					cb.setSelected(!moduleButton.isSelected());
				}
			}
		});
		JRadioButton channelButton = new JRadioButton(Hub.string("channel"));
		typeGroup.add(channelButton);

		eventList = new JList();
		eventList.setCellRenderer(new ListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus)
			{
				((Component)value).setEnabled(list.isEnabled());
				return (Component)value;
			}
		});
		eventList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting() || eventList.isSelectionEmpty())
				{
					return;
				}
				((JCheckBox)eventList.getSelectedValue())
						.setSelected(!((JCheckBox)eventList.getSelectedValue())
								.isSelected());
				eventList.clearSelection();
			}
		});
		eventList.setPreferredSize(new Dimension(100, 400));
		mainBox.add(new JScrollPane(eventList));
		updateEvents();

		mainBox.add(moduleButton);
		mainBox.add(channelButton);

		JButton okButton = new JButton();
		JButton cancelButton = new JButton();
		okButton = new JButton(Hub.string("OK"));
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if ("".equals(fsaList.getSelectedItem())
						|| nameField.getText().equals(""))
				{
					Hub.displayAlert(Hub.string("createTemplateIncomplete"));
				}
				else
				{
					doCreate = true;
					onEscapeEvent();
				}
			}
		});
		cancelButton = new JButton(Hub.string("cancel"));
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				doCreate = false;
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
	}

	public Template createTemplate()
	{
		Collection<FSAModel> fsas = Hub
				.getWorkspace().getModelsOfType(FSAModel.class);
		fsaList.removeAllItems();
		TreeSet<String> sortedFSAs = new TreeSet<String>();
		for (FSAModel fsa : fsas)
		{
			sortedFSAs.add(fsa.getName());
		}
		for (String s : sortedFSAs)
		{
			fsaList.addItem(s);
		}
		doCreate = false;
		setLocation(Hub.getCenteredLocationForDialog(getSize()));
		setVisible(true);
		if (!doCreate)
		{
			return null;
		}
		HashSet<Long> iface = new HashSet<Long>();
		FSAModel fsa = (FSAModel)Hub.getWorkspace().getModel((String)fsaList
				.getSelectedItem());
		HashMap<String, FSAEvent> eventMap = new HashMap<String, FSAEvent>();
		for (FSAEvent event : fsa.getEventSet())
		{
			eventMap.put(event.getSymbol(), event);
		}
		for (JCheckBox cb : checkBoxes)
		{
			if (cb.isSelected())
			{
				FSAEvent event = eventMap.get(cb.getText());
				if (event != null)
				{
					iface.add(new Long(event.getId()));
				}
			}
		}
		Template newTemplate = new Template(
				fsa.getName(),
				iface,
				moduleButton.isSelected() ? Template.TYPE_MODULE
						: Template.TYPE_CHANNEL,
				nameField.getText());
		return newTemplate;
	}

	@Override
	public void onEscapeEvent()
	{
		dispose();
	}

	protected void updateEvents()
	{
		checkBoxes.clear();
		if (fsaList.getSelectedItem() != null
				&& !"".equals(fsaList.getSelectedItem()))
		{
			FSAModel fsa = (FSAModel)Hub
					.getWorkspace().getModel((String)fsaList.getSelectedItem());
			for (FSAEvent e : fsa.getEventSet())
			{
				JCheckBox cb = new JCheckBox(e.getSymbol());
				// all events in a channel must be interface events
				if (!moduleButton.isSelected())
				{
					cb.setSelected(true);
				}
				checkBoxes.add(cb);
			}
		}
		eventList.setListData(checkBoxes);
		eventList.setPreferredSize(new Dimension(100, checkBoxes.size() * 30));
	}

	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == fsaList)
		{
			updateEvents();
		}
	}
}
