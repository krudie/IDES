package ui;

import ides.api.core.Annotable;
import ides.api.core.Hub;
import ides.api.plugin.model.DESModel;
import ides.api.plugin.presentation.Presentation;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

/**
 * Displays and allows edit of user annotations to models. Supports local "undo"
 * and "redo" which supersedes the ones from the main application.
 * 
 * @author Lenko Grigorov
 */
public class AnnotationTab extends JTextArea implements Presentation,
		DocumentListener
{
	private static final long serialVersionUID = -5562434449489133647L;

	protected final static String TITLE = "Annotations";

	private JScrollPane sp;

	protected DESModel model;

	private UndoManager undoManager;

	public AnnotationTab(DESModel model)
	{
		this.model = model;

		setWrapStyleWord(true);
		setLineWrap(true);
		setFont(Hub.getMainWindow().getFont());
		setName(TITLE);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		if (model.hasAnnotation(Annotable.TEXT_ANNOTATION))
		{
			setText((String)model.getAnnotation(Annotable.TEXT_ANNOTATION));
		}

		undoManager = new UndoManager();
		getDocument().addUndoableEditListener(undoManager);
		String undo = "undo";
		String redo = "redo";
		getActionMap().put(undo, new AbstractAction()
		{
			private static final long serialVersionUID = -3127870472167076298L;

			public void actionPerformed(ActionEvent e)
			{
				undo();
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				ActionEvent.CTRL_MASK),
				undo);
		getActionMap().put(redo, new AbstractAction()
		{
			private static final long serialVersionUID = 1995810338966332209L;

			public void actionPerformed(ActionEvent e)
			{
				redo();
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				ActionEvent.CTRL_MASK),
				redo);

		getDocument().addDocumentListener(this);
		sp = new JScrollPane(this);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	}

	public void forceRepaint()
	{
		sp.repaint();
	}

	public JComponent getGUI()
	{
		return sp;
	}

	public DESModel getModel()
	{
		return model;
	}

	public void release()
	{
		model = null;
	}

	public void setTrackModel(boolean b)
	{
	}

	public void changedUpdate(DocumentEvent arg0)
	{
		if ("".equals(getText()))
		{
			model.removeAnnotation(Annotable.TEXT_ANNOTATION);
		}
		else
		{
			model.setAnnotation(Annotable.TEXT_ANNOTATION, getText());
		}
		model.metadataChanged();
	}

	public void insertUpdate(DocumentEvent arg0)
	{
		if ("".equals(getText()))
		{
			model.removeAnnotation(Annotable.TEXT_ANNOTATION);
		}
		else
		{
			model.setAnnotation(Annotable.TEXT_ANNOTATION, getText());
		}
		model.metadataChanged();
	}

	public void removeUpdate(DocumentEvent arg0)
	{
		if ("".equals(getText()))
		{
			model.removeAnnotation(Annotable.TEXT_ANNOTATION);
		}
		else
		{
			model.setAnnotation(Annotable.TEXT_ANNOTATION, getText());
		}
		model.metadataChanged();
	}

	void undo()
	{
		if (undoManager.canUndo())
		{
			undoManager.undo();
		}
	}

	protected void redo()
	{
		if (undoManager.canRedo())
		{
			undoManager.redo();
		}
	}
}