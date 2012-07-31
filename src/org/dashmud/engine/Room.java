package org.dashmud.engine;

import java.util.Set;

import org.dashmud.cli.User;

import com.db4o.collections.ActivatableHashSet;

public class Room extends MudObject {
	private Set<User> users =
		new ActivatableHashSet<User>();
	
	private Set<MudObject> objects =
		new ActivatableHashSet<MudObject>();
	
	public void remove(final MudObject mudObject) {
		users.remove(mudObject);
		objects.remove(mudObject);
	}

	public void add(final MudObject mudObject) {
		if (mudObject instanceof User) {
			users.add((User) mudObject);
		} 
		
		objects.add(mudObject);
	}
}
