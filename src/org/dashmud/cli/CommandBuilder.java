package org.dashmud.cli;

public interface CommandBuilder extends TabCompleter {
	public Command parse(final String command);
}
