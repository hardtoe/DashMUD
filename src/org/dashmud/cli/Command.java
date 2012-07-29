package org.dashmud.cli;

public abstract class Command {
	public abstract void run(final Terminal terminal, final User user);
}
