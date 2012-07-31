package org.dashmud.cli;

import java.util.LinkedList;
import java.util.List;

public class GetBooleanCommand extends Command {
	public static final CommandBuilder BUILDER() {
		return new CommandBuilder() {	
			@Override
			public List<String> getCompletions(final String prefix) {
				LinkedList<String> completions = 
					new LinkedList<String>();
				
				completions.add("yes");
				completions.add("no");
				
				return completions;
			}
	
			@Override
			public boolean isComplete(final String command) {
				return isValid(command);
			}
	
			@Override
			public boolean isValid(final String command) {
				return 
					command.equals("y") || command.equals("n") ||
					command.equals("yes") || command.equals("no");
			}
	
			@Override
			public Command parse(final String command) {
				return new GetBooleanCommand(command);
			}
		};
	}
	
	private final boolean value;
	
	public GetBooleanCommand(
		final String command
	) {
		this.value = command.startsWith("y");
	}

	public boolean value() {
		return value;
	}

	@Override
	public void run(final Shell shell) {
		// TODO Auto-generated method stub
		
	}
}
