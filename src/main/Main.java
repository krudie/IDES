package main;

import ui.MainWindow;

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
