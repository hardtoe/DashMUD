package org.dashmud.cli;

public class User {
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
