package org.dashmud.d20modern;

public abstract class Creature {
	// ability scores
	private Ability strength;
	private Ability dexterity;
	private Ability constitution;
	private Ability intelligence;
	private Ability wisdom;
	private Ability charisma;
	
	private CreatureSize size;

	private int totalHitPoints;
	private int currentHitPoints;
	
	private int level;
	
	public Ability getStrength() {
		return strength;
	}
	
	public Ability getDexterity() {
		return dexterity;
	}
	
	public Ability getConstitution() {
		return constitution;
	}
	
	public Ability getIntelligence() {
		return intelligence;
	}
	
	public Ability getWisdom() {
		return wisdom;
	}
	
	public Ability getCharisma() {
		return charisma;
	}
	
	public CreatureSize getSize() {
		return size;
	}
	
	public int getHitPoints() {
		return currentHitPoints;
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getMaxHitPoints() {
		return totalHitPoints;
	}
	
	public boolean isDisabled() {
		return getHitPoints() <= 0;
	}
	
	public boolean isDying() {
		return getHitPoints() < 0;
	}
	
	public boolean isDead() {
		return 
			getHitPoints() <= -10 ||
			getConstitution().getScore() == 0;
	}
	
	public abstract int getBaseAttackBonus();
	
	public abstract int getFortBaseSave();
	
	public abstract int getRefBaseSave();
	
	public abstract int getWillBaseSave();
	
	public abstract int getReputationBonus();
	
	public abstract int getDefenseBonus();
	
	/**
	 * @return Speed in feet per turn.
	 */
	public abstract int getSpeed();
	
	/**
	 * @return Number of attacks per turn.
	 */
	public abstract int getNumAttacks();
}
