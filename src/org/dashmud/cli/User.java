package org.dashmud.cli;

import org.dashmud.engine.MudObject;

public class User extends MudObject {
	private final String name;
	private final String hash;
	
	public User(
		final String name,
		final String hash
	) {
		this.name = name;
		this.hash = hash;
	}
	
	public String getName() {
		return name;
	}

	public String getHash() {
		return hash;
	}
}
