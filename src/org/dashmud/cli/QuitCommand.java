package org.dashmud.cli;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class QuitCommand extends Command {
	public static final CommandBuilder BUILDER =
		new CommandBuilder() {
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
				return new QuitCommand(command);
			}
		};

	public static class QuitError extends Error {
		private static final long serialVersionUID = 158536888426101971L;
	}
	
	public QuitCommand(
		final String command
	) {

	}

	@Override
	public void run(final Terminal terminal, final User user) {
		try {
			if (terminal.promptBoolean("Do you want to disconnect from the server? ")) {
				throw new QuitError();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
