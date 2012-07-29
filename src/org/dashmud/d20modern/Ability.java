package org.dashmud.d20modern;

public class Ability {
	private int score;
	private int damage;
	
	public void set(final int value) {
		this.score = value;
	}
	
	public void addDamage(final int damage) {
		this.damage += Math.abs(damage);
	}
	
	public void clearDamage() {
		this.damage = 0;
	}
	
	public void decrementDamage(final int amount) {
		this.damage = Math.max(0, getDamage() - amount);
	}

	public int getDamage() {
		return damage;
	}
	
	public int getScore() {
		return score - damage;
	}
	
	public int getModifier() {
		return (getScore() / 2) - 5;
	}
	
	public boolean isDamaged() {
		return damage != 0;
	}
}
