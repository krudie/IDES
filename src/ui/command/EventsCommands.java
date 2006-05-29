package ui.command;

import org.pietschy.command.ActionCommand;

public class EventsCommands {

	public static class CreateEventCommand extends ActionCommand {

		public CreateEventCommand(){
			super("event.create.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			System.out.println("Create an event and add to global and local alphabets.");
		}
		
	}
	
	public static class RemoveEventCommand extends ActionCommand {

		public RemoveEventCommand(){
			super("event.remove.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			System.out.println("Remove an event from local alphabet (leave it in the global alphabet).");
		}
		
	}

	public static class PruneEventsCommand extends ActionCommand {

		public PruneEventsCommand(){
			super("event.prune.command");
		}
		
		@Override
		protected void handleExecute() {
			// TODO Auto-generated method stub
			System.out.println("Remove all events from global alphabet that don't exist in any local alphabet in the workspace.");
		}
		
	}

	
}
