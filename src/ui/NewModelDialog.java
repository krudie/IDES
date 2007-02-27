package ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import main.Hub;
import model.ModelDescriptor;
import model.ModelManager;

import util.EscapeDialog;

public class NewModelDialog extends EscapeDialog {

	protected JList modelList;
	protected ModelDescriptor[] modelDescriptors;
	private ModelDescriptor selectedMD=null;
	private static int lastIdx=0;
	
	public NewModelDialog() throws HeadlessException {
		super(Hub.getMainWindow(),Hub.string("newModelTitle"),true);
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onEscapeEvent();
		    }
		});
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		SelectModelListener sml=new SelectModelListener();
		
		Box mainBox=Box.createVerticalBox();
		
		Box titleBox=Box.createHorizontalBox();
		titleBox.add(new JLabel(Hub.string("newModelDescription")));
		titleBox.add(Box.createHorizontalGlue());
		mainBox.add(titleBox);
		
		mainBox.add(Box.createRigidArea(new Dimension(0,5)));
		
		Vector<Component> items=new Vector<Component>();
		modelDescriptors=ModelManager.getAllModels();
		for(int i=0;i<modelDescriptors.length;++i)
		{
			Box vbox=Box.createVerticalBox();
			JLabel l=new JLabel(new ImageIcon(modelDescriptors[i].getIcon()));
			l.setAlignmentX(Component.CENTER_ALIGNMENT);
			vbox.add(l);
			l=new JLabel(modelDescriptors[i].getTypeDescription());
			l.setAlignmentX(Component.CENTER_ALIGNMENT);
			vbox.add(l);
			items.add(vbox);
		}
		modelList=new JList(items);
		modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		modelList.setLayoutOrientation(JList.VERTICAL_WRAP);
		modelList.setVisibleRowCount(1);
		modelList.setCellRenderer(new ComponentCellRenderer());
		modelList.addMouseListener(sml);
		if(items.size()<=lastIdx)
			lastIdx=0;
		if(items.size()>0)
		{
			modelList.setSelectedIndex(lastIdx);
			modelList.ensureIndexIsVisible(lastIdx);
		}
		JScrollPane sp=new JScrollPane(modelList);
		sp.setPreferredSize(new Dimension(400,75));
		mainBox.add(sp);
		
		JButton OKButton=new JButton(Hub.string("OK"));
		OKButton.addActionListener(sml);
		getRootPane().setDefaultButton(OKButton);

		JButton cancelButton=new JButton(Hub.string("cancel"));
		cancelButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						onEscapeEvent();
					}
				});
		
		JPanel p = new JPanel(new FlowLayout());
		p.add(OKButton);		
		p.add(cancelButton);
		mainBox.add(p);
		
		mainBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		getContentPane().add(mainBox);
		pack();

		OKButton.setPreferredSize(new Dimension(
				Math.max(OKButton.getWidth(),cancelButton.getWidth()),OKButton.getHeight()));
		OKButton.invalidate();
		cancelButton.setPreferredSize(new Dimension(
				Math.max(OKButton.getWidth(),cancelButton.getWidth()),cancelButton.getHeight()));
		cancelButton.invalidate();
	}

	public ModelDescriptor selectModel()
	{
		setVisible(true);
		return selectedMD;
	}
	
	/**
	 * Called when the user presses the <code>Escape</code> key.
	 */
	protected void onEscapeEvent()
	{
		dispose();		
	}
	
	private class SelectModelListener extends MouseAdapter implements ActionListener{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			Component selected = (Component)modelList.getSelectedValue();
			if(selected != null){
				selectedMD=modelDescriptors[modelList.getSelectedIndex()];
				onEscapeEvent();
			}
		}
		
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount()>1&&!modelList.isSelectionEmpty())
			{
				Component selected = (Component)modelList.getSelectedValue();
				int idx=modelList.getSelectedIndex();
				Point m=e.getPoint();
				m.x-=modelList.getCellBounds(idx, idx).x;
				m.y-=modelList.getCellBounds(idx, idx).y;
				if(selected.getParent().getBounds().contains(m))
				{
					actionPerformed(new ActionEvent(modelList,0,""));
				}
			}
		}
	}

}
