package io.template.ver2_1;

import ilc.classes.DefaultICLInput;
import ilc.classes.ILCProject;
import io.CommonFileActions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import main.Hub;
import model.fsa.FSAEvent;
import model.fsa.FSAModel;
import model.fsa.FSASupervisor;
import des.interfaces.Automaton;
import des.interfaces.Event;

public class PLCExporter
{

	public static void export(Collection<FSAModel> modules,
			Collection<FSASupervisor> supervisors, Map<String, String> plcCode)
	{
		JFileChooser fc;

		fc = new JFileChooser(Hub.persistentData
				.getProperty(CommonFileActions.LAST_PATH_SETTING_NAME));

		fc.setDialogTitle(Hub.string("saveModelTitle"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int retVal;
		boolean fcDone = true;
		File file = null;
		do
		{
			retVal = fc.showSaveDialog(Hub.getMainWindow());
			if (retVal != JFileChooser.APPROVE_OPTION)
			{
				break;
			}
			file = fc.getSelectedFile();
			if (new File(file.getAbsolutePath() + File.separator
					+ "declarations.txt").exists()
					|| new File(file.getAbsolutePath() + File.separator
							+ "code.txt").exists())
			{
				int choice = JOptionPane.showConfirmDialog(Hub.getMainWindow(),
						Hub.string("fileExistAsk1") + file.getPath()
								+ Hub.string("fileExistAsk2"),
						Hub.string("saveModelTitle"),
						JOptionPane.YES_NO_CANCEL_OPTION);
				fcDone = choice != JOptionPane.NO_OPTION;
				if (choice != JOptionPane.YES_OPTION)
				{
					retVal = JFileChooser.CANCEL_OPTION;
				}
			}
		}
		while (!fcDone);

		if (retVal != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		Set<Event> globalSet = new HashSet<Event>();
		Vector<Automaton> sups = new Vector<Automaton>();
		Vector<Automaton> sys = new Vector<Automaton>();
		for (FSAModel m : modules)
		{
			sys.add(new AutomatonWrapper(m));
			for (FSAEvent e : m.getEventSet())
			{
				globalSet.add(new EventWrapper(e));
			}
		}
		for (FSAModel s : supervisors)
		{
			sups.add(new AutomatonWrapper(s));
			for (FSAEvent e : s.getEventSet())
			{
				globalSet.add(new EventWrapper(e));
			}
		}
		Map<Event, Vector<String>> codeMap = new HashMap<Event, Vector<String>>();
		for (String event : plcCode.keySet())
		{
			FSAEvent fsaEvent = new model.fsa.ver2_1.Event(0);
			fsaEvent.setSymbol(event);
			String plcEvent = "";
			for (int i = 0; i < event.length(); ++i)
			{
				if (Character.isLetterOrDigit(event.charAt(i)))
				{
					plcEvent += "" + event.charAt(i);
				}
			}
			Vector<String> code = new Vector<String>();
			for (String line : plcCode.get(event).split("\n"))
			{
				line = line.replaceAll("<event>", plcEvent);
				code.add(line);
			}
			codeMap.put(new EventWrapper(fsaEvent), code);
		}
		DefaultICLInput input = new DefaultICLInput(
				new Vector<Event>(globalSet),
				sups,
				sys,
				"siemens",
				10,
				1,
				codeMap);
		ILCProject project = new ILCProject(input);
		Vector<String> declaration = project.getDeclarationCode();
		Vector<String> code = project.getPLCCode();
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file
					.getAbsolutePath()
					+ File.separator + "declarations.txt"));
			for (String line : declaration)
			{
				out.write(line + "\r\n");
			}
			out.close();
			out = new BufferedWriter(new FileWriter(file.getAbsolutePath()
					+ File.separator + "code.txt"));
			for (String line : code)
			{
				out.write(line + "\r\n");
			}
			out.close();
		}
		catch (IOException e)
		{
			// TODO use string resource for the message
			Hub.displayAlert("Export failed.");
		}
	}
}
