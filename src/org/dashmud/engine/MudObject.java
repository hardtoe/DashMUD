package org.dashmud.engine;

public class MudObject {
	private Room room;

	public Room getRoom() {
		return room;
	}

	public void enterRoom(final Room room) {
		this.room.remove(this);
		this.room = room;
		this.room.add(this);
	}
	
	
}
