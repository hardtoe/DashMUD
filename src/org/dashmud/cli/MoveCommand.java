package org.dashmud.cli;

import java.util.LinkedList;
import java.util.List;

public class MoveCommand extends Command {
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
				return new MoveCommand(command);
			}
		};
	}
	
	public MoveCommand(final String command) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(final Shell shell) {
		// TODO Auto-generated method stub
		
	}
}
