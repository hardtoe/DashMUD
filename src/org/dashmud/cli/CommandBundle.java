package org.dashmud.cli;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandBundle implements CommandBuilder {
	private final Map<String, CommandBuilder> builders =
		new LinkedHashMap<String, CommandBuilder>();
	
	protected void register(
		final String verb,
		final CommandBuilder builder
	) {
		builders.put(verb, builder);
	}
	
	@Override
	public List<String> getCompletions(final String prefix) {
		String[] args =
			prefix.trim().split("\\s+", 2);
		
		String verb = args[0];
		String parameters = (args.length > 1 ? args[1] : "");
		
		if (builders.containsKey(verb)) {
			return builders.get(verb).getCompletions(parameters);
			
		} else {
			List<String> completions =
				new LinkedList<String>();
			
			for (String knownVerb : builders.keySet()) {
				if (knownVerb.startsWith(verb)) {
					completions.add(knownVerb);
				}
			}
			
			return completions;
		}
	}

	@Override
	public boolean isComplete(final String command) {
		String[] args =
			command.trim().split("\\s+", 2);
		
		String verb = args[0];
		String parameters = (args.length > 1 ? args[1] : "");
		
		if (builders.containsKey(verb)) {
			return builders.get(verb).isComplete(parameters);
			
		} else {
			return false;
		}
	}

	@Override
	public boolean isValid(final String command) {
		String[] args =
			command.trim().split("\\s+", 2);
		
		String verb = args[0];
		String parameters = (args.length > 1 ? args[1] : "");
		
		if (builders.containsKey(verb)) {
			return builders.get(verb).isValid(parameters);
			
		} else {
			return false;
		}
	}

	/**
	 * Should not be called unless isValid()
	 */
	@Override
	public Command parse(final String command) {
		String[] args =
				command.trim().split("\\s+", 2);
		
		String verb = args[0];
		String parameters = (args.length > 1 ? args[1] : "");
		
		if (builders.containsKey(verb)) {
			return builders.get(verb).parse(parameters);
			
		} else {
			return null;
		}
	}
}
