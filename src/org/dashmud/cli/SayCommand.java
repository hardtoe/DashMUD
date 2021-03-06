package org.dashmud.cli;

import java.util.LinkedList;
import java.util.List;

public class SayCommand extends Command {
	public static final CommandBuilder BUILDER(final User user) {
		return new CommandBuilder() {
			@Override
			public List<String> getCompletions(final String prefix) {
				return new LinkedList<String>();
			}
	
			@Override
			public boolean isComplete(final String command) {
				return true;
			}
	
			@Override
			public boolean isValid(final String command) {
				return true;
			}
	
			@Override
			public Command parse(final String command) {
				return new SayCommand(user, command);
			}
		};
	}
	
	public SayCommand(
		final User user, 
		final String command
	) {
		//user.getRoom().sendEvent(new MessageEvent());
		System.out.println(command);
	}

	@Override
	public void run(final Shell shell) {
		// TODO Auto-generated method stub
		
	}
}
