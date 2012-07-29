package org.dashmud.cli;

import java.util.List;

public interface TabCompleter {
	public List<String> getCompletions(final String prefix);
	public boolean isComplete(final String command);
	public boolean isValid(final String command);
}
