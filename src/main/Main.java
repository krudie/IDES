package main;

import ui.MainWindow;
import ui.UIStateModel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {				
		UIStateModel.instance().setWindow(new MainWindow());
	}
}
