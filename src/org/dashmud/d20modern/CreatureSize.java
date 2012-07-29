package org.dashmud.d20modern;

public enum CreatureSize {
	COLOSSAL(-8),
	GARGANTUAN(-4),
	HUGE(-2),
	LARGE(-1),
	MEDIUM(0),
	SMALL(1),
	TINY(2),
	DIMINUTIVE(4),
	FINE(8);
	
	private final int modifier;
	
	private CreatureSize(final int modifier) {
		this.modifier = modifier;
	}
	
	public int getModifier() {
		return modifier;
	}
}
