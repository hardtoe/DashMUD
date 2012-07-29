	package org.dashmud.cli;

import java.util.LinkedList;
import java.util.List;

public class EmoteCommand extends Command {
	public static final CommandBuilder BUILDER = new CommandBuilder() {
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
			return new EmoteCommand(command);
		}
	};
	
	public EmoteCommand(final String command) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
