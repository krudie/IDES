package main;

import io.fsa.ver1.FileOperations;
import ui.MainWindow;
import ui.UIStateModel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO load UISettings and workspace in a thread
		// show splash screen
		
		new MainWindow().setVisible(true);
	}
}
