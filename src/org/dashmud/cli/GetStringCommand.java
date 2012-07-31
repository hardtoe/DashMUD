package org.dashmud.cli;

import java.util.LinkedList;
import java.util.List;

public class GetStringCommand extends Command {
	public static final CommandBuilder BUILDER() {
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
				return new GetStringCommand(command);
			}
		};
	}
	
	private final String value;
	
	public GetStringCommand(
		final String command
	) {
		this.value = command;
	}

	public String value() {
		return value;
	}

	@Override
	public void run(final Shell shell) {
		// TODO Auto-generated method stub
	}
}
